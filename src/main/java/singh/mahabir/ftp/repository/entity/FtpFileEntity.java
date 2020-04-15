/**
 * All rights reserved.
 */

package singh.mahabir.ftp.repository.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import singh.mahabir.ftp.resources.model.FileStatus;
import singh.mahabir.ftp.resources.model.FileType;

/**
 * @author Mahabir Singh
 *
 */
@Table(name = "ftp_file_entity", schema = "ftp")
@Entity
@Getter
@Setter
public class FtpFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    @Enumerated
    private FileType fileType;

    @Column(name = "status")
    @Enumerated
    private FileStatus status;

    @Column(name = "file_data")
    private byte[] data;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_Modified_By")
    private String lastModifiedBy;

    @Column(name = "last_Modified_date")
    private Timestamp lastModifiedDate;
}
