package com.me.crawler.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static void makeDir(String path) {
        File folder = new File(path);

        if (!folder.exists()) {
            try {
                folder.mkdirs();
                logger.info("Make New Directory.");
            } catch (Exception e) {
                logger.error("ERROR!!", e);
            }
        } else {
            logger.info("Already Exist Dir.");
        }
    }

}
