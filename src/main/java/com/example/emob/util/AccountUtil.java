package com.example.emob.util;

import com.example.emob.entity.Account;
import com.example.emob.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountUtil implements ApplicationContextAware {

    @Autowired
    private static AccountRepository accountRepository;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        accountRepository = applicationContext.getBean(AccountRepository.class);
    }

    public static Account getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepository.findAccountByEmail(email);
    }
}
