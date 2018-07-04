package cn.sdkd.ccse.jsqles.service.impl;

import cn.sdkd.ccse.jsqles.mapper.CurriculaMapper;
import cn.sdkd.ccse.jsqles.model.Curricula;
import cn.sdkd.ccse.jsqles.model.CurriculaVO;
import cn.sdkd.ccse.jsqles.service.ICurriculaService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wangzhixuan.commons.result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 2017/7/13.
 */
@Service
public class CurriculaServiceImpl extends ServiceImpl<CurriculaMapper, Curricula> implements ICurriculaService {
    @Autowired
    private CurriculaMapper curriculaMapper;

    public List<Curricula> selectAll() {
        EntityWrapper<Curricula> wrapper = new EntityWrapper<Curricula>();
        wrapper.orderBy("quesid");
        return curriculaMapper.selectList(wrapper);
    }

    @Override
    public List<CurriculaVO> selectCurricula(Long id) {
        List<Map<String, Object>>  ls = curriculaMapper.selectExclassCurriculaByTermId(id);
        List<CurriculaVO> result = new ArrayList<CurriculaVO>();
        Map<String, CurriculaVO> currmap = new HashMap<String, CurriculaVO>();
        for (int i = 1; i <=5 ; i++){
            CurriculaVO currvo = new CurriculaVO();
            currvo.setId(new Long(i));
            currvo.setMonday("<BR><BR><BR>");
            currvo.setTuesday("<BR><BR><BR>");
            currvo.setWednesday("<BR><BR><BR>");
            currvo.setThursday("<BR><BR><BR>");
            currvo.setFriday("<BR><BR><BR>");
            currvo.setSaturday("<BR><BR><BR>");
            currvo.setSunday("<BR><BR><BR>");
            currmap.put(i + "", currvo);
            result.add(currvo);
        }
        for (Map<String, Object> m : ls){
            String section = m.get("section").toString();
            String weekday = m.get("weekday").toString();
            String curr = "<BR>" + (String)m.get("curr") + "<BR>";
            CurriculaVO currvo = currmap.get(section);
            switch (weekday){
                case "1" : currvo.setMonday(curr);break;
                case "2" : currvo.setTuesday(curr);break;
                case "3" :currvo.setWednesday(curr);break;
                case "4" :currvo.setThursday(curr);break;
                case "5" :currvo.setFriday(curr);break;
                case "6" :currvo.setSaturday(curr);break;
                case "7" :currvo.setSunday(curr);break;
            }

        }
        return result;
    }

    @Override
    public void selectDataGrid(PageInfo pageInfo) {

        Page<Curricula> page = new Page<Curricula>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = curriculaMapper.selectExclassCurriculaPage(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());

    }


}
