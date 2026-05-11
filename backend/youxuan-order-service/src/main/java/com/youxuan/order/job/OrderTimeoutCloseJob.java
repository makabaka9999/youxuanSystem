package com.youxuan.order.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderTimeoutCloseJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderTimeoutCloseJob.class);

    @Scheduled(cron = "${youxuan.order.jobs.timeout-close-cron:0 */1 * * * ?}")
    public void scanTimeoutOrders() {
        LOGGER.info("Scan timeout pending-payment orders job triggered");
    }
}
