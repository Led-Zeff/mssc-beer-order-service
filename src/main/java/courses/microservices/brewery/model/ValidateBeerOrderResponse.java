package courses.microservices.brewery.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ValidateBeerOrderResponse {
  private UUID orderId;
  private boolean isValid;
}
