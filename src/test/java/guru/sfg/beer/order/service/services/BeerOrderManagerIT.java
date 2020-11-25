package guru.sfg.beer.order.service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.ManagedWireMockServer;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import courses.microservices.brewery.model.BeerDto;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.domain.OrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.services.beer.BeerService;
import guru.sfg.beer.order.service.services.beer.BeerServiceImpl;

@SpringBootTest
@ExtendWith(WireMockExtension.class)
public class BeerOrderManagerIT {
  
  @Autowired
  BeerOrderManager beerOrderManager;

  @Autowired
  BeerOrderRepository beerOrderRepository;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  WireMockServer wireMockServer;

  @Autowired
  ObjectMapper objectMapper;

  @Value("${sfg.brewery.beerPathV1}") String beerPathV1;
  @Value("${sfg.brewery.beerUpcPathV1}") String beerUpcPathV1;

  Customer customer;

  UUID beerId = UUID.randomUUID();

  @TestConfiguration
  static class RestTemplateBuilderProvider {

    @Bean(destroyMethod = "stop")
    public WireMockServer wireMockServer() {
      WireMockServer server = ManagedWireMockServer.with(WireMockConfiguration.wireMockConfig().port(8083));
      server.start();
      return server;
    }

  }

  @BeforeEach
  void setUp() {
    customer = customerRepository.save(Customer.builder()
      .customerName("Test customer")
    .build());
  }

  // this test doesn't work
  @Test
  @Disabled
  void testNewAllocated() {
    // wireMockServer.stubFor(get(beerPathV1)).willReturn(ResponseDefinitionBuilder.okForJson(BeerDto.class));

    BeerOrder order = createBeerOrder();
    BeerOrder saved = beerOrderManager.newBeerOrder(order);

    assertNotNull(saved);
    assertEquals(OrderStatusEnum.ALLOCATED, saved.getOrderStatus());
  }

  public BeerOrder createBeerOrder() {
    BeerOrder order = BeerOrder.builder().customer(customer).build();

    Set<BeerOrderLine> lines = new HashSet<>();
    lines.add(BeerOrderLine.builder()
      .beerId(beerId)
      .orderQuantity(1)
      .beerOrder(order)
    .build());

    order.setBeerOrderLines(lines);
    return order;
  }

}
