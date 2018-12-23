package cn.sdkd.ccse.jsqles.service.impl;

import cn.sdkd.ccse.commons.utils.IJSONCompareDecision;
import cn.sdkd.ccse.commons.utils.JSONUtils;
import cn.sdkd.ccse.jsqles.mapper.ExercisebookMapper;
import cn.sdkd.ccse.jsqles.mapper.QuestionMapper;
import cn.sdkd.ccse.jsqles.model.Exercisebook;
import cn.sdkd.ccse.jsqles.model.Question;
import cn.sdkd.ccse.jsqles.service.IExercisebookService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wangzhixuan.commons.result.PageInfo;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by sam on 2018/3/19.
 */
@Service
public class ExercisebookServiceImpl extends ServiceImpl<ExercisebookMapper, Exercisebook> implements IExercisebookService, IJSONCompareDecision {
    protected Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private ExercisebookMapper exercisebookMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<Exercisebook> selectAll() {
        return exercisebookMapper.selectList(null);
    }

    @Override
    public List<Exercisebook> selectList(Long userid, Long quesid){
        EntityWrapper<Exercisebook> wrapper = new EntityWrapper<Exercisebook>();
        wrapper.orderBy("posttime");
        wrapper.where("userid = {0} and quesid = {1}", userid, quesid);
        return exercisebookMapper.selectList(wrapper);
    };
    /*
     * 验证分为两大类：结构验证和结果验证 结构验证用于验证数据库‘数据对象的创建与修改，对应ddl；
     * 结果验证用于验证select，update，delete，insert，procedure, function，对应dml
     */
    @Override
    public String verifySubmit(Exercisebook exercisebook) {
        String answer = exercisebook.getAnswer(); /* 操作结果 */
        String resultSet = exercisebook.getResultset();

        Question q = questionMapper.selectById(exercisebook.getQuesid());
        String quesEval = q.getQueseval(); /* 数据结构的验证 */
        String quesResult = q.getResultcheck();/*结果验证*/

        String r = doVerify(answer, resultSet, quesEval, quesResult);
        return r;
    }

    public String doVerify(String answer, String resultSet, String quesEval, String quesResult){

        logger.debug("resultSet: " + resultSet);
        logger.debug("quesResult: " + quesResult);

        resultSet = StringEscapeUtils.unescapeHtml(resultSet);
        quesResult = StringEscapeUtils.unescapeHtml(quesResult);
        answer = StringEscapeUtils.unescapeHtml(answer);

        logger.debug("answer: " + answer);
        logger.debug("quesEval: " + quesEval);

        logger.debug("resultSet: " + resultSet);

        try {
            boolean isVerifyStructure = false;
            if (!answer.equalsIgnoreCase("{}") && !answer.equalsIgnoreCase("")
                    && !answer.equalsIgnoreCase("null")) {
                isVerifyStructure = true;
                /*替换\, 避免被当做转义字符*/
                answer = answer.replace('\\','/');
                JSONObject jsonAnswer = new JSONObject(answer);

                JSONObject jsonQuesEval = new JSONObject(quesEval);

                String r = JSONUtils.compareJson(jsonQuesEval, jsonAnswer, "", "", this);
                if (r != null) {
                    return r;
                }
            } else {
                isVerifyStructure = false;
            }

            if (!resultSet.equalsIgnoreCase("{}")
                    && !resultSet.equalsIgnoreCase("")
                    && !resultSet.equalsIgnoreCase("null")) {
                JSONArray jsonResult = new JSONArray(resultSet);
                JSONArray jsonQuesResult = new JSONArray(quesResult);
                String r = JSONUtils.compareJson(jsonQuesResult, jsonResult,
                        "", "");
                if (r != null) {
                    return r;
                }
            } else {
                if (!isVerifyStructure) {
                    return "操作错误.";
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            logger.error(e);
            return e.getMessage();
        }

        return "ok";
    }
    /*用于处理比较时的例外。*/
    @Override
    public boolean needcompare(JSONObject o1, JSONObject o2, String parentKey,
                               String key) {
        if (parentKey.equalsIgnoreCase("constraints") && key.equalsIgnoreCase("constraint_keys")){
            if (o1.has("constraint_type")){
                String v = o1.getString("constraint_type");
                if (v.startsWith("CHECK ")|| v.startsWith("RULE ")){
					/* 当约束是check或rule时不比较constraint_keys的值*/
                    return false;
                }
            }
        }
        return true;
    }

    public Long insertAndGetId(Exercisebook exercisebook){
        return exercisebookMapper.insertAndGetId(exercisebook);
    }

    @Override
    public void selectQuestionSolvedRatio(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = exercisebookMapper.selectQuestionSolvedRatio(page) ;
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    ;
}
