/**
 * All rights reserved.
 */

package singh.mahabir.ftp.exception;

/**
 * @author Mahabir Singh
 *
 */
public class UserNotActiveException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNotActiveException(String msg) {
	super(msg);
    }

}
