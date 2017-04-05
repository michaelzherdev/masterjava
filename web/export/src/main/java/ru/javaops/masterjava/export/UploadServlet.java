package ru.javaops.masterjava.export;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static ru.javaops.masterjava.export.ThymeleafListener.engine;

@WebServlet("/")
@MultipartConfig
@Slf4j
public class UploadServlet extends HttpServlet {
    private static final int CHUNK_SIZE = 2000;

    private final UserExport userExport = new UserExport();
    private final CityExport cityExport = new CityExport();
    private final GroupExport groupExport = new GroupExport();
    private final ProjectExport projectExport = new ProjectExport();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        outExport(req, resp, "", CHUNK_SIZE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message;
        int chunkSize = CHUNK_SIZE;
        try {
//            http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
            chunkSize = Integer.parseInt(req.getParameter("chunkSize"));
            if (chunkSize < 1) {
                message = "Chunk Size must be > 1";
            } else {
                message = "";
                Part filePart = req.getPart("fileToUpload");

                List<UserExport.FailedEmail> failedUsers;
                List<GroupExport.FailedName> failedGroups;
                List<CityExport.Failed> failedCities;
                List<ProjectExport.FailedName> failedProjects;

                try (InputStream is = filePart.getInputStream()) {
                    failedProjects = projectExport.process(is, chunkSize);
                    log.info("Failed projects: " + failedProjects);
                }
                try (InputStream is = filePart.getInputStream()) {
                    failedGroups = groupExport.process(is, chunkSize);
                    log.info("Failed groups: " + failedGroups);
                }
                try (InputStream is = filePart.getInputStream()) {
                    failedCities = cityExport.process(is, chunkSize);
                    log.info("Failed cities: " + failedCities);
                }
                try (InputStream is = filePart.getInputStream()) {
                    failedUsers = userExport.process(is, chunkSize);
                    log.info("Failed users: " + failedUsers);
                }

                final WebContext webContext =
                        new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                                ImmutableMap.of(
                                        "failedProjects", failedProjects,
                                        "failedGroups", failedGroups,
                                        "failedCities", failedCities,
                                        "failedUsers", failedUsers));
                engine.process("result", webContext, resp.getWriter());
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            message = e.toString();
        }
        outExport(req, resp, message, chunkSize);
    }

    private void outExport(HttpServletRequest req, HttpServletResponse resp, String message, int chunkSize) throws IOException {
        resp.setCharacterEncoding("utf-8");
        final WebContext webContext =
                new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                        ImmutableMap.of("message", message, "chunkSize", chunkSize));
        engine.process("export", webContext, resp.getWriter());
    }
}
