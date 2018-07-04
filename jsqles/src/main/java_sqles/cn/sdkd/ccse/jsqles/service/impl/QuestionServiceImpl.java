package cn.sdkd.ccse.jsqles.service.impl;

import cn.sdkd.ccse.jsqles.mapper.QuestionMapper;
import cn.sdkd.ccse.jsqles.model.Question;
import cn.sdkd.ccse.jsqles.service.IQuestionService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wangzhixuan.commons.result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 2017/7/13.
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    public List<Question> selectAll() {
        EntityWrapper<Question> wrapper = new EntityWrapper<Question>();
        wrapper.orderBy("quesid");
        return questionMapper.selectList(wrapper);
    }

    @Override
    public void selectDataGrid(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = questionMapper.selectQuestionPage(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());

    }

    @Override
    public void selectQuestionStatusByUser(PageInfo pageInfo, Long userid, Long examid) {
        Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageInfo.getNowpage(), pageInfo.getSize());
        List<Map<String, Object>> list = questionMapper.selectQuestionStatusByUser(page, userid, examid);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    ;public boolean refreshCache(){
        return questionMapper.refreshCache();
    };
}
