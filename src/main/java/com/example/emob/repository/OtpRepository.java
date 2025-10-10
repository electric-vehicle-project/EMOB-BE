package com.example.emob.repository;

import com.example.emob.entity.Otp;
import com.example.emob.model.response.OtpResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OtpRepository extends CrudRepository<Otp, String> {
    List<Otp> findByAccountId (String accountId);

    OtpResponse findByEmail (String email);
}
