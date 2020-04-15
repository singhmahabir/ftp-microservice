/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.command;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.command.impl.listing.LISTFileFormater;
import org.apache.ftpserver.command.impl.listing.ListArgument;
import org.apache.ftpserver.command.impl.listing.ListArgumentParser;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.IODataConnectionFactory;
import org.apache.ftpserver.impl.LocalizedDataTransferFtpReply;
import org.apache.ftpserver.impl.LocalizedFtpReply;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class MyLIST extends AbstractCommand {

    private static final LISTFileFormater LIST_FILE_FORMATER = new LISTFileFormater();

    private final DBDirectoryLister directoryLister = new DBDirectoryLister();

    /**
     * Execute command.
     */
    @Override
    public void execute(final FtpIoSession session,
	    final FtpServerContext context, final FtpRequest request)
	    throws IOException, FtpException {

	try {

	    // reset state variables
	    session.resetState();

	    // parse argument
	    ListArgument parsedArg = ListArgumentParser.parse(request
		    .getArgument());

	    // checl that the directory or file exists
	    FtpFile file = session.getFileSystemView().getFile(parsedArg.getFile());

	    if (!file.doesExist()) {
		log.debug("Listing on a non-existing file");
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN, "LIST",
			null, file));
		return;
	    }

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
		    FtpReply.REPLY_150_FILE_STATUS_OKAY, "LIST", null));

	    DataConnection dataConnection;
	    try {
		dataConnection = session.getDataConnection().openConnection();
	    } catch (Exception e) {
		log.debug("Exception getting the output data stream", e);
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "LIST",
			null, file));
		return;
	    }

	    // transfer listing data
	    boolean failure = false;
	    String dirList = directoryLister.listFiles(parsedArg,
		    session.getFileSystemView(), LIST_FILE_FORMATER);
	    try {
		dataConnection.transferToClient(session.getFtpletSession(), dirList);
	    } catch (SocketException ex) {
		log.debug("Socket exception during list transfer", ex);
		failure = true;
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED,
			"LIST", null, file));
	    } catch (IOException ex) {
		log.debug("IOException during list transfer", ex);
		failure = true;
		session
			.write(LocalizedDataTransferFtpReply
				.translate(
					session,
					request,
					context,
					FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN,
					"LIST", null, file));
	    } catch (IllegalArgumentException e) {
		log.debug("Illegal list syntax: " + request.getArgument(), e);
		// if listing syntax error - send message
		session
			.write(LocalizedDataTransferFtpReply
				.translate(
					session,
					request,
					context,
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
					"LIST", null, file));
	    }

	    // if data transfer ok - send transfer complete message
	    if (!failure) {
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "LIST",
			null, file, dirList.length()));
	    }
	} finally {
	    session.getDataConnection().closeDataConnection();
	}
    }

}
