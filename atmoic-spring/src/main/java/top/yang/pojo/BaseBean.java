package top.yang.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Y
 */
@MappedSuperclass
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class BaseBean implements Serializable {

  private static final long serialVersionUID = -108907189034815108L;


  /**
   *
   */
  @CreatedDate
  @Column(name = "dt_create")
  private LocalDateTime dtCreate;

  /**
   *
   */
  @LastModifiedDate
  @Column(name = "dt_update")
  private LocalDateTime dtUpdate;

  public LocalDateTime getDtCreate() {
    return dtCreate;
  }

  public void setDtCreate(LocalDateTime dtCreate) {
    this.dtCreate = dtCreate;
  }

  public LocalDateTime getDtUpdate() {
    return dtUpdate;
  }

  public void setDtUpdate(LocalDateTime dtUpdate) {
    this.dtUpdate = dtUpdate;
  }
}
