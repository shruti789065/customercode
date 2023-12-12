package com.adiacent.menarini.menarinimaster.core.schedulers;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EncodeDecodeSecretKeyTest {
    private EncodeDecodeSecretKey fixture = new EncodeDecodeSecretKey();

    private TestLogger logger = TestLoggerFactory.getTestLogger(fixture.getClass());

    @BeforeEach
    void setup() {
        TestLoggerFactory.clear();
    }

    @Test
    void run() {
        EncodeDecodeSecretKey.Config config = mock(EncodeDecodeSecretKey.Config.class);
        when(config.getSecretKey()).thenReturn("LYA6f1TM09aL1xMD");
        when(config.getIvParameter()).thenReturn("eXRa60ZQHI0XbwJb");
        when(config.getAlgorithm()).thenReturn("AES/CBC/PKCS5PADDING");

        fixture.activate(config);

        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(6, events.size());

    }
}
