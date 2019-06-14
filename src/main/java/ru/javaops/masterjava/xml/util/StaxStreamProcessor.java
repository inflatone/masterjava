package ru.javaops.masterjava.xml.util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newFactory();

    private final XMLStreamReader reader;

    public StaxStreamProcessor(InputStream in) throws XMLStreamException {
        reader = FACTORY.createXMLStreamReader(in);
    }

    public boolean startElement(String element, String parent) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (parent != null && isElementEnd(event, parent)) {
                return false;
            }
            if (isElementStart(event, element)) {
                return true;
            }
        }
        return false;
    }

    private boolean isElementStart(int event, String element) {
        return event == XMLEvent.START_ELEMENT && element.equals(reader.getLocalName());
    }

    private boolean isElementEnd(int event, String element) {
        return event == XMLEvent.END_ELEMENT && element.equals(reader.getLocalName());
    }

    public XMLStreamReader getReader() {
        return reader;
    }

    public String getAttribute(String name) throws XMLStreamException {
        return reader.getAttributeValue(null, name);
    }

    public boolean doUntil(int stopEvent, String value) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == stopEvent && value.equals(getValue(event))) {
                return true;
            }
        }
        return false;
    }

    public String getValue(int event) throws XMLStreamException {
        return event == XMLEvent.CHARACTERS ? reader.getText() : reader.getLocalName();
    }

    public String getElementValue(String element) throws XMLStreamException {
        return doUntil(XMLEvent.START_ELEMENT, element) ? reader.getElementText() : null;
    }

    public String getText() throws XMLStreamException {
        return reader.getElementText();
    }

    @Override
    public void close() throws Exception {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException ignored) {
            }
        }
    }
}
