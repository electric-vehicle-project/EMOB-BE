package com.example.emob.service;

import com.example.emob.constant.Region;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.MonthlyDealerRevenueResponse;
import com.example.emob.repository.DealerRepository;
import com.example.emob.util.AccountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RevenueService {
     @Autowired
    private DealerRepository dealerRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<List<MonthlyDealerRevenueResponse>> getDealerRevenueReport(Integer year,
                                                                                  Region region, String country) {

        List<MonthlyDealerRevenueResponse> list =
                dealerRepository.getDealerRevenue12MonthsFiltered(year, region, country);
        return APIResponse.success(list, "Get dealer revenue report successfully");
    }


    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<List<MonthlyDealerRevenueResponse>> getDealerRevenueReportOfDealer(Integer year) {

        List<MonthlyDealerRevenueResponse> list =
                dealerRepository.getDealerRevenue12MonthsFilteredOfDealer(year, AccountUtil.getCurrentUser().getDealer().getId());
        return APIResponse.success(list, "Get dealer revenue report successfully");
    }
}
