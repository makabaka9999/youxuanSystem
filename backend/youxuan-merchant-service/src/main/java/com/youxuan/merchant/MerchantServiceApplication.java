package com.youxuan.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.youxuan.merchant", "com.youxuan.common"})
public class MerchantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantServiceApplication.class, args);
    }
}
