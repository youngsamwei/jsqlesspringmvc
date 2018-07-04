package cn.sdkd.ccse.jsqles.mapper;

import cn.sdkd.ccse.jsqles.model.Schoolyearterm;
import cn.sdkd.ccse.jsqles.model.SchoolyeartermVO;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * Created by Sam on 2017/7/13.
 */
public interface SchoolyeartermMapper extends BaseMapper<Schoolyearterm> {
    SchoolyeartermVO selectCurrentSchoolyearterm();
}
