package com.me.crawler;

import com.me.crawler.web_crawler.hsmoa.schedule.HsmoaScheduleCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;

@EnableCaching
@SpringBootApplication
public class CrawlerApplication implements CommandLineRunner, InitializingBean, DisposableBean {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Resource
    HsmoaScheduleCrawler hsmoaScheduleCrawler;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==========> Crawler Started.");
    }

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        hsmoaScheduleCrawler.doCrawling();
    }

    @Override
    public void destroy() throws Exception {
        logger.info("==========> Crawler End.");

    }

}
