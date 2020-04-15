/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.authentication;

import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UserManagerFactory;

/**
 * @author Mahabir Singh
 *
 */
public class SecurityUserManagerFactory implements UserManagerFactory {

    private String adminName = "admin";

    public UserManager createUserManager() {
	return new SecurityUserManager();
    }

    public String getAdminName() {
	return adminName;
    }

    public void setAdminName(String adminName) {
	this.adminName = adminName;
    }
}
