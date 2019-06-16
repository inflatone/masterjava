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
 * Marshalling/Unmarshalling JAXB helper
 * XML Facade
 */
public class JaxbParser {
    private JAXBContext context;
    protected Schema schema;

    public JaxbParser(Class... classesToBeBound) {
        try {
            init(JAXBContext.newInstance(classesToBeBound));
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    //    http://stackoverflow.com/questions/30643802/what-is-jaxbcontext-newinstancestring-contextpath
    public JaxbParser(String contextPath) {
        try {
            init(JAXBContext.newInstance(contextPath));
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    private void init(JAXBContext context) {
        this.context = context;
    }

    //    https://stackoverflow.com/a/7400735/548473
    public JaxbMarshaller createMarshaller() {
        try {
            JaxbMarshaller marshaller = new JaxbMarshaller(context);
            if (schema != null) {
                marshaller.setSchema(schema);
            }
            return marshaller;
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    public JaxbUnmarshaller createUnmarshaller() {
        try {
            JaxbUnmarshaller unmarshaller = new JaxbUnmarshaller(context);
            if (schema != null) {
                unmarshaller.setSchema(schema);
            }
            return unmarshaller;
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public void validate(String xml) throws IOException, SAXException {
        validate(new StringReader(xml));
    }

    public void validate(Reader reader) throws IOException, SAXException {
        schema.newValidator().validate(new StreamSource(reader));
    }

}
