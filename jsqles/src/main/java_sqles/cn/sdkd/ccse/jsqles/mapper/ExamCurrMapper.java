package cn.sdkd.ccse.jsqles.mapper;

import cn.sdkd.ccse.jsqles.model.ExamCurr;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 2017/7/13.
 */
public interface ExamCurrMapper extends BaseMapper<ExamCurr> {
    List<Map<String, Object>> selectExamCurrPage(Pagination page, Map<String, Object> params);
    List<Map<String, Object>> selectExamCurrNamedPage(Pagination page, Map<String, Object> params);

}
