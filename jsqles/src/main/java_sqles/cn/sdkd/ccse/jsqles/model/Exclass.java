package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

import java.util.HashSet;
import java.util.Set;

/**
 * 实验班级.，与行政班级存在区别。
 * 行政班级用于管理学生用户，用于登录和密码管理。
 * 实验班级用于安排实验的班级，实验班级与行政班级是多对多关系。
 *
 * Exclass entity. @author MyEclipse Persistence Tools
 */

public class Exclass implements java.io.Serializable {

	// Fields
	@TableId
	private Long exclassid;

	private String exclassname;

	// Constructors

	/** default constructor */
	public Exclass() {
	}

	/** minimal constructor */
	public Exclass(Long cno) {
		this.exclassid = cno;
	}

	/** full constructor */
	public Exclass(Long exclassid, String cname) {
		this.exclassid = exclassid;
		this.exclassname = cname;
	}

	public Long getExclassid() {
		return exclassid;
	}

	public void setExclassid(Long exclassid) {
		this.exclassid = exclassid;
	}

	public String getExclassname() {
		return exclassname;
	}

	public void setExclassname(String exclassname) {
		this.exclassname = exclassname;
	}
// Property accessors



}