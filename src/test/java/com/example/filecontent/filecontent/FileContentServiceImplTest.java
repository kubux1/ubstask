package com.example.filecontent.filecontent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FileContentServiceImpl.class})
public class FileContentServiceImplTest {

    private final FileContent testFileContent = new FileContent("1", "TEST", "TEST", Instant.parse
            ("2017-02-03T10:37:30Z"));

    @Autowired
    private FileContentService fileContentServiceImpl;

    @MockBean
    private FileContentRepository repository;

    @Test
    public void whenFindByIdThenExpectReturnFileContent() {
        // given
        Mockito.when(repository.findById(testFileContent.getId()))
                .thenReturn(Optional.of(testFileContent));

        // when
        Optional<FileContent> fileContentExpected = fileContentServiceImpl.getFileContentRecordById(testFileContent
                .getId());

        // then
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.repository).findById(argument.capture());
        Assertions.assertEquals(testFileContent.getId(), argument.getValue());

        Assertions.assertTrue(fileContentExpected.isPresent());
        Assertions.assertEquals(testFileContent, fileContentExpected.get());
    }

    @Test
    public void whenSaveValidFileExpectIsCreated() throws IOException {
        // given
        Path path = Paths.get("src/test/resources/validTestFile.txt");
        byte[] content = Files.readAllBytes(path);
        MultipartFile testFile = new MockMultipartFile("testFile", "test", "text/plain", content);

        // when
        fileContentServiceImpl.saveFileContent(testFile);

        // then
        ArgumentCaptor<FileContent> argument = ArgumentCaptor.forClass(FileContent.class);
        Mockito.verify(this.repository).save(argument.capture());
        Assertions.assertEquals(testFileContent, argument.getValue());
    }

    @Test
    public void whenSaveFileWithInvalidTimestampExpectIllegalArgumentException() throws IOException {
        // given
        Path path = Paths.get("src/test/resources/invalidTimestampTestFile.txt");
        byte[] content = Files.readAllBytes(path);
        MultipartFile testFile = new MockMultipartFile("testFile", "test", "text/plain", content);

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileContentServiceImpl.saveFileContent(testFile));
    }

    @Test
    public void whenSaveFileWithBlankIdExpectIllegalArgumentException() throws IOException {
        // given
        Path path = Paths.get("src/test/resources/blankIdTestFile.txt");
        byte[] content = Files.readAllBytes(path);
        MultipartFile testFile = new MockMultipartFile("testFile", "test", "text/plain", content);

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileContentServiceImpl.saveFileContent(testFile));
    }

    @Test
    public void whenSaveFileMissingParametersExpectIllegalArgumentException() throws IOException {
        // given
        Path path = Paths.get("src/test/resources/missingParametersTestFile.txt");
        byte[] content = Files.readAllBytes(path);
        MultipartFile testFile = new MockMultipartFile("testFile", "test", "text/plain", content);

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileContentServiceImpl.saveFileContent(testFile));
    }

    @Test
    public void whenDeleteExpectCorrectIdIsPassedToRepository() {
        //when
        fileContentServiceImpl.deleteFileContentRecord(testFileContent.getId());

        // then
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.repository).deleteById(argument.capture());
        Assertions.assertEquals(testFileContent.getId(), argument.getValue());
    }
}
