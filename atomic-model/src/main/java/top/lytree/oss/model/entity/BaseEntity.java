package top.lytree.oss.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Y
 */
@ToString
@Getter
@Setter
@RequiredArgsConstructor
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

//    @Column(name = "create_by")
//    @CreatedBy
//    private String createBy;
    /**
     *
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

//    @Column(name = "updated_by")
//    @LastModifiedBy
//    private String updatedBy;
    /**
     *
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createTime == null) {
            createTime = now;
        }

        if (updateTime == null) {
            updateTime = now;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        updateTime = LocalDateTime.now();

    }

    @PreRemove
    protected void preRemove() {
        updateTime = LocalDateTime.now();
    }

}
