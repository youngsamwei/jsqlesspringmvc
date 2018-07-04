package cn.sdkd.ccse.jsqles.mapper;

import cn.sdkd.ccse.jsqles.model.Curricula;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 2017/7/13.
 */
public interface CurriculaMapper extends BaseMapper<Curricula> {

    List<Map<String, Object>> selectCurriculaByTermId(Long id);

    List<Map<String, Object>> selectExclassCurriculaByTermId(Long id);

    List<Map<String, Object>> selectCurriculaPage(Pagination page, Map<String, Object> params);

    List<Map<String, Object>> selectExclassCurriculaPage(Pagination page, Map<String, Object> params);


}
