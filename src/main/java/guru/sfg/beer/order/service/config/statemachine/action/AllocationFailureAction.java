package guru.sfg.beer.order.service.config.statemachine.action;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.OrderStatusEnum;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AllocationFailureAction implements Action<OrderStatusEnum, BeerOrderEvent> {

  @Override
  public void execute(StateContext<OrderStatusEnum, BeerOrderEvent> context) {
    String orderId = (String) context.getMessageHeader(BeerOrderManagerImpl.ORDER_ID_HEADER);
    log.error("Allocation failed: {}", orderId);
  }
  
}