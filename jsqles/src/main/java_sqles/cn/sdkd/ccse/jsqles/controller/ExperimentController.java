package cn.sdkd.ccse.jsqles.controller;

import cn.sdkd.ccse.jsqles.model.Exclass;
import cn.sdkd.ccse.jsqles.model.Exercisebook;
import cn.sdkd.ccse.jsqles.model.Question;
import cn.sdkd.ccse.jsqles.service.IExaminationService;
import cn.sdkd.ccse.jsqles.service.IExclassService;
import cn.sdkd.ccse.jsqles.service.IExercisebookService;
import cn.sdkd.ccse.jsqles.service.IQuestionService;
import com.wangzhixuan.commons.base.BaseController;
import com.wangzhixuan.commons.result.PageInfo;
import com.wangzhixuan.commons.shiro.ShiroUser;
import com.wangzhixuan.commons.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**  学生实验的控制器，实现：
 *   查询学生所属班级、当前的实验安排，以及完成状态；
 *   查询实验的题目信息，限时限定班级，以及完成状态；
 *
 * Created by Sam on 2017/9/20.
 */

@Controller
@RequestMapping("/experiment")
public class ExperimentController  extends BaseController {

    @Autowired
    private IQuestionService questionService;
    @Autowired
    private IExaminationService examinationService;
    @Autowired
    private IExclassService exclassService;
    @Autowired
    private IExercisebookService exercisebookService;


    /**
     * 查询当前时间当前班级的实验问题,并返回每个问题的完成状态：待实现
     *
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @PostMapping("/questionDataGrid")
    @ResponseBody
    public Object questionDataGrid(Long examid, Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);
        questionService.selectQuestionStatusByUser(pageInfo, getUserId(), examid);
        return pageInfo;
    }

    /*查询当前时间当前班级的实验题目：待实现
    * 附件返回信息：学生的完成状态
    * */
    @PostMapping(value = "/examinationTree")
    @ResponseBody
    public Object examinationTree() {
        Long userid = getUserId();
        List<Exclass> exs = exclassService.selectExclassByUserid(userid);
        List<Long> classnos = new ArrayList<Long>();
        for (Exclass ex : exs){
            classnos.add(ex.getExclassid());
        }
        /*如果不属于任何实验班级，则为了确保selectCurrentExaminationListByExclassid中的in有效，则增加一个不存在的实验班级编号-1*/
        if (classnos.size() <= 0){
            classnos.add(-1L);
        }
        return examinationService.selectCurrentExaminationListByExclassid(classnos);
    }

    /**
     * 实验的初始页面
     *
     * @return
     */
    @GetMapping("/index")
    public String index() {
        return "jsqles/experiment/index";
    }

    /**
     * 开始实验
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/doPage")
    public String doPage(Model model, Long id) {
        Question question = questionService.selectById(id);
        Long userid = getUserId();
        List<Exercisebook> exers = exercisebookService.selectList(userid, id);

        boolean started = false;
        boolean solved = false;
        String postext = "";
        int size = exers.size();
        if ( size > 0) {
            started = true;
            Exercisebook exerLast = exers.get(size - 1);

            if (exerLast.getEval() != null && exerLast.getEval().equalsIgnoreCase("solved")){
                solved = true;
                postext = exerLast.getPostext();
            }
        }
        model.addAttribute("question", question);
        model.addAttribute("started", started);
        model.addAttribute("solved", solved);
        model.addAttribute("postext", postext);
        return "jsqles/experiment/do";
    }

    /**
     * 从前端接受questionid，eval参数，注入到exercisebook
     * @param exercisebook
     * @return
     */
    @RequestMapping("/start")
    @ResponseBody
    public Object start(Exercisebook exercisebook){
        Long userid = getUserId();
        exercisebook.setUserid(userid);
        Timestamp t = new Timestamp(System.currentTimeMillis());
        exercisebook.setPosttime(t);
        exercisebook.setStarttime(t);

        boolean r = exercisebookService.insert(exercisebook);
        questionService.refreshCache();
        if (r) {
            return renderSuccess("成功！");
        }else{
            return renderError("失败");
        }
    }

    /**
     * 从前端接受questionid，answer，postext，resultset参数，注入到exercisebook
     * @param exercisebook
     * @return
     */
    @RequestMapping("/submit")
    @ResponseBody
    public Object submit(Exercisebook exercisebook){
        Long userid = getUserId();
        exercisebook.setUserid(userid);
        Timestamp t = new Timestamp(System.currentTimeMillis());
        exercisebook.setPosttime(t);
        exercisebook.setStarttime(t);
        exercisebook.setEval("not");

        try {
            Long count = this.exercisebookService.insertAndGetId(exercisebook);
            questionService.refreshCache();
            if (count > 0){
                String verifyAnswer = this.exercisebookService.verifySubmit(exercisebook);

                if (!verifyAnswer.equalsIgnoreCase("ok")) {
                    logger.error(verifyAnswer);
                    return renderError(verifyAnswer);
                } else {
                    String answer = exercisebook.getAnswer();
			/* 当前仅是结构验证，需要进一步实现结果验证。 */
                    if (!(answer.equalsIgnoreCase("{}") || answer.equalsIgnoreCase(""))) {
				/* 验证通过以后，需要最后增加一条 eval是solved记录。 */
                        exercisebook.setEval("solved");
                        this.exercisebookService.updateById(exercisebook);
                    }else{
                        return renderError("作答不能为空!");
                    }
                }
            }else{
                return renderError("在保存答题记录时出错！");
            }
        } catch (DataIntegrityViolationException dive) {
			/* 可能由java.sql.DataTruncation导致 */
            logger.error(dive.getMessage());
            logger.debug(dive.getStackTrace());
            return renderError(dive.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace());
            return renderError(e.getMessage());
        }


        return renderSuccess("成功！");
    }

}
