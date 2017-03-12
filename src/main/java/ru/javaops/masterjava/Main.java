package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * User: gkislin
 * Date: 05.08.2015
 *
 * @link http://caloriesmng.herokuapp.com/
 * @link https://github.com/JavaOPs/topjava
 * <p>
 * HomeWork 02. Need to be set program arguments as project name
 * Домашнее задание
 * <p>
 * 1. Изменить XML схему:
 * 1.1 добавить проекты. Имеют название (нарпимер topjava, masterjava) и описание
 * 1.2 добавить группы. Имеют название и тип (REGISTERING/CURRENT/FINISHED). Группа принадлежат проекту, например проект topjava, группы topjava01,topjava02, ..
 * 1.3 сделать User.email аттрибутом.
 * 1.4 реализовать принадлежность участников разным группам (Admin состоит в группах topjava07, topjava08, masterjava01)
 * 2. Дополнить xml тестовыми данными.
 * 3. Реализовать консольное приложение, которые принимает параметром имя проекта в тестовом xml и выводит отсортированный список его участников (использовать JAXB).
 * Optional
 * <p>
 * 4. Сделать реализацию консольного приложения через StAX
 * 5. Из списка участников сделать html таблицу
 * 6. Вывести через XSLT преобразование html таблицу с группами заданного проекта
 */
public class Main {
    public static void main(String[] args) throws IOException, JAXBException {
        System.out.format("Hello MasterJava!\n");
        if (args.length == 1) {
            JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
            jaxbParser.setSchema(Schemas.ofClasspath("payload.xsd"));

            String groupName = args[0];
            Payload payload = jaxbParser.unmarshal(Resources.getResource("payload.xml").openStream());
            List<User> userList = payload.getUsers().getUser();

            //by group name (as command arg masterjava01, masterjava02, etc)
//            userList.stream()
//                    .filter(user -> user.getGroups()
//                            .stream()
//                            .filter(group -> group.getName().equals(groupName))
//                            .count() != 0)
//                    .sorted((o1, o2) -> o1.getFullName().compareTo(o2.getFullName()))
//                    .forEach(user -> System.out.println(user.getFullName()));

            //by project name (as command arg topjava, masterjava)
            Comparator<User> userComparator = (o1, o2) -> o1.getFullName().compareTo(o2.getFullName());
            userList.stream()
                    .filter(user -> user.getGroups()
                            .stream()
                            .filter(group -> group.getName().contains(groupName))
                            .count() != 0)
                    .sorted(userComparator)
                    .forEach(user -> System.out.println(user.getFullName()));

            //5. Из списка участников сделать html таблицу
            String html = "<html><head></head><body><table><tr>" +
                    "<th>fullname</th><th>email</th><th>city</th><th>flag</th></tr>";
            String endHtml = "</table></body></html>";
            StringBuilder stringBuilder = new StringBuilder(html);
            userList.stream()
                    .filter(user -> user.getGroups()
                            .stream()
                            .filter(group -> group.getName().contains(groupName))
                            .count() != 0)
                    .sorted(userComparator)
                    .forEach(user ->
            stringBuilder.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                    user.getFullName(), user.getEmail(), ((CityType)user.getCity()).getValue(), user.getFlag().value())));
            stringBuilder.append(endHtml);

            //write to file
            Writer writer = new FileWriter(new File("./table.html"));
            writer.write(stringBuilder.toString());
            writer.close();
        }
    }
}
