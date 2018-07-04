package cn.sdkd.ccse.jsqles.service;

import cn.sdkd.ccse.jsqles.model.Question;
import com.baomidou.mybatisplus.service.IService;
import com.wangzhixuan.commons.result.PageInfo;

import java.util.List;

/**
 *
 * Resource 表数据服务层接口
 *
 */
public interface IQuestionService extends IService<Question> {

    List<Question> selectAll();
    void selectDataGrid(PageInfo pageInfo);
    void selectQuestionStatusByUser(PageInfo pageInfo, Long userid, Long examid);

    /**
     * 更新缓存
     * @return
     */
    boolean refreshCache();
}