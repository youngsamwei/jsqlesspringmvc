package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

import java.util.HashSet;
import java.util.Set;

/**
 * Examination entity. @author MyEclipse Persistence Tools
 */

public class Examination implements java.io.Serializable {

	// Fields
	@TableId
	private Long examid;
	private String examname;
	private Short isOpen;

	// Constructors

	/** default constructor */
	public Examination() {
	}

	/** minimal constructor */
	public Examination(String examname) {
		this.examname = examname;
	}

	/** full constructor */
	public Examination(String examname, Short isOpen) {
		this.examname = examname;
		this.isOpen = isOpen;
	}

	// Property accessors

	public Long getExamid() {
		return this.examid;
	}

	public void setExamid(Long examid) {
		this.examid = examid;
	}

	public String getExamname() {
		return this.examname;
	}

	public void setExamname(String examname) {
		this.examname = examname;
	}

	public Short getIsOpen() {
		return this.isOpen;
	}

	public void setIsOpen(Short isOpen) {
		this.isOpen = isOpen;
	}


}