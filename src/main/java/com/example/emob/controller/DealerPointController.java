package com.example.emob.controller;

import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.model.request.DealerPointRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.service.DealerPointRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealer-point-rules")
@CrossOrigin("*")
@Tag(name = "Dealer Point Rules Controller", description = "Endpoints for managing dealer point rules")
@SecurityRequirement(name = "api")
public class DealerPointController {
    @Autowired
    private DealerPointRuleService dealerPointRuleService;

    @PostMapping
    @Operation(
            summary = "Create Dealer Point Rule",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Create new dealer point rule",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DealerPointRequest.class)
                    )
            )
    )
    public ResponseEntity<APIResponse<String>> saveRule (@RequestBody @Valid DealerPointRequest request) {
        return ResponseEntity.ok(dealerPointRuleService.saveRule(request.getMembershipLevel(), request.getDealerId(),
                                        request.getMinPoints(), request.getPrice()));
    }

    @GetMapping("/{level}")
    @Operation(
            summary = "Get Dealer Point Rule",
            description = "Get Dealer Point Rule By Status"
    )
    public ResponseEntity<APIResponse<DealerPointRule>> getRule (@PathVariable("level") MemberShipLevel level) {
        return ResponseEntity.ok(APIResponse.success(dealerPointRuleService.getRule(level)));
    }

    @GetMapping
    @Operation(
            summary = "Get All Dealer Point Rules",
            description = "Get All Dealer Point Rules By Status"
    )
    public ResponseEntity<APIResponse<List<DealerPointRule>>> getAllRules () {
        return ResponseEntity.ok(dealerPointRuleService.getAllRules());
    }
}
