package com.example.emob.controller;

import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import com.example.emob.service.SaleOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sale-order")
@SecurityRequirement(name = "api")
public class SaleOrderControler {
    @Autowired
    SaleOrderService saleOrderService;

    @GetMapping
    public APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return saleOrderService.getAllSaleOrders(pageable);
    }
    // 2️⃣ Lấy chi tiết Sale Order theo ID
    @GetMapping("/{id}")
    public APIResponse<SaleOrderResponse> getSaleOrderById(@PathVariable("id") UUID saleOrderId) {
        return saleOrderService.getSaleOrderById(saleOrderId);
    }

    // 3️⃣ Hủy Sale Order theo ID (set status = CANCELED)
    @DeleteMapping("/{id}")
    public APIResponse<SaleOrderResponse> deleteSaleOrderById(@PathVariable("id") UUID saleOrderId) {
        return saleOrderService.deleteSaleOrderById(saleOrderId);
    }
}
