/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Otp;
import com.example.emob.model.response.OtpResponse;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends CrudRepository<Otp, String> {
  List<Otp> findByAccountId(String accountId);

  OtpResponse findByEmail(String email);
}
