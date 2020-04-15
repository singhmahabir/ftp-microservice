/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.ftpserver.filesystem.nativefs.impl.NativeFtpFile;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class DBFtpFile implements FtpFile {

    // the file name with respect to the user root.
    // The path separator character will be '/' and
    // it will always begin with '/'.
    private final String fileName;
    private final File file;
    private final User user;

    public DBFtpFile(final String fileName, final File file,
	    final User user) {
	if (fileName == null) {
	    throw new IllegalArgumentException("fileName can not be null");
	}
	if (file == null) {
	    throw new IllegalArgumentException("file can not be null");
	}
	if (fileName.length() == 0) {
	    throw new IllegalArgumentException("fileName can not be empty");
	} else if (fileName.charAt(0) != '/') {
	    throw new IllegalArgumentException(
		    "fileName must be an absolut path");
	}

	this.fileName = fileName;
	this.file = file;
	this.user = user;
    }

    /**
     * Get full name.
     */
    @Override
    public String getAbsolutePath() {

	// strip the last '/' if necessary
	String fullName = fileName;
	int filelen = fullName.length();
	if ((filelen != 1) && (fullName.charAt(filelen - 1) == '/')) {
	    fullName = fullName.substring(0, filelen - 1);
	}
	return fullName;
    }

    /**
     * Get short name.
     */
    @Override
    public String getName() {
	// root - the short name will be '/'
	if (fileName.equals("/")) {
	    return "/";
	}
	// strip the last '/'
	String shortName = fileName;
	int filelen = fileName.length();
	if (shortName.charAt(filelen - 1) == '/') {
	    shortName = shortName.substring(0, filelen - 1);
	}
	// return from the last '/'
	int slashIndex = shortName.lastIndexOf('/');
	if (slashIndex != -1) {
	    shortName = shortName.substring(slashIndex + 1);
	}
	return shortName;
    }

    /**
     * Is a hidden file?
     */
    @Override
    public boolean isHidden() {
	return file.isHidden();
    }

    /**
     * Is it a directory?
     */
    @Override
    public boolean isDirectory() {
	return file.isDirectory();
    }

    /**
     * Is it a file?
     */
    @Override
    public boolean isFile() {
	return file.isFile();
    }

    /**
     * Does this file exists?
     */
    @Override
    public boolean doesExist() {
	return file.exists();
    }

    /**
     * Get file size.
     */
    @Override
    public long getSize() {
	return file.length();
    }

    /**
     * Get file owner.
     */
    @Override
    public String getOwnerName() {
	return "user";
    }

    /**
     * Get group name
     */
    @Override
    public String getGroupName() {
	return "group";
    }

    /**
     * Get link count
     */
    @Override
    public int getLinkCount() {
	return file.isDirectory() ? 3 : 1;
    }

    /**
     * Get last modified time.
     */
    @Override
    public long getLastModified() {
	return file.lastModified();
    }

    @Override
    public boolean setLastModified(long time) {
	return file.setLastModified(time);
    }

    /**
     * Check read permission.
     */
    @Override
    public boolean isReadable() {
	return file.canRead();
    }

    /**
     * Check file write permission.
     */
    @Override
    public boolean isWritable() {
	log.debug("Checking authorization for " + getAbsolutePath());
	if (user.authorize(new WriteRequest(getAbsolutePath())) == null) {
	    log.debug("Not authorized");
	    return false;
	}

	log.debug("Checking if file exists");
	if (file.exists()) {
	    log.debug("Checking can write: " + file.canWrite());
	    return file.canWrite();
	}

	log.debug("Authorized");
	return true;
    }

    /**
     * Has delete permission.
     */
    @Override
    public boolean isRemovable() {
	// root cannot be deleted
	if ("/".equals(fileName)) {
	    return false;
	}
	String fullName = getAbsolutePath();

	// we check FTPServer's write permission for this file.
	if (user.authorize(new WriteRequest(fullName)) == null) {
	    return false;
	}
	// In order to maintain consistency, when possible we delete the last '/'
	// character in the String
	int indexOfSlash = fullName.lastIndexOf('/');
	String parentFullName;
	if (indexOfSlash == 0) {
	    parentFullName = "/";
	} else {
	    parentFullName = fullName.substring(0, indexOfSlash);
	}

	// we check if the parent FileObject is writable.
	DBFtpFile parentObject = new DBFtpFile(parentFullName, file
		.getAbsoluteFile()
		.getParentFile(), user);
	return parentObject.isWritable();
    }

    /**
     * Delete file.
     */
    @Override
    public boolean delete() {
	boolean retVal = false;
	if (isRemovable()) {
	    retVal = file.delete();
	}
	return retVal;
    }

    /**
     * Move file object.
     */
    @Override
    public boolean move(final FtpFile dest) {
	boolean retVal = false;
	if (dest.isWritable() && isReadable()) {
	    File destFile = ((DBFtpFile) dest).file;

	    if (destFile.exists()) {
		// renameTo behaves differently on different platforms
		// this check verifies that if the destination already exists,
		// we fail
		retVal = false;
	    } else {
		retVal = file.renameTo(destFile);
	    }
	}
	return retVal;
    }

    /**
     * Create directory.
     */
    @Override
    public boolean mkdir() {
	boolean retVal = false;
	if (isWritable()) {
	    retVal = file.mkdir();
	}
	return retVal;
    }

    /**
     * Get the physical file object.
     */
    @Override
    public File getPhysicalFile() {
	return file;
    }

    /**
     * List files. If not a directory or does not exist, null will be returned.
     */
    @Override
    public List<FtpFile> listFiles() {

	// is a directory
	if (!file.isDirectory()) {
	    return null;
	}

	// directory - return all the files
	File[] files = file.listFiles();
	if (files == null) {
	    return null;
	}

	// make sure the files are returned in order
	Arrays.sort(files, new Comparator<File>() {
	    public int compare(File f1, File f2) {
		return f1.getName().compareTo(f2.getName());
	    }
	});

	// get the virtual name of the base directory
	String virtualFileStr = getAbsolutePath();
	if (virtualFileStr.charAt(virtualFileStr.length() - 1) != '/') {
	    virtualFileStr += '/';
	}

	// now return all the files under the directory
	FtpFile[] virtualFiles = new FtpFile[files.length];
	for (int i = 0; i < files.length; ++i) {
	    File fileObj = files[i];
	    String fileName = virtualFileStr + fileObj.getName();
	    virtualFiles[i] = new DBFtpFile(fileName, fileObj, user);
	}

	return Collections.unmodifiableList(Arrays.asList(virtualFiles));
    }

    /**
     * Create output stream for writing.
     */
    @Override
    public OutputStream createOutputStream(final long offset)
	    throws IOException {

	// permission check
	if (!isWritable()) {
	    throw new IOException("No write permission : " + file.getName());
	}

	// create output stream
	final RandomAccessFile raf = new RandomAccessFile(file, "rw");
	raf.setLength(offset);
	raf.seek(offset);

	// The IBM jre needs to have both the stream and the random access file
	// objects closed to actually close the file
	return new FileOutputStream(raf.getFD()) {
	    @Override
	    public void close() throws IOException {
		super.close();
		raf.close();
	    }
	};
    }

    /**
     * Create input stream for reading.
     */
    @Override
    public InputStream createInputStream(final long offset) throws IOException {

	// permission check
	if (!isReadable()) {
	    throw new IOException("No read permission : " + file.getName());
	}

	// move to the appropriate offset and create input stream
	final RandomAccessFile raf = new RandomAccessFile(file, "r");
	raf.seek(offset);

	// The IBM jre needs to have both the stream and the random access file
	// objects closed to actually close the file
	return new FileInputStream(raf.getFD()) {
	    @Override
	    public void close() throws IOException {
		super.close();
		raf.close();
	    }
	};
    }

    /**
     * Implements equals by comparing getCanonicalPath() for the underlying file
     * instabnce. Ignores the fileName and User fields
     */
    @Override
    public boolean equals(Object obj) {
	if (obj instanceof NativeFtpFile) {
	    String thisCanonicalPath;
	    String otherCanonicalPath;
	    try {
		thisCanonicalPath = this.file.getCanonicalPath();
		otherCanonicalPath = ((DBFtpFile) obj).file
			.getCanonicalPath();
	    } catch (IOException e) {
		throw new RuntimeException("Failed to get the canonical path",
			e);
	    }

	    return thisCanonicalPath.equals(otherCanonicalPath);
	}
	return false;
    }

    @Override
    public int hashCode() {
	try {
	    return file.getCanonicalPath().hashCode();
	} catch (IOException e) {
	    return 0;
	}
    }
}
