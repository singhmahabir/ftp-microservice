/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.command;

import java.io.IOException;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.command.impl.listing.LISTFileFormater;
import org.apache.ftpserver.command.impl.listing.ListArgument;
import org.apache.ftpserver.command.impl.listing.ListArgumentParser;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedDataTransferFtpReply;
import org.apache.ftpserver.impl.LocalizedFileActionFtpReply;
import org.apache.ftpserver.impl.LocalizedFtpReply;

/**
 * This command shall cause a status response to be sent over the control
 * connection in the form of a reply
 * 
 * @author Mahabir Singh
 *
 */
public class MySTAT extends AbstractCommand {

    private static final LISTFileFormater LIST_FILE_FORMATER = new LISTFileFormater();

    private final DBDirectoryLister directoryLister = new DBDirectoryLister();

    /**
     * Execute command
     */
    public void execute(final FtpIoSession session,
	    final FtpServerContext context, final FtpRequest request)
	    throws IOException {

	// reset state variables
	session.resetState();

	if (request.getArgument() != null) {
	    ListArgument parsedArg = ListArgumentParser.parse(request.getArgument());

	    // check that the directory or file exists
	    FtpFile file = null;
	    try {
		file = session.getFileSystemView().getFile(parsedArg.getFile());
		if (!file.doesExist()) {
		    session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			    FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN, "LIST",
			    null, file));
		    return;
		}

		String dirList = directoryLister.listFiles(parsedArg,
			session.getFileSystemView(), LIST_FILE_FORMATER);

		int replyCode;
		if (file.isDirectory()) {
		    replyCode = FtpReply.REPLY_212_DIRECTORY_STATUS;
		} else {
		    replyCode = FtpReply.REPLY_213_FILE_STATUS;
		}

		session.write(LocalizedFileActionFtpReply.translate(session, request, context,
			replyCode, "STAT",
			dirList, file));

	    } catch (FtpException e) {
		session
			.write(LocalizedFileActionFtpReply
				.translate(
					session,
					request,
					context,
					FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN,
					"STAT", null, file));
	    }

	} else {
	    // write the status info
	    session.write(LocalizedFtpReply.translate(session, request, context,
		    FtpReply.REPLY_211_SYSTEM_STATUS_REPLY, "STAT", null));
	}
    }

}
