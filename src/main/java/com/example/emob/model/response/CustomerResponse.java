/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.Gender;
import com.example.emob.constant.MemberShipLevel;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponse {
  UUID id;
  String fullName;
  String email;
  String phoneNumber;
  String address;
  String note;
  LocalDate dateOfBirth;
  Gender gender;
  int loyaltyPoints;
  MemberShipLevel memberShipLevel;
}
