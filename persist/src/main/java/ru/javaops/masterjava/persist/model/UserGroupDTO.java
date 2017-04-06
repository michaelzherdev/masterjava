package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * Created by Mikhail on 05.04.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupDTO {

    private @NonNull Integer userId;
    private @NonNull Integer groupId;
}
