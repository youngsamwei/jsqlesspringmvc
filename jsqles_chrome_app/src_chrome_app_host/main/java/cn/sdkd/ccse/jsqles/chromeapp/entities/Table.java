package cn.sdkd.ccse.jsqles.chromeapp.entities;

/**
 * Created by sam on 2019/8/7.
 */
public class Table {
    private String full_name; //: "dbo.student"
    private String name;//: "student"
    private String object_id; //: 2137058649
    private String schema_name; //: "dbo"
    private String type; //: "user table"

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getSchema_name() {
        return schema_name;
    }

    public void setSchema_name(String schema_name) {
        this.schema_name = schema_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
