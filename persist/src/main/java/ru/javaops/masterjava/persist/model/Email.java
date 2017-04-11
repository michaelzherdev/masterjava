package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by mikhail on 11.04.17.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class Email extends BaseEntity{


    @Column("from_name")
    private @NonNull String from;
    private String subject;
    private String body;
    private @NonNull
    LocalDateTime date;
    private @NonNull String result;

    public Email(String fromName, String subject, String body, String result, LocalDateTime date) {
        this.from = fromName;
        this.subject = subject;
        this.body = body;
        this.result = result;
        this.date = date;
    }
}
