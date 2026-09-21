package br.app.visuwall.shared;

import br.app.visuwall.auth.exception.EmailAlreadyRegisteredException;
import br.app.visuwall.auth.exception.InvalidCredentialsException;
import br.app.visuwall.auth.exception.InvalidRefreshTokenException;
import br.app.visuwall.user.exception.UserNotFoundException;
import br.app.visuwall.wallpaper.exception.DuplicateWallpaperException;
import br.app.visuwall.wallpaper.exception.InvalidImageException;
import br.app.visuwall.wallpaper.exception.ServerBusyException;
import br.app.visuwall.wallpaper.exception.WallpaperNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorField(String field, String message) {}

    public record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            List<ErrorField> fieldErrors
    ) {}

    @ExceptionHandler(WallpaperNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWallpaperNotFound(WallpaperNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DuplicateWallpaperException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateWalpaper(DuplicateWallpaperException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImage(InvalidImageException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ServerBusyException.class)
    public ResponseEntity<ErrorResponse> handleServerBusy(ServerBusyException e) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException e) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<ErrorField> fields = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorField(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return build(HttpStatus.BAD_REQUEST, "Erro de validação", fields);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Erro não tratado", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno");
    }


    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return build(status, message, List.of());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, List<ErrorField> fields) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                message,
                fields
        );
        return ResponseEntity.status(status).body(body);
    }
}