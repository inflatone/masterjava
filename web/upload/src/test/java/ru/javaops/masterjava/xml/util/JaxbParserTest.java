package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Test;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JaxbParserTest {
    private static final JaxbParser jaxbParser;
    private static final JaxbMarshaller marshaller;
    private static final JaxbUnmarshaller unmarshaller;

    static {
        jaxbParser = new JaxbParser(ObjectFactory.class);
        jaxbParser.setSchema(Schemas.ofClasspath("payload.xsd"));
        marshaller = jaxbParser.createMarshaller();
        unmarshaller = jaxbParser.createUnmarshaller();
    }

    @Test
    public void testPayload() throws Exception {
//        JaxbParserTest.class.getResourceAsStream("/city.xml");
        Payload payload = unmarshaller.unmarshal(Resources.getResource("payload.xml").openStream(), Payload.class);
        String payloadLine = marshaller.marshal(payload);
        jaxbParser.validate(payloadLine);
        System.out.println(payloadLine);
    }

    @Test
    public void testCity() throws Exception {
        CityType city = unmarshaller.unmarshal(Resources.getResource("city.xml").openStream(), CityType.class);
        JAXBElement<CityType> cityElement2 = new JAXBElement<>(
                new QName("http://javaops.ru", "City"), CityType.class, city
        );
        String cityLine = marshaller.marshal(cityElement2);
        jaxbParser.validate(cityLine);
        System.out.println(cityLine);
    }

}