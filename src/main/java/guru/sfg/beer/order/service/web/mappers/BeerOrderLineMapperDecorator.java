package guru.sfg.beer.order.service.web.mappers;

import org.springframework.beans.factory.annotation.Autowired;

import courses.microservices.brewery.model.BeerOrderLineDto;
import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.services.beer.BeerService;

public abstract class BeerOrderLineMapperDecorator implements BeerOrderLineMapper {
  
  private BeerOrderLineMapper beerOrderLineMapper;
  private BeerService beerService;

  @Autowired
  public void setBeerService(BeerService beerService) {
    this.beerService = beerService;
  }

  @Autowired
  public void setBeerOrderLineMapper(BeerOrderLineMapper beerOrderLineMapper) {
    this.beerOrderLineMapper = beerOrderLineMapper;
  }

  @Override
  public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
    BeerOrderLineDto dto = beerOrderLineMapper.beerOrderLineToDto(line);
    beerService.getBeerByUpc(dto.getUpc()).ifPresent(beer -> {
      dto.setBeerId(beer.getId());
      dto.setBeerName(beer.getName());
      dto.setBeerStyle(beer.getStyle());
      dto.setPrice(beer.getPrice());
    });

    return dto;
  }

}
