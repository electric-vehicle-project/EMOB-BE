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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.AuthenticationException;

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
    public APIResponse<AccountResponse> login(LoginRequest request) throws AuthenticationException {
        Authentication authentication = null;
        try {
            System.out.println("chưa vào");
            // authenticated email and password are existed ?
             authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (request.getEmail(), request.getPassword()));
            System.out.println("vào ròi");
            // get Object from authenticatedauthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
            //                    (request.getEmail(), request.getPassword()));
            Account account = (Account) authentication.getPrincipal();
            // convert to AccountResponse
            AccountResponse accountResponse = accountMapper.toAccountResponse(account);
            // generate token
            accountResponse.setToken(tokenService.generateToken(account));
            APIResponse<AccountResponse> apiResponse = new APIResponse<>();
            apiResponse.setMessage("Login Successful");
            apiResponse.setResult(accountResponse);
            return apiResponse;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new GlobalException(ErrorCode.INVALID_CREDENTIALS, "Email");
        }
    }


   

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findAccountByEmail(email);
    }

}
