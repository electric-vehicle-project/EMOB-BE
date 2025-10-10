/* EMOB-2025 */
package com.example.emob.model.request;

import com.example.emob.constant.Gender;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest {
  String fullName;
  String email;
  String phoneNumber;
  String address;
  String note;
  LocalDate dateOfBirth;
  Gender gender;
  int loyaltyPoints;
}
