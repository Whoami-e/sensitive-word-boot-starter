package com.nmgjc.word.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.nmgjc.config.SensitiveWordProperties;
import com.nmgjc.word.constant.Constants;
import com.nmgjc.word.domain.ocr.OcrBaseVo;
import com.nmgjc.word.domain.ocr.OcrDataVo;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.List;

@Component
public class FileContentReader {

    public static final Logger log = org.slf4j.LoggerFactory.getLogger(FileContentReader.class);


    public static String readFileContent(String ext, MultipartFile file) throws Exception {
        InputStream is = file.getInputStream();
        switch (ext.toLowerCase()) {
            case "pdf":
                return readPdf(is);
            case "xlsx":
            case "xls":
                return readExcel(is, ext);
            case "txt":
            case "csv":
                return readText(is);
            case "doc":
                return readDoc(is);
            case "docx":
                return readDocx(is);
            case "jpg":
            case "png":
                return readImage(file);
            default:
                return "";
        }
    }

    private static String readImage(MultipartFile file) {
        try {
            String ocrUrl = SpringUtil.getBean(SensitiveWordProperties.class).getOcrUrl();
            if(StringUtils.isEmpty(ocrUrl)) {
                log.error("未配置 ocrUrl，故无法读取图片内容");
                return "";
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("multipart/form-data");
            httpHeaders.setContentType(mediaType);
            org.springframework.core.io.Resource resource = file.getResource();
            MultiValueMap<String, Object> mapFile = new LinkedMultiValueMap<>();

            mapFile.add("file", resource);
            mapFile.add("compress", 0);
            mapFile.add("is_draw", 0);
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(mapFile, httpHeaders);

            ResponseEntity<String> postForEntity = SpringUtil.getBean(RestTemplate.class).postForEntity(ocrUrl, httpEntity, String.class);
            String body = postForEntity.getBody();
            String s = StringEscapeUtils.unescapeJava(body);

            OcrBaseVo<OcrDataVo> ocrBaseVo = JSONObject.parseObject(s, new TypeReference<OcrBaseVo<OcrDataVo>>() {
            });

            log.info("ocr 识别结果：{}", JSONObject.toJSONString(ocrBaseVo));
            if(ocrBaseVo == null) {
                return "";
            }

            OcrDataVo data = ocrBaseVo.getData();
            List<Object> rawOut = data.getRaw_out();

            StringBuilder ocrText = new StringBuilder();
            int size = rawOut.size();
            for (Object o : rawOut) {
                String ocrRaw = JSONObject.toJSONString(o);
                JSONArray array = JSONArray.parseArray(ocrRaw);
                ocrText.append(array.get(1));
            }

            return ocrText.toString().replaceAll("\\s+", "");
        } catch (Exception e) {
            log.error("读取图片内容出错", e);
            return "";
        }
    }

    // 读取PDF内容
    private static String readPdf(InputStream is) throws Exception {
        try (PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true); // 按页面顺序读取
            StringBuilder content = new StringBuilder();

            // 逐页提取文本
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(document);
                content.append(pageText);

                // 每处理 10 页清理一次缓存（根据内存调整）
                if (page % 10 == 0) {
                    content.setLength(Math.min(content.length(), 100_000)); // 限制内存占用
                }
            }
            return content.toString().replaceAll("\\s+", "");
        } catch (Exception e) {
            log.error("读取PDF内容出错", e);
            return "";
        }
    }

    // 读取Excel内容（支持xls/xlsx）
    private static String readExcel(InputStream is, String ext){
        Workbook workbook = null;
        try {
            workbook = ext.endsWith("xlsx") ?
                new XSSFWorkbook(is) : new HSSFWorkbook(is);
            StringBuilder sb = new StringBuilder();
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        sb.append(getCellValue(cell)).append("\t");
                    }
                    sb.append("\n");
                }
            }
            workbook.close();
            return sb.toString().replaceAll("\\s+", "");
        } catch (Exception e) {
            log.error("读取Excel内容出错", e);
            return "";
        }
    }

    // 获取Excel单元格值
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }

    // 读取文本文件内容
    private static String readText(InputStream is) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, Constants.UTF8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().replaceAll("\\s+", "");
        } catch (Exception e) {
            log.error("读取文本文件内容出错", e);
            return "";
        }
    }
    // 读取 .doc 文件
    private static String readDoc(InputStream is) throws Exception {
        try (HWPFDocument doc = new HWPFDocument(is)) {
            WordExtractor extractor = new WordExtractor(doc);
            String[] paragraphs = extractor.getParagraphText();
            StringBuilder sb = new StringBuilder();
            for (String para : paragraphs) {
                sb.append(para).append("\n");
                // 类似分段清理逻辑
            }
            return sb.toString().replaceAll("\\s+", "");
        } catch (Exception e) {
            log.error("读取.doc 文件出错", e);
            return "";
        }
    }

    // 读取 .docx 文件
    private static String readDocx(InputStream is) throws Exception {
        try (XWPFDocument docx = new XWPFDocument(is)) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph para : docx.getParagraphs()) {
                sb.append(para.getText()).append("\n");

                // 每 1000 段落清理一次缓存
                if (docx.getParagraphs().indexOf(para) % 1000 == 0) {
                    sb.setLength(Math.min(sb.length(), 100_000));
                }
            }
            return sb.toString().replaceAll("\\s+", "");
        } catch (Exception e) {
            log.error("读取.docx 文件出错", e);
            return "";
        }
    }


}