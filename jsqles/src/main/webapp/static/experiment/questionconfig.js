var questionconfig = {};

questionconfig.initDB = function(quespreq){
        progressLoad();
        experiment.initDB(quespreq);
        var sqls = experiment.createSQLText(quespreq);
        var db = quespreq ? quespreq.database[0].name : null;

        for(var s in sqls){
            dbmetadata2.execute(sqls[s], db);
        }
        progressClose();
}
