package ru.javaops.masterjava.export;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mikhail on 02.04.2017.
 */
public class CityExport {

    private static final int NUMBER_THREADS = 4;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
    private final CityDao cityDao = DBIProvider.getDao(CityDao.class);

    public List<City> process(final InputStream is) throws XMLStreamException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        List<City> cities = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "City")) {
            final String id = processor.getAttribute("id");
            final String value = processor.getText();
            final City city = new City(id, value);
            cityDao.insert(city);
            cities.add(city);
        }
        return cities;
    }

}
