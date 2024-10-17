package com.jakala.menarini.core.service.interfaces;

import java.io.InputStream;

public interface FileReaderServiceInterface {
    InputStream getFileAsStream(String fileName);
}
