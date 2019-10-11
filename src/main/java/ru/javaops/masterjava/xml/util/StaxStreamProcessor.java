package ru.javaops.masterjava.xml.util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private final XMLStreamReader reader;

    public StaxStreamProcessor(InputStream in) throws XMLStreamException {
        reader = FACTORY.createXMLStreamReader(in);
    }

    public XMLStreamReader getReader() {
        return reader;
    }

    public boolean doUntil(int stopEventCode, String value) throws XMLStreamException {
        return doUntilAny(stopEventCode, value) != null;
    }

    public String getAttribute(String name) throws XMLStreamException {
        return reader.getAttributeValue(null, name);
    }

    public String doUntilAny(int stopEventCode, String... values) throws XMLStreamException {
        while (reader.hasNext()) {
            int eventCode = reader.next();
            if (eventCode == stopEventCode) {
                var xmlValue = getValue(eventCode);
                for (String value : values) {
                    if (value.equals(xmlValue)) {
                        return xmlValue;
                    }
                }
            }
        }
        return null;
    }

    public String getValue(int eventCode) {
        return eventCode == XMLEvent.CHARACTERS ? reader.getText() : reader.getLocalName();
    }

    public String getElementValue(String element) throws XMLStreamException {
        return doUntil(XMLEvent.START_ELEMENT, element) ? reader.getElementText() : null;
    }

    public String getText() throws XMLStreamException {
        return reader.getElementText();
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                // ignored
            }
        }
    }
}