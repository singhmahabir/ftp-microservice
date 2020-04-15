/**
 * All rights reserved.
 */

package singh.mahabir.ftp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Mahabir Singh
 *
 */
@Configuration
public class BCryptPasswordEncoderConfiguration {

    @Value("${ftp.password.strength:31}")
    private Integer strength;

    @Bean
    public PasswordEncoder getBCryptPasswordEncoder() {
	return new BCryptPasswordEncoder(BCryptVersion.$2Y, strength);
    }
}
