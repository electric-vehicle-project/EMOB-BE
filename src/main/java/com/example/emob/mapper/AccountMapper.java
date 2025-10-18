/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Account;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
  @Mapping(
      target = "dealerId",
      expression = "java(account.getDealer() != null ? account.getDealer().getId() : null)")
  AccountResponse toAccountResponse(Account account);

  Account toAccount(RegisterRequest request);
}
