package cn.sdkd.ccse.jsqles.controller;

import cn.sdkd.ccse.jsqles.model.Examination;
import cn.sdkd.ccse.jsqles.model.Question;
import cn.sdkd.ccse.jsqles.service.IExaminationService;
import cn.sdkd.ccse.jsqles.service.IQuestionService;
import com.wangzhixuan.commons.base.BaseController;
import com.wangzhixuan.commons.result.PageInfo;
import com.wangzhixuan.commons.utils.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/question")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;
    @Autowired
    private IExaminationService examinationService;
    /**
     * 权限管理页
     *
     * @return
     */
    @GetMapping("/manager")
    public String manager() {
        return "jsqles_admin/question/question";
    }

    /**
     * 权限列表
     *
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @PostMapping("/dataGrid")
    @ResponseBody
    public Object dataGrid(Question ques, Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);

        Map<String, Object> condition = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(ques.getQuesname())) {
            condition.put("quesname", ques.getQuesname());
        }
        if (ques.getExamid() != null) {
            condition.put("examid", ques.getExamid());
        }
        pageInfo.setCondition(condition);
        questionService.selectDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 添加权限页
     *
     * @return
     */
    @GetMapping("/addPage")
    public String addPage(Model model, Long id) {
        Examination e = examinationService.selectById(id);
        model.addAttribute("examination", e);
        return "jsqles_admin/question/questionAdd";
    }

    /**
     * 添加权限
     *
     * @param question
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid Question question) {
        questionService.insert(question);
        return renderSuccess("添加成功！");
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {
        questionService.deleteById(id);
        return renderSuccess("删除成功！");
    }

    /**
     * 编辑页
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/editPage")
    public String editPage(Model model, Long id) {
        Question question = questionService.selectById(id);
        model.addAttribute("question", question);
        return "jsqles_admin/question/questionEdit";
    }

    /**
     * 更新
     *
     * @param question
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(@Valid Question question) {

        /* 带html标记的string被转义，所以需要恢复 */
        String content = StringEscapeUtils.unescapeHtml(question.getQuescontent());
        String preq = StringEscapeUtils.unescapeHtml(question.getQuespreq());
        String resultcheck = StringEscapeUtils.unescapeHtml(question.getResultcheck());
        String resultquery = StringEscapeUtils.unescapeHtml(question.getResultquery());
        question.setQuescontent(content);
        question.setQuespreq(preq);
        question.setResultcheck(resultcheck);
        question.setResultquery(resultquery);
        questionService.updateById(question);
        return renderSuccess("编辑成功！");
    }

    /*题目配置页面*/
    @RequestMapping("/configPage")
    public String configPage(Model model, Long id) {
        Question question = questionService.selectById(id);
        model.addAttribute("question", question);
        return "jsqles_admin/question/questionConfig";
    }

    /*题目的实验前提配置页面*/
    @RequestMapping("/preqConfigPage")
    public String configPreqPage(Model model, Long id) {
        Question question = questionService.selectById(id);
        model.addAttribute("question", question);
        return "jsqles_admin/question/preqConfigPage";
    }

    /*题目的结构验证配置页面*/
    @RequestMapping("/evalConfigPage")
    public String evalConfigPage(Model model, Long id) {
        Question question = questionService.selectById(id);
        model.addAttribute("question", question);
        return "jsqles_admin/question/evalConfigPage";
    }

    /*题目的结果验证配置页面*/
    @RequestMapping("/resultQueryConfigPage")
    public String resultQueryConfigPage(Model model, Long id) {
        Question question = questionService.selectById(id);
        model.addAttribute("question", question);
        return "jsqles_admin/question/resultQueryConfigPage";
    }

}
