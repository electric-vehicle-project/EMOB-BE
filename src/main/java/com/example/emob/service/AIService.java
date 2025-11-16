/* EMOB-2025 */
package com.example.emob.service;

import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {
  private final ChatClient chatClient;

  public AIService(ChatClient.Builder builder) {
    this.chatClient = builder.build();
  }

  public String getAIResponse(List<Map<String, Object>> requests) {
    String prompt =
            """
        You are an AI assistant for an electric vehicle manufacturer.
        Forecast dealer import demand for the next manufacturing cycle and produce a production plan.
        
        INPUT FORMAT:
        [
          {
            "modelName": "string",
            "data": [
              {
                "color": "string",
                "inventoryRemaining": number,   // manufacturer stock (by color)
                "demandHistory": {
                  "three_months_ago": number | "N/A",  // units sold to dealers
                  "two_months_ago":   number | "N/A",
                  "last_month":       number | "N/A"
                }
              }
            ]
          }
        ]
        
        NOTES:
        - demandHistory = wholesale sales to dealers; "N/A" means unavailable data (ignore, not zero).
        - inventoryRemaining = current manufacturer stock by color (used to offset NEW production).
        
        STEPS (per model):
        1) Color baseline = weighted avg of months:
             last_month (0.6), two_months_ago (0.3), three_months_ago (0.1).
           - Renormalize weights over available months.
           - If all values are "N/A", baseline = 0.
        
        2) Model baseline = sum of all color baselines.
        
        3) Growth rate:
           - Estimate from recent month-to-month change.
           - Clamp to [-10%%, +25%%].
           - If only one historical value is available → choose conservative growth in [10%%–25%%].
           - If downward trend → choose [-10%% to +10%%].
        
        4) Predicted dealer demand (gross):
           - preliminary = round(modelBaseline × (1 + growthRate)).
           - predictedDealerDemand = max(preliminary, 0).
        
        5) Net production after stock offset:
           - modelStock = sum(inventoryRemaining across all colors).
           - netToProduce = max(predictedDealerDemand - modelStock, 0).
           - recommendedProduction = ceil(netToProduce × 1.1).  // 10%% safety buffer
        
        6) Color-level forecast:
           - Use color shares from the **most recent non-N/A month**, in priority:
               last_month → two_months_ago → three_months_ago.
           - If no valid month exists, fallback to ratios of color baselines.
           - If still no usable data, split evenly.
           - Allocate integers such that:
               sum(predictedColorDemand[*]) == predictedDealerDemand.
        
        OUTPUT RULES:
        - Valid JSON only.
        - No country/region fields.
        - For each model:
          * predictedDealerDemand ≥ 0
          * recommendedProduction = ceil(max(predictedDealerDemand - modelStock, 0) × 1.1)
          * Color allocations must sum exactly to predictedDealerDemand.
        
        OUTPUT FORMAT:
        [
          {
            "modelName": "string",
            "predictedDealerDemand": number,
            "recommendedProduction": number,
            "colorForecast": [
              { "color": "string", "predictedColorDemand": number }
            ]
          }
        ]
        
        Input data:
        %s
        """
                    .formatted(requests);


    // 🚀 Gọi AI
    var response = chatClient.prompt(prompt).call().content();

    // ✅ Dọn chuỗi JSON, loại bỏ ```json ... ```
    String cleaned =
        response
            .replaceAll("(?s)```json\\s*", "") // xóa mở đầu ```json
            .replaceAll("(?s)```", "") // xóa kết thúc ```
            .trim();

    // ✅ Trả JSON gốc (sạch, dễ parse)
    return cleaned;
  }
}
