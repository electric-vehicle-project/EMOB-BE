package com.example.emob.util;


import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.Dealer;
import com.example.emob.exception.GlobalException;
import com.example.emob.repository.DealerPointRuleRepository;
import com.example.emob.repository.DealerRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@AllArgsConstructor
public class DealerPointUtil {
    private static final int MIN_POINTS_PLATINUM = 500;
    private static final int MIN_POINTS_BRONZE = 200;
    private static final int MIN_POINTS_SILVER = 300;
    private static final int MIN_POINTS_GOLD = 400;

    @Autowired
    private static DealerRepository dealerRepository;

    @Autowired
    private static DealerPointRuleRepository dealerPointRepository;

    public static String determineMembership (int point) {
        if (point >= MIN_POINTS_PLATINUM) return "PLATINUM";
        else if (point >= MIN_POINTS_GOLD) return "GOLD";
        else if (point >= MIN_POINTS_SILVER) return "SILVER";
        else if (point >= MIN_POINTS_BRONZE) return "BRONZE";
        else return "NORMAL";
    }

    public static int nextLevel(MemberShipLevel current) {
        return switch (current) {
            case BRONZE -> MIN_POINTS_SILVER;
            case SILVER -> MIN_POINTS_GOLD;
            case GOLD -> MIN_POINTS_PLATINUM;
            default -> 0;
        };
    }

    public static void updateRank (UUID dealerId, int point) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));


    }

}
