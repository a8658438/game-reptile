package com.tc.reptile.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description  
 * @Author  Yzjiang
 * @Date 2019-03-29 
 */

@Entity
@Table ( name ="game_appear_record" )
public class GameAppearRecordEntity implements Serializable {

	private static final long serialVersionUID =  4807268259340580889L;

   	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id" )
	private Long id;

	/**
	 * 游戏名称ID
	 */
   	@Column(name = "game_name" )
	private String gameName;

	/**
	 * 发布时间
	 */
   	@Column(name = "release_time" )
	private Integer releaseTime;

	/**
	 * 文章ID
	 */
   	@Column(name = "article_id" )
	private Long articleId;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Integer getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Integer releaseTime) {
		this.releaseTime = releaseTime;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
}
