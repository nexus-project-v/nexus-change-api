package br.com.nexus.change.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * The type Resource found exception.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "O recurso já existe")
public class ClientCodeFoundException extends RuntimeException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new Resource found exception.
     *
     * @param message the message
     */
    public ClientCodeFoundException(final String message) {
        super(message);
    }

}