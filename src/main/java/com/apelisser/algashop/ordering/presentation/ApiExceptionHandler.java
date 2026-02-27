package com.apelisser.algashop.ordering.presentation;

import com.apelisser.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.apelisser.algashop.ordering.domain.model.DomainException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Nullable
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Invalid fields");
        problemDetail.setDetail("One or more field are invalid");
        problemDetail.setType(URI.create("/errors/invalid-field"));

        Map<String, String> fieldErrors = ex.getBindingResult().getAllErrors().stream().collect(
            Collectors.toMap(
                objectError -> ((FieldError) objectError).getField(),
                objectError -> messageSource.getMessage(objectError, LocaleContextHolder.getLocale())
            ));

        problemDetail.setProperty("fieldErrors", fieldErrors);

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(DomainEntityNotFoundException.class)
    public ProblemDetail handleDomainEntityNotFoundException(DomainEntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Domain entity not found");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/not-found"));
        return problemDetail;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setTitle("Unprocessable entity");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/unprocessable-entity"));
        return problemDetail;
    }

    @ExceptionHandler(CustomerEmailIsInUseException.class)
    public ProblemDetail handleCustomerEmailIsInUseException(DomainException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Conflict");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/conflict"));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected internal error occurred.");
        problemDetail.setType(URI.create("/errors/internal"));
        return problemDetail;
    }

}
