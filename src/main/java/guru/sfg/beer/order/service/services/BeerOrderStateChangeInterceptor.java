package guru.sfg.beer.order.service.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.OrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderStatusEnum, BeerOrderEvent> {
  
  private final BeerOrderRepository beerOrderRepository;

  @Override
  public void preStateChange(State<OrderStatusEnum, BeerOrderEvent> state, Message<BeerOrderEvent> message,
    Transition<OrderStatusEnum, BeerOrderEvent> transition, StateMachine<OrderStatusEnum, BeerOrderEvent> stateMachine
  ) {
    Optional.ofNullable(UUID.class.cast(message.getHeaders().getOrDefault(BeerOrderManagerImpl.ORDER_ID_HEADER, null))).ifPresent(orderId -> {
      BeerOrder order = beerOrderRepository.findOneById(orderId);
      order.setOrderStatus(state.getId());
      beerOrderRepository.save(order);
    });
  }

}
