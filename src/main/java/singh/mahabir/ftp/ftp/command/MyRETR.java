/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.ftpserver.command.AbstractCommand;
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
import org.apache.ftpserver.impl.ServerFtpStatistics;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.ftp.FileService;
import singh.mahabir.ftp.repository.entity.FtpFileEntity;
import singh.mahabir.ftp.resources.model.FileType;
import singh.mahabir.ftp.util.BeanUtil;
import singh.mahabir.ftp.util.FileCryptoUtil;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class MyRETR extends AbstractCommand {

    @Override
    /**
     * Execute command.
     */
    public void execute(final FtpIoSession session,
	    final FtpServerContext context, final FtpRequest request)
	    throws IOException, FtpException {

	try {

	    // argument check
	    String fileName = request.getArgument();
	    if (fileName == null) {
		session.write(LocalizedDataTransferFtpReply.translate(
			session,
			request,
			context,
			FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
			"RETR", null, null));
		return;
	    }

	    // get file object
	    FtpFile file = null;
	    try {
		file = session.getFileSystemView().getFile(fileName);
	    } catch (Exception ex) {
		log.debug("Exception getting file object", ex);
	    }
	    if (file == null) {
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
			"RETR.missing", fileName, file));
		return;
	    }
	    fileName = file.getAbsolutePath();

	    FileService service = BeanUtil.getBean(FileService.class);
	    FileType fileType = service.getFileTypeFromPath(fileName);
	    boolean isValid = service.isCorrectRequestFprFileUpload(fileName, session.getUser().getName());

	    // check file existance
	    if (!isValid /* || !file.doesExist() */ || fileType.equals(FileType.UNKNOWN)) {
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
			"RETR.missing", fileName, file));
		return;
	    }

//	    // check valid file
//	    if (!file.isFile()) {
//		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
//			FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
//			"RETR.invalid", fileName, file));
//		return;
//	    }
//
//	    // check permission
//	    if (!file.isReadable()) {
//		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
//			FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
//			"RETR.permission", fileName, file));
//		return;
//	    }

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
		    FtpReply.REPLY_150_FILE_STATUS_OKAY, "RETR", null));

	    // send file data to client
	    boolean failure = false;

	    DataConnection dataConnection;
	    try {
		dataConnection = session.getDataConnection().openConnection();
	    } catch (Exception e) {
		log.debug("Exception getting the output data stream", e);
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "RETR",
			null, file));
		return;
	    }

	    long transSz = 0L;
	    try {

		FtpFileEntity ftpFile = service.getFtpFile(session.getUser().getName(), fileType, file.getName());

		String secretkeyspec = service.getSecretkeyspec();
		byte[] filedata = FileCryptoUtil.decryptBytes(secretkeyspec, ftpFile.getData());

		try (ByteArrayInputStream bis = new ByteArrayInputStream(filedata)) {
		    dataConnection.transferToClient(session.getFtpletSession(), bis);
		}

		log.info("File downloaded {}", fileName);

		// notify the statistics component
		ServerFtpStatistics ftpStat = (ServerFtpStatistics) context
			.getFtpStatistics();
		if (ftpStat != null) {
		    ftpStat.setDownload(session, file, transSz);
		}

	    } catch (Exception ex) {
		log.debug("IOException during data transfer", ex);
		failure = true;
		session
			.write(LocalizedDataTransferFtpReply
				.translate(
					session,
					request,
					context,
					FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN,
					"RETR", fileName, file, transSz));
	    } finally {
		// make sure we really close the input stream
	    }

	    // if data transfer ok - send transfer complete message
	    if (!failure) {
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "RETR",
			fileName, file, transSz));

	    }
	} finally {
	    session.resetState();
	    session.getDataConnection().closeDataConnection();
	}
    }
}
