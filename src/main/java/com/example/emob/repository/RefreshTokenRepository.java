/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.RefreshToken;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    List<RefreshToken> findAllByAccountId(String accountId);
}
