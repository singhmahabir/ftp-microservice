/**
 * All rights reserved.
 */

package singh.mahabir.ftp.util;

/**
 * @author Mahabir Singh
 *
 */
public interface IPasswordGenerator {

    char[] generateTemporaryPassword();

    String generateHashedPassword(String rawPassword);

    boolean isMatches(String rawPassword, String storedPassword);
}
