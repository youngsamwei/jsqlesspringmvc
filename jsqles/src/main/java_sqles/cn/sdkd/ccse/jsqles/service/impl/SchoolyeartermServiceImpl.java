package cn.sdkd.ccse.jsqles.service.impl;

import cn.sdkd.ccse.jsqles.mapper.SchoolyeartermMapper;
import cn.sdkd.ccse.jsqles.model.Schoolyearterm;
import cn.sdkd.ccse.jsqles.model.SchoolyeartermVO;
import cn.sdkd.ccse.jsqles.service.ISchoolyeartermService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wangzhixuan.commons.result.PageInfo;
import com.wangzhixuan.commons.result.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 2017/7/13.
 */
@Service
public class SchoolyeartermServiceImpl extends ServiceImpl<SchoolyeartermMapper, Schoolyearterm> implements ISchoolyeartermService {
    @Autowired
    private SchoolyeartermMapper schoolyeartermMapper;

    public List<Schoolyearterm> selectAll() {
        EntityWrapper<Schoolyearterm> wrapper = new EntityWrapper<Schoolyearterm>();
        wrapper.orderBy("id");
        return schoolyeartermMapper.selectList(wrapper);
    }

    @Override
    public List<Tree> selectTree() {
        List<Schoolyearterm> schoolyeartermList = selectAll();

        List<Tree> trees = new ArrayList<Tree>();
        if (schoolyeartermList != null) {
            for (Schoolyearterm schoolyearterm : schoolyeartermList) {
                Tree tree = new Tree();
                tree.setId(schoolyearterm.getId());
                tree.setText(schoolyearterm.getName());
                tree.setPid(schoolyearterm.getPid());
                trees.add(tree);
            }
        }
        return trees;
    }

    @Override
    public void selectDataGrid(PageInfo pageInfo) {
        Page<Schoolyearterm> page = new Page<Schoolyearterm>(pageInfo.getNowpage(), pageInfo.getSize());

        EntityWrapper<Schoolyearterm> wrapper = new EntityWrapper<Schoolyearterm>();
        wrapper.orderBy(pageInfo.getSort(), pageInfo.getOrder().equalsIgnoreCase("ASC"));
        selectPage(page, wrapper);

        pageInfo.setRows(page.getRecords());
        pageInfo.setTotal(page.getTotal());
    }

    @Override
    public List<Schoolyearterm> selectTreeGrid() {
        EntityWrapper<Schoolyearterm> wrapper = new EntityWrapper<Schoolyearterm>();
        wrapper.orderBy("name");
        return schoolyeartermMapper.selectList(wrapper);
    }

    @Override
    public SchoolyeartermVO selectCurrentSchoolyearterm(){
        return schoolyeartermMapper.selectCurrentSchoolyearterm();

    };
}
