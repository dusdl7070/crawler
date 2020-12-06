package com.me.crawler.web_crawler.hsmoa.schedule;

import com.me.crawler.common.concurrent.Producer;
import com.me.crawler.common.crawler.WebDataCrawler;
import com.me.crawler.common.manager.cache.DataCacheManager;
import com.me.crawler.web_crawler.hsmoa.schedule.concurrent.HsmoaScheduleProducer;
import com.me.crawler.web_crawler.hsmoa.schedule.concurrent.HsmoaScheduleConsumer;
import com.me.crawler.web_crawler.hsmoa.schedule.excel.HsmoaScheduleExcelWriter;
import com.me.crawler.web_crawler.hsmoa.vo.HsmoaMain;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class HsmoaScheduleCrawler implements WebDataCrawler, InitializingBean, DisposableBean {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private static int maxPoolSize = 2;
    private static int maxQueueSize = 10;
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36";

    private String url = "https://hsmoa.com/?";
    public static String date = "";
    private List<String> sites = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    DataCacheManager cache;

    @Autowired
    public HsmoaScheduleCrawler(DataCacheManager cacheManager) {
        this.cache = cacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void doCrawling() {
        ExecutorService executorService = Executors.newFixedThreadPool(maxPoolSize);

        List<String> reqUrls = getRequestUrls();
        BlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(maxQueueSize);

        HsmoaScheduleProducer producer = new HsmoaScheduleProducer(queue);
        producer.setReqUrls(reqUrls);
        producer.setUserAgent(userAgent);
        producer.setUrl(url);
        producer.setCategories(categories);

        HsmoaScheduleConsumer consumer = new HsmoaScheduleConsumer(queue);
        consumer.setUserAgent(userAgent);

        executorService.execute(producer);
        executorService.execute(consumer);

        executorService.shutdown();
        try {
            if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println(LocalTime.now() + " All jobs are terminated");
            } else {
                System.out.println(LocalTime.now() + " some jobs are not terminated");

                executorService.shutdownNow();
            }
        } catch (InterruptedException ie) {
            logger.error("ERROR!", ie);

        } finally {

            HsmoaScheduleExcelWriter scheduleExcelWriter = new HsmoaScheduleExcelWriter();
            scheduleExcelWriter.xlsxWrite();

//            List<HsmoaMain> list = (List<HsmoaMain>) cache.find(HsmoaScheduleConsumer.cachekey);
//            for (HsmoaMain hsmoaMain : list) {
//                logger.info(hsmoaMain.toString());
//            }
        }

    }

    @Override
    public List<String> getRequestUrls() {
        List<String> requestUrls = new ArrayList<>();

        this.date = LocalDate.now().plusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE);
        this.categories.add("식품·건강");

        for (String category : categories) {
            requestUrls.add(url + "date=" + date + "&site=" + "&cate=" + category);
        }

        return requestUrls;
    }

    @Override
    public void destroy() throws Exception {

    }
}
