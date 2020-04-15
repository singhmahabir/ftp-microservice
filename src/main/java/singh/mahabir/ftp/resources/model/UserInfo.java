/**
 * All rights reserved.
 */

package singh.mahabir.ftp.resources.model;

import java.time.LocalDateTime;

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
public class UserInfo {

    private String ftpUserName;

    private String password;

    private Boolean enable;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedDate;

    private String createdBy;

    private LocalDateTime creationDate;

    private String lastActivatedBy;

    private LocalDateTime lastPasswordDate;

}
