package br.app.visuwall.shared;

import br.app.visuwall.wallpaper.exception.DuplicateWallpaperException;
import br.app.visuwall.wallpaper.exception.InvalidImageException;
import br.app.visuwall.wallpaper.exception.ServerBusyException;
import br.app.visuwall.wallpaper.exception.WallpaperNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

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
    public ResponseEntity<ErrorResponse> handleNotFound(WallpaperNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DuplicateWallpaperException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateWallpaperException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImage(InvalidImageException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ServerBusyException.class)
    public ResponseEntity<ErrorResponse> handleBusy(ServerBusyException e) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<ErrorField> fields = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorField(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                fields
        );

        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                message,
                List.of()
        );

        return ResponseEntity.status(status).body(body);
    }
}