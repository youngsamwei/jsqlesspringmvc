package cn.sdkd.ccse.jsqles.service;

import cn.sdkd.ccse.jsqles.model.ExamCurr;
import com.baomidou.mybatisplus.service.IService;
import com.wangzhixuan.commons.result.PageInfo;

import java.util.List;

/**
 *
 * Resource 表数据服务层接口
 *
 */
public interface IExamCurrService extends IService<ExamCurr> {

    List<ExamCurr> selectAll();
    void selectDataGrid(PageInfo pageInfo);
}