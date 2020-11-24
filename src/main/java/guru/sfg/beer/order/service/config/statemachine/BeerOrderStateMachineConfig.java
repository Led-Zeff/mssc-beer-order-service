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

  @Override
  public void configure(StateMachineStateConfigurer<OrderStatusEnum, BeerOrderEvent> states) throws Exception {
    states.withStates()
      .initial(OrderStatusEnum.NEW)
      .states(EnumSet.allOf(OrderStatusEnum.class))
      .end(OrderStatusEnum.PICKED_UP)
      .end(OrderStatusEnum.DELIVERED)
      .end(OrderStatusEnum.DELIVER_EXCEPTION)
      .end(OrderStatusEnum.VALIDATION_EXCEPTION)
      .end(OrderStatusEnum.ALLOCATION_EXCEPTION);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, BeerOrderEvent> transitions) throws Exception {
    transitions.withExternal().source(OrderStatusEnum.NEW).target(OrderStatusEnum.VALIDATION_PENDING).event(BeerOrderEvent.VALIDATE_ORDER)
      .action(validateOrderRequestAction)
    .and()
    .withExternal().source(OrderStatusEnum.VALIDATION_PENDING).target(OrderStatusEnum.VALIDATED).event(BeerOrderEvent.VALIDATION_PASSED)
    .and()
    .withExternal().source(OrderStatusEnum.VALIDATION_PENDING).target(OrderStatusEnum.VALIDATION_EXCEPTION).event(BeerOrderEvent.VALIDATION_FAILED)
    .and()
    .withExternal().source(OrderStatusEnum.VALIDATED).target(OrderStatusEnum.ALLOCATION_PENDING).event(BeerOrderEvent.ALLOCATE_ORDER)
      .action(allocateOrderAction);
  }

}
