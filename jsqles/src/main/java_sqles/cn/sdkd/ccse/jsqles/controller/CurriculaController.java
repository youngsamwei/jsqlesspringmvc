package cn.sdkd.ccse.jsqles.controller;

import cn.sdkd.ccse.jsqles.model.Curricula;
import cn.sdkd.ccse.jsqles.model.Examination;
import cn.sdkd.ccse.jsqles.model.Exclass;
import cn.sdkd.ccse.jsqles.model.Schoolyearterm;
import cn.sdkd.ccse.jsqles.service.ICurriculaService;
import cn.sdkd.ccse.jsqles.service.IExaminationService;
import cn.sdkd.ccse.jsqles.service.IExclassService;
import cn.sdkd.ccse.jsqles.service.ISchoolyeartermService;
import com.wangzhixuan.commons.base.BaseController;
import com.wangzhixuan.commons.result.PageInfo;
import com.wangzhixuan.model.Organization;
import com.wangzhixuan.service.IOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 2017/7/19.
 */
@Controller
@RequestMapping("/curricula")
public class CurriculaController extends BaseController {
    @Autowired
    private ICurriculaService curriculaService;
    @Autowired
    private IExaminationService examinationService;
    @Autowired
    private ISchoolyeartermService schoolyeartermService;
    //    @Autowired
//    private IExclassService exclassService;
    @Autowired
    private IOrganizationService exclassService;

    @GetMapping("/manager")
    public String manager() {
        return "jsqles_admin/curricula/curricula";
    }

    @PostMapping("/dataGrid")
    @ResponseBody
    public Object dataGrid(Curricula curr, Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);

        Map<String, Object> condition = new HashMap<String, Object>();

        if (curr.getSection() != null) {
            condition.put("section", curr.getSection());
        }
        if (curr.getTermid() != null) {
            condition.put("termid", curr.getTermid());
        }
        if (curr.getWeekday() != null) {
            condition.put("weekday", curr.getWeekday());
        }
        pageInfo.setCondition(condition);
        curriculaService.selectDataGrid(pageInfo);
        return pageInfo;
    }

    @PostMapping("/currGrid")
    @ResponseBody
    public Object currGrid(Curricula curr, Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);

        List ls = curriculaService.selectCurricula(curr.getTermid());
        pageInfo.setRows(ls);
        pageInfo.setTotal(ls.size());
        return pageInfo;
    }

    @GetMapping("/curriculaAddPage")
    public String addPage() {
        return "jsqles_admin/curricula/curriculaAddPage";
    }

    @GetMapping("/curriculaEditPage")
    public String editPage(Model model, Long id) {
        Curricula curricula = curriculaService.selectById(id);
        model.addAttribute("curricula", curricula);
        return "jsqles_admin/curricula/curriculaEditPage";
    }

    @GetMapping("/editSectionPage")
    public String corriculaEditSectionPage(Model model, @Valid Curricula curricula) {
        model.addAttribute("curricula", curricula);
        return "jsqles_admin/curricula/editSectionPage";
    }

    /**
     * 更新
     *
     * @param curricula
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(@Valid Curricula curricula) {
        curriculaService.updateById(curricula);
        examinationService.refreshCache();
        return renderSuccess("编辑成功！");
    }

    @RequestMapping("/add")
    @ResponseBody
    public Object add(@Valid Curricula curricula) {
        curriculaService.insert(curricula);
        examinationService.refreshCache();
        return renderSuccess("添加成功！");
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {
        curriculaService.deleteById(id);
        examinationService.refreshCache();
        return renderSuccess("删除成功！");
    }

    @GetMapping("/arrangeExaminationPage")
    public String arrangeExamination(Model model, @Valid Curricula curricula) {
        //curricula = curriculaService.selectById(curricula.getCurriculaid());
        model.addAttribute("curricula", curricula);
        return "jsqles_admin/curricula/arrangeExaminationPage";
    }

    @GetMapping("/arrangeExaminationCurriculaSetWeekPage")
    public String arrangeExaminationCurriculaSetWeekPage(Model model, @Valid Curricula curricula, @Valid Examination examination) {
        examination = examinationService.selectById(examination.getExamid());
        model.addAttribute("examination", examination);
        curricula = curriculaService.selectById(curricula.getCurriculaid());
        model.addAttribute("curricula", curricula);

        /*只有termid，没有关联学年*/
        Schoolyearterm sy = schoolyeartermService.selectById(curricula.getTermid());
        model.addAttribute("schoolyearterm", sy);
        /*此处应该是从organization中获取班级*/
        Organization exc = exclassService.selectById(curricula.getClassno());
        model.addAttribute("exclass", exc);
        return "jsqles_admin/curricula/arrangeExaminationCurriculaSetWeekPage";
    }


}
