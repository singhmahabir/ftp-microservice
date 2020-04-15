/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.ftpserver.command.impl.listing.FileFilter;
import org.apache.ftpserver.command.impl.listing.FileFormater;
import org.apache.ftpserver.command.impl.listing.ListArgument;
import org.apache.ftpserver.command.impl.listing.RegexFileFilter;
import org.apache.ftpserver.command.impl.listing.VisibleFileFilter;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.util.DateUtils;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.ftp.FileService;
import singh.mahabir.ftp.repository.entity.FtpFileEntity;
import singh.mahabir.ftp.resources.model.FileType;
import singh.mahabir.ftp.util.BeanUtil;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class DBDirectoryLister {

    private static final char[] NEWLINE = { '\r', '\n' };

    private String traverseFiles(final List<? extends FtpFile> files,
	    final FileFilter filter, final FileFormater formater) {
	StringBuilder sb = new StringBuilder();

	sb.append(traverseFiles(files, filter, formater, true));
	sb.append(traverseFiles(files, filter, formater, false));

	return sb.toString();
    }

    private String traverseFiles(final List<? extends FtpFile> files,
	    final FileFilter filter, final FileFormater formater,
	    boolean matchDirs) {
	StringBuilder sb = new StringBuilder();
	for (FtpFile file : files) {
	    if (file == null) {
		continue;
	    }

	    if (filter == null || filter.accept(file)) {
		if (file.isDirectory() == matchDirs) {
		    sb.append(formater.format(file));
		}
	    }
	}

	return sb.toString();
    }

    public String listFilesOld(final ListArgument argument,
	    final FileSystemView fileSystemView, final FileFormater formater)
	    throws IOException {
	StringBuilder sb = new StringBuilder();

	// get all the file objects
	List<? extends FtpFile> files = listFiles(fileSystemView, argument.getFile());
	if (files != null) {
	    FileFilter filter = null;
	    if (!argument.hasOption('a')) {
		filter = new VisibleFileFilter();
	    }
	    if (argument.getPattern() != null) {
		filter = new RegexFileFilter(argument.getPattern(), filter);
	    }

	    sb.append(traverseFiles(files, filter, formater));
	}

	return sb.toString();
    }

    public String listFiles(final ListArgument argument,
	    final FileSystemView fileSystemView, final FileFormater formater)
	    throws IOException {
	log.info("DBDirectoryLister listFiles with home directory {}");
	StringBuilder sb = new StringBuilder();

	FileService service = BeanUtil.getBean(FileService.class);
	String[] fileStructure = service.getFileStructure();

	String absolutePath = null;
	try {
	    absolutePath = fileSystemView.getWorkingDirectory().getAbsolutePath();
	} catch (FtpException e) {
	    log.error("Exception while retrieving working directory {}", e);
	    return null;
	}
	log.info("DBDirectoryLister absolutePath {}", absolutePath);
	String rootPath = service.getRoot();
	String userName = service.getUsernameFromPath(absolutePath);
	String userDirectory = rootPath + "/" + userName;
	if (absolutePath.equalsIgnoreCase(rootPath)) {
	    generateDirectory(sb, userName);
	    return sb.toString();
	} else if (absolutePath.equalsIgnoreCase(userDirectory)) {
	    generateDirectory(sb, fileStructure);
	    return sb.toString();
	} else if (absolutePath.equalsIgnoreCase(userDirectory + "/" + fileStructure[0])) {
	    List<FtpFileEntity> ftpFileList = service.getListOfFtpFile(userName, FileType.INPUT);
	    appendAllFiles(sb, ftpFileList);
	    return sb.toString();
	} else if (absolutePath.equalsIgnoreCase(userDirectory + "/" + fileStructure[1])) {
	    List<FtpFileEntity> ftpFileList = service.getListOfFtpFile(userName, FileType.OUTPUT);
	    appendAllFiles(sb, ftpFileList);
	    return sb.toString();
	}
	return sb.toString();
    }

    private void appendAllFiles(StringBuilder sb, List<FtpFileEntity> ftpFileList) {
	for (FtpFileEntity e : ftpFileList) {
	    sb.append(generateFiles(e));
	}
    }

    private String generateFiles(FtpFileEntity entity) {
	StringBuilder sb = new StringBuilder();
	sb.append("Size=" + entity.getFileSize());
	sb.append(";Modify=" + DateUtils.getFtpDate(entity.getCreatedDate().getTime()));
	sb.append(";Type=" + "file" + "; ");
	sb.append(entity.getFileName());
	sb.append(NEWLINE);
	return sb.toString();
    }

    private void generateDirectory(StringBuilder sb, String... folderStructure) {
	Arrays.stream(folderStructure).forEach(f -> {
	    sb.append("Size=" + 0);
	    sb.append(";Modify=" + DateUtils.getFtpDate(new Date().getTime()));
	    sb.append(";Type=" + "dir" + "; ");
	    sb.append(f);
	    sb.append(NEWLINE);
	});

    }

    /**
     * Get the file list. Files will be listed in alphabetlical order.
     */
    private List<? extends FtpFile> listFiles(FileSystemView fileSystemView, String file) {
	List<? extends FtpFile> files = null;
	try {
	    FtpFile virtualFile = fileSystemView.getFile(file);
	    if (virtualFile.isFile()) {
		List<FtpFile> auxFiles = new ArrayList<>();
		auxFiles.add(virtualFile);
		files = auxFiles;
	    } else {
		files = virtualFile.listFiles();
	    }
	} catch (FtpException ex) {
	}
	return files;
    }
}
