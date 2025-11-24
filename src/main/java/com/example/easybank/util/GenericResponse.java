package com.example.easybank.util;

import com.example.easybank.domain.dto.response.UserResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse {

    private String message;
    private Object data;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int pageNumber;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int pageSize;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long totalElements;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int totalPages;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean first;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean last;

    @Builder.Default
    @JsonIgnore
    private HttpStatus status = HttpStatus.OK;

    public ResponseEntity<GenericResponse> buildResponse() {
        return ResponseEntity.status(status).body(this);
    }



    public static ResponseEntity<GenericResponse> success(String message) {
        return GenericResponse.builder()
                .message(message)
                .status(HttpStatus.OK)
                .build()
                .buildResponse();
    }

    public static ResponseEntity<GenericResponse> success(String message, Object data) {
        return GenericResponse.builder()
                .message(message)
                .data(data)
                .status(HttpStatus.OK)
                .build()
                .buildResponse();
    }

    public static ResponseEntity<GenericResponse> created(String message, Object data) {
        return GenericResponse.builder()
                .message(message)
                .data(data)
                .status(HttpStatus.CREATED)
                .build()
                .buildResponse();
    }

    public static ResponseEntity<GenericResponse> accepted(String message) {
        return GenericResponse.builder()
                .message(message)
                .status(HttpStatus.ACCEPTED)
                .build()
                .buildResponse();
    }

    public static ResponseEntity<GenericResponse> error(String message, HttpStatus status) {
        return GenericResponse.builder()
                .message(message)
                .status(status)
                .build()
                .buildResponse();
    }


    public static ResponseEntity<GenericResponse> error(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }
}

