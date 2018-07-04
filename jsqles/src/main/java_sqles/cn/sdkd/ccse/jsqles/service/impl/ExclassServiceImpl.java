package cn.sdkd.ccse.jsqles.service.impl;

import cn.sdkd.ccse.jsqles.mapper.ExclassMapper;
import cn.sdkd.ccse.jsqles.mapper.ExclassOrganizationMapper;
import cn.sdkd.ccse.jsqles.model.Exclass;
import cn.sdkd.ccse.jsqles.model.ExclassOrganization;
import cn.sdkd.ccse.jsqles.model.ExclassVO;
import cn.sdkd.ccse.jsqles.service.IExclassService;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wangzhixuan.commons.result.Tree;
import com.wangzhixuan.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 2017/9/29.
 */
@Service
public class ExclassServiceImpl extends ServiceImpl<ExclassMapper, Exclass> implements IExclassService {
    @Autowired
    private ExclassMapper exclassMapper;
    @Autowired
    private ExclassOrganizationMapper exclassOrganizationMapper;

    @Override
    public List<Exclass> selectAll() {
        EntityWrapper<Exclass> wrapper = new EntityWrapper<Exclass>();
        return exclassMapper.selectList(wrapper);
    }

    @Override
    public List<Tree> selectTree() {
        List<ExclassVO> exclassList = exclassMapper.selectTree();

        List<Tree> trees = new ArrayList<Tree>();
        if (exclassList != null){
            for (ExclassVO exclass : exclassList){
                Tree tree = new Tree();
                tree.setId(exclass.getExclassid());
                tree.setText(exclass.getExclassname());
                tree.setPid(exclass.getPid());
                trees.add(tree);
            }
        }
        return trees;

    }

    @Override
    public List<Exclass> selectTreeGrid(){
        EntityWrapper<Exclass> wrapper = new EntityWrapper<>();
        return exclassMapper.selectList(wrapper);
    }

    public List<Exclass> selectExclassByUserid(Long userid){
        return exclassMapper.selectExclassByUserid(userid);
    };

    public List<Tree> selectClassTree(Long exclassid, Integer flag){
        List<Tree> trees = new ArrayList<Tree>();
        List<Organization> organizationList = new ArrayList<Organization>();
        if (flag == 0){
            organizationList = exclassMapper.selectLinkedClass(exclassid);
        }else if (flag == 1){
            organizationList = exclassMapper.selectUnlinkedClass(exclassid);
        }
        if (organizationList != null) {
            for (Organization organization : organizationList) {
                Tree tree = new Tree();
                tree.setId(organization.getId());
                tree.setText(organization.getName());
                tree.setIconCls(organization.getIcon());
//                tree.setPid(organization.getPid());
                trees.add(tree);
            }
        }
        return trees;
    };

    /**
     * 为实验班级增加行政班或删除行政班
     * @param exclassid
     * @param classids
     * @param flag 0 删除行政班， 1增加行政班
     * @return
     */
    public boolean updateLinkClass(Long exclassid, String classids, Integer flag){
        String[] classida = classids.split(",");
        for (String classid : classida) {
            ExclassOrganization eo = new ExclassOrganization();
            eo.setExclassid(exclassid);
            eo.setOrganizationid(Long.parseLong(classid));

            if (flag == 1) {
                exclassOrganizationMapper.insert(eo);
            } else if (flag == 0) {
                EntityWrapper<ExclassOrganization> wrapper = new EntityWrapper<ExclassOrganization>();
                wrapper.where("exclassid = {0} and organizationid = {1}", exclassid, Long.parseLong(classid));
                exclassOrganizationMapper.delete(wrapper);
            }
        }
        exclassMapper.refreshCache();
        return true;
    };
}
