package com.sourcecode.translator.service.impl;

import com.sourcecode.translator.service.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackServiceImpl
  implements SlackService
{
  static Logger log = LoggerFactory.getLogger(SlackServiceImpl.class);
  
  public void sendMessage(String message)
  {
    RestTemplate restTemplate = new RestTemplate();
    String url = "https://hooks.slack.com/services/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";
    SlackDTO slackDTO = new SlackDTO();
    slackDTO.setText(message);
    ResponseEntity<String> response = restTemplate.postForEntity(url, slackDTO, String.class, new Object[0]);
    log.info("Message sent successfully. Respnse:" + (String)response.getBody());
  }
  
  class SlackDTO
  {
    private String text;
    
    SlackDTO() {}
    
    public String getText()
    {
      return this.text;
    }
    
    public void setText(String text)
    {
      this.text = text;
    }
  }
}
