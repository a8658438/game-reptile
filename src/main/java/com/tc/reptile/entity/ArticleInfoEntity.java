package com.tc.reptile.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description  
 * @Author  Yzjiang
 * @Date 2019-03-29 
 */

@Entity
@Table ( name ="article_info" )
public class ArticleInfoEntity implements Serializable {

	private static final long serialVersionUID =  3714337261101212891L;

   	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id" )
	private Long id;

	/**
	 * 来源
	 */
   	@Column(name = "source" )
	private String source;

	/**
	 * 文章标题
	 */
   	@Column(name = "title" )
	private String title;

	/**
	 * 文章链接
	 */
   	@Column(name = "url" )
	private String url;

	/**
	 * 发布时间
	 */
   	@Column(name = "release_time" )
	private Integer releaseTime;

	/**
	 * 作者
	 */
   	@Column(name = "author" )
	private String author;

	/**
	 * 创建时间
	 */
   	@Column(name = "create_time" )
	private Integer createTime;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Integer releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
}
