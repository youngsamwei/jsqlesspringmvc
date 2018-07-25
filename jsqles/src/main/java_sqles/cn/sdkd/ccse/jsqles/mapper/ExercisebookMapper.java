package cn.sdkd.ccse.jsqles.mapper;

import cn.sdkd.ccse.jsqles.model.Exercisebook;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.wangzhixuan.commons.result.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 2017/7/13.
 */
public interface ExercisebookMapper extends BaseMapper<Exercisebook> {
    Long insertAndGetId(Exercisebook exercisebook);
    List<Map<String, Object>> selectQuestionSolvedRatio(Pagination page);
}
