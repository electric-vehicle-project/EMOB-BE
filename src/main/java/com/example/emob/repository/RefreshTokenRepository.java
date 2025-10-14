/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.RefreshToken;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

  List<RefreshToken> findAllByAccountId(String accountId);
}
