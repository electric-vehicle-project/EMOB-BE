package com.example.emob.service;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.Role;
import com.example.emob.entity.Account;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.AccountMapper;
import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;
import com.example.emob.repository.AccountRepository;
import com.example.emob.service.iml.IAuthentication;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements IAuthentication, UserDetailsService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    TokenService tokenService;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public APIResponse<AccountResponse> login(LoginRequest request) {
        Authentication authentication = null;
        try {
            // authenticated email and password are existed ?
             authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (request.getEmail(), request.getPassword()));
            // get Object from authenticatedauthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
            //                    (request.getEmail(), request.getPassword()));
            Account account = (Account) authentication.getPrincipal();
            // convert to AccountResponse
            AccountResponse accountResponse = accountMapper.toAccountResponse(account);
            // generate token
            accountResponse.setToken(tokenService.generateToken(account));
            return APIResponse.success(accountResponse,"Login Successful");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new GlobalException(ErrorCode.INVALID_CREDENTIALS);
        }
    }


    @Override
    public APIResponse<AccountResponse> register(RegisterRequest request) {
        // Map RegisterRequest => Account
        Account account = accountMapper.toAccount(request);

        try {
            // Mã hóa mật khẩu trước khi lưu
            account.setPassword(passwordEncoder.encode(request.getPassword()));

            // Lưu tài khoản vào DB
            Account newAccount = accountRepository.save(account);

            return APIResponse.success(accountMapper.toAccountResponse(newAccount),"Login Successful");

        } catch (Exception e) {
            // Kiểm tra lỗi từ database
            String errorMessage = e.getMessage().toLowerCase();
            System.out.println(errorMessage);
            if (errorMessage.contains("email")) {
                throw new GlobalException(ErrorCode.EMAIL_EXISTED);
            } else if (errorMessage.contains("phone")) {
                throw new GlobalException(ErrorCode.PHONE_EXISTED);
            } else {
                throw new GlobalException(ErrorCode.OTHER);

            }
        }
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findAccountByEmail(email);
    }

}