package ru.javaops.masterjava;

import com.google.common.io.Resources;
import j2html.tags.ContainerTag;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static j2html.TagCreator.*;

/**
 * User: gkislin
 * Date: 05.08.2015
 *
 * @link http://caloriesmng.herokuapp.com/
 * @link https://github.com/JavaOPs/topjava
 */
public class MainXml {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Format: projectName");
            System.exit(1);
        }
        String projectName = args[0];
        URL payloadUrl = Resources.getResource("payload.xml");
        MainXml main = new MainXml();


        Set<User> users = main.parceByJaxb(projectName, payloadUrl);
        users.forEach(System.out::println);
        
        
        String html = toHtml(users, projectName, Paths.get("out/usersJaxb.html"));
        System.out.println(html);
        try (Writer writer = Files.newBufferedWriter(Paths.get("out/users.html"))) {
            writer.write(html);
        }
    }

    private Set<User> parceByJaxb(String projectName, URL payloadUrl) throws Exception {
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
                    .collect(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(User::getValue).thenComparing(User::getEmail)))
                    );
        }
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
}
