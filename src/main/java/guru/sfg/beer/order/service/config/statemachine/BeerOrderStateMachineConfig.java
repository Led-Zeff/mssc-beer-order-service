package guru.sfg.beer.order.service.config.statemachine;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.OrderStatusEnum;

@Configuration
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, BeerOrderEvent> {
  
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

}
