package com.hieuboy.service;

import com.hieuboy.model.BinanceItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Component
public class ExecuteDataBinanceService {

    private static final Map<String, Object> itemCache = new HashMap<>();
    private static CacheManager component;

    @Autowired
    private CacheManager autowiredComponent;

    @PostConstruct
    private void init() {
        component = this.autowiredComponent;
    }

    public List<BinanceItem> executeBinanceAPIByTicker24h() {
        // https://api.binance.com/api/v3/ticker/24hr
        // https://api.binance.com/api/v3/klines
        try {
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
            converter.setDefaultCharset(StandardCharsets.UTF_8);
            messageConverters.add(converter);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setMessageConverters(messageConverters);
            HttpMethod method = HttpMethod.GET;
            URI uri = UriComponentsBuilder.fromHttpUrl("https://api.binance.com/api/v3/ticker/24hr").build().toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/xml; charset=utf-8");

            RequestEntity<List<BinanceItem>> requestEntity = new RequestEntity<>(headers, method, uri);
            ParameterizedTypeReference<List<BinanceItem>> typeRef = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<List<BinanceItem>> responseEntity = restTemplate.exchange(requestEntity, typeRef);
            return responseEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void processData() {
        try {
            List<BinanceItem> binanceItems = executeBinanceAPIByTicker24h();
            binanceItems = binanceItems
                    .stream()
                    .filter(item -> item.getSymbol().endsWith("USDT") && item.getPriceChangePercent() > 10)
                    .collect(Collectors.toList());
            log.info("===> Thời gian: {} ===> Số Coin tăng > 10%: {}", new Date(), binanceItems.size());
            for (BinanceItem item : binanceItems) {
                getMessage(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMessage(BinanceItem item) {
        boolean isPutAndShow = false;
        BinanceItem dataCache = (BinanceItem) itemCache.get(item.getSymbol());
        if (dataCache != null) {
            if (item.getPriceChangePercent() > dataCache.getPriceChangePercent()
                    && item.getPriceChangePercent() > (dataCache.getPriceChangePercent() + 2)) {
                isPutAndShow = true;
            }
        } else {
            isPutAndShow = true;
        }
        if (Boolean.TRUE.equals(isPutAndShow)) {
            itemCache.put(item.getSymbol(), item);
            log.info("Đồng Coin: {}, có giá mới nhất: {}, đã được tăng: {}%", item.getSymbol(), item.getLastPrice(), item.getPriceChangePercent());
        }
        /*Cache cache = component.getCache(item.getSymbol());
        if (cache != null && cache.get(item.getSymbol()) == null) {
            cache.put(item.getSymbol(), item);
        } else if (cache != null && cache.get(item.getSymbol()) != null) {
            BinanceItem cacheData = (BinanceItem) Objects.requireNonNull(cache.get(item.getSymbol())).get();
            if (cacheData != null) {
                if (item.getPriceChangePercent() > cacheData.getPriceChangePercent()
                        && item.getPriceChangePercent() > (cacheData.getPriceChangePercent() + 2)) {
                    itemCache.putIfAbsent(item.getSymbol(), item);
                    log.info("Đồng Coin: {}, có giá mới nhất: {}, đã được tăng: {}%", item.getSymbol(), item.getLastPrice(), item.getPriceChangePercent());
                }
            } else {
                itemCache.put(item.getSymbol(), item);
                log.info("Đồng Coin: {}, có giá mới nhất: {}, đã được tăng: {}%", item.getSymbol(), item.getLastPrice(), item.getPriceChangePercent());
            }
        }*/
    }

}
