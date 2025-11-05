/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.model.request.AIRequest.DemandForecastRequest;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {
  private final ChatClient chatClient;

  public AIService(ChatClient.Builder builder) {
    this.chatClient = builder.build();
  }

  public String getAIResponse(List<DemandForecastRequest> requests) {
    String prompt =
        """
You are an AI assistant for an electric vehicle manufacturer.
Your goal is to forecast dealer demand to plan production and regional distribution for the next manufacturing cycle.

Input JSON shows dealer demand from the last 3 months, grouped by region and model.
Each model has a "data" array listing color variants and their "totalRequests".

Tasks:
1. Sum all color variants per model → total dealer demand.
2. Forecast import demand for the next cycle using a realistic growth rate (10–25%%).
3. Calculate recommendedProduction = ceil(predictedDealerDemand × 1.1) to ensure sufficient stock.
4. Distribute predicted demand across colors proportionally to past color requests.
5. Output the plan for production and distribution per model and region.

Return only valid JSON in this structure:
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

Rules:
- Focus on manufacturer planning, not retail sales commentary.
- All numeric values must be realistic integers (no decimals).
- sum(colorForecast[*].predictedColorDemand) = predictedDealerDemand.
- recommendedProduction = ceil(predictedDealerDemand × 1.1).
- Keep all region and country names exactly as in the input.
- Do not include any explanation or text outside the JSON.

Input (3-month dealer demand data):
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
