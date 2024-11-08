package com.jakala.menarini.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FileReaderServiceTest {

    @InjectMocks
    private FileReaderService fileReaderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFileAsStreamWithValidFile() {
        InputStream inputStream = fileReaderService.getFileAsStream("/validFileName.txt");
        assertNotNull(inputStream);
    }

    @Test
    void testGetFileAsStreamWithInvalidFile() {
        InputStream inputStream = fileReaderService.getFileAsStream("/invalidFileName.txt");
        assertNull(inputStream);
    }
}