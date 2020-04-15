/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
import org.apache.ftpserver.util.IoUtils;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.ftp.FileService;
import singh.mahabir.ftp.repository.entity.FtpFileEntity;
import singh.mahabir.ftp.resources.model.FileStatus;
import singh.mahabir.ftp.resources.model.FileType;
import singh.mahabir.ftp.util.BeanUtil;
import singh.mahabir.ftp.util.FileCryptoUtil;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class MySTOR extends AbstractCommand {

    /**
     * Execute command.
     */
    @Override
    public void execute(final FtpIoSession session,
	    final FtpServerContext context, final FtpRequest request)
	    throws IOException, FtpException {

	try {
	    // argument check
	    String fileName = request.getArgument();
	    if (fileName == null) {
		session
			.write(LocalizedDataTransferFtpReply
				.translate(
					session,
					request,
					context,
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
					"STOR", null, null));
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

	    // get filename
	    FtpFile file = null;
	    try {
		file = session.getFileSystemView().getFile(fileName);
	    } catch (Exception ex) {
		log.debug("Exception getting file object", ex);
	    }
	    if (file == null) {
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
			"STOR.invalid", fileName, file));
		return;
	    }
	    fileName = file.getAbsolutePath();

	    FileService service = BeanUtil.getBean(FileService.class);
	    FileType fileType = service.getFileTypeFromPath(fileName);
	    boolean isValid = service.isCorrectRequestFprFileUpload(fileName, session.getUser().getName());

	    // get permission
	    if (!isValid || fileType.equals(FileType.UNKNOWN) || !file.isWritable()) {
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
			"STOR.permission", fileName, file));
		return;
	    }

	    boolean isValidFile = service.isValidFileExtension(fileName);
	    if (!isValidFile) {
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
			"STOR.permission", fileName, file));
		return;
	    }
	    // get data connection
	    session.write(
		    LocalizedFtpReply.translate(session, request, context,
			    FtpReply.REPLY_150_FILE_STATUS_OKAY, "STOR",
			    fileName))
		    .awaitUninterruptibly(10000);

	    DataConnection dataConnection;
	    try {
		dataConnection = session.getDataConnection().openConnection();
	    } catch (Exception e) {
		log.debug("Exception getting the input data stream", e);
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "STOR",
			fileName, file));
		return;
	    }

	    // transfer data
	    boolean failure = false;
	    OutputStream outStream = null;
	    long transSz = 0L;
	    try {
		FtpFileEntity entity = new FtpFileEntity();
		entity.setCreatedBy(session.getUser().getName());
		entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
		entity.setFileName(file.getName());
		entity.setFileType(fileType);
		entity.setLastModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
		entity.setLastModifiedBy(session.getUser().getName());
		entity.setStatus(FileStatus.UPLOADED);
		entity.setUserName(session.getUser().getName());

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		dataConnection.transferFromClient(session.getFtpletSession(), outputStream);

		File tempfile = new File("temp");
		FileOutputStream fos = new FileOutputStream(tempfile);
		outputStream.writeTo(fos);

		entity.setFileSize(tempfile.length());

		String secretKeySpec = service.getSecretkeyspec();
		byte[] encryptBytes = FileCryptoUtil.encryptBytes(secretKeySpec, outputStream.toByteArray());

		entity.setData(encryptBytes);

		service.uploadFile(entity);

		log.info("File uploaded {}", fileName);

		// closing the resources

		outputStream.close();
		fos.close();
		Files.deleteIfExists(tempfile.toPath());

		// notify the statistics component
		ServerFtpStatistics ftpStat = (ServerFtpStatistics) context
			.getFtpStatistics();
		ftpStat.setUpload(session, file, transSz);

	    } catch (IOException ex) {
		log.debug("IOException during data transfer", ex);
		failure = true;
		session
			.write(LocalizedDataTransferFtpReply
				.translate(
					session,
					request,
					context,
					FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN,
					"STOR", fileName, file));
	    } finally {
		// make sure we really close the output stream
		IoUtils.close(outStream);
	    }

	    // if data transfer ok - send transfer complete message
	    if (!failure) {
		session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
			FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "STOR",
			fileName, file, transSz));

	    }
	} finally {
	    session.resetState();
	    session.getDataConnection().closeDataConnection();
	}
    }
}
