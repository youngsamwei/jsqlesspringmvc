package cn.sdkd.ccse.commons.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by sam on 2019/9/28.
 */
public class ConfigUtils {
    protected static Logger logger = LogManager.getLogger(ConfigUtils.class);

    /*当为true进行时间控制，当为false时，测试状态下获取得到所有的测试列表*/
    static public boolean selectCurrentExaminationListControl = true;

    private static Properties getProperties() {
        // 读取配置文件
        Resource resource = new ClassPathResource("/config/jsqles.properties");
        Properties props = new Properties();
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    private static void init(){
        Properties props =ConfigUtils.getProperties();
        String p = props.getProperty("select.current.examination.list.control");
        if (p.equalsIgnoreCase("false")){
            selectCurrentExaminationListControl = false;
            logger.info("select.current.examination.list.control 设置为 false" );
        }

    }

    static {
        ConfigUtils.init();
    }
}
