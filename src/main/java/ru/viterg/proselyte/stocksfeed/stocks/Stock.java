package ru.viterg.proselyte.stocksfeed.stocks;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@RedisHash("stocks")
public class Stock {

    private String ticker;
    private BigDecimal stock;
    private Instant actualOn;

    public String id() {
        return String.format("%s-%s-%s", ticker, stock, actualOn);
    }
}
