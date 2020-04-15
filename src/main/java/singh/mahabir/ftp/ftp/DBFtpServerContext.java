/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpStatistics;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.ftpletcontainer.FtpletContainer;
import org.apache.ftpserver.ftpletcontainer.impl.DefaultFtpletContainer;
import org.apache.ftpserver.impl.DefaultFtpStatistics;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.message.MessageResource;
import org.apache.ftpserver.message.MessageResourceFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.ftp.storage.DBCommandFactory;
import singh.mahabir.ftp.ftp.storage.DBFileSystemViewFactory;

/**
 * <strong> Internal class , don't use directly.</strong> FTP Server
 * Configuration implementation. It holds all the components used.
 * 
 * @author Mahabir Singh
 *
 */
@Slf4j
public class DBFtpServerContext implements FtpServerContext {

    private MessageResource messageResource = new MessageResourceFactory().createMessageResource();

    private UserManager userManager = new PropertiesUserManagerFactory().createUserManager();

    private FileSystemFactory fileSystemFactory = new DBFileSystemViewFactory();

    private FtpletContainer ftpletContainer = new DefaultFtpletContainer();

    private FtpStatistics ftpStatistics = new DefaultFtpStatistics();

    private CommandFactory commandFactory = new DBCommandFactory().createCommandFactory();

    private ConnectionConfig connectionConfig = new ConnectionConfigFactory().createConnectionConfig();

    private Map<String, Listener> listeners = new HashMap<>();

    private static final List<Authority> ADMIN = new ArrayList<>();
    private static final List<Authority> AMON = new ArrayList<>();

    /**
     * The ThreadPoolExecutor to be used by the server using this context
     */
    private ThreadPoolExecutor threadPoolExecutor = null;

    static {
	ADMIN.add(new WritePermission());

	AMON.add(new ConcurrentLoginPermission(20, 2));
	AMON.add(new TransferRatePermission(4800, 4800));
    }

    public DBFtpServerContext() {
	// create default listener
	listeners.put("default", new ListenerFactory().createListener());
    }

    @Override
    public UserManager getUserManager() {
	return userManager;
    }

    @Override
    public FileSystemFactory getFileSystemManager() {
	return fileSystemFactory;
    }

    @Override
    public FtpStatistics getFtpStatistics() {
	return ftpStatistics;
    }

    @Override
    public Ftplet getFtplet(String name) {
	return ftpletContainer.getFtplet(name);
    }

    @Override
    public ConnectionConfig getConnectionConfig() {
	return connectionConfig;
    }

    @Override
    public MessageResource getMessageResource() {
	return messageResource;
    }

    @Override
    public FtpletContainer getFtpletContainer() {
	return ftpletContainer;
    }

    @Override
    public Listener getListener(String name) {
	return listeners.get(name);
    }

    @Override
    public Map<String, Listener> getListeners() {
	return listeners;
    }

    @Override
    public CommandFactory getCommandFactory() {
	return commandFactory;
    }

    /**
     * Close all the components.
     */
    @Override
    public void dispose() {
	listeners.clear();
	ftpletContainer.getFtplets().clear();
	if (threadPoolExecutor != null) {
	    log.info("shutting down the thread pool executor");
	    threadPoolExecutor.shutdown();
	    try {
		threadPoolExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
	    } catch (InterruptedException e) {
		log.error("InterruptedException {}", e.getMessage());
		Thread.currentThread().interrupt();
	    } finally {

	    }
	}
    }

    @Override
    public synchronized ThreadPoolExecutor getThreadPoolExecutor() {
	if (threadPoolExecutor == null) {
	    int maxThread = connectionConfig.getMaxThreads();
	    if (maxThread < 1) {
		int maxlogins = connectionConfig.getMaxLogins();
		if (maxlogins > 0) {
		    maxThread = maxlogins;
		} else {
		    maxThread = 16;
		}
	    }
	    log.info("Initializing shared thread pool executor with max threads of {}", maxThread);
	    threadPoolExecutor = new OrderedThreadPoolExecutor(maxThread);
	}
	return threadPoolExecutor;
    }

    public void setMessageResource(MessageResource messageResource) {
	this.messageResource = messageResource;
    }

    public void setUserManager(UserManager userManager) {
	this.userManager = userManager;
    }

    public void setFileSystemFactory(FileSystemFactory fileSystemFactory) {
	this.fileSystemFactory = fileSystemFactory;
    }

    public void setFtpletContainer(FtpletContainer ftpletContainer) {
	this.ftpletContainer = ftpletContainer;
    }

    public void setFtpStatistics(FtpStatistics ftpStatistics) {
	this.ftpStatistics = ftpStatistics;
    }

    public void setCommandFactory(CommandFactory commandFactory) {
	this.commandFactory = commandFactory;
    }

    public void setConnectionConfig(ConnectionConfig connectionConfig) {
	this.connectionConfig = connectionConfig;
    }

    public void setListeners(Map<String, Listener> listeners) {
	this.listeners = listeners;
    }

    public Listener removeListener(String name) {
	return listeners.remove(name);
    }

    public void addListener(String name, Listener listener) {
	listeners.put(name, listener);
    }
}
