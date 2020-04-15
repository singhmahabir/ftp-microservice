/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.command;

import java.io.File;
import java.io.IOException;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFileActionFtpReply;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.ftp.FileService;
import singh.mahabir.ftp.ftp.storage.DBFtpFile;
import singh.mahabir.ftp.util.BeanUtil;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class MyCWD extends AbstractCommand {

    /**
     * Execute command
     */
    @Override
    public void execute(final FtpIoSession session,
	    final FtpServerContext context, final FtpRequest request)
	    throws IOException, FtpException {

	// reset state variables
	session.resetState();
	log.info("CWD Command called with request : {} and user : {}", request.getArgument(),
		session.getUser().getName());
	FileService service = BeanUtil.getBean(FileService.class);

	// get new directory name
	String dirName = service.getRoot();
	if (request.hasArgument()) {
	    dirName = request.getArgument();
	}

	if (dirName.contains("/")) {
	    boolean isFtpRoot = dirName.equalsIgnoreCase(service.getRoot())
		    || dirName.equalsIgnoreCase(service.getRoot() + "/");
	    if (dirName.equals("/") || isFtpRoot) {
		session
			.write(LocalizedFileActionFtpReply.translate(session, request, context,
				FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
				"CWD", null,
				new DBFtpFile(service.getRoot(), new File(service.getRoot()), session.getUser())));
		return;
	    }

	    String requestedUser = service.getUsernameFromPath(dirName);
	    boolean isValidUser = requestedUser.equalsIgnoreCase(session.getUser().getName());
	    if (!isValidUser) {
		session
			.write(LocalizedFileActionFtpReply.translate(session, request, context,
				FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
				"CWD", null,
				new DBFtpFile(service.getRoot(), new File(service.getRoot()), session.getUser())));
		return;
	    }
	}

	// change directory
	FileSystemView fsview = session.getFileSystemView();
	boolean success = false;
	if (dirName.equals("..") && fsview.getWorkingDirectory()
		.getAbsolutePath()
		.equalsIgnoreCase(session.getUser().getHomeDirectory() + "/" + session.getUser().getName())) {
	    session
		    .write(LocalizedFileActionFtpReply.translate(session, request, context,
			    FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
			    "CWD", null,
			    new DBFtpFile(service.getRoot(), new File(service.getRoot()), session.getUser())));
	    return;
	}

	try {
	    success = fsview.changeWorkingDirectory(dirName);
	} catch (Exception ex) {
	    log.debug("Failed to change directory in file system", ex);
	}
	FtpFile cwd = fsview.getWorkingDirectory();
	if (success) {
	    dirName = cwd.getAbsolutePath();
	    session.write(LocalizedFileActionFtpReply.translate(session, request, context,
		    FtpReply.REPLY_250_REQUESTED_FILE_ACTION_OKAY, "CWD",
		    dirName, cwd));
	} else {
	    session
		    .write(LocalizedFileActionFtpReply.translate(session, request, context,
			    FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
			    "CWD", null, cwd));
	}
    }
}
