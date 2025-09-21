package com.example.emob.mapper;

import com.example.emob.entity.Account;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.response.AccountResponse;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Bean;


@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Bean
    AccountResponse toAccountResponse (Account account);
    @Bean
    Account toAccount (RegisterRequest request);
}
