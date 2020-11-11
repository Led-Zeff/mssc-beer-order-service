package guru.sfg.beer.order.service.services.beer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import guru.sfg.beer.order.service.web.model.BeerDto;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
public class BeerServiceImpl implements BeerService {
  
  private final RestTemplate restTemplate;

  @Setter
  private String beerPathV1;
  @Setter
  private String beerUpcPathV1;
  @Setter
  private String beerServiceHost;

  public BeerServiceImpl(RestTemplateBuilder restTemplateBuilder) {
    restTemplate = restTemplateBuilder.build();
  }

  @Override
  public Optional<BeerDto> getBeerById(UUID id) {
    return Optional.of(restTemplate.getForObject(beerServiceHost + beerPathV1 + id, BeerDto.class));
  }

  @Override
  public Optional<BeerDto> getBeerByUpc(String upc) {
    log.info("Getting beer with UPC {}", upc);
    return Optional.of(restTemplate.getForObject(beerServiceHost + beerUpcPathV1 + upc, BeerDto.class));
  }

}
