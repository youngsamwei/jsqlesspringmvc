package cn.sdkd.ccse.jsqles.service;

import cn.sdkd.ccse.jsqles.model.Schoolyearterm;
import cn.sdkd.ccse.jsqles.model.SchoolyeartermVO;
import com.baomidou.mybatisplus.service.IService;
import com.wangzhixuan.commons.result.PageInfo;
import com.wangzhixuan.commons.result.Tree;

import java.util.List;

/**
 *
 * Resource 表数据服务层接口
 *
 */
public interface ISchoolyeartermService extends IService<Schoolyearterm> {

    List<Schoolyearterm> selectAll();
    void selectDataGrid(PageInfo pageInfo);
    List<Tree> selectTree();
    List<Schoolyearterm> selectTreeGrid();

    SchoolyeartermVO selectCurrentSchoolyearterm();
}