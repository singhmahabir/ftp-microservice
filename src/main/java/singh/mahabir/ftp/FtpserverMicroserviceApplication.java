package singh.mahabir.ftp;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahabir Singh
 *
 */
@SpringBootApplication
@Slf4j
public class FtpserverMicroserviceApplication extends SpringBootServletInitializer /* implements ApplicationRunner */ {

    public static void main(String[] args) {
	SpringApplication.run(FtpserverMicroserviceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	return builder.sources(FtpserverMicroserviceApplication.class);
    }

//    @Override
    public void run(ApplicationArguments args) throws Exception {
	log.info("Starting the FTP Server with command line argument {}", args);
//	FTPServerBuilder.initDBStorageFtpServer();
    }

}
