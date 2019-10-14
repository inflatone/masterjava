package ru.javaops.masterjava;

import com.google.common.base.Splitter;
import j2html.tags.ContainerTag;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JAXBParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.stream.events.XMLEvent;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.io.Resources.getResource;
import static j2html.TagCreator.*;

public class MainXml {
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Format: projectName");
            args = new String[]{"topjava"};
            // System.exit(1);
        }
        String projectName = args[0];
        var payloadUrl = getResource("payload.xml");
        var users = parseByJaxb(projectName, payloadUrl);
        users.forEach(System.out::println);
        System.out.println();

        var html = toHtml(users, projectName);
        System.out.println(html);
        try (var writer = Files.newBufferedWriter(Paths.get("out/users.html"))) {
            writer.write(html);
        }
        System.out.println();
        users = processByStax(projectName, payloadUrl);
        users.forEach(System.out::println);

        System.out.println();
        html = transform(projectName, payloadUrl);
        try (var writer = Files.newBufferedWriter(Paths.get("out/groups.html"))) {
            writer.write(html);
        }
    }

    private static Set<User> parseByJaxb(String projectName, URL payloadUrl) throws Exception {
        var parser = new JAXBParser(ObjectFactory.class);
        var unmarshaller = parser.createUnmarshaller();
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        Payload payload;
        try (var in = payloadUrl.openStream()) {
            payload = unmarshaller.unmarshal(in);
        }

        var project = StreamEx.of(payload.getProjects().getProject())
                .filter(p -> p.getName().equals(projectName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid project name '" + projectName + '\''));
        final var groups = new HashSet<>(project.getGroup()); // // identity compare
        return StreamEx.of(payload.getUsers().getUser())
                .filter(u -> !Collections.disjoint(groups, u.getGroupRefs()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(USER_COMPARATOR)));
    }

    private static Set<User> processByStax(String projectName, URL payloadUrl) throws Exception {
        try (var in = payloadUrl.openStream()) {
            var processor = new StaxStreamProcessor(in);
            final var groupNames = new HashSet<>();

            while (processor.startElement("Project", "Projects")) {
                if (projectName.equals(processor.getAttribute("name"))) {
                    while (processor.startElement("Group", "Project")) {
                        groupNames.add(processor.getAttribute("name"));
                    }
                    break;
                }
            }
            if (groupNames.isEmpty()) {
                throw new IllegalArgumentException("Invalid project name '" + projectName + "' or there're no groups");
            }

            final var users = new TreeSet<>(USER_COMPARATOR);
            final var parser = new JAXBParser(ObjectFactory.class);
            final var unmarshaller = parser.createUnmarshaller();

            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                var groupRefs = processor.getAttribute("groupRefs");
                if (!Collections.disjoint(groupNames, Splitter.on(' ').splitToList(nullToEmpty(groupRefs)))) {
                    var user = unmarshaller.unmarshal(processor.getReader(), User.class);
                    users.add(user);
                }
            }
            return users;
        }
    }

    private static String toHtml(Set<User> users, String projectName) {
        final ContainerTag table = table().with(
                tr().with(th("Fullname"), th("email")))
                .attr("border", "1")
                .attr("cellpadding", "8")
                .attr("cellspacing", "0");
        users.forEach(u -> table.with(tr().with(td(u.getValue()), td(u.getEmail()))));
        return html().with(
                head().with(title(projectName + " users")),
                body().with(h1(projectName + " users"), table)
        ).render();
    }

    private static String transform(String projectName, URL payloadUrl) throws Exception {
        var xslUrl = getResource("groups.xsl");
        try (var xmlIn = payloadUrl.openStream();
             var xslIn = xslUrl.openStream()) {
            final var processor = new XsltProcessor(xslIn);
            processor.setParameter("projectName", projectName);
            return processor.transform(xmlIn);
        }
    }
}
