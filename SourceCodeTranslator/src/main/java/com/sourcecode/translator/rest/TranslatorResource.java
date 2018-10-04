package com.sourcecode.translator.rest;
import com.sourcecode.translator.service.TranslatorService;
import com.sourcecode.translator.utils.StaticInformationLoad;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sourcecode.translator.service.TranslatorService;
import com.sourcecode.translator.utils.StaticInformationLoad;

@RestController
@RequestMapping("/api")
public class TranslatorResource {


	  static Logger log = LoggerFactory.getLogger(TranslatorResource.class);
	  @Autowired
	  TranslatorService translatorService;
	  @Autowired
	  StaticInformationLoad info;
	  
	  class FileNameDTO
	  {
	    String name;
	    
	    FileNameDTO() {}
	    
	    public String getName()
	    {
	      return this.name;
	    }
	    
	    public void setName(String name)
	    {
	      this.name = name;
	    }
	  }
	  
	  @RequestMapping(path={"/file/{extension}"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, consumes={"multipart/form-data"})
	  public FileNameDTO processFile(@RequestParam String sourceLanguage, @RequestParam String targetLanguage, @RequestParam Boolean plainTxtFlag, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes, @PathVariable String extension, HttpServletResponse response)
	    throws Exception
	  {
	    String srcLan = (String)this.info.getLanguagesMap().get(sourceLanguage);
	    String tarLan = (String)this.info.getLanguagesMap().get(targetLanguage);
	    log.info("processing file:" + file.getOriginalFilename());
	    File outputFile = this.translatorService.processFile(file, extension, srcLan, tarLan, plainTxtFlag);
	    FileNameDTO nameDTO = new FileNameDTO();
	    nameDTO.setName(outputFile.getName());
	    return nameDTO;
	  }
	  
	  @RequestMapping(path={"/download/file"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public ResponseEntity<InputStreamResource> fileDownload(@RequestParam String fileName)
	    throws FileNotFoundException
	  {
	    File outputFile = new File("translated" + File.separator + fileName);
	    InputStream inputStream = new BufferedInputStream(new FileInputStream(outputFile));
	    log.info("Downloding file:" + fileName);
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Description", "File Transfer");
	    headers.add("Content-Disposition", "attachment; filename=" + outputFile.getName());
	    headers.add("Content-Transfer-Encoding", "binary");
	    headers.add("Connection", "Keep-Alive");
	    
	    InputStreamResource isr = new InputStreamResource(new FileInputStream(outputFile));
	    return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().contentLength(outputFile.length()).headers(headers)).body(isr);
	  }
	  
	  @RequestMapping(path={"/heartbeat"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String heartbeat()
	  {
	    return "UP";
	  }
}
