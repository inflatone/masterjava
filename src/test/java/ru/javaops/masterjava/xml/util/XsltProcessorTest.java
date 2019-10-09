package ru.javaops.masterjava.xml.util;

import org.junit.jupiter.api.Test;

import static com.google.common.io.Resources.getResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XsltProcessorTest {
    @Test
    void transform() throws Exception {
        try (var xslIn = getResource("cities.xsl").openStream();
             var xmlIn = getResource("payload.xml").openStream()
        ) {
            var processor = new XsltProcessor(xslIn);
            var result = processor.transform(xmlIn);
            assertEquals(String.format("Санкт-Петербург%nКиев%nМинск%n"), result);
            System.out.println(result);
        }
    }
}