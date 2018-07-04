package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

import java.util.HashSet;
import java.util.Set;


/**
 * Question entity. @author MyEclipse Persistence Tools
 */

public class Question implements java.io.Serializable {

	// Fields
	@TableId
	private Long quesid;

	private Long examid;
	private String quesname;
	private String quescontent;
	private String queseval; /* 结构验证的内容 */
	private String quesanswer;
	private String quesrequired; /* 需要从客户端读取那些对象的信息 */
	private String quespreq; /* 实验题目的前提 */
	private String resultcheck; /* 结果验证的结果 */
	private String resultquery; /* 教师进行结果验证时的sql语句 */
	private Boolean ifpostext; /* 是否需要学生提交sql语句或代码 */
	private Integer postexttype; /* 是否需要学生提交sql语句或代码 */

	// Constructors

	/** default constructor */
	public Question() {
	}

	/** minimal constructor */
	public Question(Long quesid, String quesname, String quescontent) {
		this.quesid = quesid;
		this.quesname = quesname;
		this.quescontent = quescontent;
	}

	/** full constructor */
	public Question(Long quesid, Long examination, String quesname,
			String quescontent, String queseval, String quesanswer,
			String quesrequired, String quespreq, String resultcheck,
			String resultquery, Boolean ifpostext, Integer postexttype) {
		this.quesid = quesid;
		this.examid = examination;
		this.quesname = quesname;
		this.quescontent = quescontent;
		this.queseval = queseval;
		this.quesanswer = quesanswer;
		this.quesrequired = quesrequired;
		this.quespreq = quespreq;
		this.resultcheck = resultcheck;
		this.resultquery = resultquery;
		this.ifpostext = ifpostext;
		this.postexttype = postexttype;
	}

	// Property accessors

	public Long getExamid() {
		return examid;
	}

	public void setExamid(Long examid) {
		this.examid = examid;
	}

	public Long getQuesid() {
		return this.quesid;
	}

	public void setQuesid(Long quesid) {
		this.quesid = quesid;
	}

	public String getQuesname() {
		return this.quesname;
	}

	public void setQuesname(String quesname) {
		this.quesname = quesname;
	}

	public String getQuescontent() {
		return this.quescontent;
	}

	public void setQuescontent(String quescontent) {
		this.quescontent = quescontent;
	}

	public String getQueseval() {
		return this.queseval;
	}

	public void setQueseval(String queseval) {
		this.queseval = queseval;
	}

	public String getQuesanswer() {
		return this.quesanswer;
	}

	public void setQuesanswer(String quesanswer) {
		this.quesanswer = quesanswer;
	}

	public String getQuesrequired() {
		return this.quesrequired;
	}

	public void setQuesrequired(String quesrequired) {
		this.quesrequired = quesrequired;
	}

	public String getQuespreq() {
		return this.quespreq;
	}

	public void setQuespreq(String quespreq) {
		this.quespreq = quespreq;
	}

	public String getResultcheck() {
		return this.resultcheck;
	}

	public void setResultcheck(String resultcheck) {
		this.resultcheck = resultcheck;
	}

	public String getResultquery() {
		if (resultquery != null) {
			return resultquery.replaceAll("[\\t\\n\\r]", " ");
		} else {
			return null;
		}
	}

	public void setResultquery(String resultquery) {
		this.resultquery = resultquery;
	}

	public Boolean getIfpostext() {
		return this.ifpostext;
	}

	public void setIfpostext(Boolean ifpostext) {
		this.ifpostext = ifpostext;
	}

	public Integer getPostexttype() {
		return this.postexttype;
	}

	public void setPostexttype(Integer postexttype) {
		this.postexttype = postexttype;
	}


}