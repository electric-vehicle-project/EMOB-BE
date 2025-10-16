package com.example.emob.entity;

import com.example.emob.constant.OrderStatus;
import com.example.emob.constant.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "sale_order")
public class SaleOrder {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", unique = true)
    UUID id;

    LocalDateTime saleDate;
    Float salePrice;

    LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    LocalDateTime scheduleAt; // lịch hẹn khaách có thể đến ký hợp đồng

    @OneToOne(mappedBy = "saleOrder", cascade = CascadeType.ALL)
    @JsonIgnore
    SaleContract contract;

    @ManyToOne
    @JoinColumn(name = "customer", referencedColumnName = "id")
    Customer customer;

    @ManyToOne
    @JoinColumn(name = "dealer", referencedColumnName = "id")
    Dealer dealer;

//    @ManyToOne
//    @JoinColumn(name = "account", referencedColumnName = "id")
//    Account account;
//
}
