package cn.sdkd.ccse.jsqles.controller;

import com.wangzhixuan.commons.base.BaseController;
import com.wangzhixuan.commons.shiro.PasswordHash;
import com.wangzhixuan.commons.utils.StringUtils;
import com.wangzhixuan.model.vo.UserVo;
import com.wangzhixuan.service.IRoleService;
import com.wangzhixuan.service.IUserService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sam on 2017/7/14.
 */
@Controller
@RequestMapping("/common")
public class CommonController extends BaseController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;
    @Autowired
    private PasswordHash passwordHash;

    @PostMapping("/importuser")
    @ResponseBody
    public Object importuser(Integer deptid, MultipartFile upfile) {
        /*deptid为部门id，即班级id*/

        String msg = "";
        if (upfile == null) {
            msg = "导入失败：缺少文件.";
        } else {
            //获取文件名
            String name = upfile.getOriginalFilename();
            //进一步判断文件是否为空（即判断其大小是否为0或其名称是否为null）
            long size = upfile.getSize();
            logger.debug(name + ", size : " + size);
            if (name == null || ("").equals(name) && size == 0) {
                msg = "导入失败:缺少文件名称或者文件大小为0.";
            } else {
                if (upfile.isEmpty()) {
                    msg = "文件不存在！";
                } else {
                    try {
                        InputStream in = null;
                        List<List<Object>> listob = null;
                        in = upfile.getInputStream();
                        msg = this.importStudent(deptid, upfile);
                        in.close();
                    } catch (Exception e) {
                        msg = "发生错误：" + e.getMessage();
                    }

                }
            }
            ;
        }
        return renderSuccess(msg);
    }

    private String importStudent(Integer deptid, MultipartFile file) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(
                file.getInputStream());
        Workbook workbook = WorkbookFactory.create(bis); // 这种方式 Excel
        // 2003/2007/2010
        // 都是可以处理的
        int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量
        if (sheetCount > 0) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows(); // 获取总行数
            if (rowCount > 2) {
                Long roleid = roleService.selectByName("学生");

					/* 第一行作为标题行：包含学号，姓名等字段 */
                Row row = sheet.getRow(0);
                int snono = -1, snameno = -1;
                int cellCount = row.getPhysicalNumberOfCells();
                for (int c = 0; c < cellCount; c++) {
                    Cell cell = row.getCell(c);
                    String cellValue = cell.getStringCellValue();
                    if (cellValue.equalsIgnoreCase("学号")) {
                        snono = c;
                    } else if (cellValue.equalsIgnoreCase("姓名")) {
                        snameno = c;
                    }
                }
                int importcount = 0;
                for (int r = 1; r < rowCount; r++) {
                    Row arow = sheet.getRow(r);
                    String sno = "", sname = "";
                    Cell snocell = arow.getCell(snono);
                    Cell snamecell = arow.getCell(snameno);

                    sno = snocell.getStringCellValue();
                    sname = snamecell.getStringCellValue();

                    if ((sno != null) && (sname != null)) {
                        UserVo user = new UserVo();
                        user.setLoginName(sno);
                        user.setName(sname);
                        user.setOrganizationId(deptid);
                        user.setUserType(1); /*管理员0， 用户1*/
                        user.setPassword("123456789");
                        user.setRoleIds(roleid.toString());

                        String salt = StringUtils.getUUId();
                        String pwd = passwordHash.toHex(user.getPassword(), salt);
                        user.setSalt(salt);
                        user.setPassword(pwd);
                        userService.insertByVo(user);

                        importcount++;
                    }
                }

                return "表格包含" + rowCount + "行，共导入" + importcount + "位学生。";
            } else {
                return "没有包含学生。";
            }

        } else {
            return "没有包含表格。";
        }
    }
}
