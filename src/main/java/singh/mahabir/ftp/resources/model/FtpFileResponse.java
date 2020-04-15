/**
 * All rights reserved.
 */

package singh.mahabir.ftp.resources.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mahabir Singh
 *
 */
@Setter
@Getter
public class FtpFileResponse {

    List<FileInfo> files = new ArrayList<>();
}
