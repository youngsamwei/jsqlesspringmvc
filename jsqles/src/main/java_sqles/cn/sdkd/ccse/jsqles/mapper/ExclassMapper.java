package cn.sdkd.ccse.jsqles.mapper;

import cn.sdkd.ccse.jsqles.model.Exclass;
import cn.sdkd.ccse.jsqles.model.ExclassVO;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.wangzhixuan.model.Organization;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Sam on 2017/7/13.
 */
public interface ExclassMapper extends BaseMapper<Exclass> {
    List<Exclass> selectExclassByUserid(Long userid);

    List<ExclassVO> selectTree();

    /**
     * 查询指定实验班级中的行政班级
     * @param exclassid
     * @return
     */
    List<Organization> selectLinkedClass(@Param("exclassid") Long exclassid);

    /**
     * 查询指定不属于实验班级中的行政班级
     * @param exclassid
     * @return
     */
    List<Organization> selectUnlinkedClass(@Param("exclassid") Long exclassid);

    /**
     * 用于刷新缓存：当增加或删除实验班级中的行政班级后调用
     * @return
     */
    boolean refreshCache();
}
