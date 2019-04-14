package com.tc.reptile.entity;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
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
	@Column(name = "source_id" )
	private Long sourceId;

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
	 * 文章缩略图
	 */
   	@Column(name = "image_url" )
	private String imageUrl;
   	/**
	 * 文章内容缩略
	 */
   	@Column(name = "content_breviary" )
	private String contentBreviary;
   	/**
	 * 文章分类
	 */
   	@Column(name = "type" )
	private String type;

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
	 * 热度
	 */
   	@Column(name = "hot" )
	private Integer hot;

	/**
	 * 状态
	 */
	@Column(name = "status")
	private Integer status;

	/**
	 * 创建时间
	 */
   	@Column(name = "create_time" )
	private Integer createTime;


	public String getContentBreviary() {
		return contentBreviary;
	}

	public void setContentBreviary(String contentBreviary) {
		this.contentBreviary = contentBreviary;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
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

	public Integer getHot() {
		return hot;
	}

	public void setHot(Integer hot) {
		this.hot = hot;
	}
}
