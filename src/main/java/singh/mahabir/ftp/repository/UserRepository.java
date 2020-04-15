/**
 * All rights reserved.
 */

package singh.mahabir.ftp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import singh.mahabir.ftp.repository.entity.User;

/**
 * @author Mahabir Singh
 *
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
}