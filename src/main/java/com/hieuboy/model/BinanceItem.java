package com.hieuboy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BinanceItem implements Serializable {

    private String symbol;
    private Double priceChange;
    private Double priceChangePercent;
    private Double weightedAvgPrice;
    private Double prevClosePrice;
    private Double lastPrice;
    private Double lastQty;
    private Double bidPrice;
    private Double bidQty;
    private Double askPrice;
    private Double askQty;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double volume;
    private Double quoteVolume;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.NUMBER_INT)
    private Date openTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.NUMBER_INT)
    private Date closeTime;
    private Long firstId;
    private Long lastId;
    private Long count;
}
