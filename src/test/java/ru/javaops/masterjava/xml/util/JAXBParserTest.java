package ru.javaops.masterjava.xml.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import static com.google.common.io.Resources.getResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JAXBParserTest {
    private static final JAXBParser JAXB_PARSER = new JAXBParser(ObjectFactory.class);

    @BeforeAll
    static void init() {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    @Test
    void testPayload() throws Exception {
        var payload = (Payload) JAXB_PARSER.unmarshal(
                getResource("payload.xml").openStream()
        );

        assertEquals(3, payload.getUsers().getUser().size());
        assertEquals(3, payload.getCities().getCity().size());

        assertEquals("gmail@gmail.com", payload.getUsers().getUser().get(0).getEmail());
        assertEquals("Admin", payload.getUsers().getUser().get(1).getFullName());
        assertEquals("deleted", payload.getUsers().getUser().get(2).getFlag().value());

        var payloadAsLine = JAXB_PARSER.marshal(payload);
        JAXB_PARSER.validate(payloadAsLine);
        System.out.println(payloadAsLine);
    }

    @Test
    void testCity() throws Exception {
        JAXBElement<CityType> cityElement = JAXB_PARSER.unmarshal(
                getResource("city.xml").openStream()
        );
        var city = cityElement.getValue();

        assertEquals("Санкт-Петербург", city.getValue().trim());
        assertEquals("spb", city.getId());

        var expectedCityElement = new JAXBElement<>(
                new QName("http://javaops.ru", "City"), CityType.class, city
        );

        String cityAsLine = JAXB_PARSER.marshal(expectedCityElement);
        JAXB_PARSER.validate(cityAsLine);
        System.out.println(cityAsLine);
    }
}