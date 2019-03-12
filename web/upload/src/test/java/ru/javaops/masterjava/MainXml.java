package ru.javaops.masterjava;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import j2html.tags.ContainerTag;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;
import static j2html.TagCreator.*;

/**
 * User: gkislin
 * Date: 05.08.2015
 *
 * @link http://caloriesmng.herokuapp.com/
 * @link https://github.com/JavaOPs/topjava
 */
public class MainXml {

    public static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Required argument: projectName");
            System.exit(1);
        }
        String projectName = args[0];
        URL payloadUrl = Resources.getResource("payload.xml");
        MainXml main = new MainXml();


        Set<User> users = main.parseByJaxb(projectName, payloadUrl);
        printUsers(users);

        String html = toHtml(users, projectName, Paths.get("out/usersJaxb.html"));
        System.out.println(html);

        System.out.println();
        printUsers(processByStax(projectName, payloadUrl));

        html = main.transform(projectName, payloadUrl);
        try (Writer writer = Files.newBufferedWriter(Paths.get("out/users.html"))) {
            writer.write(html);
        }

    }

    private static void printUsers(Iterable<User> users) {
        users.forEach(u -> System.out.printf("Name: '%s', email: %s%n", u.getValue(), u.getEmail()));
        System.out.println();
    }

    private static Set<User> processByStax(String projectName, URL payloadUrl) throws Exception {
        try (InputStream is = payloadUrl.openStream()) {
            StaxStreamProcessor processor = new StaxStreamProcessor(is);
            final Set<String> groupNames = parseGroupNames(processor, projectName);
            if (groupNames.isEmpty()) {
                throw new IllegalArgumentException(String.format("Invalid project name %s or no groups", projectName));
            }
            return parseUsers(processor, groupNames);
        }
    }

    private static Set<String> parseGroupNames(StaxStreamProcessor processor, String projectName) throws XMLStreamException {
        final Set<String> result = new HashSet<>();
        while (processor.startElement("Project", "Projects")) {
            if (projectName.equals(processor.getAttribute("name"))) {
                while (processor.startElement("Group", "Project")) {
                    result.add(processor.getAttribute("name"));
                }
                break;
            }
        }
        return result;
    }

    private static Set<User> parseUsers(StaxStreamProcessor processor, Set<String> groupNames) throws XMLStreamException, JAXBException {
        Set<User> result = new TreeSet<>(USER_COMPARATOR);
        JaxbParser parser = new JaxbParser(User.class);
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            String groupRefs = processor.getAttribute("groupRefs");
            if (!Collections.disjoint(groupNames, Splitter.on(' ').splitToList(nullToEmpty(groupRefs)))) {
                result.add(parser.unmarshal(processor.getReader(), User.class));
            }
        }
        return result;
    }

    private static String toHtml(Set<User> users, String projectName, Path path) throws IOException {
        final ContainerTag table = table().with(tr().with(th("FullName"), th("email")))
                .attr("border", "1")
                .attr("cellspacing", "8")
                .attr("cellspacing", "0");
        users.forEach(u -> table.with(tr().with(td(u.getValue()), th(u.getEmail()))));
        return html().with(
                head().with(title(projectName + " users")),
                body().with(h1(projectName + " users"), table)
        ).render();


    }

    private String transform(String projectName, URL payloadUrl) throws IOException, TransformerException {
        URL xsl = Resources.getResource("groups.xsl");
        try (InputStream xmlIn = payloadUrl.openStream();
             InputStream xslIn = xsl.openStream()
        ) {
            XsltProcessor processor = new XsltProcessor(xslIn);
            processor.setParameter("projectName", projectName);
            return processor.transform(xmlIn);
        }
    }

    private Set<User> parseByJaxb(String projectName, URL payloadUrl) throws Exception {
        JaxbParser parser = new JaxbParser(ObjectFactory.class);
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        try (InputStream is = payloadUrl.openStream()) {
            Payload payload = parser.unmarshal(is);
            Project project = StreamEx.of(payload.getProjects().getProject())
                    .filterBy(Project::getName, projectName)
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid project name '%s'", projectName)));
            Set<Project.Group> groups = new HashSet<>(project.getGroup());  // identity compare
            return StreamEx.of(payload.getUsers().getUser())
                    .filter(u -> !Collections.disjoint(groups, u.getGroupRefs()))
                    .toCollection(() -> new TreeSet<>(USER_COMPARATOR));
        }
    }
}
