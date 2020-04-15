/**
 * All rights reserved.
 */

package singh.mahabir.ftp.resources;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.configuration.FtpServerConfiguration;
import singh.mahabir.ftp.exception.UserExistException;
import singh.mahabir.ftp.exception.UserNotActiveException;
import singh.mahabir.ftp.exception.UserNotFoundException;
import singh.mahabir.ftp.repository.FtpUserRepository;
import singh.mahabir.ftp.repository.entity.FtpUserEntity;
import singh.mahabir.ftp.resources.model.FtpUserRequest;
import singh.mahabir.ftp.resources.model.FtpUserResponse;
import singh.mahabir.ftp.resources.model.ServerInfo;
import singh.mahabir.ftp.resources.model.UserInfo;
import singh.mahabir.ftp.resources.model.UserInfos;
import singh.mahabir.ftp.util.IPasswordGenerator;

/**
 * @author Mahabir Singh
 *
 */
@RestController
@Validated
@Slf4j
@RequestMapping(value = "${ftp.rest.apibasepath}")
public class FtpUserResource {

    public static final String NOT_FOUND = " Not Found";

    @Autowired
    private FtpUserRepository repository;

    @Autowired
    private IPasswordGenerator passwordGenerator;

    @Autowired
    private FtpServerConfiguration serverConfig;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FtpUserResponse> creteFtpUser(@RequestBody @Valid FtpUserRequest userRequest) {
	log.info("request at service layer for create user: {}", userRequest);

	Optional<FtpUserEntity> ftpUserOptional = repository.findById(userRequest.getFtpUsername());
	ftpUserOptional.ifPresent((user) -> {
	    throw new UserExistException(user.getFtpUserName() + " Alredy Exist");
	});

	char[] temporaryPassword = passwordGenerator.generateTemporaryPassword();
	String rawPassword = new String(temporaryPassword);

	FtpUserEntity entity = new FtpUserEntity();
	entity.setFtpUserName(userRequest.getFtpUsername());
	entity.setActive(true);
	entity.setCreatedBy(userRequest.getLoggedInUser());
	entity.setCreationDate(getCurrentTimeStamp());
	entity.setLastActivatedBy(userRequest.getLoggedInUser());
	entity.setLastModifiedBy(userRequest.getLoggedInUser());
	entity.setLastModifiedDate(getCurrentTimeStamp());
	entity.setLastPasswordChangedDate(getCurrentTimeStamp());
	entity.setPassword(passwordGenerator.generateHashedPassword(rawPassword));

	FtpUserEntity savedEntity = repository.save(entity);

	FtpUserResponse response = new FtpUserResponse();

	UserInfo userInfo = new UserInfo();
	userInfo.setFtpUserName(savedEntity.getFtpUserName());
	userInfo.setEnable(savedEntity.isActive());
	userInfo.setPassword(rawPassword);

	response.setUserInfo(userInfo);

	ServerInfo serverInfo = getServerInfo();

	response.setServerInfo(serverInfo);

	return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfos> getAllFtpUserDetail() {
	log.info("request at service layer for get all ftp user: {}");

	UserInfos response = new UserInfos();

	List<FtpUserEntity> userList = repository.findAll();
	if (userList.isEmpty()) {
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
	userList.forEach(user -> {
	    UserInfo info = new UserInfo();
	    info.setFtpUserName(user.getFtpUserName());
	    info.setEnable(user.isActive());
	    response.getUserList().add(info);
	});
	return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FtpUserResponse> generatePassword(@RequestBody @Valid FtpUserRequest userRequest) {
	log.info("request at service layer for generatePassword for user: {}", userRequest);

	Optional<FtpUserEntity> userOp = repository.findById(userRequest.getFtpUsername());
	FtpUserEntity entity = userOp
		.orElseThrow(() -> new UserNotFoundException(userRequest.getFtpUsername() + NOT_FOUND));
	if (!entity.isActive()) {
	    throw new UserNotActiveException(entity.getFtpUserName() + " Is Not Active");
	}
	String rawPassword = new String(passwordGenerator.generateTemporaryPassword());

	entity.setPassword(passwordGenerator.generateHashedPassword(rawPassword));
	entity.setLastModifiedBy(userRequest.getLoggedInUser());
	entity.setLastModifiedDate(getCurrentTimeStamp());
	entity.setLastPasswordChangedDate(getCurrentTimeStamp());

	repository.save(entity);

	FtpUserResponse response = new FtpUserResponse();

	UserInfo userInfo = new UserInfo();
	userInfo.setFtpUserName(entity.getFtpUserName());
	userInfo.setPassword(rawPassword);

	response.setUserInfo(userInfo);

	return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{ftpUserId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FtpUserResponse> getFtpUserDetail(@PathVariable String ftpUserId) {
	log.info("request at service layer getFtpUserDetail for user: {}", ftpUserId);

	FtpUserEntity entity = repository.findById(ftpUserId)
		.orElseThrow(() -> new UserNotFoundException(ftpUserId + NOT_FOUND));
	FtpUserResponse response = generateResponse(entity);
	return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FtpUserResponse> activateDeactivate(@RequestBody @Valid FtpUserRequest userRequest,
	    @PathVariable Boolean status) {
	log.info("request at service layer for activateDeactivate user {}", userRequest);

	FtpUserEntity entity = repository.findById(userRequest.getFtpUsername())
		.orElseThrow(() -> new UserNotFoundException(userRequest.getFtpUsername() + NOT_FOUND));

	entity.setLastModifiedBy(userRequest.getLoggedInUser());
	entity.setLastModifiedDate(getCurrentTimeStamp());

	if (status.booleanValue()) {
	    entity.setActive(true);
	    entity.setLastActivatedBy(userRequest.getLoggedInUser());
	} else {
	    entity.setActive(false);
	}
	return ResponseEntity.ok(generateResponse(repository.save(entity)));
    }

    @DeleteMapping(value = "/{ftpUserId}")
    public ResponseEntity<String> deleteFtpUser(@PathVariable String ftpUserId) {
	log.info("request at service layer for deleteFtpUser user: {}", ftpUserId);

	FtpUserEntity entity = repository.findById(ftpUserId)
		.orElseThrow(() -> new UserNotFoundException(ftpUserId + NOT_FOUND));

	repository.delete(entity);
	return ResponseEntity.ok(ftpUserId + " successfully deleted");
    }

    private Timestamp getCurrentTimeStamp() {
	return Timestamp.valueOf(LocalDateTime.now());
    }

    private ServerInfo getServerInfo() {
	ServerInfo serverInfo = new ServerInfo();
	serverInfo.setPort(serverConfig.getPort());
	serverInfo.setProtocol(serverConfig.getProtocol());
	serverInfo.setServername(serverConfig.getServerName());
	return serverInfo;
    }

    private FtpUserResponse generateResponse(FtpUserEntity entity) {
	FtpUserResponse response = new FtpUserResponse();

	UserInfo userInfo = new UserInfo();
	userInfo.setEnable(entity.isActive());
	userInfo.setFtpUserName(entity.getFtpUserName());

	response.setUserInfo(userInfo);

	if (entity.isActive()) {
	    response.setServerInfo(getServerInfo());
	}
	return response;
    }
}
