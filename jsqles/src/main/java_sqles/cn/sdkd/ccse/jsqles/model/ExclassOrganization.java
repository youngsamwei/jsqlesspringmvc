package cn.sdkd.ccse.jsqles.model;

import com.baomidou.mybatisplus.annotations.TableId;

/**
 * 实验班级.，与行政班级存在区别。
 * 行政班级用于管理学生用户，用于登录和密码管理。
 * 实验班级用于安排实验的班级，实验班级与行政班级是多对多关系。
 *
 * Exclass entity. @author MyEclipse Persistence Tools
 */

public class ExclassOrganization implements java.io.Serializable {

	// Fields
	@TableId
    private Long id;
	private Long exclassid;

    private Long organizationid;

	// Constructors

	/** default constructor */
	public ExclassOrganization() {
	}

	/** minimal constructor */
	public ExclassOrganization(Long cno) {
		this.exclassid = cno;
	}


	public Long getExclassid() {
		return exclassid;
	}

	public void setExclassid(Long exclassid) {
		this.exclassid = exclassid;
	}

// Property accessors


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationid() {
        return organizationid;
    }

    public void setOrganizationid(Long organizationid) {
        this.organizationid = organizationid;
    }


}