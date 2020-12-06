package com.me.crawler;

import com.me.crawler.common.manager.cache.DataCacheManager;
import com.me.crawler.web_crawler.hsmoa.schedule.HsmoaScheduleCrawler;
import com.me.crawler.web_crawler.hsmoa.schedule.concurrent.HsmoaScheduleConsumer;
import com.me.crawler.web_crawler.hsmoa.vo.HsmoaDetail;
import com.me.crawler.web_crawler.hsmoa.vo.HsmoaMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;
import java.util.List;

@EnableCaching
@SpringBootApplication
public class CrawlerApplication implements CommandLineRunner, InitializingBean, DisposableBean {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Resource
    HsmoaScheduleCrawler hsmoaScheduleCrawler;

    DataCacheManager cache;

    @Autowired
    public CrawlerApplication(DataCacheManager cacheManager) {
        this.cache = cacheManager;
    }

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
