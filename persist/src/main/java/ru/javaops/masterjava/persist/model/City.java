package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

/**
 * Created by Mikhail on 02.04.2017.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class City extends BaseEntity {

    @Column("id_str")
    private @NonNull String idStr;
    private @NonNull String value;

    public City(int id, String idStr, String value) {
        this(idStr, value);
        this.id = id;
    }
}
