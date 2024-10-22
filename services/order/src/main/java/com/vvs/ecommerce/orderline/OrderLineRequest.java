package com.vvs.ecommerce.orderline;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderLineRequest(Object o, Integer id, @NotNull(message = "Product is mandatory") Integer integer,
                               @Positive(message = "Quantity is mandatory") double quantity) {
}
