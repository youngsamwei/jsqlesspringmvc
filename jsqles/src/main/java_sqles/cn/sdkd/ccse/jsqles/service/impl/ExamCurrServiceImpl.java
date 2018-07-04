package cn.sdkd.ccse.jsqles.service.impl;

import cn.sdkd.ccse.jsqles.mapper.ExamCurrMapper;
import cn.sdkd.ccse.jsqles.model.Curricula;
import cn.sdkd.ccse.jsqles.model.ExamCurr;
import cn.sdkd.ccse.jsqles.service.IExamCurrService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wangzhixuan.commons.result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 2017/9/28.
 */
@Service
public class ExamCurrServiceImpl extends ServiceImpl<ExamCurrMapper, ExamCurr> implements IExamCurrService {
    @Autowired
    private ExamCurrMapper examCurrMapper;

    public List<ExamCurr> selectAll() {
        EntityWrapper<ExamCurr> wrapper = new EntityWrapper<ExamCurr>();
        return examCurrMapper.selectList(wrapper);
    }

    public List<ExamCurr> selectListByCurrid(Long currid) {
        EntityWrapper<ExamCurr> wrapper = new EntityWrapper<ExamCurr>();
        wrapper.eq("curriculaid", currid.toString());
        return examCurrMapper.selectList(wrapper);
    }

    @Override
    public void selectDataGrid(PageInfo pageInfo) {
        Page<Curricula> page = new Page<Curricula>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = examCurrMapper.selectExamCurrNamedPage(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());

    }
}
