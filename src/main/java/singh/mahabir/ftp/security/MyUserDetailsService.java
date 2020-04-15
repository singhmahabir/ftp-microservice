/**
 * All rights reserved.
 */

package singh.mahabir.ftp.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import singh.mahabir.ftp.repository.UserRepository;
import singh.mahabir.ftp.repository.entity.User;

/**
 * @author Mahabir Singh
 *
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
	Optional<User> user = userRepository.findByUserName(username);

	return user.map(MyUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
    }

}
