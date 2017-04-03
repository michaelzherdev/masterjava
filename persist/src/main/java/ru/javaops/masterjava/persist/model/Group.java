package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * Created by Mikhail on 02.04.2017.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class Group extends BaseEntity{

    private @NonNull String name;
    private @NonNull GroupType type;

    public Group(int id, String name, GroupType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
