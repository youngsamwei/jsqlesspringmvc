package cn.sdkd.ccse.jsqles.mapper;

import cn.sdkd.ccse.jsqles.model.Examination;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Sam on 2017/7/13.
 */
public interface ExaminationMapper  extends BaseMapper<Examination> {
    /**
     * <p>
     * 根据 当前班级、当前时间的课程表查询实验
     * </p>
     *
     * @param wrapper
     *            实体对象封装操作类（可以为 null）
     * @return List<T>
     */
    List<Examination> selectListByCurricula(@Param("ew") Wrapper<Examination> wrapper);

    List<Examination> selectCurrentExaminationListByExclassid(@Param("classnos")List<Long> classnos, @Param("currdate")Timestamp ts);

    boolean refreshCache();
}
