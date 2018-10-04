package com.sourcecode.translator;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.MultipartConfigElement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@SpringBootApplication
public class SourceCodeTranslatorApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(SourceCodeTranslatorApplication.class, args);
//    System.setProperty("java.net.useSystemProxies", "true");
//    System.setProperty("java.awt.headless", "false");
//    System.out.println("Starting the browser.." + Desktop.isDesktopSupported());
//    if (Desktop.isDesktopSupported()) {
//      try
//      {
//        Desktop.getDesktop().browse(new URI("http://localhost:8500/codetranslator/"));
//      }
//      catch (IOException|URISyntaxException e)
//      {
//        e.printStackTrace();
//      }
//    }
  }
  
  @Bean(name={"commonsMultipartResolver"})
  public MultipartResolver multipartResolver()
  {
    return new StandardServletMultipartResolver();
  }
  
  @Bean
  public MultipartConfigElement multipartConfigElement()
  {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    
    factory.setMaxFileSize("10MB");
    factory.setMaxRequestSize("10MB");
    
    return factory.createMultipartConfig();
  }
}
