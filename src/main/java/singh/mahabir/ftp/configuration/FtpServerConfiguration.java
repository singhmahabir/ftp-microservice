/**
 * All rights reserved.
 */

package singh.mahabir.ftp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * @author Mahabir Singh
 *
 */

@Component
@Getter
public class FtpServerConfiguration {

    @Value("${ftp.server.port}")
    private String port;

    @Value("${ftp.server.name}")
    private String serverName;

    @Value("${ftp.server.protocol}")
    private String protocol;
}
