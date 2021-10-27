package top.yang.domain.pojo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

/**
 * @author PrideYang
 */

public abstract class BaseEntity extends BaseBean {

  public BaseEntity() {
    super();
  }

  public BaseEntity(LocalDateTime createTime, LocalDateTime updateTime) {
    super();
    this.createTime = createTime;
    this.updateTime = updateTime;
  }

  /**
   *
   */
  @CreatedDate
  @Column(value = "create_time")
  private LocalDateTime createTime;

  /**
   *
   */
  @LastModifiedDate
  @Column(value = "update_time")
  private LocalDateTime updateTime;

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }
}
