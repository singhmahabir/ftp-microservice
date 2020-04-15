/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.command;

import java.io.IOException;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFileActionFtpReply;
import org.apache.ftpserver.impl.ServerFtpStatistics;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class MyDELE extends AbstractCommand {

    /**
     * Execute command.
     */
    @Override
    public void execute(final FtpIoSession session,
	    final FtpServerContext context, final FtpRequest request)
	    throws IOException, FtpException {

	// reset state variables
	session.resetState();

	// argument check
	String fileName = request.getArgument();
	if (fileName == null) {
	    session.write(LocalizedFileActionFtpReply.translate(session, request, context,
		    FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
		    "DELE", null, null));
	    return;
	}

	// get file object
	FtpFile file = null;

	try {
	    file = session.getFileSystemView().getFile(fileName);
	} catch (Exception ex) {
	    log.debug("Could not get file " + fileName, ex);
	}
	if (file == null) {
	    session.write(LocalizedFileActionFtpReply.translate(session, request, context,
		    FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
		    "DELE.invalid", fileName, null));
	    return;
	}

	// check file
	fileName = file.getAbsolutePath();

	if (file.isDirectory()) {
	    session.write(LocalizedFileActionFtpReply.translate(session, request, context,
		    FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
		    "DELE.invalid", fileName, file));
	    return;
	}

	if (!file.isRemovable()) {
	    session.write(LocalizedFileActionFtpReply.translate(session, request, context,
		    FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN,
		    "DELE.permission", fileName, file));
	    return;
	}

	// now delete
	if (file.delete()) {
	    session.write(LocalizedFileActionFtpReply.translate(session, request, context,
		    FtpReply.REPLY_250_REQUESTED_FILE_ACTION_OKAY, "DELE",
		    fileName, file));

	    // log message
	    String userName = session.getUser().getName();

	    log.info("File delete : " + userName + " - " + fileName);

	    // notify statistics object
	    ServerFtpStatistics ftpStat = (ServerFtpStatistics) context
		    .getFtpStatistics();
	    ftpStat.setDelete(session, file);
	} else {
	    session.write(LocalizedFileActionFtpReply.translate(session, request, context,
		    FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN, "DELE",
		    fileName, file));
	}
    }

}