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
public class FileInfo {

    private String fileId;

    private String fileName;

    private FileStatus status;

    private FileType fileType;

    private LocalDateTime creationDate;

    private String createdBy;

    private String ftpUserName;

    private Long fileSize;

}
