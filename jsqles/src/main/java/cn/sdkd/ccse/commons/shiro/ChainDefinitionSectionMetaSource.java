package cn.sdkd.ccse.commons.shiro;

/**
 * Created by Sam on 2017/7/11.
 */

import com.wangzhixuan.model.Resource;
import com.wangzhixuan.service.IResourceService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.config.Ini;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 产生责任链，确定每个url的访问权限
 *
 */
public class ChainDefinitionSectionMetaSource implements FactoryBean<Ini.Section> {

    @Autowired
    private IResourceService resourceService;

    // 静态资源访问权限
    private String filterChainDefinitions = null;

    public Ini.Section getObject() throws Exception {
        /*初始化到缓存*/
//        new ConfigUtils().initTableField(resourcesMapper);
        Ini ini = new Ini();
        // 加载默认的url
        ini.load(filterChainDefinitions);
        Ini.Section section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        // 循环Resource的url,逐个添加到section中。section就是filterChainDefinitionMap,
        // 里面的键就是链接URL,值就是存在什么条件才能访问该链接
        List<Resource> lists = resourceService.selectAll();
        for (Resource resource : lists) {
            // 构成permission字符串
            if (StringUtils.isNotEmpty(resource.getUrl() + "")
                    && StringUtils.isNotEmpty(resource.getName() + "")) {
                String permission = "perms[" + resource.getUrl() + "], kickout";
                System.out.println(permission);
                // 不对角色进行权限验证
                // 如需要则 permission = "roles[" + resources.getResKey() + "]";
                section.put(resource.getUrl() + "", permission);
            }

        }
        // 所有资源的访问权限，必须放在最后
		/*section.put("/**", "authc");*/
        /** 如果需要一个用户只能登录一处地方,,修改为 section.put("/**", "authc,kickout,sysUser,user"); **/
//        section.put("/**", "kickout, authc");
        section.put("/**", " authc");
        return section;
    }

    /**
     * 通过filterChainDefinitions对默认的url过滤定义
     *
     * @param filterChainDefinitions
     *            默认的url过滤定义
     */
    public void setFilterChainDefinitions(String filterChainDefinitions) {
        this.filterChainDefinitions = filterChainDefinitions;
    }

    public Class<?> getObjectType() {
        return this.getClass();
    }

    public boolean isSingleton() {
        return false;
    }
}
