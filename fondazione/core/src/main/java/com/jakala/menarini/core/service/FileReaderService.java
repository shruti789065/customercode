package com.jakala.menarini.core.service;

import java.io.InputStream;

import org.osgi.service.component.annotations.Component;

import com.jakala.menarini.core.service.interfaces.FileReaderServiceInterface;

@Component(service = FileReaderServiceInterface.class)
public class FileReaderService implements FileReaderServiceInterface {

    @Override
    public InputStream getFileAsStream(String fileName) {
        return DataMigrationService.class.getResourceAsStream(fileName);
    }

}