package cn.sdkd.ccse.jsqles.controller;

import cn.sdkd.ccse.jsqles.model.Examination;
import cn.sdkd.ccse.jsqles.service.IExaminationService;
import com.wangzhixuan.commons.base.BaseController;
import com.wangzhixuan.commons.result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/examination")
public class ExaminationController extends BaseController {

    @Autowired
    private IExaminationService examinationService;

    /**
     * 权限管理页
     *
     * @return
     */
    @GetMapping("/manager")
    public String manager() {
        return "jsqles_admin/examination/examination";
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
    public Object dataGrid(Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);
        examinationService.selectDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 添加权限页
     *
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "jsqles_admin/examination/examinationAdd";
    }

    /**
     * 添加实验
     *
     * @param examination
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid Examination examination) {
        examinationService.insert(examination);
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
        examinationService.deleteById(id);
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
        Examination examination = examinationService.selectById(id);
        model.addAttribute("examination", examination);
        return "jsqles_admin/examination/examinationEdit";
    }

    /**
     * 更新
     *
     * @param examination
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(@Valid Examination examination) {
        examinationService.updateById(examination);
        return renderSuccess("编辑成功！");
    }

    @PostMapping(value = "/tree")
    @ResponseBody
    public Object tree() {
        return examinationService.selectTree();
    }

    @PostMapping(value = "/curriculaExamTree")
    @ResponseBody
    public Object curriculaExamTree(List<Long> classnos) {
        return examinationService.selectCurrentExaminationListByExclassid(classnos);
    }



}
