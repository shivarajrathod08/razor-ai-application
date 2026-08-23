package com.hackathon.repository;

import com.hackathon.model.Payment;
import com.hackathon.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    List<Payment> findAllByOrderByCreatedAtDesc();
    long countByStatus(PaymentStatus status);
}