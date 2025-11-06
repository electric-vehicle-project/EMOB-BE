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
Forecast dealer import demand for the next manufacturing cycle and produce a production & regional distribution plan.

INPUT FORMAT:
[
  {
    "country": "string",
    "region": "string",
    "vehicles": [
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
  }
]

NOTES:
- demandHistory = wholesale sales to dealers; "N/A" = missing (ignore, not zero).
- inventoryRemaining = current manufacturer stock by color (used to offset NEW production).

STEPS (per {country, region, model}):
1) Color baseline = weighted avg of last_month (0.6), two_months_ago (0.3), three_months_ago (0.1); renormalize weights over available months; 0 if all "N/A".
2) Model baseline = sum of color baselines.
3) Growth rate:
   - From recent change between the last available months; clamp to [-10%%, +25%%].
   - If only one month → pick 10–25%%; if decline → pick in [-10%%, +10%%].
4) Predicted demand (gross):
   - preliminary = round(modelBaseline × (1 + growthRate)).
   - Ensure integer ≥ 0 → predictedDealerDemand = max(preliminary, 0).
5) Manufacturer stock offset (net production):
   - modelStock = sum(inventoryRemaining across colors).
   - netToProduce = max(predictedDealerDemand - modelStock, 0).
   - recommendedProduction = ceil(netToProduce × 1.1).  // 10%% buffer on NEW production only
6) Color forecast:
   - Use shares from the most recent month with data (prefer last_month, else two_months_ago, else three_months_ago); fallback to baseline ratios; if all zero → equal split.
   - Allocate integers so sum(colorForecast[*].predictedColorDemand) == predictedDealerDemand.
7) Output one record per region-country.

OUTPUT RULES:
- Valid JSON only; preserve exact "country" and "region".
- Integers only (no decimals).
- For each model:
  * predictedDealerDemand ≥ 0
  * recommendedProduction = ceil(max(predictedDealerDemand - sum(inventoryRemaining), 0) × 1.1)
  * sum(colorForecast.predictedColorDemand) = predictedDealerDemand

OUTPUT STRUCTURE:
[
  {
    "country": "string",
    "region": "string",
    "supplyPlan": [
      {
        "modelName": "string",
        "predictedDealerDemand": number,
        "recommendedProduction": number,
        "colorForecast": [
          { "color": "string", "predictedColorDemand": number }
        ]
      }
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
