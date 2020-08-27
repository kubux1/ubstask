package com.example.filecontent.filecontent;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static com.example.filecontent.constant.Constant.*;

@Service
@NoArgsConstructor
public class FileContentServiceImpl implements FileContentService {

    private FileContentRepository fileContentRepository;

    @Autowired
    FileContentServiceImpl(FileContentRepository fileContentRepository) {
        this.fileContentRepository = fileContentRepository;
    }

    public void saveFileContent(MultipartFile file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            // Skip header
            reader.readLine();
            while (reader.ready()) {
                String[] fileRow = reader.readLine().split(FILE_CONTENT_ROW_PARAMS_SPLITTER);
                fileContentRepository.save(parse(fileRow));
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException(FILE_PROCESSING_EXCEPTION_MSG, ex);
        }
    }

    private FileContent parse(String[] fileRow) {
        String primaryKey;
        String name;
        String description;
        Instant instant;

        try {
            primaryKey = fileRow[0].trim();
            if (primaryKey.isEmpty()) {
                throw new IllegalArgumentException(PRIMARY_KEY_EMPTY_EXCEPTION_MSG);
            }

            name = fileRow[1].trim();
            description = fileRow[2].trim();

            try {
                instant = Instant.parse(fileRow[3].trim());
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException(TIMESTAMP_FORMAT_WRONG_EXCEPTION_MSG + ex);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException(MISSING_PARAMTER_IN_FILE_EXCPETION_MSG + ex);
        }

        return new FileContent(primaryKey, name, description, instant);
    }

    public void deleteFileContentRecord(String id) {
        fileContentRepository.deleteById(id);
    }

    public Optional<FileContent> getFileContentRecordById(String id) {
        return fileContentRepository.findById(id);
    }
}
