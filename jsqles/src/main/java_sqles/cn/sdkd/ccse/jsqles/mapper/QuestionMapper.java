package cn.sdkd.ccse.jsqles.mapper;

import cn.sdkd.ccse.jsqles.model.Question;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 2017/7/13.
 */
public interface QuestionMapper extends BaseMapper<Question> {
    Question selectById(Long quesid);

    List<Map<String, Object>> selectQuestionPage(Pagination page, Map<String, Object> params);

    List<Map<String, Object>> selectQuestionStatusByUser(Pagination page, @Param("userid") Long userid, @Param("examid")Long examid);

    boolean refreshCache();
}
