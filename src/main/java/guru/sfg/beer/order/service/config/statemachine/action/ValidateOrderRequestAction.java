package guru.sfg.beer.order.service.config.statemachine.action;

import java.util.UUID;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import courses.microservices.brewery.event.ValidateBeerOrderRequestEvent;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.OrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderRequestAction implements Action<OrderStatusEnum, BeerOrderEvent> {

  private final JmsTemplate jmsTemplate;
  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderMapper beerOrderMapper;

  @Override
  public void execute(StateContext<OrderStatusEnum, BeerOrderEvent> context) {
    log.info("Validating order request");

    UUID orderId = UUID.class.cast(context.getMessageHeader(BeerOrderManagerImpl.ORDER_ID_HEADER));
    BeerOrder order = beerOrderRepository.getOne(orderId);
    jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, new ValidateBeerOrderRequestEvent(beerOrderMapper.beerOrderToDto(order)));
  }
  
}
