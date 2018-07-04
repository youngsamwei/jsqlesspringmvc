package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

/**
 * Curricula entity. @author MyEclipse Persistence Tools
 */

public class Curricula implements java.io.Serializable {

	// Fields

	@TableId
	private Long curriculaid;
	private Long schoolyearid;

	private Long classno;
	private Long termid;
	private Long teacherid;
	private Integer startweek;
	private Integer endweek;
	private Integer section;
	private Integer weekday;

	// Constructors

	/** default constructor */
	public Curricula() {
	}

	/** full constructor */
	public Curricula(Long schoolyear, Long exclass, Long term,
					 Long teacher, Integer startweek, Integer endweek,
			Integer section, Integer weekday) {
		this.setSchoolyearid(schoolyear);
		this.setTermid(term);
		this.setTeacherid(teacher);
		this.setStartweek(startweek);
		this.setEndweek(endweek);
		this.setSection(section);
		this.setWeekday(weekday);
	}

	public Long getCurriculaid() {
		return curriculaid;
	}

	public void setCurriculaid(Long curriculaid) {
		this.curriculaid = curriculaid;
	}

	public Long getSchoolyearid() {
		return schoolyearid;
	}

	public void setSchoolyearid(Long schoolyearid) {
		this.schoolyearid = schoolyearid;
	}

	public Long getTermid() {
		return termid;
	}

	public void setTermid(Long termid) {
		this.termid = termid;
	}

	public Long getClassno() {
		return classno;
	}

	public void setClassno(Long classno) {
		this.classno = classno;
	}

	public Long getTeacherid() {
		return teacherid;
	}

	public void setTeacherid(Long teacherid) {
		this.teacherid = teacherid;
	}

	public Integer getStartweek() {
		return startweek;
	}

	public void setStartweek(Integer startweek) {
		this.startweek = startweek;
	}

	public Integer getEndweek() {
		return endweek;
	}

	public void setEndweek(Integer endweek) {
		this.endweek = endweek;
	}

	public Integer getSection() {
		return section;
	}

	public void setSection(Integer section) {
		this.section = section;
	}

	public Integer getWeekday() {
		return weekday;
	}

	public void setWeekday(Integer weekday) {
		this.weekday = weekday;
	}

	// Property accessors


}