package com.sourcecode.translator.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StaticInformationLoad
{
  private Map<String, String> languagesMap = new HashMap();
  @Autowired
  Environment env;
  
  @PostConstruct
  public void init()
  {
    loadLanguages();
    checkTranslatedFolder();
  }
  
  public void checkTranslatedFolder()
  {
    File folder = new File("translated");
    if (!folder.exists()) {
      folder.mkdirs();
    }
  }
  
  public void loadLanguages()
  {
    String languages = this.env.getProperty(Constants.GOOGLE_LANGUAGES);
    String[] langs = languages.split(",", -1);
    for (int i = 0; i < langs.length; i++)
    {
      String[] splt = langs[i].split("#", -1);
      this.languagesMap.put(splt[0], splt[1]);
    }
  }
  
  public Map<String, String> getLanguagesMap()
  {
    return this.languagesMap;
  }
  
  public void setLanguagesMap(Map<String, String> languagesMap)
  {
    this.languagesMap = languagesMap;
  }
}
