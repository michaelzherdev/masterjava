package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * Created by Mikhail on 02.04.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {

    private @NonNull String id;
    private @NonNull String value;
}
