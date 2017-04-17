package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.web.WsClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet("/")
@MultipartConfig
@Slf4j
public class UsersServlet extends HttpServlet {
    private UserDao userDao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("users", userDao.getWithLimit(20), "message", ""));
        engine.process("users", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message;
        try {
            String subject = req.getParameter("subject");
            String body = req.getParameter("mailBody");
            String[] userEmails = req.getParameterValues("checkbox");
            Set<Addressee> emails = Arrays.stream(userEmails)
                    .map(m -> new Addressee(m))
                    .collect(Collectors.toSet());

            MailWSClient.sendMail(emails, Collections.emptySet(), subject, body);

            message = "Mails sended";
            final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                    ImmutableMap.of("users", userDao.getWithLimit(20), "message", message));
            engine.process("users", webContext, resp.getWriter());

        } catch (Exception e) {
            log.info(e.getMessage(), e);
            message = e.toString();
        }
    }
}
