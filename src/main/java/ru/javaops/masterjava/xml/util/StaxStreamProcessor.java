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

    public boolean startElement(String element, String parent) throws XMLStreamException {
        while (reader.hasNext()) {
            int eventCode = reader.next();
            if (parent != null && isElementEnd(eventCode, parent)) {
                return false;
            }
            if (isElementStart(eventCode, element)) {
                return true;
            }
        }
        return false;
    }

    private boolean isElementStart(int eventCode, String value) {
        return eventCode == XMLEvent.START_ELEMENT && value.equals(reader.getLocalName());
    }

    private boolean isElementEnd(int eventCode, String value) {
        return eventCode == XMLEvent.END_ELEMENT && value.equals(reader.getLocalName());
    }

    public String getAttribute(String name) {
        return reader.getAttributeValue(null, name);
    }

    public boolean doUntil(int stopEventCode, String value) throws XMLStreamException {
        while (reader.hasNext()) {
            int eventCode = reader.next();
            if (eventCode == stopEventCode && value.equals(getValue(eventCode))) {
                return true;
            }
        }
        return false;
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