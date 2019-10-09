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
        while (reader.hasNext()) {
            int eventCode = reader.next();
            if (eventCode == stopEventCode) {
                if (value.equals(getValue(eventCode))) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getValue(int eventCode) throws XMLStreamException {
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