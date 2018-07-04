package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

/**
 * ExamCurr entity. @author MyEclipse Persistence Tools
 */

public class ExamCurr implements java.io.Serializable {

	// Fields
	@TableId
	private Long examCurrId;

	private Long examid;

	private Long curriculaid;
	private Integer week;

	// Constructors

	/** default constructor */
	public ExamCurr() {
	}

	/** full constructor */
	public ExamCurr(Long examination, Long curricula, Integer week) {
		this.examid = examination;
		this.curriculaid = curricula;
		this.week = week;
	}

	public Long getCurriculaid() {
		return curriculaid;
	}

	public void setCurriculaid(Long curriculaid) {
		this.curriculaid = curriculaid;
	}

	// Property accessors

	public Long getExamCurrId() {
		return this.examCurrId;
	}

	public void setExamCurrId(Long examCurrId) {
		this.examCurrId = examCurrId;
	}

	public Integer getWeek() {
		return this.week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}


	public Long getExamid() {
		return examid;
	}

	public void setExamid(Long examid) {
		this.examid = examid;
	}
}