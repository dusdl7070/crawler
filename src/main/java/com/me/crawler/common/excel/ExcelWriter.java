package com.me.crawler.common.excel;

import com.me.crawler.web_crawler.hsmoa.vo.HsmoaMain;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface ExcelWriter {
    void xlsxWrite();
    void setHeader(XSSFSheet sheet);
    void setRow(XSSFSheet sheet, List<HsmoaMain> list);
    void makeFile(XSSFWorkbook workbook);
}
