package com.sourcecode.translator.rest;

import com.sourcecode.translator.service.SlackService;
import com.sourcecode.translator.utils.Constants;
import com.sourcecode.translator.utils.StaticInformationLoad;
import com.sourcecode.translator.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UtilResources {

	@Autowired
	StaticInformationLoad info;
	
	@Autowired
	Environment env;
	
	@Autowired
	SlackService slackService;
	
	@RequestMapping(path="/languages", method=RequestMethod.GET)
	public Set<String> getLanguages(){
		return info.getLanguagesMap().keySet();
	}
	@RequestMapping(path="/supportedtypes", method=RequestMethod.GET)
	public List<String> getSupportedTypes(){
		String types = env.getProperty(Constants.SUPPORTED_TYPES);
		String[] typSplt = types.split(",", -1);
		return Arrays.asList(typSplt);
	}
	

	  @RequestMapping(path={"/translatedfiles"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public List<String> getTranslatedFiles()
	  {
	    File folder = new File("translated");
	    File[] files = folder.listFiles();
	    List<String> fileNames = new ArrayList();
	    for (int i = 0; i < files.length; i++) {
	      if (files[i].isFile()) {
	        fileNames.add(files[i].getName());
	      }
	    }
	    return fileNames;
	  }
	  
	  @RequestMapping(path={"/slack"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public void sendMessage(@RequestParam String message)
	  {
	    this.slackService.sendMessage(message);
	  }
	  
	  @RequestMapping(path={"/deleteTrndFiles"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String deleteFiles() {
		  return Utils.removeFiles();
	  }
}
