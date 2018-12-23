package cn.sdkd.ccse.jsqles.service;

import cn.sdkd.ccse.jsqles.model.Exclass;
import cn.sdkd.ccse.jsqles.model.Exercisebook;
import com.baomidou.mybatisplus.service.IService;
import com.wangzhixuan.commons.result.PageInfo;

import java.util.List;

/**
 *
 * Resource 表数据服务层接口
 *
 */
public interface IExercisebookService extends IService<Exercisebook> {

    List<Exercisebook> selectAll();

    String verifySubmit(Exercisebook exercisebook);

    List<Exercisebook> selectList(Long userid, Long quesid);

    Long insertAndGetId(Exercisebook exercisebook);

    void selectQuestionSolvedRatio(PageInfo pageInfo);
}