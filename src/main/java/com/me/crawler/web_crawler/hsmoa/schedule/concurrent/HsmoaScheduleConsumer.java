package com.me.crawler.web_crawler.hsmoa.schedule.concurrent;

import com.me.crawler.common.concurrent.Consumer;
import com.me.crawler.common.concurrent.Message;
import com.me.crawler.common.manager.cache.DataCacheManager;
import com.me.crawler.common.utils.BeanUtils;
import com.me.crawler.common.utils.StringUtils;
import com.me.crawler.web_crawler.hsmoa.vo.HsmoaDetail;
import com.me.crawler.web_crawler.hsmoa.vo.HsmoaMain;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class HsmoaScheduleConsumer extends Consumer {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private String userAgent;
    private DataCacheManager cache;

    public static final String cachekey = "HsmoaScheduleData";

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public HsmoaScheduleConsumer(BlockingQueue<Object> queue) {
        super(queue);

        cache = (DataCacheManager) BeanUtils.getBean(DataCacheManager.class);
    }

    @Override
    public void run() {
        try {
            Object obj;

            while(true) {
                obj = getQueue().take();
                logger.info("BlockingQueue.take() : 1, RemainingCapacity : " + getQueue().remainingCapacity());

                if (obj instanceof HsmoaMain) {
                    HsmoaMain hsmoaMain = (HsmoaMain) obj;

                    findDetail(hsmoaMain);

                    Map<String, Object> result = validate(hsmoaMain.getHsmoaDetail());
                    if ((boolean) result.get("result")) {

                        List<HsmoaMain> list = new ArrayList<>();
                        if(!ObjectUtils.isEmpty(cache.find(cachekey))) {
                            list = (List<HsmoaMain>) cache.find(cachekey);
                        }
                        list.add(hsmoaMain);
                        cache.update(cachekey, list);

                    } else {
                        logger.error("ERROR!!", "Fail Cause : " + result.get("rstMsg").toString());
                    }

                } else if (obj instanceof Message) {
                    Message msg = (Message) obj;
                    logger.info("Arrived Exit.");
                    if (msg.getMsg().equals("exit")) {
                        break;
                    }
                }
            }

        } catch (InterruptedException ie) {
            logger.error("ERROR!!", ie);
        }
    }

    private void findDetail(HsmoaMain hsmoaMain) {
        try {
            Connection conn = Jsoup.connect(hsmoaMain.getLink())
                    .userAgent(userAgent)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true);

            Document doc = conn.get();

            hsmoaMain.setHsmoaDetail(targetDetailParse(doc));

        } catch (IOException ie) {
            logger.error("ERROR!!", ie);
        }
    }

    private HsmoaDetail targetDetailParse(Document doc) {
        HsmoaDetail hsmoaDetail = new HsmoaDetail();

        Element div_display_table = doc.selectFirst("div.display-table-fixed > div.table-cell > div > div.display-table");

        if (!ObjectUtils.isEmpty(div_display_table.select("div.table-cell > div.position-relative.border"))) {
            for (Element e : div_display_table.select("div.table-cell > div.position-relative.border").first().children()) {
                if (!ObjectUtils.isEmpty(e.selectFirst("div#swipe_img > div > img"))) {
                    hsmoaDetail.setImage(e.select("div#swipe_img > div > img").attr("src"));
                }
            }
        }

        if (!ObjectUtils.isEmpty(div_display_table.select("div.table-cell > div.position-relative").not("div.border"))) {
            for (Element e : div_display_table.select("div.table-cell > div.position-relative").not("div.border").first().children()) {
                if (!ObjectUtils.isEmpty(e.selectFirst("img"))) {
                    hsmoaDetail.setShopMark(e.select("img").attr("src"));

                } else if (!ObjectUtils.isEmpty(e.select("span.margin-left.c-darkgray.font-14"))) {
                    hsmoaDetail.setPredTime(e.select("span.margin-left.c-darkgray.font-14").text());

                } else if (!ObjectUtils.isEmpty(e.select("div.font-24").not("div.c-red.strong"))) {
                    hsmoaDetail.setTitle(e.select("div.font-24").text());

                } else if (!ObjectUtils.isEmpty(e.select("s.c-gray.font-15"))) {
                    hsmoaDetail.setOrgPrice(e.select("s.c-gray.font-15").text());

                } else if (!ObjectUtils.isEmpty(e.select("div.font-24.c-red.strong"))) {
                    hsmoaDetail.setSalesPrice(e.select("div.font-24.c-red.strong").text());
                }
            }
        }

        if (!ObjectUtils.isEmpty(div_display_table.select("div#block_entityinfo > table > tbody > tr"))) {
            hsmoaDetail.setShopName(div_display_table.select("div#block_entityinfo > table > tbody > tr").first().select("td").last().text());
            hsmoaDetail.setRealTime(div_display_table.select("div#block_entityinfo > table > tbody > tr").last().select("td").last().text());
        }

        if (!ObjectUtils.isEmpty(doc.select("div.display-table-fixed > div.table-cell > div > div.margin-9 > img"))) {
            hsmoaDetail.setDetailImage(doc.selectFirst("div.display-table-fixed > div.table-cell > div > div.margin-9 > img").attr("src"));
        }

        return hsmoaDetail;
    }

    private Map<String, Object> validate (HsmoaDetail hsmoaDetail) {
        boolean isOk = true;
        String cause = "Title -> " + hsmoaDetail.getTitle() + ", Link -> " + hsmoaDetail.getShopName();

        if(StringUtils.isNullOrEmpty(hsmoaDetail.getPredTime())) {
            isOk = false;
            cause += ", PredTime is empty!! PredTime -> " + hsmoaDetail.getPredTime();
        }
        if(StringUtils.isNullOrEmpty(hsmoaDetail.getTitle())) {
            isOk = false;
            cause += ", Title is empty!! Title -> " + hsmoaDetail.getTitle();
        }
        if(StringUtils.isNullOrEmpty(hsmoaDetail.getSalesPrice())) {
            isOk = false;
            cause += ", SalesPrice is empty!! SalesPrice -> " + hsmoaDetail.getSalesPrice();
        }
        if(StringUtils.isNullOrEmpty(hsmoaDetail.getShopName())) {
            isOk = false;
            cause += ", ShopName is empty!! ShopName -> " + hsmoaDetail.getShopName();
        }
        if(StringUtils.isNullOrEmpty(hsmoaDetail.getRealTime())) {
            isOk = false;
            cause += ", RealTime is empty!! RealTime -> " + hsmoaDetail.getRealTime();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", isOk);
        result.put("rstMsg", cause);

        return result;
    }
}
