package com.sourcecode.translator.service.impl;

import com.sourcecode.translator.googleclient.GoogleAPIClient;
import com.sourcecode.translator.service.MSOfficeTranslatorService;
import com.sourcecode.translator.utils.Utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MSOfficeTranslatorServiceImpl
  implements MSOfficeTranslatorService
{
  @Autowired
  GoogleAPIClient googleApiClient;
  static Logger log = LoggerFactory.getLogger(MSOfficeTranslatorServiceImpl.class);
  
  public File processXLSFile(MultipartFile file, String sourceLan, String targetLan)
    throws Exception
  {
    HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
    return processExcelFile(file, workbook, sourceLan, targetLan);
  }
  
  public File processXLSXFile(MultipartFile file, String sourceLan, String targetLan)
    throws Exception
  {
    XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
    return processExcelFile(file, workbook, sourceLan, targetLan);
  }
  
  public File processExcelFile(MultipartFile file, Workbook workbook, String sourceLan, String targetLan)
    throws Exception
  {
    String transFileName = Utils.getTranslatedFileName(file);
    File translatedFile = new File(transFileName);
    log.info("Beginning translation.");
    int noOfSheets = workbook.getNumberOfSheets();
    for (int i = 0; i < noOfSheets; i++)
    {
      Sheet sheet = workbook.getSheetAt(i);
      String sheetName = sheet.getSheetName();
      
      Iterator<Row> rowIterator = sheet.rowIterator();
      while (rowIterator.hasNext())
      {
        Row row = (Row)rowIterator.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext())
        {
          Cell cell = (Cell)cellIterator.next();
          if (cell.getCellType() == 1)
          {
            String text = cell.getStringCellValue();
            String translatedtxt = this.googleApiClient.simpleTranslate(text, sourceLan, targetLan);
            cell.setCellValue(translatedtxt);
          }
        }
      }
      String translatedName = this.googleApiClient.simpleTranslate(sheetName, sourceLan, targetLan);
      workbook.setSheetName(i, translatedName);
    }
    log.info("Translation complete. Writing into file.");
    workbook.write(new FileOutputStream(translatedFile));
    log.info("File written to memory.");
    return translatedFile;
  }
  
  public File processDocFile(MultipartFile file, String sourceLan, String targetLan)
    throws Exception
  {
    String transFileName = Utils.getTranslatedFileName(file);
    File translatedFile = new File(transFileName);
    log.info("Beginning translation.");
    HWPFDocument wordFile = new HWPFDocument(file.getInputStream());
    Range range = wordFile.getRange();
    for (int i = 0; i < range.numParagraphs(); i++)
    {
      Paragraph para = range.getParagraph(i);
      for (int j = 0; j < para.numCharacterRuns(); j++)
      {
        CharacterRun charRun = para.getCharacterRun(j);
        String oldtext = charRun.text();
        String tempString = oldtext;
        if ((tempString.length() != 1) || 
          (tempString.matches("/^[A-z]+$/")))
        {
          if ((tempString != null) && (tempString.contains(".")) && (!tempString.equalsIgnoreCase("")) && (!tempString.equalsIgnoreCase(" ")))
          {
            String[] textStrings = tempString.split("\\.");
            for (int k = 0; k < textStrings.length; k++)
            {
              String translated = this.googleApiClient.simpleTranslate(textStrings[k], sourceLan, targetLan);
              tempString = tempString.replace(textStrings[k], translated);
            }
          }
          else if ((tempString != null) && (!tempString.equalsIgnoreCase("")) && (!tempString.equalsIgnoreCase(" ")))
          {
            String translated = this.googleApiClient.simpleTranslate(tempString, sourceLan, targetLan);
            tempString = tempString.replace(tempString, translated);
          }
          charRun.insertAfter(tempString);
        }
      }
    }
    log.info("Translation complete. Writing into file.");
    wordFile.write(new FileOutputStream(translatedFile));
    log.info("File written to memory.");
    return translatedFile;
  }
  
  public File processDocFilesText(MultipartFile file, String sourceLan, String targetLan)
    throws Exception
  {
    String transFileName = Utils.getTranslatedFileName(file, "rtf");
    File translatedFile = new File(transFileName);
    BufferedWriter writer = new BufferedWriter(new FileWriter(translatedFile.getName()));
    
    HWPFDocument wordFile = new HWPFDocument(file.getInputStream());
    Range range = wordFile.getRange();
    String text = range.text();
    if (text.contains("."))
    {
      String[] textStrings = text.split("\\.");
      for (int j = 0; j < textStrings.length; j++)
      {
        String translated = this.googleApiClient.simpleTranslate(textStrings[j], sourceLan, targetLan);
        text = text.replace(textStrings[j], translated);
      }
    }
    else
    {
      String translated = this.googleApiClient.simpleTranslate(text, sourceLan, targetLan);
      text = text.replace(text, translated);
    }
    writer.write(text);
    writer.newLine();
    writer.flush();
    writer.close();
    return translatedFile;
  }
  
  public File processDocxFile(MultipartFile file, String sourceLan, String targetLan)
    throws Exception
  {
    String transFileName = Utils.getTranslatedFileName(file);
    File translatedFile = new File(transFileName);
    log.info("Beginning translation.");
    XWPFDocument wordFile = new XWPFDocument(file.getInputStream());
    for (XWPFTable tbl : wordFile.getTables()) {
      for (XWPFTableRow row : tbl.getRows()) {
        for (XWPFTableCell cell : row.getTableCells()) {
          for (XWPFParagraph p : cell.getParagraphs()) {
            for (XWPFRun r : p.getRuns())
            {
              String text = r.getText(0);
              if ((text != null) && (text.contains(".")))
              {
                String[] textStrings = text.split("\\.");
                for (int j = 0; j < textStrings.length; j++)
                {
                  String translated = this.googleApiClient.simpleTranslate(textStrings[j], sourceLan, targetLan);
                  text = text.replace(textStrings[j], translated);
                  r.addBreak(BreakType.TEXT_WRAPPING);
                  r.setText(text, 0);
                }
              }
              else if ((text != null) && (!text.equalsIgnoreCase("")) && (!text.equalsIgnoreCase(" ")))
              {
                String translated = this.googleApiClient.simpleTranslate(text, sourceLan, targetLan);
                text = text.replace(text, translated);
                r.addBreak(BreakType.TEXT_WRAPPING);
                r.setText(text, 0);
              }
            }
          }
        }
      }
    }
    log.info("Translation complete. Writing into file.");
    wordFile.write(new FileOutputStream(translatedFile));
    log.info("File written to memory.");
    return translatedFile;
  }
}
