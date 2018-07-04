package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

import java.sql.Timestamp;

/**
 * Exercisebook entity. @author MyEclipse Persistence Tools
 */

public class Exercisebook implements java.io.Serializable {

	// Fields
	@TableId
	private Long exerbookid;
	private Long quesid;
	private Long curriculaid;
	private Long userid;
	private String answer;
	private Timestamp posttime;
	private Timestamp starttime;
	private String eval;  /* 验证结果，如果正确，值是 solved*/
	private String postext; /* 完成实验题目，用户提交的sql或代码*/
	private String resultset; /*用户提交的结果验证数据*/

	// Constructors

	/** default constructor */
	public Exercisebook() {
	}

	/** minimal constructor */
	public Exercisebook(Long question, Long exerbookid,
			String answer, Timestamp posttime, Timestamp starttime, String eval) {
		this.setQuesid(question);
		this.exerbookid = exerbookid;
		this.answer = answer;
		this.posttime = posttime;
		this.starttime = starttime;
		this.eval = eval;
	}

	/** full constructor */
	public Exercisebook(Long question, Long appUser,
			Long exerbookid, String answer, Timestamp posttime,
			Timestamp starttime, String eval) {
		this.setQuesid(question);
		this.setUserid(appUser);
		this.exerbookid = exerbookid;
		this.answer = answer;
		this.posttime = posttime;
		this.starttime = starttime;
		this.eval = eval;
	}

	// Property accessors

	public Long getExerbookid() {
		return this.exerbookid;
	}

	public void setExerbookid(Long exerbookid) {
		this.exerbookid = exerbookid;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Timestamp getPosttime() {
		return this.posttime;
	}

	public void setPosttime(Timestamp posttime) {
		this.posttime = posttime;
	}

	public Timestamp getStarttime() {
		return this.starttime;
	}

	public void setStarttime(Timestamp starttime) {
		this.starttime = starttime;
	}

	public String getEval() {
		return this.eval;
	}

	public void setEval(String eval) {
		this.eval = eval;
	}

	public String getPostext() {
		return this.postext;
	}

	public void setPostext(String postext) {
		this.postext = postext;
	}

	public String getResultset() {
		return this.resultset;
	}

	public void setResultset(String resultset) {
		this.resultset = resultset;
	}

	public Long getCurriculaid() {
		return curriculaid;
	}

	public void setCurriculaid(Long curriculaid) {
		this.curriculaid = curriculaid;
	}

	public Long getQuesid() {
		return quesid;
	}

	public void setQuesid(Long quesid) {
		this.quesid = quesid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}
}