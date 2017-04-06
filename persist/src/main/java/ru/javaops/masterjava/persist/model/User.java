package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
    @Column("full_name")
    private @NonNull String fullName;
    private @NonNull String email;
    private @NonNull UserFlag flag;
    @Column("user_id")
    private @NonNull Integer cityId;

    public User(String fullName, String email, UserFlag flag) {
        this.fullName = fullName;
        this.email = email;
        this.flag = flag;
    }

    public User(Integer id, String fullName, String email, UserFlag flag) {
        this(fullName, email, flag);
        this.id=id;
    }

    public User(Integer id, String fullName, String email, UserFlag flag, Integer cityId) {
        this(id, fullName, email, flag);
        this.cityId=cityId;
    }
}