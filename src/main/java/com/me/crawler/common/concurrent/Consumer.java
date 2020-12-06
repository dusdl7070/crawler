package com.me.crawler.common.concurrent;

import java.util.concurrent.BlockingQueue;

public abstract class Consumer implements Runnable{
    private BlockingQueue<Object> queue;

    public Consumer(BlockingQueue<Object> queue) {
        this.queue = queue;
    }

    public BlockingQueue<Object> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Object> queue) {
        this.queue = queue;
    }
}
