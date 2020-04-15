/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.storage;

import java.io.File;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.filesystem.nativefs.impl.NameEqualsFileFilter;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.ftp.FileService;
import singh.mahabir.ftp.util.BeanUtil;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class DBFileSystemView implements FileSystemView {

    /**
     * the root directory will always end with '/'.
     */
    private String rootDir;

    /**
     * the first and the last character will always be '/' It is always with respect
     * to the root directory.
     */
    private String currDir;

    private User user;

    // private boolean writePermission;

    private boolean caseInsensitive;

    /**
     * Constructor - internal do not use directly, use
     * {@link NativeFileSystemFactory} instead
     */
    protected DBFileSystemView(User user) {
	this(user, false);
    }

    /**
     * Constructor - internal do not use directly, use
     * {@link NativeFileSystemFactory} instead
     */
    public DBFileSystemView(User user, boolean caseInsensetive) {
	if (user == null) {
	    throw new IllegalArgumentException("user can not be null");
	}
	if (user.getHomeDirectory() == null) {
	    throw new IllegalArgumentException(
		    "User home directory can not be null");
	}

	this.caseInsensitive = caseInsensetive;

	/**
	 * add last '/' if necessary
	 */
	this.rootDir = appendSlash(normalizeSeparateChar(user.getHomeDirectory()));
	log.info("DB filesystem view created for user \"{}\" with root directory \"{}\"", user.getName(), rootDir);
	this.user = user;
	currDir = user.getName();
//	currDir = "/";
    }

    @Override
    public FtpFile getHomeDirectory() throws FtpException {
//	return new DBFtpFile("/", new File(rootDir), user);
	return new DBFtpFile(rootDir, new File(rootDir), user);
    }

    @Override
    public FtpFile getWorkingDirectory() throws FtpException {
	log.info("DBFileSystemView  getWorkingDirectory called with current directory \"{}\" and root directory \"{}\"",
		currDir, rootDir);
	FtpFile fileObj = null;
	if (currDir.equals("/")) {
	    fileObj = new DBFtpFile("/", new File(rootDir), user);
	} else {
	    String fileName = rootDir + currDir;
	    fileObj = new DBFtpFile(fileName, new File(fileName), user);

	}
	return fileObj;
    }

    @Override
    public boolean changeWorkingDirectory(String dir) throws FtpException {
	log.info("DBFileSystemView changeWorkingDirectory with root directory {} and current directory {}",
		currDir, rootDir);
	if (dir.contains(rootDir)) {
	    dir = dir.replace(rootDir, "");
	}

	FileService service = BeanUtil.getBean(FileService.class);
	String[] fileStructure = service.getFileStructure();
	String subPkg = fileStructure[0].length() > fileStructure[1].length() ? fileStructure[0] : fileStructure[1];

	if (rootDir.length() < dir.length()
		&& dir.length() > (currDir.length() + user.getName().length() + subPkg.length() + 1)) {
	    log.info("DBFileSystemView  checking length request in valid path directory ");
	    return false;
	}

	if (dir.contains(appendSlash(user.getName()))) {
	    String[] d = dir.split("/");
	    String workingDirCheck = d[d.length - 1];
	    if (Arrays.stream(fileStructure).noneMatch(workingDirCheck::equals)) {
		return false;
	    }
	}

	if (dir.equalsIgnoreCase(user.getName())) {
	    currDir = "";
	}

	if (dir.contains(appendSlash(user.getName()))) {
	    dir = dir.replace(appendSlash(user.getName()), "");
	}

	if (!dir.contains("..") && currDir.contains("/") && currDir.length() > user.getName().length()) {
	    currDir = user.getName();
	}

	// not a directory - return false
	dir = getPhysicalName(rootDir, currDir, dir, caseInsensitive);

//	File dirObj = new File(dir);
//	if (!dirObj.isDirectory()) {
//	    return false;
//	}
//
//	// strip user root and add last '/' if necessary
//	dir = dir.substring(rootDir.length() - 1);
//	if (dir.charAt(dir.length() - 1) != '/') {
//	    dir = dir + '/';
//	}

	currDir = dir.replace(rootDir, "");
	log.info("DBFileSystemView changeWorkingDirectory current directory return is \"{}\"", currDir);
	return true;
    }

    @Override
    public FtpFile getFile(String file) throws FtpException {
	// get actual file object
	String physicalName = getPhysicalName(rootDir,
		currDir, file, caseInsensitive);
	File fileObj = new File(physicalName);

	// strip the root directory and return
	String userFileName = physicalName.substring(rootDir.length() - 1);
	return new DBFtpFile(userFileName, fileObj, user);
    }

    /**
     * Is the file content random accessible?
     */
    @Override
    public boolean isRandomAccessible() throws FtpException {
	return true;
    }

    /**
     * Dispose file system view - does nothing.
     */
    @Override
    public void dispose() {
    }

    /**
     * Get the physical canonical file name. It works like File.getCanonicalPath().
     * 
     * @param rootDir  The root directory.
     * @param currDir  The current directory. It will always be with respect to the
     *                 root directory.
     * @param fileName The input file name.
     * @return The return string will always begin with the root directory. It will
     *         never be null.
     */
    protected String getPhysicalName(final String rootDir,
	    final String currDir, final String fileName,
	    final boolean caseInsensitive) {

	// normalize root dir
	String normalizedRootDir = normalizeSeparateChar(rootDir);
	normalizedRootDir = appendSlash(normalizedRootDir);

	// normalize file name
	String normalizedFileName = normalizeSeparateChar(fileName);
	String result;

	// if file name is relative, set resArg to root dir + curr dir
	// if file name is absolute, set resArg to root dir
	if (normalizedFileName.charAt(0) != '/') {
	    // file name is relative
	    String normalizedCurrDir = normalize(currDir, "/");

	    result = normalizedRootDir + normalizedCurrDir.substring(1);
	} else {
	    result = normalizedRootDir;
	}

	// strip last '/'
	result = trimTrailingSlash(result);

	// replace ., ~ and ..
	// in this loop resArg will never end with '/'
	StringTokenizer st = new StringTokenizer(normalizedFileName, "/");
	while (st.hasMoreTokens()) {
	    String tok = st.nextToken();

	    // . => current directory
	    if (tok.equals(".")) {
		// ignore and move on
	    } else if (tok.equals("..")) {
		// .. => parent directory (if not root)
		if (result.startsWith(normalizedRootDir)) {
		    int slashIndex = result.lastIndexOf('/');
		    if (slashIndex != -1) {
			result = result.substring(0, slashIndex);
		    }
		}
	    } else if (tok.equals("~")) {
		// ~ => home directory (in this case the root directory)
		result = trimTrailingSlash(normalizedRootDir);
		continue;
	    } else {
		// token is normal directory name

		if (caseInsensitive) {
		    // we're case insensitive, find a directory with the name, ignoring casing
		    File[] matches = new File(result)
			    .listFiles(new NameEqualsFileFilter(tok, true));

		    if (matches != null && matches.length > 0) {
			// found a file matching tok, replace tok for get the right casing
			tok = matches[0].getName();
		    }
		}

		result = result + '/' + tok;

	    }
	}

	// add last slash if necessary
	if ((result.length()) + 1 == normalizedRootDir.length()) {
	    result += '/';
	}

	// make sure we did not end up above root dir
	if (!result.startsWith(normalizedRootDir)) {
	    result = normalizedRootDir;
	}

	return result;
    }

    /**
     * Append trailing slash ('/') if missing
     */
    private String appendSlash(String path) {
	return path.charAt(path.length() - 1) != '/' ? path + '/' : path;
    }

    /**
     * Prepend leading slash ('/') if missing
     */
    private String prependSlash(String path) {
	return path.charAt(0) != '/' ? '/' + path : path;
    }

    /**
     * Trim trailing slash ('/') if existing
     */
    private String trimTrailingSlash(String path) {
	if (path.charAt(path.length() - 1) == '/') {
	    return path.substring(0, path.length() - 1);
	} else {
	    return path;
	}
    }

    /**
     * Normalize separate character. Separate character should be '/' always.
     */
    private String normalizeSeparateChar(final String pathName) {
	return pathName.replace(File.separatorChar, '/').replace('\\', '/');
    }

    /**
     * Normalize separator char, append and prepend slashes. Default to defaultPath
     * if null or empty
     */
    private String normalize(String path, String defaultPath) {
	if (path == null || path.trim().length() == 0) {
	    path = defaultPath;
	}

	path = normalizeSeparateChar(path);
	path = prependSlash(appendSlash(path));
	return path;
    }

}
