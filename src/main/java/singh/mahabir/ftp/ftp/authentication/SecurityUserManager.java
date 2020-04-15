/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.authentication;

import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.ftp.FileService;
import singh.mahabir.ftp.repository.FtpUserRepository;
import singh.mahabir.ftp.repository.entity.FtpUserEntity;
import singh.mahabir.ftp.resources.FtpUserResource;
import singh.mahabir.ftp.util.BeanUtil;

/**
 * @author Mahabir Singh
 *
 */
@Slf4j
public class SecurityUserManager extends AbstractUserManager {

    public SecurityUserManager() {
	super();
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
	return new String[] {};
    }

    @Override
    public void delete(String username) throws FtpException {

    }

    @Override
    public void save(User user) throws FtpException {

    }

    @Override
    public boolean doesExist(String username) throws FtpException {
	return false;
    }

    /**
     * User Authentication Method
     */
    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
	log.info("\n authenticate the user and password");

	if (authentication instanceof UsernamePasswordAuthentication) {
	    UsernamePasswordAuthentication passwordAuthentication = (UsernamePasswordAuthentication) authentication;
	    String username = passwordAuthentication.getUsername();
	    String password = passwordAuthentication.getPassword();

	    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
		log.info("Either username or password is empty of null");
		throw new AuthenticationFailedException(
			"Authentication failed : username or password should not be empty of null");
	    }

	    FtpUserRepository repository = BeanUtil.getBean(FtpUserRepository.class);
	    BCryptPasswordEncoder passwordEncoder = BeanUtil.getBean(BCryptPasswordEncoder.class);

	    FtpUserEntity entity = repository.findById(username)
		    .orElseThrow(() -> new AuthenticationFailedException(username + FtpUserResource.NOT_FOUND));

	    if (!passwordEncoder.matches(password, entity.getPassword())) {
		throw new AuthenticationFailedException("Authentication Failed : Invalid Password");
	    }

	    if (!entity.isActive()) {
		throw new AuthenticationFailedException("Authentication Failed :" + username + " Not Active");
	    }

	    return getUserByName(entity);

	} else {
	    log.info("\n authenticate not supported user and password");
	    throw new IllegalArgumentException("Authenticate not supported by User Manager");
	}
    }

    private User getUserByName(FtpUserEntity entity) {
	BaseUser user = new BaseUser();
	user.setEnabled(entity.isActive());
	FileService service = BeanUtil.getBean(FileService.class);
	user.setHomeDirectory(service.getRoot());
	user.setName(entity.getFtpUserName());

	List<Authority> authorities = new ArrayList<>();
	if (user.getEnabled()) {
	    authorities.add(new WritePermission());
	}

	int idleTimeSec = 0;

	int maxLogin = idleTimeSec;
	int maxloginPerIp = idleTimeSec;
	authorities.add(new ConcurrentLoginPermission(maxLogin, maxloginPerIp));

	int uploadrate = idleTimeSec;
	int downloadrate = idleTimeSec;
	authorities.add(new TransferRatePermission(downloadrate, uploadrate));

	user.setMaxIdleTime(idleTimeSec);

	user.setAuthorities(authorities);
	return user;
    }

    @Override
    public User getUserByName(String username) throws FtpException {
	FtpUserEntity entity = BeanUtil.getBean(FtpUserRepository.class)
		.findById(username)
		.orElseThrow(() -> new IllegalArgumentException(username + FtpUserResource.NOT_FOUND));
	return getUserByName(entity);
    }

}
