package cn.sdkd.ccse.jsqles.controller;

import cn.sdkd.ccse.jsqles.model.Schoolyearterm;
import cn.sdkd.ccse.jsqles.service.ISchoolyeartermService;
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

/**
 * Created by Sam on 2017/7/19.
 */
@Controller
@RequestMapping("/schoolyearterm")
public class SchoolyeartermController extends BaseController {

    @Autowired
    private ISchoolyeartermService schoolyeartermService;

    /**
     * 权限管理页
     *
     * @return
     */
    @GetMapping("/manager")
    public String manager() {
        return "jsqles_admin/schoolyearterm/schoolyearterm";
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
        schoolyeartermService.selectDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 添加学年页
     *
     * @return
     */
    @GetMapping("/addSchoolyearPage")
    public String addSchoolyearPage() {
        return "jsqles_admin/schoolyearterm/addSchoolyearPage";
    }

    /**
     * 添加学期页
     *
     * @return
     */
    @GetMapping("/addTermPage")
    public String addTermPage() {
        return "jsqles_admin/schoolyearterm/addTermPage";
    }

    /**
     * 添加学年或学期
     *
     * @param schoolyearterm
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid Schoolyearterm schoolyearterm) {
        schoolyeartermService.insert(schoolyearterm);
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
        schoolyeartermService.deleteById(id);
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
        Schoolyearterm schoolyearterm = schoolyeartermService.selectById(id);
        model.addAttribute("schoolyearterm", schoolyearterm);
        if (schoolyearterm.getPid() == null) {
            /*编辑学年*/
            return "jsqles_admin/schoolyearterm/editSchoolyearPage";
        }else{
            /*编辑学期*/
            return "jsqles_admin/schoolyearterm/editTermPage";
        }
    }

    /**
     * 更新
     *
     * @param schoolyearterm
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(@Valid  Schoolyearterm schoolyearterm) {
        schoolyeartermService.updateById(schoolyearterm);
        return renderSuccess("编辑成功！");
    }

    @PostMapping(value = "/tree")
    @ResponseBody
    public Object tree() {
        return schoolyeartermService.selectTree();
    }

    /**
     * 学年管理
     *
     * @return
     */
    @RequestMapping("/treeGrid")
    @ResponseBody
    public Object treeGrid() {
        return schoolyeartermService.selectTreeGrid();
    }

    @RequestMapping("/currentSchoolyearterm")
    @ResponseBody
    public Object currentSchoolyearterm(){
        return schoolyeartermService.selectCurrentSchoolyearterm();
    }
}
