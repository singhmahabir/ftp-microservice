
package singh.mahabir.ftp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the class where we can handle any exception thrown by our application
 * and we can re-send the appropriate response to consumer.
 *
 * @author Mahabir Singh
 *
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerMapper {

    @ExceptionHandler(FtpFileNotFoundException.class)
    public ResponseEntity<ApiError> ftpFileNotFoundException(FtpFileNotFoundException ex) {
	log.error("Receive FtpFileNotFoundException : {}", ex);

	final ApiError error = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), ex.toString());
	return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> userNotFoundException(UserNotFoundException ex) {
	log.error("Receive UserNotFoundException : {}", ex);
	final ApiError error = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), ex.toString());
	return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ApiError> userNotActiveException(UserNotActiveException ex) {
	log.error("Receive UserNotActiveException : {}", ex);
	final ApiError error = new ApiError(HttpStatus.PRECONDITION_FAILED, ex.getMessage(), ex.toString());
	return new ResponseEntity<>(error, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<ApiError> userExistException(UserExistException ex) {
	log.error("Receive UserExistException : {}", ex);
	final ApiError error = new ApiError(HttpStatus.CONFLICT, ex.getMessage(), ex.toString());
	return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FileCryptoException.class)
    public ResponseEntity<ApiError> fileCryptoException(FileCryptoException ex) {
	log.error("Receive FileCryptoException : {}", ex);
	final ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.toString());
	return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> exceptionHandler(Exception ex) {
	log.error("Receive Exception : {}", ex);
	final ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.toString());
	return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
