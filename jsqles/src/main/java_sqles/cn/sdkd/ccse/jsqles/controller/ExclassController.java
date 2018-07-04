package cn.sdkd.ccse.jsqles.controller;

import cn.sdkd.ccse.jsqles.model.Exclass;
import cn.sdkd.ccse.jsqles.service.IExclassService;
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

/** 实验班控制器
 * Created by sam on 2018/3/9.
 */
@Controller
@RequestMapping("/exclass")
public class ExclassController extends BaseController {
    @Autowired
    IExclassService exclassService;

    @RequestMapping("/treeGrid")
    @ResponseBody
    public Object treeGrid() {
        return exclassService.selectTreeGrid();
    }

    @PostMapping(value = "/tree")
    @ResponseBody
    public Object tree() {
        return exclassService.selectTree();
    }

    @GetMapping("/manager")
    public String manager() {
        return "jsqles_admin/exclass/exclass";
    }

    @GetMapping("/addPage")
    public String addExclassPage() {
        return "jsqles_admin/exclass/addExclassPage";
    }

    @GetMapping("/editPage")
    public String editExclassPage(Model model, Long exclassid) {
        Exclass schoolyearterm = exclassService.selectById(exclassid);
        model.addAttribute("exclass", schoolyearterm);

        return "jsqles_admin/exclass/editExclassPage";
    }

    @GetMapping("/deletePage")
    public String deleteExclassPage() {
        return "jsqles_admin/exclass/deleteExclassPage";
    }

    @GetMapping("/linkPage")
    public String linkExclassPage(Model model, Long exclassid, Integer flag) {
        model.addAttribute("flag", flag);
        model.addAttribute("exclassid", exclassid);
        return "jsqles_admin/exclass/linkExclassPage";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(Long exclassid) {
        exclassService.deleteById(exclassid);
        return renderSuccess("删除成功！");
    }
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid Exclass exclass) {
        exclassService.insert(exclass);
        return renderSuccess("添加成功！");
    }
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(@Valid  Exclass exclass) {
        exclassService.updateById(exclass);
        return renderSuccess("编辑成功！");
    }

    /**
     * 关联行政班级与实验班级
     * @param exclassid
     * @param classids
     * @param flag flag=1增加行政班级，flag=0移除行政班级
     * @return
     */
    @RequestMapping("/link")
    @ResponseBody
    public Object link(Long exclassid, String classids, Integer flag) {
        logger.info(exclassid + ":" + classids);
        exclassService.updateLinkClass( exclassid,  classids,  flag);
        return renderSuccess("编辑成功！");
    }

    /**
     * 返回行政班级的树形数据
     * @return
     */
    @PostMapping(value = "/classtree")
    @ResponseBody
    public Object classtree(Long exclassid, Integer flag) {
        return exclassService.selectClassTree(exclassid, flag);
    }

}
