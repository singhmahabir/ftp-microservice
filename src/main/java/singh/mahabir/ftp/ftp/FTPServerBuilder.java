/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.ftp.authentication.SecurityUserManagerFactory;

/**
 * @author Mahabir Singh
 *
 */
@Service
@Transactional
@Slf4j
public class FTPServerBuilder {

    private static Integer port;
    private static String filename;
    private static String password;
    private static String keypassword;
    private static String passivePort;
    private static String address;

    private static FtpServer server;

    @PostConstruct
    public static void initDBStorageFtpServer() {
	log.info("Initializing DB storage FTP Server");

	DBFtpServerFactory serverFactory = new DBFtpServerFactory();
	ListenerFactory listenerFactory = getListenerFactory();
	SecurityUserManagerFactory userManagerFactory = new SecurityUserManagerFactory();
	serverFactory.setUserManager(userManagerFactory.createUserManager());

	Map<String, Ftplet> ftplets = new HashMap<>();
	ftplets.put("ftplet", new CustomFtplet());

	serverFactory.setFtplets(ftplets);
	serverFactory.addListener("default", listenerFactory.createListener());
	server = serverFactory.createServer();
	try {
	    server.start();
	} catch (FtpException e) {
	    log.error("unable to start the server");
	}
    }

    @PreDestroy
    public void destroy() {
	log.info("@PreDestroy stoping FTP Server");
	server.stop();
    }

    @Value("${ftp.server.port}")
    public void setPort(Integer port) {
	FTPServerBuilder.port = port;
    }

    @Value("${ftp.keystore.filename}")
    public void setFilename(String filename) {
	FTPServerBuilder.filename = filename;
    }

    @Value("${ftp.keystore.password}")
    public void setPassword(String password) {
	FTPServerBuilder.password = password;
    }

    @Value("${ftp.keystore.keypassword}")
    public void setKeypassword(String keypassword) {
	FTPServerBuilder.keypassword = keypassword;
    }

    @Value("${ftp.passive.port}")
    public void setPassivePort(String passivePort) {
	FTPServerBuilder.passivePort = passivePort;
    }

    @Value("${ftp.address}")
    public void setAddress(String address) {
	FTPServerBuilder.address = address;
    }

    private static ListenerFactory getListenerFactory() {
	ListenerFactory factory = new ListenerFactory();
	factory.setPort(port);

	// ssl configuration
	SslConfigurationFactory ssl = new SslConfigurationFactory();
	ssl.setKeystoreFile(new File(filename));
	ssl.setKeyPassword(password);
	ssl.setKeystorePassword(keypassword);
//	factory.setSslConfiguration(ssl.createSslConfiguration());
	factory.setImplicitSsl(false);

	DataConnectionConfigurationFactory dccFactory = new DataConnectionConfigurationFactory();
	dccFactory.setPassivePorts(passivePort);
	if (address != null && !address.isEmpty()) {
	    dccFactory.setPassiveAddress(address);
	}
	factory.setDataConnectionConfiguration(dccFactory.createDataConnectionConfiguration());
	return factory;
    }
}
