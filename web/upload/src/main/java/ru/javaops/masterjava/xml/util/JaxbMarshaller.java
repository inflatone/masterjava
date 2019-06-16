package ru.javaops.masterjava.xml.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.validation.Schema;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class JaxbMarshaller {
    private Marshaller marshaller;

    public JaxbMarshaller(JAXBContext context) throws JAXBException {
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
    }

    public void setProperty(String property, Object value) {
        try {
            marshaller.setProperty(property, value);
        } catch (PropertyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setSchema(Schema schema) {
        marshaller.setSchema(schema);
    }

    public String marshal(Object instance) throws JAXBException {
        StringWriter writer = new StringWriter();
        marshal(instance, writer);
        return writer.toString();
    }

    public void marshal(Object instance, Writer writer) throws JAXBException {
        marshaller.marshal(instance, writer);
    }
}
