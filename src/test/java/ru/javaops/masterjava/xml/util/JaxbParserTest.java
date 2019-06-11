package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Test;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import static org.junit.Assert.*;

public class JaxbParserTest {
    private static final JaxbParser PARSER = new JaxbParser(ObjectFactory.class);

    static {
        PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    @Test
    public void testPayload() throws Exception {
//        JaxbParserTest.class.getResourceAsStream("/city.xml");
        Payload payload = PARSER.unmarshal(Resources.getResource("payload.xml").openStream());
        String payloadLine = PARSER.marshal(payload);
        PARSER.validate(payloadLine);
        System.out.println(payloadLine);
    }

    @Test
    public void testCity() throws Exception {
        JAXBElement<CityType> cityElement = PARSER.unmarshal(Resources.getResource("city.xml").openStream());
        CityType city = cityElement.getValue();
        JAXBElement<CityType> cityElement2 = new JAXBElement<>(
                new QName("http://javaops.ru", "City"), CityType.class, city
        );
        String cityLine = PARSER.marshal(cityElement2);
        PARSER.validate(cityLine);
        System.out.println(cityLine);
    }

}