package ru.javaops.masterjava.xml.util;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.*;

/**
 * Marshalling/Unmarshalling JAXB helper
 * XML Facade
 */
public class JaxbParser {
    protected JaxbMarshaller marshaller;
    protected JaxbUnmarshaller unmarshaller;
    protected Schema schema;

    public JaxbParser(Class... classesToBeBound) {
        try {
            init(JAXBContext.newInstance(classesToBeBound));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    //    http://stackoverflow.com/questions/30643802/what-is-jaxbcontext-newinstancestring-contextpath
    public JaxbParser(String contextPath) {
        try {
            init(JAXBContext.newInstance(contextPath));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void init(JAXBContext context) throws JAXBException {
        marshaller = new JaxbMarshaller(context);
        unmarshaller = new JaxbUnmarshaller(context);
    }

    //  Unmarshaller

    public <T> T unmarshal(InputStream in) throws JAXBException {
        return (T) unmarshaller.unmarshal(in);
    }

    public <T> T unmarshal(Reader reader) throws JAXBException {
        return (T) unmarshaller.unmarshal(reader);
    }

    public <T> T unmarshal(String line) throws JAXBException {
        return (T) unmarshaller.unmarshal(line);
    }

    //  Marshaller

    public void setMarshallerProperty(String property, Object value) {
        try {
            marshaller.setProperty(property, value);
        } catch (PropertyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String marshal(Object instance) throws JAXBException {
        return marshaller.marshal(instance);
    }

    public void marshal(Object instance, Writer writer) throws JAXBException {
        marshaller.marshal(instance, writer);
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
        unmarshaller.setSchema(schema);
        marshaller.setSchema(schema);
    }

    public void validate(String xml) throws IOException, SAXException {
        validate(new StringReader(xml));
    }

    public void validate(Reader reader) throws IOException, SAXException {
        schema.newValidator().validate(new StreamSource(reader));
    }

}
