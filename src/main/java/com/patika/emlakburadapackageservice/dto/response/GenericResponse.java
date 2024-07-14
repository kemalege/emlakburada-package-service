package com.patika.emlakburadapackageservice.dto.response;

import com.patika.emlakburadapackageservice.constants.EmlakBuradaConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class GenericResponse<T> {

    private String message;
    private String status;
    private HttpStatus httpStatus;
    private T data;

    public static GenericResponse<ExceptionResponse> failed(String message) {
        return GenericResponse.<ExceptionResponse>builder()
                .status(EmlakBuradaConstants.FAILED)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(message)
                .build();
    }

    public static <T> GenericResponse<T> success(T data) {
        return GenericResponse.<T>builder()
                .status(EmlakBuradaConstants.SUCCESS)
                .httpStatus(HttpStatus.OK)
                .data(data)
                .build();
    }

}
