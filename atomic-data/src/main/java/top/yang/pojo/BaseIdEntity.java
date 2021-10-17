package top.yang.pojo;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;

/**
 * @author PrideYang
 */
public abstract class BaseIdEntity extends BaseEntity {

  public BaseIdEntity() {
  }

  public BaseIdEntity(LocalDateTime createTime, LocalDateTime updateTime, Long id) {
    super(createTime, updateTime);
    this.id = id;
  }


  @Id
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
