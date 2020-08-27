package com.example.filecontent.filecontent;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.filecontent.constant.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileContentController {

    private FileContentService service;

    @Autowired
    public FileContentController(FileContentService fileContentServiceImpl) {
        this.service = fileContentServiceImpl;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String handleFileUpload(@RequestParam(FILE_PARAM_NAME) MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_FILE_EXCEPTION_MSG);
        }

        service.saveFileContent(file);

        return FILE_POST_SUCCESSFUL_RESPONSE_MSG;
    }

    @GetMapping(path = "/")
    @ResponseBody
    public FileContent getSingleRecord(@RequestParam(ID_PARAM_NAME) String id) {

        if (id.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_REQ_PARAM_EXCEPTION_MSG);
        }

        return service.getFileContentRecordById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String deleteSingleRecord(@RequestParam(ID_PARAM_NAME) String id) {

        if (id.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_REQ_PARAM_EXCEPTION_MSG);
        }

        service.deleteFileContentRecord(id);

        return FILE_SINGLE_RECORD_DELETE_SUCCESSFUL_RESPONSE_MSG;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEmptyParam(IllegalArgumentException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoItemFound(EmptyResultDataAccessException ex) {
        return ex.getMessage();
    }
}
