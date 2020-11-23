package guru.sfg.beer.order.service.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import courses.microservices.brewery.model.ValidateBeerOrderResponse;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationOrderListener {

  private final BeerOrderManager beerOrderManager;

  @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESULT_QUEUE)
  public void listen(ValidateBeerOrderResponse response) {
    log.info("Validation response for order {}, result: {}", response.getOrderId(), response.isValid());

    if (response.isValid()) {
      beerOrderManager.inventoryValidateSuccess(response.getOrderId());
    } else {
      beerOrderManager.inventoryValidateError(response.getOrderId());
    }
  }

}

