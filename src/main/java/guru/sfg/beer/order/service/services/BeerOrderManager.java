package guru.sfg.beer.order.service.services;

import java.util.UUID;

import courses.microservices.brewery.model.BeerOrderDto;
import guru.sfg.beer.order.service.domain.BeerOrder;

public interface BeerOrderManager {
  BeerOrder newBeerOrder(BeerOrder beerOrder);
  void inventoryValidateSuccess(UUID orderId);
  void inventoryValidateError(UUID orderId);
  void beerOrderAllocationPassed(BeerOrderDto beerOrderDto);
  void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto);
  void beerOrderAllocationFailed(BeerOrderDto beerOrderDto);
}
