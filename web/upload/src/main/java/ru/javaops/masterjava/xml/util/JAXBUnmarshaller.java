package ru.javaops.masterjava.xml.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class JAXBUnmarshaller {
    private Unmarshaller unmarshaller;

    public JAXBUnmarshaller(JAXBContext context) throws JAXBException {
        unmarshaller = context.createUnmarshaller();
    }

    public void setSchema(Schema schema) {
        unmarshaller.setSchema(schema);
    }

    @SuppressWarnings("unchecked")
    public <T> T unmarshal(InputStream in) throws JAXBException {
        return (T) unmarshaller.unmarshal(in);
    }

    @SuppressWarnings("unchecked")
    public <T> T unmarshal(Reader reader) throws JAXBException {
        return (T) unmarshaller.unmarshal(reader);
    }

    @SuppressWarnings("unchecked")
    public <T> T unmarshal(String line) throws JAXBException {
        return (T) unmarshal(new StringReader(line));
    }

    public <T> T unmarshal(XMLStreamReader reader, Class<T> clazz) throws JAXBException {
        return unmarshaller.unmarshal(reader, clazz).getValue();
    }
}
