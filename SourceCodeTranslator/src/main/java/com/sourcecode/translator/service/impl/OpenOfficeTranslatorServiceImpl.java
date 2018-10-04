package com.sourcecode.translator.service.impl;

import com.sourcecode.translator.googleclient.GoogleAPIClient;
import com.sourcecode.translator.service.OpenOfficeTranslatorService;
import com.sourcecode.translator.utils.Utils;
import java.io.File;
import java.io.IOException;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service
public class OpenOfficeTranslatorServiceImpl
  implements OpenOfficeTranslatorService
{
  @Autowired
  GoogleAPIClient googleAPIClient;
  static Logger log = LoggerFactory.getLogger(OpenOfficeTranslatorServiceImpl.class);
  
  public File translateSpreadSheet(MultipartFile file, String sourceLan, String targetLan)
    throws IOException, Exception
  {
    String transFileName = Utils.getTranslatedFileName(file);
    File translatedFile = new File(transFileName);
    
    OdfSpreadsheetDocument odfSpreadSheetDoc = OdfSpreadsheetDocument.loadDocument(file.getInputStream());
    OfficeSpreadsheetElement rootElement = odfSpreadSheetDoc.getContentRoot();
    NodeList sheetList = rootElement.getChildNodes();
    log.info("Beginning translation.");
    for (int i = 0; i < sheetList.getLength(); i++)
    {
      Node sheet = sheetList.item(i);
      Node sheetNameNode = sheet.getAttributes().item(0);
     try {
      String sheetNameTranslated = this.googleAPIClient.simpleTranslate(sheetNameNode.getNodeValue(), sourceLan, targetLan);
      sheetNameNode.setNodeValue(sheetNameTranslated);
    }
    catch (Exception e)
    {
      log.error("Error during translation:"+sheetNameNode.getNodeValue()+" Error:" + e.getMessage());
    }
      
      recursiveProcess(sheet, sourceLan, targetLan);
    }
    log.info("Translation completed. Writing into file.");
    odfSpreadSheetDoc.save(translatedFile);
    log.info("Translated file is writted in to directory");
    return translatedFile;
  }
  
  public void recursiveProcess(Node node, String source, String target)
  {
    if (node.getNodeType() == 3)
    {
      String text = node.getNodeValue();
      try
      {
        String translatedText = this.googleAPIClient.simpleTranslate(text, source, target);
        node.setNodeValue(translatedText);
      }
      catch (Exception e)
      {
        log.error("Error during translation:"+text+" Error:" + e.getMessage());
      }
      return;
    }
    if (node.hasChildNodes())
    {
      NodeList childNodes = node.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++)
      {
        Node childNode = childNodes.item(i);
        recursiveProcess(childNode, source, target);
      }
    }
    else {}
  }
  
  public File translateDocFile(MultipartFile file, String sourceLan, String targetLan)
    throws Exception
  {
    String transFileName = Utils.getTranslatedFileName(file);
    File translatedFile = new File(transFileName);
    
    OdfTextDocument odfTextSheetDoc = OdfTextDocument.loadDocument(file.getInputStream());
    OfficeTextElement rootElement = odfTextSheetDoc.getContentRoot();
    NodeList sheetList = rootElement.getChildNodes();
    log.info("Beginning translation.");
    for (int i = 0; i < sheetList.getLength(); i++)
    {
      Node sheet = sheetList.item(i);
      recursiveProcess(sheet, sourceLan, targetLan);
    }
    log.info("Translation completed. Writing into file.");
    odfTextSheetDoc.save(translatedFile);
    log.info("Translated file is writted in to directory");
    return translatedFile;
  }
}
