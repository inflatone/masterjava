package ru.javaops.masterjava.xml.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class JaxbUnmarshaller {
    private Unmarshaller unmarshaller;

    public JaxbUnmarshaller(JAXBContext context) throws JAXBException {
        unmarshaller = context.createUnmarshaller();
    }

    public synchronized void setSchema(Schema schema) {
        unmarshaller.setSchema(schema);
    }

    public synchronized Object unmarshal(InputStream in) throws JAXBException {
        return unmarshaller.unmarshal(in);
    }

    public synchronized Object unmarshal(Reader reader) throws JAXBException {
        return unmarshaller.unmarshal(reader);
    }

    public Object unmarshal(String line) throws JAXBException {
        return unmarshal(new StringReader(line));
    }

    public synchronized <T> T unmarshal(XMLStreamReader reader, Class<T> elementClass) throws JAXBException {
        return unmarshaller.unmarshal(reader, elementClass).getValue();
    }
}
