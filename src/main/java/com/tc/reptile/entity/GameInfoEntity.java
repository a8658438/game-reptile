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
@Table ( name ="game_info" )
public class GameInfoEntity implements Serializable {

	private static final long serialVersionUID =  3745332898782374735L;

   	@Id
	@Column(name = "id" )
	private Long id;

	/**
	 * 游戏名称
	 */
   	@Column(name = "game_name" )
	private String gameName;

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
}
