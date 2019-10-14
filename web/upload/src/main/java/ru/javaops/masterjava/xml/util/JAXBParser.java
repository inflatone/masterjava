package ru.javaops.masterjava.xml.util;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Marshalling/Unmarshalling JAXB facade
 */
public class JAXBParser {
    private JAXBContext context;
    protected Schema schema;

    public JAXBParser(Class... classesToBeBound) {
        try {
            init(JAXBContext.newInstance(classesToBeBound));
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    // http://stackoverflow.com/questions/30643802/what-is-jaxbcontext-newinstancestring-contextpath
    public JAXBParser(String contextPath) {
        try {
            init(JAXBContext.newInstance(contextPath));
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    private void init(JAXBContext context) {
        this.context = context;
    }

    // https://stackoverflow.com/a/7400735/548473
    public JAXBMarshaller createMarshaller() {
        try {
            var marshaller = new JAXBMarshaller(context);
            if (schema != null) {
                marshaller.setSchema(schema);
            }
            return marshaller;
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    // https://stackoverflow.com/a/7400735/548473
    public JAXBUnmarshaller createUnmarshaller() {
        try {
            var unmarshaller = new JAXBUnmarshaller(context);
            if (schema != null) {
                unmarshaller.setSchema(schema);
            }
            return unmarshaller;
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    // Schema validator

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public void validate(String line) throws IOException, SAXException {
        validate(new StringReader(line));
    }

    public void validate(Reader reader) throws IOException, SAXException {
        schema.newValidator().validate(new StreamSource(reader));
    }
}