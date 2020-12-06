package com.me.crawler.common.concurrent;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class Producer implements Runnable {
    private BlockingQueue<Object> queue;
    private List<String> reqUrls;

    public Producer(BlockingQueue<Object> queue) {
        this.queue = queue;
    }

    public BlockingQueue<Object> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Object> queue) {
        this.queue = queue;
    }

    public List<String> getReqUrls() {
        return reqUrls;
    }

    public void setReqUrls(List<String> reqUrls) {
        this.reqUrls = reqUrls;
    }
}
