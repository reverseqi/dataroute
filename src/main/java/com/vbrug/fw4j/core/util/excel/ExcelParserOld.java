package com.vbrug.fw4j.core.util.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class ExcelParserOld {

    private static final String[] fields = new String[] {"序号:seqNo", "单号:taskId", "编号:taskId", "标题:title", "时间:time", "姓名:person",
            "电话:phone", "号码:phone", "联系:phone", "内容:content", "描述:content", "类型:type"};

    private static final String[] xqdws = new String[] {"苏州市本级:苏州市本级","张家港市:张家港市", "常熟市:常熟市", "太仓市:太仓市",
            "昆山市:昆山市", "吴江区:吴江区", "吴中区:吴中区", "相城区:相城区", "姑苏区:姑苏区", "工业园区:工业园区", "高新区:高新区"};

    private static final Log logger = LogFactory.getLog(ExcelParserOld.class);

    /**
     * 解析xls文件
     * @param file excel文件
     */
    public List<Map<String, String>> parseXLS(File file) throws IOException {
        Workbook workbook = null;
        if (file.getName().endsWith(".xls"))
            workbook   = new HSSFWorkbook(new FileInputStream(file));
        else
            workbook = new XSSFWorkbook(new FileInputStream(file));
        logger.debug("======    开始解析Excel {"+ file.getName() +"}   ========");
        List<Map<String, String>> list = dealWorkbook(workbook, file.getName().split("\\.")[0]);
        logger.debug(list.size());
        return list;
    }

    private List<Map<String, String>> dealWorkbook(Workbook workbook, String dataDate){
        List<Map<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null || sheet.getLastRowNum() == 0)
                return null;
            logger.info("-------    开始解析Sheet {"+ sheet.getSheetName() +"}   --------");
            String xqdwName = this.matchXqdw(xqdws, sheet.getSheetName());
            // 处理标题
            Row colRow = sheet.getRow(1);
            String columnNames = "";
            for(int j = 0; j< colRow.getLastCellNum(); j++){
                String value = Optional.ofNullable(colRow.getCell(j)).map(Cell::getStringCellValue).orElse("");
                columnNames += value + ",";
            }
            List<String> fieldList = getFields(columnNames);

            // 处理内容
            for (int k = 2; k < sheet.getPhysicalNumberOfRows(); k ++){
                Map<String, String> loopMap = new HashMap<>();
                loopMap.put("xqdwName", xqdwName);
                loopMap.put("dataDate", dataDate);
                Row row = sheet.getRow(k);
                if (row == null || row.getLastCellNum() == 0)
                    continue;
                for(int p = 0; p < fieldList.size() && p < row.getLastCellNum(); p++){
                    Cell cell = row.getCell(p);
                    if (ObjectUtils.isEmpty(fieldList.get(p)) || cell == null)
                        continue ;

                    String value = "";
                    if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.BLANK
                            || cell.getCellType() == CellType.BOOLEAN || cell.getCellType() == CellType.ERROR
                            || cell.getCellType() == CellType.FORMULA) {
                        cell.setCellType(CellType.STRING);
                        value = cell.getStringCellValue();
                    } else if(cell.getCellType()==CellType.NUMERIC){
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date tempValue = cell.getDateCellValue();
                            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            value = simpleFormat.format(tempValue);
                        }else {
                            cell.setCellType(CellType.STRING);
                            value = cell.getStringCellValue();
                        }
                    } else {
                        value = this.parseCellTime(cell);
                    }
                    loopMap.put(fieldList.get(p), value);
                }
                if (ObjectUtils.isEmpty(loopMap.get("taskId")))
                    continue;
                dataList.add(loopMap);
            }
            logger.info(dataList.get(dataList.size() - 1));
        }
        return dataList;
    }

    private String parseCellTime(Cell cell){
        if (cell.getDateCellValue() != null)
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
        return null;
    }


    private List<String> getFields(String columnNames){
        if (ObjectUtils.isEmpty(columnNames))
            throw  new IllegalArgumentException("columnNames is null!!!");

        List<String> fieldList = new ArrayList<>();
        for (String s : columnNames.split(",")) {
            fieldList.add(this.matchField(fields, s));
        }
        return fieldList;
    }

    private String filterString(String string){
        return ObjectUtils.isEmpty(string) ? "" : string.replaceAll("( | |)", "");
    }

    private String matchXqdw(String[] sourceKeys, String name){
        String key = Stream.of(sourceKeys).filter(x -> x.contains(name.replaceAll("( | |苏州|区|诉求|统计|表)", ""))).findFirst().orElse(null);
        return ObjectUtils.isEmpty(key) ? null : key.split(":")[1];
    }

    private String matchField(String[] sourceKeys, String name){
        String key = Stream.of(sourceKeys).filter(x -> name.replaceAll("( | )", "").contains(x.split(":")[0])).findFirst().orElse(null);
        return ObjectUtils.isEmpty(key) ? null : key.split(":")[1];
    }

}
