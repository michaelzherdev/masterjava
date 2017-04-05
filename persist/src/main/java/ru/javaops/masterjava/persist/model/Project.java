package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * Created by Mikhail on 02.04.2017.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class Project extends BaseEntity {
    private @NonNull String name;
    private String description;

    public Project(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
