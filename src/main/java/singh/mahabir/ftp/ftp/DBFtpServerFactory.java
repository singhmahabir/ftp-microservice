/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp;

import java.util.Map;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpStatistics;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.ftpletcontainer.FtpletContainer;
import org.apache.ftpserver.ftpletcontainer.impl.DefaultFtpletContainer;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.message.MessageResource;

/**
 * @author Mahabir Singh
 *
 */
public class DBFtpServerFactory {

    private DBFtpServerContext serverContext;

    public DBFtpServerFactory() {
	serverContext = new DBFtpServerContext();
    }

    public FtpServer createServer() {
	return new DefaultFtpServer(serverContext);
    }

    public void setUserManager(UserManager userManager) {
	serverContext.setUserManager(userManager);
    }

    public UserManager getUserManager() {
	return serverContext.getUserManager();
    }

    public void setFtplets(Map<String, Ftplet> ftplets) {
	serverContext.setFtpletContainer(new DefaultFtpletContainer(ftplets));
    }

    public Map<String, Ftplet> getFtplets() {
	return serverContext.getFtpletContainer().getFtplets();
    }

    public FileSystemFactory getFileSystemManager() {
	return serverContext.getFileSystemManager();
    }

    public FtpStatistics getFtpStatistics() {
	return serverContext.getFtpStatistics();
    }

    public Ftplet getFtplet(String name) {
	return serverContext.getFtplet(name);
    }

    public ConnectionConfig getConnectionConfig() {
	return serverContext.getConnectionConfig();
    }

    public MessageResource getMessageResource() {
	return serverContext.getMessageResource();
    }

    public FtpletContainer getFtpletContainer() {
	return serverContext.getFtpletContainer();
    }

    public Listener getListener(String name) {
	return serverContext.getListener(name);
    }

    public Map<String, Listener> getListeners() {
	return serverContext.getListeners();
    }

    public CommandFactory getCommandFactory() {
	return serverContext.getCommandFactory();
    }

    public void setMessageResource(MessageResource messageResource) {
	serverContext.setMessageResource(messageResource);
    }

    public void setFileSystemFactory(FileSystemFactory fileSystemFactory) {
	serverContext.setFileSystemFactory(fileSystemFactory);
    }

    public void setFtpletContainer(FtpletContainer ftpletContainer) {
	serverContext.setFtpletContainer(ftpletContainer);
    }

    public void setFtpStatistics(FtpStatistics ftpStatistics) {
	serverContext.setFtpStatistics(ftpStatistics);
    }

    public void setCommandFactory(CommandFactory commandFactory) {
	serverContext.setCommandFactory(commandFactory);
    }

    public void setConnectionConfig(ConnectionConfig connectionConfig) {
	serverContext.setConnectionConfig(connectionConfig);
    }

    public void setListeners(Map<String, Listener> listeners) {
	serverContext.setListeners(listeners);
    }

    public Listener removeListener(String name) {
	return serverContext.removeListener(name);
    }

    public void addListeners(Map<String, Listener> listeners) {
	serverContext.setListeners(listeners);
    }

    public void addListener(String name, Listener listener) {
	serverContext.addListener(name, listener);
    }

}
