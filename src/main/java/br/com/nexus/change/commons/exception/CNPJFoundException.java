package br.com.nexus.change.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "CNPJ já cadastrado.")
public class CNPJFoundException extends RuntimeException {


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
    public CNPJFoundException(final String message) {
        super(message);
    }
}
