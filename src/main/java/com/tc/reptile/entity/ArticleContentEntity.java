package com.tc.reptile.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.io.InputStream;

/**
 * @Description  
 * @Author  Yzjiang
 * @Date 2019-03-29 
 */

@Entity
@Table ( name ="article_content" )
public class ArticleContentEntity implements Serializable {

	private static final long serialVersionUID =  7380510781684918519L;

   	@Id
	@Column(name = "id" )
	private Long id;

	/**
	 * 文章详细内容
	 */
   	@Column(name = "content" )
	private InputStream content;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}
}
