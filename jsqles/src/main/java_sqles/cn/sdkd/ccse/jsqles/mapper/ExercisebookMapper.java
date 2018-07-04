package cn.sdkd.ccse.jsqles.mapper;

import cn.sdkd.ccse.jsqles.model.Exercisebook;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * Created by Sam on 2017/7/13.
 */
public interface ExercisebookMapper extends BaseMapper<Exercisebook> {
    Long insertAndGetId(Exercisebook exercisebook);
}
