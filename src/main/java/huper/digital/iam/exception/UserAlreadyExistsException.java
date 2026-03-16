package huper.digital.iam.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * Exception thrown when attempting to create a user that already exists.
 * Returns HTTP 409 Conflict status.
 */
public class UserAlreadyExistsException extends WebApplicationException {

    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message
     */
    public UserAlreadyExistsException(String message) {
        super(message, Response.Status.CONFLICT);
    }

    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause, Response.Status.CONFLICT);
    }
}

