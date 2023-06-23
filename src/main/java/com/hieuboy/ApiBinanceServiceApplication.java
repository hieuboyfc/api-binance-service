package com.hieuboy;

import com.hieuboy.service.ExecuteDataBinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableCaching
public class ApiBinanceServiceApplication {

    private static ExecuteDataBinanceService component;

    @Autowired
    private ExecuteDataBinanceService autowiredComponent;

    @PostConstruct
    private void init() {
        component = this.autowiredComponent;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiBinanceServiceApplication.class, args);
        boolean isRun = true;
        while (isRun) {
            try {
                component.processData();
                Thread.sleep(10000L);
            } catch (Exception e) {
                isRun = false;
                e.printStackTrace();
            }
        }
    }
}
