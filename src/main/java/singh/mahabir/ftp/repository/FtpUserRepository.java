/**
 * All rights reserved.
 */

package singh.mahabir.ftp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import singh.mahabir.ftp.repository.entity.FtpUserEntity;

/**
 * @author Mahabir Singh
 *
 */
public interface FtpUserRepository extends JpaRepository<FtpUserEntity, String> {

    Optional<FtpUserEntity> findByFtpUserNameAndPassword(String ftpUserName, String password);

}
