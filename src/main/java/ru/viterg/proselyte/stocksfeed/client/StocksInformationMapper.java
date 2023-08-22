package ru.viterg.proselyte.stocksfeed.client;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StocksInformationMapper {

    @Mapping(target = "ticker", source = "symbol")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "givenAt", source = "timestamp")
    StocksInformation toStockInformation(Map<String, String> stockClientResponse);
}
