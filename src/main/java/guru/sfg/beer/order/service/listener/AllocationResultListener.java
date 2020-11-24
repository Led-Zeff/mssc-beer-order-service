package guru.sfg.beer.order.service.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import courses.microservices.brewery.model.AllocationOrderResult;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationResultListener {

  private final BeerOrderManager beerOrderManager;
  
  @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE)
  public void listen(AllocationOrderResult allocationOrderResult) {
    log.debug("Beero order {}, allocation result isAllocationError: {}, isInventoryPending: ", allocationOrderResult.getBeerOrderDto().getId(), allocationOrderResult.isAllocationError(), allocationOrderResult.isPendingInventory());

    if (allocationOrderResult.isAllocationError()) {
      beerOrderManager.beerOrderAllocationFailed(allocationOrderResult.getBeerOrderDto());
    } else if (allocationOrderResult.isPendingInventory()) {
      beerOrderManager.beerOrderAllocationPendingInventory(allocationOrderResult.getBeerOrderDto());
    } else {
      beerOrderManager.beerOrderAllocationPassed(allocationOrderResult.getBeerOrderDto());
    }
  }

}
