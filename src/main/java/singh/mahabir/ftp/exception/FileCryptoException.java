/**
 * All rights reserved.
 */

package singh.mahabir.ftp.exception;

import java.security.GeneralSecurityException;

/**
 * @author Mahabir Singh
 *
 */
public class FileCryptoException extends RuntimeException {

    public FileCryptoException(String msg, GeneralSecurityException ex) {
	super(msg, ex);
    }

    private static final long serialVersionUID = 1L;

}
