/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import com.example.emob.service.SaleOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sale-order")
@SecurityRequirement(name = "api")
public class SaleOrderControler {
  @Autowired SaleOrderService saleOrderService;

  @GetMapping("/of-dealer")
  public ResponseEntity<APIResponse<PageResponse<SaleOrderResponse>>> getAllSaleOrdersOfDealer(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(saleOrderService.getAllSaleOrdersOfDealer(pageable));
  }

  @GetMapping("/of-customer")
  public ResponseEntity<APIResponse<PageResponse<SaleOrderResponse>>> getAllSaleOrdersOfCustomer(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(saleOrderService.getAllSaleOrdersByCustomer(pageable));
  }

  // 2️⃣ Lấy chi tiết Sale Order theo ID
  @GetMapping("/{id}")
  public ResponseEntity<APIResponse<SaleOrderResponse>> getSaleOrderById(
      @PathVariable("id") UUID saleOrderId) {
    return ResponseEntity.ok(saleOrderService.getSaleOrderById(saleOrderId));
  }

  // 3️⃣ Hủy Sale Order theo ID (set status = CANCELED)
  @DeleteMapping("/{id}")
  public ResponseEntity<APIResponse<SaleOrderResponse>> deleteSaleOrderById(
      @PathVariable("id") UUID saleOrderId) {
    return ResponseEntity.ok(saleOrderService.deleteSaleOrderById(saleOrderId));
  }

  @PostMapping("/completed")
  public ResponseEntity<APIResponse<SaleOrderResponse>> completeSaleOrderById(
      InstallmentRequest request) {
    return ResponseEntity.ok(saleOrderService.completeSaleOrderById(request));
  }
}
