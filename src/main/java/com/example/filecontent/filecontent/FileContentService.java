package com.example.filecontent.filecontent;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public interface FileContentService {
    void saveFileContent(MultipartFile multipartFile);

    void deleteFileContentRecord(String id);

    Optional<FileContent> getFileContentRecordById(String id);
}
