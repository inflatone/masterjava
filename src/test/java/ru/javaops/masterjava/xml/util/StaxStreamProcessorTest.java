package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Test;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import static org.junit.Assert.*;

public class StaxStreamProcessorTest {
    @Test
    public void rawReadCities() throws Exception {
        try (StaxStreamProcessor processor
                     = new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    if ("City".equals(reader.getLocalName())) {
                        System.out.println(reader.getElementText());
                    }
                }
            }
        }
    }

    @Test
    public void wrappedReadCities() throws Exception {
        try (StaxStreamProcessor processor
                = new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            for (String city = processor.getElementValue("City"); city != null; city = processor.getElementValue("City")) {
                System.out.println(city);
            }
        }
    }

}