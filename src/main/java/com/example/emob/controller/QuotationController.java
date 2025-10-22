/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.PaymentStatus;
import com.example.emob.model.request.SaleOrderItemRequest;
import com.example.emob.model.request.quotation.QuotationItemRequest;
import com.example.emob.model.request.quotation.QuotationItemUpdateRequest;
import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.quotation.QuotationResponse;
import com.example.emob.service.QuotationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotation")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class QuotationController {

  @Autowired private QuotationService quotationService;

  @PostMapping
  public ResponseEntity<APIResponse<QuotationResponse>> create(
      @RequestBody QuotationRequest<QuotationItemRequest> quotationRequest) {
    return ResponseEntity.ok(quotationService.create(quotationRequest));
  }

  @PutMapping("{id}")
  public ResponseEntity<APIResponse<QuotationResponse>> update(@RequestParam UUID id,
          @RequestBody QuotationRequest<QuotationItemUpdateRequest> quotationRequest) {
    return ResponseEntity.ok(quotationService.update(id,quotationRequest));
  }
  @PutMapping("{id}/approved")
  public ResponseEntity<APIResponse<QuotationResponse>> approved(@RequestParam UUID id,
                                                                @RequestBody List<SaleOrderItemRequest> itemRequests, PaymentStatus paymentStatus) {
    return ResponseEntity.ok(quotationService.approveQuotation(id,itemRequests,paymentStatus));
  }



  @GetMapping("{id}")
  public ResponseEntity<APIResponse<QuotationResponse>> get(@RequestParam UUID id) {
    return ResponseEntity.ok(quotationService.get(id));
  }



  @DeleteMapping("{id}")
  public ResponseEntity<APIResponse<QuotationResponse>> delete(@RequestParam UUID id) {
    return ResponseEntity.ok(quotationService.delete(id));
  }

  @DeleteMapping("{id}/item")
  public ResponseEntity<APIResponse<Void>> deleteItem(@RequestParam UUID id) {
    quotationService.deleteItem(id);
    return ResponseEntity.ok(APIResponse.success(null, "Deleted successfully"));
  }

  @GetMapping
  public ResponseEntity<APIResponse<PageResponse<QuotationResponse>>> getAllQuotations(
          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(quotationService.getAll(pageable));
  }
}
