package com.example.emob.controller;

import com.example.emob.model.request.DealerRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ContractResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/contract")
@CrossOrigin("*")
@Tag(name = "Contract Controller", description = "Endpoints for contract management")
@SecurityRequirement(name = "api")
public class ContractController {
    @Autowired
    ContractService contractService;


    @GetMapping("/view-contract")
    @Operation(
            summary = "View contract by ID",
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "View contract"))
    public ResponseEntity<APIResponse<ContractResponse>> viewContract (@RequestParam UUID id) {
        return ResponseEntity.ok(contractService.viewContract(id));
    }

    @GetMapping("/view-all-contracts")
    @Operation(
            summary = "View all contracts by ID",
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "View all contracts"))
    public ResponseEntity<APIResponse<PageResponse<ContractResponse>>> viewAllContracts (@RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contractService.viewAllContracts(pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete contract by ID",
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cancel contract by status"))
    public ResponseEntity<APIResponse<Void>> cancelContract (@RequestParam @PathVariable("id") UUID id) {
        return  ResponseEntity.ok(contractService.cancelContract(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update contract by ID",
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update status contract after signed"))
    public ResponseEntity<APIResponse<ContractResponse>> updateContract (@RequestParam @PathVariable("id") UUID id) {
        return ResponseEntity.ok(contractService.updateContractStatus(id));
    }

    @PostMapping("/sign-contract/{id}")
    @Operation(
            summary = "Sign contract by ID",
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer sign contract"))
    public ResponseEntity<APIResponse<ContractResponse>> signContract (@RequestParam @PathVariable("id") UUID id) {
        return ResponseEntity.ok(contractService.signContract(id));
    }

    @PostMapping()
    public ResponseEntity<APIResponse<ContractResponse>> create (@RequestParam UUID id) {
        return ResponseEntity.ok(contractService.createContract(id));
    }
}
