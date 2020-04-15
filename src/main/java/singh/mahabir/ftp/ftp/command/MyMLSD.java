/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.command;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.command.impl.listing.FileFormater;
import org.apache.ftpserver.command.impl.listing.ListArgument;
import org.apache.ftpserver.command.impl.listing.ListArgumentParser;
import org.apache.ftpserver.command.impl.listing.MLSTFileFormater;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.IODataConnectionFactory;
import org.apache.ftpserver.impl.LocalizedFtpReply;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class MyMLSD extends AbstractCommand {

    private final DBDirectoryLister directoryLister = new DBDirectoryLister();

    /**
     * Execute command.
     */
    public void execute(final FtpIoSession session,
	    final FtpServerContext context, final FtpRequest request)
	    throws IOException, FtpException {

	try {

	    // reset state
	    session.resetState();

	    // 24-10-2007 - added check if PORT or PASV is issued, see
	    // https://issues.apache.org/jira/browse/FTPSERVER-110
	    DataConnectionFactory connFactory = session.getDataConnection();
	    if (connFactory instanceof IODataConnectionFactory) {
		InetAddress address = ((IODataConnectionFactory) connFactory)
			.getInetAddress();
		if (address == null) {
		    session.write(new DefaultFtpReply(
			    FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS,
			    "PORT or PASV must be issued first"));
		    return;
		}
	    }

	    // get data connection
	    session.write(LocalizedFtpReply.translate(session, request, context,
		    FtpReply.REPLY_150_FILE_STATUS_OKAY, "MLSD", null));

	    // print listing data
	    DataConnection dataConnection;
	    try {
		dataConnection = session.getDataConnection().openConnection();
	    } catch (Exception e) {
		log.debug("Exception getting the output data stream", e);
		session.write(LocalizedFtpReply.translate(session, request, context,
			FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "MLSD",
			null));
		return;
	    }

	    boolean failure = false;
	    try {
		// parse argument
		ListArgument parsedArg = ListArgumentParser.parse(request
			.getArgument());

		FileFormater formater = new MLSTFileFormater((String[]) session
			.getAttribute("MLST.types"));

		dataConnection.transferToClient(session.getFtpletSession(), directoryLister.listFiles(
			parsedArg, session.getFileSystemView(), formater));
	    } catch (SocketException ex) {
		log.debug("Socket exception during data transfer", ex);
		failure = true;
		session.write(LocalizedFtpReply.translate(session, request, context,
			FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED,
			"MLSD", null));
	    } catch (IOException ex) {
		log.debug("IOException during data transfer", ex);
		failure = true;
		session
			.write(LocalizedFtpReply
				.translate(
					session,
					request,
					context,
					FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN,
					"MLSD", null));
	    } catch (IllegalArgumentException e) {
		log
			.debug("Illegal listing syntax: "
				+ request.getArgument(), e);
		// if listing syntax error - send message
		session
			.write(LocalizedFtpReply
				.translate(
					session,
					request,
					context,
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
					"MLSD", null));
	    }

	    // if data transfer ok - send transfer complete message
	    if (!failure) {
		session.write(LocalizedFtpReply.translate(session, request, context,
			FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "MLSD",
			null));
	    }
	} finally {
	    session.getDataConnection().closeDataConnection();
	}
    }
}
