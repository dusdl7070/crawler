package com.me.crawler.web_crawler.hsmoa.schedule.concurrent;

import com.me.crawler.common.concurrent.Message;
import com.me.crawler.common.concurrent.Producer;
import com.me.crawler.common.utils.StringUtils;
import com.me.crawler.web_crawler.hsmoa.vo.HsmoaMain;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class HsmoaScheduleProducer extends Producer {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private String userAgent;
    private String url;
    private List<String> categories;

    public HsmoaScheduleProducer(BlockingQueue<Object> queue) {
        super(queue);
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public void run() {

        for (String reqUrl : getReqUrls()) {

            try {
                Connection conn = Jsoup.connect(reqUrl)
                        .userAgent(userAgent)
                        .method(Connection.Method.GET)
                        .ignoreContentType(true);

                Document doc = conn.maxBodySize(0).get();

                findTarget(doc);

            } catch (IOException ie) {
                logger.error("ERROR!!", ie);
            }

        }

        Message msg = new Message("exit");
        try {
            getQueue().put(msg);
        } catch (InterruptedException e) {
            logger.error("ERROR!!", e);
        }

    }

    private void findTarget(Document doc) {
        Elements div_timeline_groups = doc.select("div.timeline-group");

        for (Element div_timeline_group : div_timeline_groups) {
            String startTime = "";
            String endTime = "";
            for (Element div_timeline_item : div_timeline_group.children()) {
                for (String category : categories) {
                    if (div_timeline_item.hasClass(category)) {
                        for (Element e : div_timeline_item.children()) {
                            if (e.hasClass("disblock")) {
                                HsmoaMain hsmoaMain = targetDisplayParse(e);
                                startTime = hsmoaMain.getStartTime();
                                endTime = hsmoaMain.getEndTime();

                                putQueue(hsmoaMain);

                            } else if (e.hasClass("sametime-block")) {
                                List<HsmoaMain> list = targetSametimeBlockParse(e);
                                for (HsmoaMain hsmoaMain : list) {
                                    hsmoaMain.setStartTime(startTime);
                                    hsmoaMain.setEndTime(endTime);

                                    putQueue(hsmoaMain);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private HsmoaMain targetDisplayParse(Element e) {
        HsmoaMain hsmoaMain = new HsmoaMain();

        if (!ObjectUtils.isEmpty(e.select("a.disblock"))) {
            String link = url + e.select("a.disblock").attr("href");
            hsmoaMain.setLink(link.replace("/?",""));
        }

        if (!ObjectUtils.isEmpty(e.select("div.display-table > div.table-cell > img"))) {
            hsmoaMain.setMainImage(e.select("div.display-table > div.table-cell > img").attr("data-src"));
        }

        for (Element cell : e.select("div.display-table > div.table-cell").last().children()) {
            if (!ObjectUtils.isEmpty(cell.select("img"))) {
                hsmoaMain.setShopMark(cell.select("img").attr("src"));

            } else if (!ObjectUtils.isEmpty(cell.select("span.font-12.c-midgray"))) {
                String timeInfo = cell.select("span.font-12.c-midgray").text();
                if (timeInfo.contains("~")) {
                    if (!ObjectUtils.isEmpty(timeInfo.split("~")[0]))
                        hsmoaMain.setStartTime(timeInfo.split("~")[0].trim());

                    if (!ObjectUtils.isEmpty(timeInfo.split("~")[1]))
                        hsmoaMain.setEndTime(timeInfo.split("~")[1].trim());

                }

            } else if (!ObjectUtils.isEmpty(cell.select("div.font-15"))) {
                hsmoaMain.setTitle(cell.select("div.font-15").text());

            } else if (!ObjectUtils.isEmpty(cell.select("s.c-gray"))) {
                hsmoaMain.setOrgPrice(cell.select("s.c-gray").text());

            } else if (!ObjectUtils.isEmpty(cell.selectFirst("span.strong.font-17.c-black"))) {
                hsmoaMain.setSalesPrice(cell.selectFirst("span.strong.font-17.c-black").text());

            }
        }

        return hsmoaMain;
    }

    private List<HsmoaMain> targetSametimeBlockParse(Element e) {
        List<HsmoaMain> list = new ArrayList<>();

        for (Element a : e.children()) {
            if (a.hasClass("sametime-item")) {
                HsmoaMain hsmoaMain = new HsmoaMain();

                String link = url + a.attr("href");
                hsmoaMain.setLink(link.replace("/?",""));

                if (!ObjectUtils.isEmpty(a.select("div.display-table > div.table-cell > img").first())) {
                    hsmoaMain.setMainImage(a.select("div.display-table > div.table-cell > img").attr("data-src"));
                }

                if (!ObjectUtils.isEmpty(a.select("div.display-table > div.table-cell > div.font-13"))) {
                    hsmoaMain.setTitle(a.select("div.display-table > div.table-cell > div.font-13").text());

                }

                if (!ObjectUtils.isEmpty(a.select("div.display-table > div.table-cell > span.strong.font-14.c-black"))) {
                    hsmoaMain.setSalesPrice(a.select("div.display-table > div.table-cell > span.strong.font-14.c-black").text());
                }

                list.add(hsmoaMain);
            }
        }

        return list;
    }

    private Map<String, Object> validate (HsmoaMain hsmoaMain) {
        boolean isOk = true;
        String cause = "Title -> " + hsmoaMain.getTitle() + ", Link -> " + hsmoaMain.getLink();

        if(StringUtils.isNullOrEmpty(hsmoaMain.getLink())) {
            isOk = false;
            cause += ", Link is empty!! Link -> " + hsmoaMain.getLink();
        }
        if(StringUtils.isNullOrEmpty(hsmoaMain.getStartTime())) {
            isOk = false;
            cause += ", StartTime is empty!! StartTime -> " + hsmoaMain.getStartTime();
        }
        if(StringUtils.isNullOrEmpty(hsmoaMain.getEndTime())) {
            isOk = false;
            cause += ", EndTime is empty!! EndTime -> " + hsmoaMain.getEndTime();
        }
        if(StringUtils.isNullOrEmpty(hsmoaMain.getTitle())) {
            isOk = false;
            cause += ", Title is empty!! Title -> " + hsmoaMain.getTitle();
        }
        if(StringUtils.isNullOrEmpty(hsmoaMain.getSalesPrice())) {
            isOk = false;
            cause += ", SalesPrice is empty!! SalesPrice -> " + hsmoaMain.getSalesPrice();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", isOk);
        result.put("rstMsg", cause);

        return result;
    }

    public void putQueue (HsmoaMain hsmoaMain) {
        try {
            Map<String, Object> result = validate(hsmoaMain);
            if ((boolean) result.get("result")) {
                getQueue().put(hsmoaMain);
                logger.info("BlockingQueue.put() : 1, RemainingCapacity : " + getQueue().remainingCapacity());
            } else {
                logger.info("Fail : BlockingQueue.put(), Cause : " + result.get("rstMsg").toString());
            }

        } catch (InterruptedException ie) {
            logger.error("ERROR!!", ie);
        }
    }

}
