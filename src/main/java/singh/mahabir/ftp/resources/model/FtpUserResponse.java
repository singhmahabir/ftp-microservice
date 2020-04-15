/**
 * All rights reserved.
 */

package singh.mahabir.ftp.resources.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mahabir Singh
 *
 */
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class FtpUserResponse {

    private UserInfo userInfo;

    private ServerInfo serverInfo;

    private DeactivatedUserInfo deactivatedUserInfo;

}
