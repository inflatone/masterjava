package ru.javaops.masterjava.xml.util;

import org.junit.jupiter.api.Test;

import javax.xml.stream.events.XMLEvent;
import java.util.StringJoiner;

import static com.google.common.io.Resources.getResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StaxStreamProcessorTest {
    @Test
    void manualReadCities() throws Exception {
        var buffer = new StringJoiner("\n");
        try (var processor = new StaxStreamProcessor(getResource("payload.xml").openStream())) {
            var reader = processor.getReader();
            while (reader.hasNext()) {
                int eventCode = reader.next();
                if (eventCode == XMLEvent.START_ELEMENT) {
                    if ("City".equals(reader.getLocalName())) {
                        buffer.add(reader.getElementText());
                    }
                }
            }
        }
        var result = buffer.toString();
        assertEquals("Санкт-Петербург\nКиев\nМинск", result);
        System.out.println(result);
    }

    @Test
    void autoReadCities() throws Exception {
        var buffer = new StringJoiner("\n");
        try (var processor = new StaxStreamProcessor(getResource("payload.xml").openStream())) {
            for (var city = processor.getElementValue("City"); city != null; city = processor.getElementValue("City")) {
                buffer.add(city);
            }
        }
        var result = buffer.toString();
        assertEquals("Санкт-Петербург\nКиев\nМинск", buffer.toString());
        System.out.println(result);
    }

}