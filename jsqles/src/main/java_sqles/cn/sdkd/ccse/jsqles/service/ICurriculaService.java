package cn.sdkd.ccse.jsqles.service;

import cn.sdkd.ccse.jsqles.model.Curricula;
import cn.sdkd.ccse.jsqles.model.CurriculaVO;
import com.baomidou.mybatisplus.service.IService;
import com.wangzhixuan.commons.result.PageInfo;

import java.util.List;

/**
 *
 * Resource 表数据服务层接口
 *
 */
public interface ICurriculaService extends IService<Curricula> {

    List<Curricula> selectAll();

    List<CurriculaVO> selectCurricula(Long id);

    void selectDataGrid(PageInfo pageInfo);

}