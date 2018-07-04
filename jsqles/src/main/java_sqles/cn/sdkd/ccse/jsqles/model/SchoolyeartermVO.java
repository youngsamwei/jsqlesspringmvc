package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

import java.sql.Timestamp;

/**
 * Term entity. @author MyEclipse Persistence Tools
 */

public class SchoolyeartermVO implements java.io.Serializable {


	// Fields
	@TableId
	private Long termid;
	private String termname;

	private Long schoolYearId;
	private String schoolYearName;
	private Timestamp termStartdate;

	private Timestamp termEnddate;
	private Integer termWeeks;

	// Constructors

	public Long getTermid() {
		return termid;
	}

	public void setTermid(Long termid) {
		this.termid = termid;
	}

	public String getTermname() {
		return termname;
	}

	public void setTermname(String termname) {
		this.termname = termname;
	}

	public Long getSchoolYearId() {
		return schoolYearId;
	}

	public void setSchoolYearId(Long schoolYearId) {
		this.schoolYearId = schoolYearId;
	}

	public String getSchoolYearName() {
		return schoolYearName;
	}

	public void setSchoolYearName(String schoolYearName) {
		this.schoolYearName = schoolYearName;
	}

	public Timestamp getTermStartdate() {
		return termStartdate;
	}

	public void setTermStartdate(Timestamp termStartdate) {
		this.termStartdate = termStartdate;
	}

	public Timestamp getTermEnddate() {
		return termEnddate;
	}

	public void setTermEnddate(Timestamp termEnddate) {
		this.termEnddate = termEnddate;
	}

	public Integer getTermWeeks() {
		return termWeeks;
	}

	public void setTermWeeks(Integer termWeeks) {
		this.termWeeks = termWeeks;
	}
}