/**
 * All rights reserved.
 */

package singh.mahabir.ftp.resources;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.exception.FtpFileNotFoundException;
import singh.mahabir.ftp.repository.FtpFileRepository;
import singh.mahabir.ftp.repository.entity.FtpFileEntity;
import singh.mahabir.ftp.resources.model.FileInfo;
import singh.mahabir.ftp.resources.model.FtpFileRequest;
import singh.mahabir.ftp.resources.model.FtpFileResponse;

/**
 * @author Mahabir Singh
 *
 */
@RestController
@Validated
@Slf4j
@RequestMapping(value = "${ftp.rest.filestoragebasepath}")
public class FtpFileResource {

    @Autowired
    private FtpFileRepository repository;

    @PutMapping
    public ResponseEntity<String> updateFileStatus(@RequestBody @Valid FtpFileRequest fileRequest) {
	log.info("request at service layer for updateFileStatus fileRequest {}", fileRequest);
	FtpFileEntity ftpFile = repository.findById(fileRequest.getFileId())
		.orElseThrow(() -> new FtpFileNotFoundException(fileRequest.getFileId() + FtpUserResource.NOT_FOUND));

	ftpFile.setStatus(fileRequest.getStatus());
	ftpFile.setLastModifiedBy(fileRequest.getFtpUserName());
	ftpFile.setLastModifiedDate(getCurrentTimeStamp());

	repository.save(ftpFile);
	return ResponseEntity.ok("Updated Successfully");
    }

    @DeleteMapping(value = "/{fileId}")
    public ResponseEntity<String> deleteFtpUser(@PathVariable Long fileId) {
	log.info("request at service layer for deleteFtpUser user: {}", fileId);

	FtpFileEntity ftpFile = repository.findById(fileId)
		.orElseThrow(() -> new FtpFileNotFoundException(fileId + FtpUserResource.NOT_FOUND));

	repository.delete(ftpFile);
	return ResponseEntity.ok(null + " successfully deleted");
    }

    @GetMapping(value = "/{ftpUserId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FtpFileResponse> getAllFtpUserdetails(@PathVariable String ftpUserId) {
	log.info("request at service layer getAllFtpUserdetails for ftpUserId: {}", ftpUserId);

	List<FtpFileEntity> fileList = repository.findByUserName(ftpUserId);
	FtpFileResponse response = new FtpFileResponse();
	if (fileList.isEmpty()) {
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
	fileList.forEach(file -> {
	    FileInfo info = new FileInfo();
	    info.setFtpUserName(file.getUserName());
	    info.setFileName(file.getFileName());
	    info.setFileType(file.getFileType());
	    info.setFileSize(file.getFileSize());
	    info.setCreatedBy(file.getCreatedBy());
	    info.setCreationDate(file.getCreatedDate().toLocalDateTime());
	    info.setStatus(file.getStatus());

	    response.getFiles().add(info);
	});
	return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{fileId}/{ftpUserId}")
    public ResponseEntity<StreamingResponseBody> downloadFile(HttpServletResponse response, @PathVariable Long fileId,
	    @PathVariable String ftpUserId) {
	log.info("request at service layer downloadFile for fileId: {} and ftpUserId: {}", fileId, ftpUserId);

	FtpFileEntity fileEntity = repository.findByIdAndUserName(fileId, ftpUserId)
		.orElseThrow(() -> new FtpFileNotFoundException(fileId + FtpUserResource.NOT_FOUND));
	return new ResponseEntity<>(r -> r.write(fileEntity.getData()), HttpStatus.OK);
    }

    private Timestamp getCurrentTimeStamp() {
	return Timestamp.valueOf(LocalDateTime.now());
    }
}
