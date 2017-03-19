package ru.javaops.masterjava.servlet;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.javaops.masterjava.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.schema.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Created by Mikhail on 18.03.2017.
 */
public class LoadServlet extends HttpServlet {

    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Set<User> users = null;

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);
        factory.setRepository(new File("/tmp"));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1000000);

        List fileItems = null;
        try {
            fileItems = upload.parseRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        Iterator i = fileItems.iterator();
        //getting xml content
        FileItem fi = (FileItem)i.next();
        String content = fi.getString();
        // getting group name
        fi = (FileItem) i.next();
        String groupName = fi.getString();

        try {
            users = processByStax(groupName, content);
            users.forEach(System.out::println);
            Files.write(users.toString(), new File("c:\\temp\\result.txt"), Charset.forName("windows-1251"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = "/fine.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(url);
        dispatcher.forward(request, response);
    }

    private static Set<User> processByStax(String projectName, String fileContent) throws Exception {

        try (InputStream is = new ByteArrayInputStream( fileContent.getBytes() );) {
            StaxStreamProcessor processor = new StaxStreamProcessor(is);
            final Set<String> groupNames = new HashSet<>();

            // Projects loop
            projects:
            while (processor.doUntil(XMLEvent.START_ELEMENT, "Project")) {
                if (projectName.equals(processor.getAttribute("name"))) {
                    // Groups loop
                    String element;
                    while ((element = processor.doUntilAny(XMLEvent.START_ELEMENT, "Project", "Group", "Users")) != null) {
                        if (!element.equals("Group")) {
                            break projects;
                        }
                        groupNames.add(processor.getAttribute("name"));
                    }
                }
            }
            if (groupNames.isEmpty()) {
                throw new IllegalArgumentException("Invalid " + projectName + " or no groups");
            }

            // Users loop
            Set<User> users = new TreeSet<>(USER_COMPARATOR);

            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                String groupRefs = processor.getAttribute("groupRefs");
                if (!Collections.disjoint(groupNames, Splitter.on(' ').splitToList(nullToEmpty(groupRefs)))) {
                    User user = new User();
                    user.setEmail(processor.getAttribute("email"));
                    user.setValue(processor.getText());
                    users.add(user);
                }
            }
            return users;
        }
    }
}
