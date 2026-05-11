package com.youxuan.finance.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FinanceScheduleJobs {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceScheduleJobs.class);

    @Scheduled(cron = "${youxuan.finance.jobs.daily-bill-cron:0 10 0 * * ?}")
    public void generateDailyMerchantBills() {
        LOGGER.info("Generate daily merchant bills job triggered");
    }

    @Scheduled(cron = "${youxuan.finance.jobs.reconciliation-cron:0 30 1 * * ?}")
    public void executeChannelReconciliation() {
        LOGGER.info("Execute channel reconciliation job triggered");
    }

    @Scheduled(cron = "${youxuan.finance.jobs.settlement-cron:0 0 2 * * ?}")
    public void generateSettlementOrders() {
        LOGGER.info("Generate settlement orders job triggered");
    }
}
