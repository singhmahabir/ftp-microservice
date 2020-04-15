
package singh.mahabir.ftp.exception;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is the default response of the application when application thrown
 * any exceptoin
 *
 * @author Mahabir Singh
 *
 */
// @XmlRootElement
@Setter
@Getter
@NoArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiError(HttpStatus status, String message, List<String> errors) {
	super();
	this.status = status;
	this.message = message;
	this.errors = errors;
    }

    public ApiError(HttpStatus status, String message, String error) {
	super();
	this.status = status;
	this.message = message;
	errors = Arrays.asList(error);
    }
}
