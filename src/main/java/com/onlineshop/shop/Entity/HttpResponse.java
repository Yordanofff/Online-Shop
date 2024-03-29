package com.onlineshop.shop.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static java.time.LocalTime.now;

@Data
@Builder
@JsonInclude(NON_DEFAULT)
public class HttpResponse {
    protected String timeStamp;
    protected int statusCode;
    protected HttpStatus status;
    protected String reason;
    protected String message;
    protected String developerMessage;
    protected Object data;

    public static ResponseEntity<HttpResponse> ok(Object data) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Successful operation!")
                .data(data)
                .build();
        return ResponseEntity.ok().body(response);
    }

    public static ResponseEntity<HttpResponse> conflict(Object data){
        HttpResponse response = HttpResponse.builder()
                .timeStamp(now().toString())
                .statusCode(HttpStatus.CONFLICT.value())
                .status(HttpStatus.CONFLICT)
                .message("Operation not permitted. Data conflict.")
                .data(Map.of("error", data))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    public static ResponseEntity<HttpResponse> badRequest(String message, Object data) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(now().toString())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .data(data != null ? Map.of("errors", data) : null)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    public static ResponseEntity<HttpResponse> validationError(BindingResult bindingResult) {
        List<String> errors = bindingResult.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        HttpResponse response = HttpResponse.builder()
                .timeStamp(now().toString())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Validation error")
                .data(Map.of("errors", errors))
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    public static ResponseEntity<HttpResponse> internalServerError(String developerMessage, Object data) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(now().toString())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Something went wrong on the server side.")
                .developerMessage(developerMessage)
                .data(data != null ? Map.of("error", data) : null)
                .build();
        return ResponseEntity.internalServerError().body(response);
    }

    public static ResponseEntity<HttpResponse> createdWithUri(URI uri, Object data) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(now().toString())
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Successfully created!")
                .data(data)
                .build();
        return ResponseEntity.created(uri).body(response);
    }

    public static ResponseEntity<HttpResponse> created(Object data) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(now().toString())
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Successfully created!")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
