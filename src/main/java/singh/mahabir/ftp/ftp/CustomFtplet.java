/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp;

import java.io.IOException;

import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;

/**
 * @author Mahabir Singh
 *
 */
public class CustomFtplet implements Ftplet {

    @Override
    public void init(FtpletContext ftpletContext) throws FtpException {
	// TODO Auto-generated method stub

    }

    @Override
    public void destroy() {
	// TODO Auto-generated method stub

    }

    @Override
    public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply)
	    throws FtpException, IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
	// TODO Auto-generated method stub
	return null;
    }

}
