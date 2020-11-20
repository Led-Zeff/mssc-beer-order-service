package guru.sfg.beer.order.service.services;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.OrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

  private final StateMachineFactory<OrderStatusEnum, BeerOrderEvent> stateMachineFactory;
  private final BeerOrderRepository beerOrderRepository;

  @Transactional
  @Override
  public BeerOrder newBeerOrder(BeerOrder beerOrder) {
    beerOrder.setId(null);
    beerOrder.setOrderStatus(OrderStatusEnum.NEW);

    BeerOrder saved = beerOrderRepository.save(beerOrder);
    sendBeerOrderEvent(saved, BeerOrderEvent.VALIDATE_ORDER);
    return saved;
  }

  private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEvent event) {
    StateMachine<OrderStatusEnum, BeerOrderEvent> sm = build(beerOrder);

    Message<BeerOrderEvent> msg = MessageBuilder.withPayload(event).build();

    sm.sendEvent(msg);
  }

  private StateMachine<OrderStatusEnum, BeerOrderEvent> build(BeerOrder beerOrder) {
    StateMachine<OrderStatusEnum, BeerOrderEvent> sm = stateMachineFactory.getStateMachine(beerOrder.getId());

    sm.stop();

    sm.getStateMachineAccessor().doWithAllRegions(sma -> {
      sma.resetStateMachine(new DefaultStateMachineContext<OrderStatusEnum,BeerOrderEvent>(beerOrder.getOrderStatus(), null, null, null));
    });

    sm.start();

    return sm;
  }
  
}
