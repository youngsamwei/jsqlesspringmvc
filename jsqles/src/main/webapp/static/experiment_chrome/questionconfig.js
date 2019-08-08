var questionconfig = {};

questionconfig.initDB = function(quespreq){
    progressLoad();
    try {
        var sqls = experiment.createSQLText(quespreq);

        dbmetadata2.initDB(quespreq, sqls, function(r){
        //			    console.info("initDB_callback：" + sqls);
                    /* 再初始化数据表，数据，约束 */
                    if (r && ("success" in r) &&(r.success)){

                        progressClose();
                    } else {

                        progressClose();
                    }

                });
    } catch (e) {
        progressClose();
    }
}
