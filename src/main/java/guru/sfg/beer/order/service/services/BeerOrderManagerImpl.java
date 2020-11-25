package guru.sfg.beer.order.service.services;

import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import courses.microservices.brewery.model.BeerOrderDto;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.OrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

  public static final String ORDER_ID_HEADER = "order_id";

  private final StateMachineFactory<OrderStatusEnum, BeerOrderEvent> stateMachineFactory;
  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderStateChangeInterceptor beerOrderStateChangeInterceptor;

  @Transactional
  @Override
  public BeerOrder newBeerOrder(BeerOrder beerOrder) {
    beerOrder.setId(null);
    beerOrder.setOrderStatus(OrderStatusEnum.NEW);

    BeerOrder saved = beerOrderRepository.save(beerOrder);
    sendBeerOrderEvent(saved, BeerOrderEvent.VALIDATE_ORDER);
    return saved;
  }

  @Override
  public void inventoryValidateSuccess(UUID orderId) {
    BeerOrder order = beerOrderRepository.getOne(orderId);
    sendBeerOrderEvent(order, BeerOrderEvent.VALIDATION_PASSED);
    
    BeerOrder validatedOrder = beerOrderRepository.getOne(orderId);
    sendBeerOrderEvent(validatedOrder, BeerOrderEvent.ALLOCATE_ORDER);
  }
  
  @Override
  public void inventoryValidateError(UUID orderId) {
    BeerOrder order = beerOrderRepository.getOne(orderId);
    sendBeerOrderEvent(order, BeerOrderEvent.VALIDATION_FAILED);
  }

  @Override
  public void beerOrderAllocationPassed(BeerOrderDto beerOrderDto) {
    BeerOrder beerOrder = beerOrderRepository.getOne(beerOrderDto.getId());
    sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_SUCCESS);
    updateAllocationQty(beerOrderDto);
  }
  
  @Override
  public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
    BeerOrder beerOrder = beerOrderRepository.getOne(beerOrderDto.getId());
    sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_NO_INVENTORY);
    updateAllocationQty(beerOrderDto);
  }
  
  @Override
  public void beerOrderAllocationFailed(BeerOrderDto beerOrderDto) {
    BeerOrder beerOrder = beerOrderRepository.getOne(beerOrderDto.getId());
    sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_FAILED);
  }
  
  @Override
  public void beerOrderPickedUp(UUID orderId) {
    BeerOrder beerOrder = beerOrderRepository.getOne(orderId);
    sendBeerOrderEvent(beerOrder, BeerOrderEvent.BEER_ORDER_PICKED_UP);
  }
  
  private void updateAllocationQty(BeerOrderDto beerOrderDto) {
    BeerOrder allocatedOrder = beerOrderRepository.findById(beerOrderDto.getId()).orElseThrow();

    allocatedOrder.getBeerOrderLines().forEach(orderLine -> {
      beerOrderDto.getBeerOrderLines().forEach(orderLineDto -> {
        if (orderLineDto.getId().equals(orderLine.getId())) {
          orderLine.setQuantityAllocated(orderLineDto.getQuantityAllocated());
        }
      });
    });

    beerOrderRepository.saveAndFlush(allocatedOrder);
  }

  private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEvent event) {
    StateMachine<OrderStatusEnum, BeerOrderEvent> sm = build(beerOrder);

    Message<BeerOrderEvent> msg = MessageBuilder
      .withPayload(event)
      .setHeader(ORDER_ID_HEADER, beerOrder.getId())
      .build();

    sm.sendEvent(msg);
  }

  private StateMachine<OrderStatusEnum, BeerOrderEvent> build(BeerOrder beerOrder) {
    StateMachine<OrderStatusEnum, BeerOrderEvent> sm = stateMachineFactory.getStateMachine(beerOrder.getId());

    sm.stop();

    sm.getStateMachineAccessor().doWithAllRegions(sma -> {
      sma.addStateMachineInterceptor(beerOrderStateChangeInterceptor);
      sma.resetStateMachine(new DefaultStateMachineContext<OrderStatusEnum,BeerOrderEvent>(beerOrder.getOrderStatus(), null, null, null));
    });

    sm.start();

    return sm;
  }
  
}
