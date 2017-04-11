package ru.javaops.masterjava.export;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mikhail on 11.04.17.
 */

@Slf4j
public class GroupImporter {
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    public Map<String, Group> process(StaxStreamProcessor processor) throws XMLStreamException {
        val projectMap = projectDao.getAsMap();
        val newProjects = new ArrayList<Project>();

        val groupMap = groupDao.getAsMap();
        val newGroups = new ArrayList<Group>();
        String element;
        String projectName = "";

        while ((element = processor.doUntilAny(XMLEvent.START_ELEMENT, "Project", "Group", "Cities")) != null) {
            if (element.equals("Cities")) break;


            List<PayloadImporter.FailedGroup> failed = new ArrayList<>();

            if (element.equals("Project")) {
                final String name = processor.getAttribute("name");
                final String description = processor.getElementValue("description");
                projectName = name;
                if (!projectMap.containsKey(name)) {
                    Project project = new Project(name, description);
                    newProjects.add(project);
                    log.info("Insert " + project);
                    projectDao.insertGeneratedId(project);
                }
            }

            if (element.equals("Group")) {
                final String name = processor.getAttribute("name");
                final GroupType type = GroupType.valueOf(processor.getAttribute("type"));

                if (!groupMap.containsKey(name)) {
                    Project project = projectDao.getAsMap().get(projectName);
                    if (project == null) {
                        // must not be here
                        failed.add(new PayloadImporter.FailedGroup(name, "Project '" + projectName + "' is not present in DB"));
                    } else {
                        Group group = new Group(name, type, project.getId());
                        newGroups.add(group);
                        log.info("Insert " + group);
                        groupDao.insert(group);

                    }
                }
            }
        }
        return groupDao.getAsMap();
    }
}
