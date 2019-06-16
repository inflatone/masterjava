package ru.javaops.masterjava.xml.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class JaxbUnmarshaller {
    private Unmarshaller unmarshaller;

    public JaxbUnmarshaller(JAXBContext context) throws JAXBException {
        unmarshaller = context.createUnmarshaller();
    }

    public void setSchema(Schema schema) {
        unmarshaller.setSchema(schema);
    }

    public <T> T unmarshal(InputStream in, Class<T> classToBeCast) throws JAXBException {
        return (T) unmarshaller.unmarshal(new StreamSource(in), classToBeCast).getValue();
    }

    public <T> T unmarshal(Reader reader, Class<T> classToBeCast) throws JAXBException {
        return unmarshaller.unmarshal(new StreamSource(reader), classToBeCast).getValue();
    }

    public <T> T unmarshal(String line, Class<T> classToBeCast) throws JAXBException {
        return unmarshal(new StringReader(line), classToBeCast);
    }

    public <T> T unmarshal(XMLStreamReader reader, Class<T> elementClass) throws JAXBException {
        return unmarshaller.unmarshal(reader, elementClass).getValue();
    }
}
