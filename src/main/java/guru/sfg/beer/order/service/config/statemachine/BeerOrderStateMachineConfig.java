package guru.sfg.beer.order.service.config.statemachine;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.OrderStatusEnum;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, BeerOrderEvent> {
  
  private final Action<OrderStatusEnum, BeerOrderEvent> validateOrderRequestAction;
  private final Action<OrderStatusEnum, BeerOrderEvent> allocateOrderAction;
  private final Action<OrderStatusEnum, BeerOrderEvent> validationFailureAction;
  private final Action<OrderStatusEnum, BeerOrderEvent> allocationFailureAction;
  private final Action<OrderStatusEnum, BeerOrderEvent> deallocateOrderAction;

  @Override
  public void configure(StateMachineStateConfigurer<OrderStatusEnum, BeerOrderEvent> states) throws Exception {
    states.withStates()
      .initial(OrderStatusEnum.NEW)
      .states(EnumSet.allOf(OrderStatusEnum.class))
      .end(OrderStatusEnum.PICKED_UP)
      .end(OrderStatusEnum.DELIVERED)
      .end(OrderStatusEnum.DELIVER_EXCEPTION)
      .end(OrderStatusEnum.VALIDATION_EXCEPTION)
      .end(OrderStatusEnum.ALLOCATION_EXCEPTION)
      .end(OrderStatusEnum.CANCELED);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, BeerOrderEvent> transitions) throws Exception {
    transitions.withExternal().source(OrderStatusEnum.NEW).target(OrderStatusEnum.VALIDATION_PENDING).event(BeerOrderEvent.VALIDATE_ORDER)
      .action(validateOrderRequestAction)
    .and()
    .withExternal().source(OrderStatusEnum.VALIDATION_PENDING).target(OrderStatusEnum.VALIDATED).event(BeerOrderEvent.VALIDATION_PASSED)
    .and()
    .withExternal().source(OrderStatusEnum.VALIDATION_PENDING).target(OrderStatusEnum.CANCELED).event(BeerOrderEvent.CANCEL_ORDER)
    .and()
    .withExternal().source(OrderStatusEnum.VALIDATION_PENDING).target(OrderStatusEnum.VALIDATION_EXCEPTION).event(BeerOrderEvent.VALIDATION_FAILED)
      .action(validationFailureAction)
    .and()
    .withExternal().source(OrderStatusEnum.VALIDATED).target(OrderStatusEnum.ALLOCATION_PENDING).event(BeerOrderEvent.ALLOCATE_ORDER)
      .action(allocateOrderAction)
    .and()
    .withExternal().source(OrderStatusEnum.VALIDATED).target(OrderStatusEnum.CANCELED).event(BeerOrderEvent.CANCEL_ORDER)
    .and()
    .withExternal().source(OrderStatusEnum.ALLOCATION_PENDING).target(OrderStatusEnum.ALLOCATED).event(BeerOrderEvent.ALLOCATION_SUCCESS)
    .and()
    .withExternal().source(OrderStatusEnum.ALLOCATION_PENDING).target(OrderStatusEnum.ALLOCATION_EXCEPTION).event(BeerOrderEvent.ALLOCATION_FAILED)
      .action(allocationFailureAction)
    .and()
    .withExternal().source(OrderStatusEnum.ALLOCATION_PENDING).target(OrderStatusEnum.CANCELED).event(BeerOrderEvent.CANCEL_ORDER)
    .and()
    .withExternal().source(OrderStatusEnum.ALLOCATION_PENDING).target(OrderStatusEnum.PENDING_INVENTORY).event(BeerOrderEvent.ALLOCATION_NO_INVENTORY)
    .and()
    .withExternal().source(OrderStatusEnum.ALLOCATED).target(OrderStatusEnum.PICKED_UP).event(BeerOrderEvent.BEER_ORDER_PICKED_UP)
    .and()
    .withExternal().source(OrderStatusEnum.ALLOCATED).target(OrderStatusEnum.CANCELED).event(BeerOrderEvent.CANCEL_ORDER)
      .action(deallocateOrderAction);
  }

}
