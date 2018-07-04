package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

/**
 * 用于实验班级的管理
 * Exclass entity. @author MyEclipse Persistence Tools
 */

public class ExclassVO implements java.io.Serializable {

	// Fields
	@TableId
	private Long exclassid;

	private String exclassname;

	/**
	 * 行政班所属的实验班的编号，实验班的pid为null
	 */
	private Long pid;

	// Constructors

	/** default constructor */
	public ExclassVO() {
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


	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}
}