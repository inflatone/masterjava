package ru.javaops.masterjava.xml.util;

import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import static com.google.common.io.Resources.getResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JAXBParserTest {
    // https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names
    private static final JAXBParser parser;
    private static final JAXBMarshaller marshaller;
    private static final JAXBUnmarshaller unmarshaller;

    static {
        parser = new JAXBParser(ObjectFactory.class);
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        marshaller = parser.createMarshaller();
        unmarshaller = parser.createUnmarshaller();
    }

    @Test
    void testPayload() throws Exception {
        var payload = (Payload) unmarshaller.unmarshal(
                getResource("payload.xml").openStream()
        );

        assertEquals(6, payload.getUsers().getUser().size());
        assertEquals(4, payload.getCities().getCity().size());

        assertEquals("gmail@gmail.com", payload.getUsers().getUser().get(0).getEmail());
        assertEquals("Admin", payload.getUsers().getUser().get(1).getValue());
        assertEquals("deleted", payload.getUsers().getUser().get(2).getFlag().value());

        var payloadAsLine = marshaller.marshal(payload);
        parser.validate(payloadAsLine);
        System.out.println(payloadAsLine);
    }

    @Test
    void testCity() throws Exception {
        JAXBElement<CityType> cityElement = unmarshaller.unmarshal(
                getResource("city.xml").openStream()
        );
        var city = cityElement.getValue();

        assertEquals("Санкт-Петербург", city.getValue().trim());
        assertEquals("spb", city.getId());

        var expectedCityElement = new JAXBElement<>(
                new QName("http://javaops.ru", "City"), CityType.class, city
        );

        String cityAsLine = marshaller.marshal(expectedCityElement);
        parser.validate(cityAsLine);
        System.out.println(cityAsLine);
    }
}