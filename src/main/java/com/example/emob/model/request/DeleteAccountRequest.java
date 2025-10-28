package com.example.emob.model.request;

import com.example.emob.constant.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteAccountRequest {
    AccountStatus status;
}
