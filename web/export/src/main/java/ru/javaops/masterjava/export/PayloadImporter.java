package ru.javaops.masterjava.export;

import lombok.Value;
import lombok.val;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.List;

public class PayloadImporter {
    private final CityImporter cityImporter = new CityImporter();
    private final UserImporter userImporter = new UserImporter();
    private final GroupImporter groupImporter = new GroupImporter();

    @Value
    public static class FailedEmail {
        public String emailOrRange;
        public String reason;

        @Override
        public String toString() {
            return emailOrRange + " : " + reason;
        }
    }

    @Value
    public static class FailedGroup {
        public String nameOrRange;
        public String reason;

        @Override
        public String toString() {
            return nameOrRange + " : " + reason;
        }
    }

    public List<FailedEmail> process(InputStream is, int chunkSize) throws XMLStreamException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        val groups = groupImporter.process(processor);
        val cities = cityImporter.process(processor);
        return userImporter.process(processor, cities, groups, chunkSize);
    }
}
