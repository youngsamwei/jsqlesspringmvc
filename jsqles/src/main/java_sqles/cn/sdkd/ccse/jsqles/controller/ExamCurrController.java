package cn.sdkd.ccse.jsqles.controller;

import cn.sdkd.ccse.jsqles.model.ExamCurr;
import cn.sdkd.ccse.jsqles.service.IExamCurrService;
import cn.sdkd.ccse.jsqles.service.IExaminationService;
import com.wangzhixuan.commons.base.BaseController;
import com.wangzhixuan.commons.result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam on 2017/9/28.
 */
@Controller
@RequestMapping("/examcurr")
public class ExamCurrController extends BaseController {
    @Autowired
    private IExamCurrService examCurrService;
    @Autowired
    private IExaminationService examinationService;

    @RequestMapping("/add")
    @ResponseBody
    public Object add(@Valid ExamCurr examCurr) {
        examCurrService.insert(examCurr);
        examinationService.refreshCache();
        return renderSuccess("添加成功！");
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {
        examCurrService.deleteById(id);
        examinationService.refreshCache();
        return renderSuccess("删除成功！");
    }

    @RequestMapping("/dataGrid")
    @ResponseBody
    public Object selectListByCurrid(ExamCurr examCurr, Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);

        Map<String, Object> condition = new HashMap<String, Object>();

        if (examCurr.getCurriculaid() != null){
            condition.put("curriculaid", examCurr.getCurriculaid());
        }
        if (examCurr.getExamid() != null){
            condition.put("examid", examCurr.getExamid());
        }
        if (examCurr.getExamCurrId() != null) {
            condition.put("examCurrId", examCurr.getExamCurrId());
        }
        if (examCurr.getWeek() != null) {
            condition.put("week", examCurr.getWeek());
        }
        pageInfo.setCondition(condition);
        examCurrService.selectDataGrid(pageInfo);
        return pageInfo;
    }

}
