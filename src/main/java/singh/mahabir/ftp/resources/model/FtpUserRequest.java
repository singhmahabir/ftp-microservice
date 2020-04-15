/**
 * All rights reserved.
 */

package singh.mahabir.ftp.resources.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Mahabir Singh
 *
 */
@Setter
@Getter
@ToString
public class FtpUserRequest {

    private String ftpUsername;

    private String loggedInUser;

}
