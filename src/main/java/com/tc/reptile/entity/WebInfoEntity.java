package com.tc.reptile.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Description  
 * @Author  Yzjiang
 * @Date 2019-03-29 
 */

@Entity
@Table ( name ="web_info" )
public class WebInfoEntity implements Serializable {

	private static final long serialVersionUID =  7506170567919262238L;

   	@Id
	@Column(name = "id" )
	private Long id;

	/**
	 * 网站地址
	 */
   	@Column(name = "url" )
	private String url;

	/**
	 * 最后爬取时间
	 */
   	@Column(name = "last_time" )
	private Integer lastTime;

	/**
	 * 网站名称
	 */
   	@Column(name = "web_name" )
	private String webName;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getLastTime() {
		return lastTime;
	}

	public void setLastTime(Integer lastTime) {
		this.lastTime = lastTime;
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}
}
