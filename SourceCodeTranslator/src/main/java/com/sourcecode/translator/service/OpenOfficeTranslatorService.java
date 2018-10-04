package com.sourcecode.translator.service;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface OpenOfficeTranslatorService {

	public File translateSpreadSheet(MultipartFile file,String sourceLan,String targetLan) throws IOException, Exception;
	public File translateDocFile(MultipartFile file,String sourceLan,String targetLan) throws Exception;
}
