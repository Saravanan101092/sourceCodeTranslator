package com.sourcecode.translator.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface MSOfficeTranslatorService {

	public File processXLSFile(MultipartFile file,String sourceLan,String targetLan) throws Exception;
	public File processXLSXFile(MultipartFile file,String sourceLan,String targetLan) throws Exception;
	public File processDocFile(MultipartFile file,String sourceLan,String targetLan) throws Exception;
	public File processDocxFile(MultipartFile file,String sourceLan,String targetLan) throws Exception;
}
