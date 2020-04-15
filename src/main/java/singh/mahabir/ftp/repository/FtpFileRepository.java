/**
 * All rights reserved.
 */

package singh.mahabir.ftp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import singh.mahabir.ftp.repository.entity.FtpFileEntity;
import singh.mahabir.ftp.resources.model.FileType;

/**
 * @author Mahabir Singh
 *
 */
public interface FtpFileRepository extends JpaRepository<FtpFileEntity, Long> {

    List<FtpFileEntity> findByUserName(String userName);

    Optional<FtpFileEntity> findByIdAndUserName(Long id, String userName);

    List<FtpFileEntity> findByUserNameAndFileType(String userName, FileType fileType);

    Optional<FtpFileEntity> findByUserNameAndFileTypeAndFileName(String userName, FileType fileType, String fileName);

}
