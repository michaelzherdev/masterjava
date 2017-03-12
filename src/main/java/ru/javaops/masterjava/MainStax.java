package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikhail on 12.03.2017.
 * HW 2
 * 4. Сделать реализацию консольного приложения через StAX
 */

public class MainStax {
    public static void main(String[] args) throws IOException, XMLStreamException {
        String groupName = args[0];
        List<String> list = new ArrayList<>();
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    if ("User".equals(reader.getLocalName())) {
                        String groups = reader.getAttributeValue(null, "groups");
                        if(groups != null &&groups.contains(groupName))
                            while (reader.hasNext()) {
                                event = reader.next();
                                if (event == XMLEvent.START_ELEMENT) {
                                    list.add(reader.getElementText());
                                    break;
                                }
                            }
                    }
                }
            }
            list.stream().sorted()
                    .forEach(System.out::println);
        }
    }
}
