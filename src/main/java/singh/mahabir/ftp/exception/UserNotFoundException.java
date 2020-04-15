/**
 * All rights reserved.
 */

package singh.mahabir.ftp.exception;

/**
 * @author Mahabir Singh
 *
 */
public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String msg) {
	super(msg);
    }

}
