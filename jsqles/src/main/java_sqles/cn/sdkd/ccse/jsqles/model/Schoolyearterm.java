package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Term entity. @author MyEclipse Persistence Tools
 */

public class Schoolyearterm implements java.io.Serializable {


	// Fields
	@TableId
	private Long id;
	private String name;

	private Long pid;
	private Timestamp startdate;

	private Timestamp enddate;
	private Integer weeks;

	// Constructors

	/** default constructor */
	public Schoolyearterm() {
	}

	/** minimal constructor */
	public Schoolyearterm(Long termid) {
		this.id = termid;
	}

	/** full constructor */
	public Schoolyearterm(Long termid, String name,
			Timestamp startdate, Integer weeks) {
		this.id = termid;
		this.name = name;
		this.startdate = startdate;
		this.weeks = weeks;
	}

	// Property accessors
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getStartdate() {
		return this.startdate;
	}

	public void setStartdate(Timestamp startdate) {
		this.startdate = startdate;
	}

	public Integer getWeeks() {
		return this.weeks;
	}

	public void setWeeks(Integer weeks) {
		this.weeks = weeks;
	}

	public Timestamp getEnddate() {
		return enddate;
	}

	public void setEnddate(Timestamp enddate) {
		this.enddate = enddate;
	}


}