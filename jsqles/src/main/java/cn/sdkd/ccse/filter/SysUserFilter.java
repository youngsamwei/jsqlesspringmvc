package cn.sdkd.ccse.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.wangzhixuan.commons.shiro.ShiroUser;
import com.wangzhixuan.service.IUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;

import org.springframework.beans.factory.annotation.Autowired;


public class SysUserFilter extends PathMatchingFilter {

    @Autowired
	private IUserService userService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();

        request.setAttribute("user", user);
        return true;
    }
}