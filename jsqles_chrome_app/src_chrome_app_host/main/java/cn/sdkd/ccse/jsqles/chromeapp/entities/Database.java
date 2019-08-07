package cn.sdkd.ccse.jsqles.chromeapp.entities;

/**
 * Created by sam on 2019/8/7.
 */
public class Database {

    private String name;
    private String db_size;

    private DatabaseFile[] files;
    private Table[] tables;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Table[] getTables() {
        return tables;
    }

    public void setTables(Table[] tables) {
        this.tables = tables;
    }

    public String getDb_size() {
        return db_size;
    }

    public void setDb_size(String db_size) {
        this.db_size = db_size;
    }

    public DatabaseFile[] getFiles() {
        return files;
    }

    public void setFiles(DatabaseFile[] files) {
        this.files = files;
    }
}
