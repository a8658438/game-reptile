package com.tc.reptile.entity;

import javax.persistence.*;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id" )
	private Long id;

	/**
	 * 文章详细内容
	 */
   	@Column(name = "content" )
	private String content;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(columnDefinition="BLOB",nullable=true)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void String(String content) {
		this.content = content;
	}
}
