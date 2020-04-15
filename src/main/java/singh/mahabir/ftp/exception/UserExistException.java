/**
 * All rights reserved.
 */

package singh.mahabir.ftp.exception;

/**
 * @author Mahabir Singh
 *
 */
public class UserExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserExistException(String msg) {
	super(msg);
    }
}
