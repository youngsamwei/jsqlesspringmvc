package cn.sdkd.ccse.jsqles.service;

import cn.sdkd.ccse.jsqles.model.Exclass;
import com.baomidou.mybatisplus.service.IService;
import com.wangzhixuan.commons.result.Tree;
import com.wangzhixuan.model.Organization;

import java.util.List;

/**
 *
 * Resource 表数据服务层接口
 *
 */
public interface IExclassService extends IService<Exclass> {

    List<Exclass> selectAll();

    List<Tree> selectTree();

    List<Exclass> selectTreeGrid();

    List<Exclass> selectExclassByUserid(Long userid);

    List<Tree> selectClassTree(Long exclassid, Integer flag);

    boolean updateLinkClass(Long exclassid, String classids, Integer flag);
}