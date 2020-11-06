package guru.sfg.beer.order.service.web.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class BeerDto {
  private UUID id;
  private Integer version;
  private OffsetDateTime createdDate;
  private OffsetDateTime lastModifiedDate;
  private String name;
  private BeerStyle style;
  private String upc;
  private BigDecimal price;
  private Integer quantityOnHand;
}