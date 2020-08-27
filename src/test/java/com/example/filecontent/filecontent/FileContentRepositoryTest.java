package com.example.filecontent.filecontent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class FileContentRepositoryTest {

    private final FileContent testFileContent = new FileContent("1", "TestName", "TestDesc", Instant.now());

    @Autowired
    private FileContentRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void whenFindByIdThenExpectReturnFileContent() {
        // given
        entityManager.persist(testFileContent);
        entityManager.flush();

        // when
        Optional<FileContent> fileContentExpected = repository.findById(testFileContent.getId());

        // then
        Assertions.assertTrue(fileContentExpected.isPresent());
        Assertions.assertEquals(testFileContent, fileContentExpected.get());
    }

    @Test
    public void whenFindByNonExistingIdThenExpectReturnEmpty() {
        // when
        Optional<FileContent> fileContentExpected = repository.findById(testFileContent.getId());

        // then
        Assertions.assertFalse(fileContentExpected.isPresent());
    }

    @Test
    public void whenDeleteByIdThenExpectReturnEmpty() {
        // given
        entityManager.persist(testFileContent);
        entityManager.flush();

        // when
        repository.deleteById(testFileContent.getId());
        Optional<FileContent> fileContentExpected = repository.findById(testFileContent.getId());

        // then
        Assertions.assertFalse(fileContentExpected.isPresent());
    }

    @Test
    public void whenDeleteByNonExistingIdThenExpectEmptyResultDataAccessExceptionThrown() {
        // when - then
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(testFileContent
                .getId()));
    }
}
