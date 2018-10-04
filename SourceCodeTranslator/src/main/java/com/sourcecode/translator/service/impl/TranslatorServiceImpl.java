package com.sourcecode.translator.service.impl;

import com.sourcecode.translator.googleclient.GoogleAPIClient;
import com.sourcecode.translator.service.MSOfficeTranslatorService;
import com.sourcecode.translator.service.OpenOfficeTranslatorService;
import com.sourcecode.translator.service.TranslatorService;
import com.sourcecode.translator.utils.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TranslatorServiceImpl
  implements TranslatorService
{
  @Autowired
  GoogleAPIClient apiClient;
  @Autowired
  OpenOfficeTranslatorService openOfficeTranslator;
  @Autowired
  MSOfficeTranslatorService msOfficeTranslator;
  static Logger log = LoggerFactory.getLogger(TranslatorServiceImpl.class);
  
  public static List<String> fetchKeywords(String keyword)
  {
    boolean onlycaps = true;
    List<String> texts = new ArrayList();
    int i = 1;
    int start = 0;
    if (Character.isLowerCase(keyword.charAt(0))) {
      onlycaps = false;
    }
    while (i < keyword.length())
    {
      char c = keyword.charAt(i);
      if (Character.isUpperCase(c))
      {
        String thisKeyword = keyword.substring(start, i);
        texts.add(thisKeyword);
        start = i;
      }
      else
      {
        onlycaps = false;
      }
      i++;
    }
    String thisKeyword = keyword.substring(start);
    texts.add(thisKeyword);
    if (onlycaps)
    {
      texts.clear();
      texts.add(keyword);
    }
    return texts;
  }
  
  public File processAsSourceFile(MultipartFile file, String sourceLan, String targetLan, Boolean isPlainTxt)
    throws Exception
  {
    String transFileName = Utils.getTranslatedFileName(file);
    File translatedFile = new File(transFileName);
    log.info("Translated file info: " + translatedFile.getAbsolutePath());
    BufferedWriter writer = new BufferedWriter(new FileWriter(translatedFile));
    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String line = "";
      log.info("Beginning translation");
      while ((line = reader.readLine()) != null)
      {
        String tempString = replaceSymbolsWithSpaces(line);
        String[] words = tempString.split(" ");
        words = removeEmptyStringsFromArray(words);
        for (int i = 0; i < words.length; i++)
        {
          List<String> keywords = fetchKeywords(words[i]);
          if (keywords.size() > 1) {
            words[i] = StringUtils.collectionToCommaDelimitedString(keywords);
          }
        }
        Map<String, String> translatedWords = this.apiClient.translate(words, sourceLan, targetLan);
        if (!CollectionUtils.isEmpty(translatedWords)) {
          for (String orgWord : translatedWords.keySet()) {
            line = line.replaceAll(orgWord, (String)translatedWords.get(orgWord));
          }
        }
        writer.write(line);
        writer.newLine();
        writer.flush();
      }
      log.info("Translation completed.");
      return translatedFile;
    }
    catch (IOException e)
    {
      File localFile1;
      log.error("Exception while translation:" + e.getMessage());
      return translatedFile;
    }
    finally
    {
      writer.close();
    }
  }
  
  public File processAsPlainFile(MultipartFile file, String sourceLan, String targetLan, Boolean isPlainTxt)
    throws Exception
  {
    String transFileName = Utils.getTranslatedFileName(file);
    File translatedFile = new File(transFileName);
    log.info("Translated file info: " + translatedFile.getAbsolutePath());
    BufferedWriter writer = new BufferedWriter(new FileWriter(translatedFile));
    try
    {
      log.info("Beginning translation");
      BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String line = "";
      while ((line = reader.readLine()) != null)
      {
        String translatedLine = this.apiClient.simpleTranslate(line, sourceLan, targetLan);
        writer.write(translatedLine);
        writer.newLine();
        writer.flush();
      }
      log.info("Translation completed.");
      return translatedFile;
    }
    catch (IOException e)
    {
      File localFile1;
      e.printStackTrace();
      log.error("Exception while translation:" + e.getMessage());
      return translatedFile;
    }
    finally
    {
      writer.close();
    }
  }
  
  public File processPlainTxtFile(MultipartFile file, String sourceLan, String targetLan, Boolean isPlainTxt)
    throws Exception
  {
    if (isPlainTxt.booleanValue()) {
      return processAsPlainFile(file, sourceLan, targetLan, isPlainTxt);
    }
    return processAsSourceFile(file, sourceLan, targetLan, isPlainTxt);
  }
  
  public File processFile(MultipartFile file, String extension, String srcLanguage, String tgtLanguage, Boolean plainTxtFlag)
    throws Exception
  {
    String str;
    File result = null;
    
    switch (extension)
    {
    case "cs":
    case "cpp":
    case "sql": 
    case "java": 
    	result = processPlainTxtFile(file, srcLanguage, tgtLanguage, plainTxtFlag);
    	break;
    case "doc": 
    	result = msOfficeTranslator.processDocFile(file, srcLanguage, tgtLanguage);
    	break;
    case "ods": 
    	result = openOfficeTranslator.translateSpreadSheet(file, srcLanguage, tgtLanguage);
    	break;
    case "odt": 
    	result = openOfficeTranslator.translateDocFile(file, srcLanguage, tgtLanguage);
      break;
    case "xls": 
      result = msOfficeTranslator.processXLSFile(file, srcLanguage, tgtLanguage);
      break;
    case "docx": 
    	result = msOfficeTranslator.processDocxFile(file, srcLanguage, tgtLanguage);
      break;
    case "xlsx": 
        result = msOfficeTranslator.processXLSXFile(file, srcLanguage, tgtLanguage);
      break;
    }
    return result;
  }
  
  public static String replaceSymbolsWithSpaces(String line)
  {
    String temp = line.replaceAll("[^a-zA-Z0-9]", " ");
    return temp;
  }
  
  public static String[] removeEmptyStringsFromArray(String[] arr)
  {
    List<String> list = new ArrayList();
    for (int i = 0; i < arr.length; i++) {
      if (!arr[i].equalsIgnoreCase("")) {
        list.add(arr[i]);
      }
    }
    return (String[])list.toArray(new String[0]);
  }
}
