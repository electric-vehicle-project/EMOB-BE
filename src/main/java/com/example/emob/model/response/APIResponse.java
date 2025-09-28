package com.example.emob.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) //field nào null sẽ ko gửi lên
public class APIResponse<T> {
    int code = 200;
    String message;
    T result;
}
