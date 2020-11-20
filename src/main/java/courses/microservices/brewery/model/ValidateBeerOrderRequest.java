package courses.microservices.brewery.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateBeerOrderRequest {
  private BeerOrderDto beerOrderDto;
}
