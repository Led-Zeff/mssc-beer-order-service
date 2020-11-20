package courses.microservices.brewery.event;

import courses.microservices.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateBeerOrderRequestEvent {
  private BeerOrderDto beerOrderDto;
}
