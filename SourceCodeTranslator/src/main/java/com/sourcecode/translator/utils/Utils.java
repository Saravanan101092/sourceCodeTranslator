package com.sourcecode.translator.utils;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class Utils
{
  public static final String OUTPUT_FOLDER = "translated";
  static Logger log = LoggerFactory.getLogger(Utils.class);
  
  public static String getTranslatedFileName(MultipartFile file)
  {
    String temp = StringUtils.stripAccents(file.getOriginalFilename());
    if (temp.contains(File.separator))
    {
      File tempFile = new File(temp);
      temp = tempFile.getName();
      if (tempFile.exists()) {
        tempFile.delete();
      }
    }
    String[] transFileNames = temp.split("\\.", -1);
    int length = transFileNames.length;
    String outputFileName = OUTPUT_FOLDER + File.separator + transFileNames[(length - 2)] + "_translated." + transFileNames[(length - 1)];
    log.info("Output file Name is :" + outputFileName);
    return outputFileName;
  }
  
  public static String getTranslatedFileName(MultipartFile file, String extension)
  {
    String temp = StringUtils.stripAccents(file.getOriginalFilename());
    String[] transFileNames = temp.split("\\.", -1);
    return OUTPUT_FOLDER + File.separator + transFileNames[0] + "_translated." + extension;
  }
  
  public static String removeFiles() {
	  File folder = new File(OUTPUT_FOLDER);
	  File[] files = folder.listFiles();
	  for(File file :files) {
		  file.delete();
	  }
	  return "deleted "+files.length+" files";
  }
}
