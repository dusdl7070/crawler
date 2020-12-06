package com.me.crawler.common.crawler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface WebDataCrawler {
    List<String> getRequestUrls();
    void doCrawling();
}
