package cn.sdkd.ccse.jsqles.service.impl;

import cn.sdkd.ccse.jsqles.mapper.ExaminationMapper;
import cn.sdkd.ccse.jsqles.model.Examination;
import cn.sdkd.ccse.jsqles.service.IExaminationService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wangzhixuan.commons.result.PageInfo;
import com.wangzhixuan.commons.result.Tree;
import com.wangzhixuan.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 2017/7/13.
 */
@Service
public class ExaminationServiceImpl extends ServiceImpl<ExaminationMapper, Examination> implements IExaminationService {
    @Autowired
    private ExaminationMapper examinationMapper;

    public List<Examination> selectAll() {
        EntityWrapper<Examination> wrapper = new EntityWrapper<Examination>();
        wrapper.orderBy("examid");
        return examinationMapper.selectList(wrapper);
    }

    /*查询当前班级、当前时间的实验课表内容。
    *
    * classid 实验班级编号 Long
    * */
    public List<Tree> selectTreeByCurricula(Long classid){
        EntityWrapper<Examination> wrapper = new EntityWrapper<Examination>();

        List<Examination> examinationList = examinationMapper.selectListByCurricula(wrapper);
        return examList2Tree(examinationList);
    }

    private List<Tree> examList2Tree(List<Examination> examinationList ){
        List<Tree> trees = new ArrayList<Tree>();
        if (examinationList != null) {
            for (Examination examination : examinationList) {
                Tree tree = new Tree();
                tree.setId(examination.getExamid());
                tree.setText(examination.getExamname());
                trees.add(tree);
            }
        }
        return trees;
    }

    /**
     *
     * @param classnos
     * @return
     */
    public List<Tree> selectCurrentExaminationListByExclassid(List<Long>  classnos){
        long time = System.currentTimeMillis();
        Timestamp ts = new java.sql.Timestamp(time);
        /*通过控制时间参数来决定缓存是否更新，一个小时刷新一次
        * 也需要配合 上课的节次，比如7，8节课从4:20开始可以查询到实验
        * */
        if (ts.getMinutes() >= 20) {
            ts.setMinutes(21);
        }else{
            ts.setMinutes(10);
        }
        ts.setSeconds(0);
        ts.setNanos(0);
        List<Examination> examinationList = examinationMapper.selectCurrentExaminationListByExclassid(classnos, ts);

        return examList2Tree(examinationList);
    }

    @Override
    public List<Tree> selectTree() {
        List<Examination> examinationList = selectAll();

        List<Tree> trees = new ArrayList<Tree>();
        if (examinationList != null) {
            for (Examination examination : examinationList) {
                Tree tree = new Tree();
                tree.setId(examination.getExamid());
                tree.setText(examination.getExamname());
                trees.add(tree);
            }
        }
        return trees;
    }

    @Override
    public void selectDataGrid(PageInfo pageInfo) {
        Page<Examination> page = new Page<Examination>(pageInfo.getNowpage(), pageInfo.getSize());

        EntityWrapper<Examination> wrapper = new EntityWrapper<Examination>();
        wrapper.orderBy(pageInfo.getSort(), pageInfo.getOrder().equalsIgnoreCase("ASC"));
        selectPage(page, wrapper);

        pageInfo.setRows(page.getRecords());
        pageInfo.setTotal(page.getTotal());
    }

    public boolean refreshCache(){
       return examinationMapper.refreshCache();
    };
}
