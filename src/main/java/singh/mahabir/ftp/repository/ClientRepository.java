/**
 * All rights reserved.
 */

package singh.mahabir.ftp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import singh.mahabir.ftp.repository.entity.Client;

/**
 * @author Mahabir Singh
 *
 */
public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByClientId(String clientId);

}
