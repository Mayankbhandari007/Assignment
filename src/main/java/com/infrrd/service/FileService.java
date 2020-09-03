package com.infrrd.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.infrrd.config.FileProperties;
import com.infrrd.controller.FileController;
import com.infrrd.exception.FileNotFoundException;
import com.infrrd.exception.FileStorageException;
import com.infrrd.response.Response;

@Service
public class FileService {

	private static Logger logger = LoggerFactory.getLogger(FileService.class);

	    private final Path fileStorageLocation;

	    @Autowired
	    public FileService(FileProperties fileStorageProperties) {
	        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
	            .toAbsolutePath().normalize();

	        try {
	            Files.createDirectories(this.fileStorageLocation);
	        } catch (Exception ex) {
	            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
	        }
	    }

	    
	    public Resource loadFileAsResource(String fileName) {
	        try {
	            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
	            Resource resource = new UrlResource(filePath.toUri());
	            if (resource.exists()) {
	                return resource;
	            } else {
	                throw new FileNotFoundException("File not found " + fileName);
	            }
	        } catch (MalformedURLException ex) {
	            throw new FileNotFoundException("File not found " + fileName, ex);
	        }
	    }


		public Response deleteFile(String fileName) {
			Response response = new Response();
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Path fileToDeletePath = Paths.get(filePath.toUri());
			    try {
					Files.delete(fileToDeletePath);
					response.setFileName(fileToDeletePath.getFileName().toString());
					response.setStatus(HttpStatus.OK.toString());
					response.setResponseMessage("DELETED SUCCESS");
					logger.info("Deleted SuccesFully "+fileToDeletePath.getFileName());
				} catch (IOException e) {
					e.printStackTrace();
					response.setFileName(fileToDeletePath.getFileName().toString());
					response.setStatus(HttpStatus.BAD_REQUEST.toString());
					response.setResponseMessage("DELETED FAILED "+ e.getLocalizedMessage());
				
				}
				return response;
			
		}


		public Response createFile(String fileName) {
			Response response = new Response();
			
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Path copyFilePath = this.fileStorageLocation.resolve("copyFile.pdf").normalize();
			
			Path path = Paths.get(filePath.toUri());
			Path tempPath = Paths.get(copyFilePath.toUri());
			
			File file = new File(path.toUri());
			File copyFile = new File(tempPath.toUri());
			try {
			    	Files.copy(file.toPath(), copyFile.toPath());
					
			    	response.setFileName(copyFilePath.getFileName().toString());
					response.setStatus(HttpStatus.OK.toString());
					response.setResponseMessage("COPIED SUCCESS");
					logger.info("COPIED SuccesFully "+copyFilePath.getFileName());
				} catch (IOException e) {
					e.printStackTrace();
					response.setFileName(copyFilePath.getFileName().toString());
					response.setStatus(HttpStatus.BAD_REQUEST.toString());
					response.setResponseMessage("COPIED FAILED "+ e.getLocalizedMessage());
				
				}
				return response;
			
		}
	}
	

