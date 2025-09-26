package com.example.emob.repository;

import com.example.emob.entity.CustomerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {
    CustomerFeedback findCustomerFeedbackByEmail(String email);
}
