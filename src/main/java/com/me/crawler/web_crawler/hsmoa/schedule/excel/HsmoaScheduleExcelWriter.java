package com.me.crawler.web_crawler.hsmoa.schedule.excel;

import com.me.crawler.common.excel.ExcelWriter;
import com.me.crawler.common.manager.cache.DataCacheManager;
import com.me.crawler.common.utils.BeanUtils;
import com.me.crawler.common.utils.FileUtils;
import com.me.crawler.web_crawler.hsmoa.schedule.HsmoaScheduleCrawler;
import com.me.crawler.web_crawler.hsmoa.schedule.concurrent.HsmoaScheduleConsumer;
import com.me.crawler.web_crawler.hsmoa.vo.HsmoaDetail;
import com.me.crawler.web_crawler.hsmoa.vo.HsmoaMain;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HsmoaScheduleExcelWriter implements ExcelWriter {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private String fontName = "맑은 고딕";
    private String path = "data/hsmoa/schedule/";
    DataCacheManager cache;

    public HsmoaScheduleExcelWriter() {
        cache = (DataCacheManager) BeanUtils.getBean(DataCacheManager.class);
    }

    @Override
    public void xlsxWrite() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        // Header
        setHeader(sheet);

        // Row(Data)
        List<HsmoaMain> list = new ArrayList<>();
        if (!ObjectUtils.isEmpty(cache.find(HsmoaScheduleConsumer.cachekey))) {
            list = (List<HsmoaMain>) cache.find(HsmoaScheduleConsumer.cachekey);
        }
        setRow(sheet, list);

        if (list.size() > 0) {
            logger.info("Excel Row Number -> " + sheet.getLastRowNum());
            makeFile(workbook);

        } else {
            logger.info("Excel Row Number -> " + sheet.getLastRowNum() + ", Cannot Make File.");
        }

    }

    @Override
    public void setHeader(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);

        String[] headerArr = {
                "채널", 
                "방송시간", 
                "상품 이미지", 
                "상품명", 
                "판매가격", 
                "할인가격", 
                "URL",
                "상세 이미지"
        };

        XSSFCell cell;

        for (int idx=0; idx < headerArr.length; idx++) {
            cell = row.createCell(idx);
            cell.setCellValue(headerArr[idx]);
        }
    }

    @Override
    public void setRow(XSSFSheet sheet, List<HsmoaMain> list) {
        XSSFRow row;
        XSSFCell cell;
        int idx = 1;

        for (HsmoaMain hsmoaMain : list) {
            row = sheet.createRow(idx);
            HsmoaDetail hsmoaDetail = hsmoaMain.getHsmoaDetail();

            String[] datas = {
                    hsmoaDetail.getShopName(),
                    hsmoaDetail.getRealTime(),
                    hsmoaDetail.getImage(),
                    hsmoaDetail.getTitle(),
                    hsmoaDetail.getOrgPrice(),
                    hsmoaDetail.getSalesPrice(),
                    hsmoaMain.getLink(),
                    hsmoaDetail.getDetailImage()
            };

            for (int i=0; i < datas.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(datas[i]);
            }

            idx++;
        }
    }

    @Override
    public void makeFile(XSSFWorkbook workbook) {
        FileUtils.makeDir(path);

        String realFilePath = path + "schedule_" + HsmoaScheduleCrawler.date + ".xlsx";
        File file = new File(realFilePath);
        FileOutputStream fos = null;

        try {
          fos = new FileOutputStream(file);
          workbook.write(fos);

          logger.info("Success Make File -> " + realFilePath);

        } catch (FileNotFoundException e) {
            logger.error("ERROR!!", e);

        } catch (IOException e) {
            logger.error("ERROR!!", e);

        } finally {
            try {
                if(workbook!=null) workbook.close();
                if(fos!=null) fos.close();

            } catch (IOException e) {
                logger.error("ERROR!!", e);

            }
        }
    }
}
