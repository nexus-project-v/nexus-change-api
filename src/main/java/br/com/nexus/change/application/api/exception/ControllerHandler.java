package br.com.nexus.change.application.api.exception;

import br.com.nexus.change.commons.exception.ResourceFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.util.Set;

@ControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ControllerHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ProblemDetail> handleCustomerAlreadyExistsException(final Exception ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

   /* @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Unauthorized", HttpStatus.UNAUTHORIZED.value(), "Credenciais inválidas! Verifique seu email e senha.");
        return new ResponseEntity<>(problemDetails, HttpStatus.UNAUTHORIZED);
    }*/

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ProblemDetail> handleNullPointException(final NullPointerException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ParseException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ProblemDetail> handleCustomerParseException(final ParseException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

/*    @ExceptionHandler(UserPolicyFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleEmailFoundException(final UserPolicyFoundException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Bad Request", HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailFoundException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ResponseEntity<ProblemDetail> handleEmailFoundException(final EmailFoundException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Bad Request", HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.CONFLICT);
    }*/

    /*@ExceptionHandler(PasswordMatchException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ProblemDetail> handlePasswordMatchException(final PasswordMatchException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Not Acceptable", HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(RegisterUserException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleResourceFoundException(final RegisterUserException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Bad Request", HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.BAD_REQUEST);
    }*/

    @ExceptionHandler(ResourceFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleResourceFoundException(final ResourceFoundException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Bad Request", HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(final Exception ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Forbidden", HttpStatus.FORBIDDEN.value(), "Sem permissão para acessar o recurso: " + ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.FORBIDDEN);
    }

/*    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(final Exception ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Forbidden", HttpStatus.FORBIDDEN.value(), "Falha na autenticação! \n Digite o email e senha corretamente! ||" + ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.FORBIDDEN);
    }*/

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(final ResourceNotFoundException ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Bad Request", HttpStatus.BAD_REQUEST.value(), "Entidade não encontrada! Cadastre a entidade! ||" + ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(final Exception ex) {
        ProblemDetail problemDetails = createProblemDetails("about:blank", "Forbidden", HttpStatus.FORBIDDEN.value(), "Entidade não encontrada! Cadastre a entidade! ||" + ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {

        // Mensagem padrão
        String message = "Violação de restrição no banco de dados. Verifique se os dados enviados estão corretos!";

        // Verifica se é a constraint específica
        String details = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        if (details != null && details.contains("tb_product_assessment_chk_1")) {
            message = "Avaliação inválida: algum campo não está conforme a regra de negócio.";
        }

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ProblemDetail problemDetails = createProblemDetails(
                "about:blank",
                "Conflict",
                HttpStatus.CONFLICT.value(),
                message
        );
        problemDetails.setInstance(URI.create(path));
        problemDetails.setDetail(details);

        return new ResponseEntity<>(problemDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> constraintViolationException(ConstraintViolationException ex) {
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            message.append(violation.getMessage().concat(";"));
        }

        ProblemDetail problemDetail = createProblemDetails("about:blank", "Not Found", HttpStatus.NOT_FOUND.value(), message.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> constraintViolationException(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
        // Collecting all constraint violations from the MethodArgumentNotValidException
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            message.append(fieldError.getDefaultMessage()).append(";");
        });

        ProblemDetail problemDetail = createProblemDetails("about:blank", "Not Found", HttpStatus.NOT_FOUND.value(), message.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    private ProblemDetail createProblemDetails(String type, String title, int status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status), detail);
        problemDetail.setType(URI.create(type));
        problemDetail.setTitle(title);
        return problemDetail;
    }
}

