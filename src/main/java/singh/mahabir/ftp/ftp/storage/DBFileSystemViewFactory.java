/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.storage;

import java.io.File;

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
@Setter
public class DBFileSystemViewFactory implements FileSystemFactory {

    private boolean createHome = true;
    private boolean caseInsensetive;

    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
	if (createHome) {
	    String url = user.getHomeDirectory();
	    File homeDir = new File(url);
	    if (homeDir.isFile()) {
		log.error("Not a directroy :: {}", url);
		throw new FtpException("Not a directroy :: " + url);
	    }
	    if (!homeDir.exists() && !homeDir.mkdir()) {
		log.error("Can't create user home :: {}", url);
		throw new FtpException("Can't create user home :: " + url);
	    }
	}
	return new DBFileSystemView(user, caseInsensetive);
    }

}
