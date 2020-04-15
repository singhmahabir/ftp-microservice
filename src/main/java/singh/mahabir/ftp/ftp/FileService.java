/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.repository.FtpFileRepository;
import singh.mahabir.ftp.repository.entity.FtpFileEntity;
import singh.mahabir.ftp.resources.model.FileType;

/**
 * @author Mahabir Singh
 *
 */

@Service
@Transactional
@Slf4j
@Getter
public class FileService {

    @Autowired
    private FtpFileRepository fileRepository;

    @Value("${ftp.filestructure}")
    private String[] fileStructure;

    @Value("${ftp.basepath}")
    private String root;

    @Value("${ftp.secretkeyspec}")
    private String secretkeyspec;

    @Value("${ftp.fileExtensionSupport}")
    private String[] fileExtensionSupport;

    public boolean uploadFile(FtpFileEntity entity) {
	log.info("service upload the file in db ");
	return fileRepository.save(entity) != null;
    }

    public List<FtpFileEntity> getListOfFtpFile(String userName) {
	log.info("service getListOfFtpFile the file in db ");
	return fileRepository.findByUserName(userName);
    }

    public List<FtpFileEntity> getListOfFtpFile(String userName, FileType fileType) {
	log.info("service getListOfFtpFile the file in db ");
	return fileRepository.findByUserNameAndFileType(userName, fileType);
    }

    public FtpFileEntity getFtpFile(String userName, FileType fileType, String fileName) {
	log.info("service getFtpFile the file in db ");
	return fileRepository.findByUserNameAndFileTypeAndFileName(userName, fileType, fileName).get();
    }

    public String getUsernameFromPath(String path) {
	return path.split("/")[2];
    }

    public FileType getFileTypeFromPath(String path) {
	String[] noOfPath = path.split("/");
	if (fileStructure[0].equalsIgnoreCase(noOfPath[noOfPath.length - 2])) {
	    return FileType.INPUT;
	} else if (fileStructure[1].equalsIgnoreCase(noOfPath[noOfPath.length - 2])) {
	    return FileType.OUTPUT;
	}
	return FileType.UNKNOWN;
    }

    public boolean isCorrectRequestFprFileUpload(String path, String userName) {
	String[] noOfPath = path.split("/");
	return noOfPath.length == 4 && noOfPath[1].equalsIgnoreCase(userName);
    }

    public boolean isValidFileExtension(String fileName) {
	String[] fileExtensionList = fileName.split("[.]");
	String fileExtension = fileExtensionList[fileExtensionList.length - 1];
	return Arrays.stream(fileExtensionSupport).anyMatch(fileExtension::equals);
    }

}
