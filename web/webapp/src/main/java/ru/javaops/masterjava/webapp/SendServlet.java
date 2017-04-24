package ru.javaops.masterjava.webapp;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.web.WebStateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@WebServlet("/send")
@Slf4j
public class SendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String users = req.getParameter("users");
        String subject = req.getParameter("subject");
        String body = req.getParameter("body");
        String groupResult;
        String attachment = "";
        String attachmentName = "";
        Part filePart = req.getPart("fileToUpload");
        if (filePart != null)
            try (InputStream is = filePart.getInputStream();
                 InputStreamReader reader = new InputStreamReader(is)) {
                attachment = CharStreams.toString(reader);
                attachmentName = filePart.getName();
            }

        try {
            groupResult = MailWSClient.sendBulk(MailWSClient.split(users), subject, body, attachment, attachmentName).toString();
        } catch (WebStateException e) {
            groupResult = e.toString();
        }
        resp.getWriter().write(groupResult);
    }
}
