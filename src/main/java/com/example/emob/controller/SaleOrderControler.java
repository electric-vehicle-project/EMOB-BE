/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.OrderStatus;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import com.example.emob.model.response.SalesByStaffResponse;
import com.example.emob.service.SaleOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sale-order")
@SecurityRequirement(name = "api")
public class SaleOrderControler {
  @Autowired SaleOrderService saleOrderService;

  private Pageable buildPageable(int page, int size, String[] sort) {
    Sort sortObj =
        Sort.by(
            Arrays.stream(sort)
                .map(
                    s -> {
                      String[] _s = s.split(",");
                      return new Sort.Order(
                          _s.length > 1 && _s[1].equalsIgnoreCase("desc")
                              ? Sort.Direction.DESC
                              : Sort.Direction.ASC,
                          _s[0]);
                    })
                .toList());
    return PageRequest.of(page, size, sortObj);
  }

  @GetMapping("/current-dealer")
  public ResponseEntity<APIResponse<PageResponse<SaleOrderResponse>>>
      getAllSaleOrdersOfCurrentDealer(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) List<OrderStatus> statuses,
          @RequestParam(defaultValue = "createdAt") String sortField,
          @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    APIResponse<PageResponse<SaleOrderResponse>> response =
        saleOrderService.getAllSaleOrdersOfCurrentDealer(statuses, keyword, pageable);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/dealer/customers")
  public ResponseEntity<APIResponse<PageResponse<SaleOrderResponse>>>
      getAllQuotedSaleOrdersOfCurrentDealer(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) List<OrderStatus> statuses,
          @RequestParam(defaultValue = "createdAt") String sortField,
          @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    APIResponse<PageResponse<SaleOrderResponse>> response =
        saleOrderService.getAllQuotedSaleOrdersOfCurrentDealer(statuses, keyword, pageable);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/staff/current")
  @Operation(summary = "Xem tất cả đơn hàng của nhân viên hiện tại")
  public ResponseEntity<APIResponse<PageResponse<SaleOrderResponse>>> getAllSaleOrdersOfStaff(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<OrderStatus> statuses,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    APIResponse<PageResponse<SaleOrderResponse>> response =
        saleOrderService.getAllSaleOrdersOfStaff(statuses, keyword, pageable);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/sale-of-staff")
  public ResponseEntity<APIResponse<PageResponse<SalesByStaffResponse>>> getAllSaleOrdersByEmployee(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    APIResponse<PageResponse<SalesByStaffResponse>> response =
        saleOrderService.getAllSaleOrdersByemployee(pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/dealers")
  public ResponseEntity<APIResponse<PageResponse<SaleOrderResponse>>> getAllSaleOrders(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<OrderStatus> statuses,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();

    Pageable pageable = PageRequest.of(page, size, sort);

    APIResponse<PageResponse<SaleOrderResponse>> response =
        saleOrderService.getAllSaleOrdersOfDealer(statuses, keyword, pageable);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/customers/{customerId}")
  public ResponseEntity<APIResponse<PageResponse<SaleOrderResponse>>>
      getAllSaleOrdersOfCurrentCustomer(
          @PathVariable UUID customerId,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) List<OrderStatus> statuses,
          @RequestParam(defaultValue = "createdAt") String sortField,
          @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    APIResponse<PageResponse<SaleOrderResponse>> response =
        saleOrderService.getAllSaleOrdersOfCurrentCustomer(customerId, statuses, keyword, pageable);

    return ResponseEntity.ok(response);
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

  @PostMapping("{id}/completed")
  public ResponseEntity<APIResponse<SaleOrderResponse>> completeSaleOrderById(
      @PathVariable("id") UUID id) {
    return ResponseEntity.ok(saleOrderService.completeSaleOrderById(id));
  }
}
