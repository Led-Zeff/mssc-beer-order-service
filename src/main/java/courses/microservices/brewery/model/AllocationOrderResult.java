package courses.microservices.brewery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AllocationOrderResult {
  private BeerOrderDto beerOrderDto;
  private boolean allocationError;
  private boolean pendingInventory;
}
