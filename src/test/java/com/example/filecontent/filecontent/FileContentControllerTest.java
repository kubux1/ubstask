package com.example.filecontent.filecontent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

import static com.example.filecontent.constant.Constant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FileContentController.class)
public class FileContentControllerTest {

    private final FileContent testFileContent = new FileContent("1", "TEST", "TEST", Instant.parse
            ("2017-02-03T10:37:30Z"));

    private MockMultipartFile testFile;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileContentService fileContentServiceImpl;

    @BeforeEach
    public void setUpTestEnv() throws IOException {
        Path path = Paths.get("src/test/resources/validTestFile.txt");
        byte[] content = Files.readAllBytes(path);
        testFile = new MockMultipartFile("testFile", "test", "text/plain", content);
    }

    @Test
    public void postValidFileExpectStatusOkResponse()
            throws Exception {
        // when
        mvc.perform(MockMvcRequestBuilders.multipart(FILE_API_URL_PATH)
                .file(FILE_PARAM_NAME, testFile.getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().string(FILE_POST_SUCCESSFUL_RESPONSE_MSG));
    }

    @Test
    public void postEmptyFileExpectStatusBadRequest()
            throws Exception {
        // when
        mvc.perform(MockMvcRequestBuilders.multipart(FILE_API_URL_PATH)
                .file(FILE_PARAM_NAME, null))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(EMPTY_FILE_EXCEPTION_MSG));
    }

    @Test
    public void postWhenIllegalArgumentExceptionOccursExpectStatusBadRequest()
            throws Exception {
        // given
        doThrow(new IllegalArgumentException()).when(fileContentServiceImpl).saveFileContent(any());

        // when
        mvc.perform(MockMvcRequestBuilders.multipart(FILE_API_URL_PATH)
                .file(FILE_PARAM_NAME, testFile.getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getExistingFileContentSingleRecordByIdExpectSuccessfulRetrieval()
            throws Exception {
        // given
        given(fileContentServiceImpl.getFileContentRecordById(testFileContent.getId())).willReturn(Optional.of
                (testFileContent));

        // when
        mvc.perform(get(FILE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param(ID_PARAM_NAME, testFileContent.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void getNonExistingFileContentSingleRecordByIdExpectResourceNotFoundException()
            throws Exception {
        // given
        given(fileContentServiceImpl.getFileContentRecordById(testFileContent.getId())).willReturn(Optional.empty());

        // when
        mvc.perform(get(FILE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param(ID_PARAM_NAME, testFileContent.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFileContentRecordByEmptyIdInExpectStatusBadRequest()
            throws Exception {
        // when
        mvc.perform(get(FILE_API_URL_PATH)
                .param(ID_PARAM_NAME, ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(EMPTY_REQ_PARAM_EXCEPTION_MSG));
    }

    @Test
    public void deleteExistingFileContentSingleRecordExpectStatusOk()
            throws Exception {
        // when
        mvc.perform(delete(FILE_API_URL_PATH)
                .param(ID_PARAM_NAME, testFileContent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(FILE_SINGLE_RECORD_DELETE_SUCCESSFUL_RESPONSE_MSG));
    }

    @Test
    public void deleteNonExistingFileContentSingleRecordExpectNotFoundResponse()
            throws Exception {
        // given
        String id = "3";
        doThrow(new EmptyResultDataAccessException(1)).when(fileContentServiceImpl).deleteFileContentRecord(id);

        // when
        mvc.perform(delete(FILE_API_URL_PATH)
                .param(ID_PARAM_NAME, id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void passEmptyIdInDeleteCommandExpectStatusBadRequest()
            throws Exception {
        // when
        mvc.perform(delete(FILE_API_URL_PATH)
                .param(ID_PARAM_NAME, ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(EMPTY_REQ_PARAM_EXCEPTION_MSG));
    }
}
