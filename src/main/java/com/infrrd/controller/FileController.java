package com.infrrd.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infrrd.response.Response;
import com.infrrd.service.FileService;



@CrossOrigin
@RestController
public class FileController {


	@Autowired
	FileService service;
	
	private static Logger logger = LoggerFactory.getLogger(FileController.class);


	@GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity < Resource > downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = service.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
	
	@GetMapping("/deleteFile/{fileName}")
    public ResponseEntity <Response> deleteFile(@PathVariable String fileName, HttpServletRequest request) {
        Response response = service.deleteFile(fileName);
           if(response.getStatus().equalsIgnoreCase(HttpStatus.OK.toString()))
        {
 return new ResponseEntity<Response>(response,HttpStatus.OK);       	
        }
        return new ResponseEntity<Response>(response,HttpStatus.BAD_REQUEST);   
    }
	
	@GetMapping("/createFile/{fileName}")
    public ResponseEntity <Response> createFile(@PathVariable String fileName, HttpServletRequest request) {
        Response response = service.createFile(fileName);
           if(response.getStatus().equalsIgnoreCase(HttpStatus.OK.toString()))
        {
 return new ResponseEntity<Response>(response,HttpStatus.OK);       	
        }
        return new ResponseEntity<Response>(response,HttpStatus.BAD_REQUEST);   
    }	
	

	
	
}
	
	
	



