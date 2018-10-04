package com.sourcecode.translator.service;

import java.io.File;
import java.net.URISyntaxException;

import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

public interface TranslatorService {

	public File processFile(MultipartFile file, String extension,String srcLanguage,String tgtLanguage,Boolean plainTxtFlag) throws RestClientException, URISyntaxException, Exception;
}
