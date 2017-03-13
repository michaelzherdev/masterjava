package ru.javaops.masterjava;

import com.google.common.io.Resources;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by mikhail on 13.03.17.
 * 6. Вывести через XSLT преобразование html таблицу с группами заданного проекта
 * program arguments: 'topjava' or 'masterjava'
 */
public class MainXslt {
    public static void main(String[] args) throws TransformerException, IOException {
        String projectName = args[0];

        InputStream payloadStream = Resources.getResource("payload.xml").openStream();
        Path path2 = Paths.get("groups.html");
        InputStream inputXSL = MainXslt.class.getResourceAsStream("/groups.xsl");

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xslStream = new StreamSource(inputXSL);
        Transformer transformer = factory.newTransformer(xslStream);

        StreamSource in = new StreamSource(payloadStream);
        StreamResult out = new StreamResult(path2.toFile());
        transformer.setParameter("projectName", projectName);
        transformer.transform(in, out);
    }
}
