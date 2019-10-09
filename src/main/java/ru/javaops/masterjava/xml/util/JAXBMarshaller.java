package ru.javaops.masterjava.xml.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.validation.Schema;
import java.io.StringWriter;
import java.io.Writer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JAXBMarshaller {
    private Marshaller marshaller;

    public JAXBMarshaller(JAXBContext context) throws JAXBException {
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, UTF_8.name());
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
    }

    public void setProperty(String key, Object value) throws PropertyException {
        marshaller.setProperty(key, value);
    }

    public synchronized void setSchema(Schema schema) {
        marshaller.setSchema(schema);
    }

    public String marshal(Object instance) throws JAXBException {
        var buffer = new StringWriter();
        marshal(instance, buffer);
        return buffer.toString();
    }

    public synchronized void marshal(Object instance, Writer writer) throws JAXBException {
        marshaller.marshal(instance, writer);
    }
}
