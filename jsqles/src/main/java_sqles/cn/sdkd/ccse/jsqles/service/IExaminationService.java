package cn.sdkd.ccse.jsqles.service;

import cn.sdkd.ccse.jsqles.model.Examination;
import com.baomidou.mybatisplus.service.IService;
import com.wangzhixuan.commons.result.PageInfo;
import com.wangzhixuan.commons.result.Tree;

import java.util.List;

/**
 *
 * Examination 表数据服务层接口
 *
 */
public interface IExaminationService extends IService<Examination> {

    void selectDataGrid(PageInfo pageInfo);

    List<Examination> selectAll();

    List<Tree> selectTree();

    List<Tree> selectCurrentExaminationListByExclassid(List<Long>  classnos);

    /**
     * 更新缓存
     * @return
     */
    boolean refreshCache();
}