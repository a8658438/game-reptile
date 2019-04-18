package com.tc.reptile.entity;

import javax.persistence.*;

@Entity
@Table( name ="reptile_record" )
public class ReptileRecordEntity {

  @Id
  @Column(name = "id" )
  private long id;

  /**
   * 爬取时间
   */
  @Column(name = "reptile_time" )
  private long reptileTime;

  /**
   * 更新时间
   */
  @Column(name = "update_time" )
  private long updateTime;

  /**
   * 爬取的网站数量
   */
  @Column(name = "reptile_count" )
  private long reptileCount;

  /**
   * 完成数量
   */
  @Column(name = "finish_count" )
  private long finishCount;

//  /**
//   * 状态 0未完成 1已完成
//   */
//  @Column(name = "status" )
//  private long status;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getReptileTime() {
    return reptileTime;
  }

  public void setReptileTime(long reptileTime) {
    this.reptileTime = reptileTime;
  }


  public long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
  }


  public long getReptileCount() {
    return reptileCount;
  }

  public void setReptileCount(long reptileCount) {
    this.reptileCount = reptileCount;
  }


  public long getFinishCount() {
    return finishCount;
  }

  public void setFinishCount(long finishCount) {
    this.finishCount = finishCount;
  }


//  public long getStatus() {
//    return status;
//  }
//
//  public void setStatus(long status) {
//    this.status = status;
//  }

}
