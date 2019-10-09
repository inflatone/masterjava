package ru.javaops.masterjava.xml.util;

import org.junit.jupiter.api.Test;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.StringJoiner;
import java.util.stream.IntStream;

import static com.google.common.io.Resources.getResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XPathProcessorTest {
    @Test
    void getCities() throws Exception {
        var buffer = new StringJoiner("\n");
        try (var in = getResource("payload.xml").openStream()) {
            var processor = new XPathProcessor(in);
            var expression = XPathProcessor.getExpression(
                    "/*[name()='Payload']/*[name()='Cities']/*[name()='City']/text()");
            NodeList nodes = processor.evaluate(expression, XPathConstants.NODESET);
            IntStream.range(0, nodes.getLength()).forEach(
                    i -> buffer.add(nodes.item(i).getNodeValue())
            );
        }
        var result = buffer.toString();
        assertEquals("Санкт-Петербург\nМосква\nКиев\nМинск", result);
        System.out.println(result);
    }


}