/**
 * All rights reserved.
 */

package singh.mahabir.ftp.exception;

/**
 * @author Mahabir Singh
 *
 */
public class FtpFileNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FtpFileNotFoundException(String msg) {
	super(msg);
    }

}
