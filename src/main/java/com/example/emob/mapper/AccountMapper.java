package com.example.emob.mapper;

import com.example.emob.entity.Account;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.response.AccountResponse;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Bean;


@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponse toAccountResponse (Account account);
    Account toAccount (RegisterRequest request);
}
