package com.autonomous.supplychain.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeoPoint(
        String name,
        double latitude,
        double longitude
) {
}
