/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Account;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.response.AccountResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
  AccountResponse toAccountResponse(Account account);

  Account toAccount(RegisterRequest request);
}
