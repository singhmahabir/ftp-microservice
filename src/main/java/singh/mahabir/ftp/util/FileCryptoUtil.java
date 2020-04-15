/**
 * All rights reserved.
 */

package singh.mahabir.ftp.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import singh.mahabir.ftp.exception.FileCryptoException;

/**
 * @author Mahabir Singh
 *
 */
public final class FileCryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    private FileCryptoUtil() {
	super();
    }

    public static byte[] encryptBytes(String key, byte[] inputBytes) {
	try {
	    Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
	    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
	    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	    return cipher.doFinal(inputBytes);
	} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
		| IllegalBlockSizeException ex) {
	    throw new FileCryptoException("Exceptoin doing decryption the file ", ex);
	}
    }

    public static byte[] decryptBytes(String key, byte[] inputBytes) {
	try {
	    Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
	    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
	    cipher.init(Cipher.DECRYPT_MODE, secretKey);
	    return cipher.doFinal(inputBytes);
	} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
		| IllegalBlockSizeException ex) {
	    throw new FileCryptoException("Exceptoin doing decryption the file ", ex);
	}
    }
}
