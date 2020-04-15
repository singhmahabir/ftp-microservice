/**
 * All rights reserved.
 */

package singh.mahabir.ftp.repository.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mahabir Singh
 *
 */
@Table(name = "ftp_user_entity", schema = "ftp")
@Entity
@Getter
@Setter
public class FtpUserEntity {

    @Id
    @Column(name = "ftp_user_name")
    private String ftpUserName;

    private String password;

    @Column(name = "last_Modified_By")
    private String lastModifiedBy;

    @Column(name = "last_Modified_date")
    private Timestamp lastModifiedDate;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_activated_by")
    private String lastActivatedBy;

    @Column(name = "last_password_changed_date")
    private Timestamp lastPasswordChangedDate;

    @Column(name = "active")
    private boolean isActive;

}
