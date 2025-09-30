package com.example.emob.repository;

import com.example.emob.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, UUID> {

}
