/* EMOB-2025 */
package com.example.emob.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // field nào null sẽ ko gửi lên
public class APIResponse<T> {
  int code;
  String message;
  T result;

  public static <T> APIResponse<T> success(T result) {
    return APIResponse.<T>builder().code(200).message("Success").result(result).build();
  }

  public static <T> APIResponse<T> success(T result, String message) {
    return APIResponse.<T>builder().code(200).message(message).result(result).build();
  }

  public static <T> APIResponse<T> error(int code, String message) {
    return APIResponse.<T>builder().code(code).message(message).build();
  }
}
