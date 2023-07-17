package penoles.sicose.restgen.model.entity;

import java.io.Serializable;

public class Attribute implements Serializable{
    public static final int VARCHAR = 1;
    public static final int NUMBER = 2;
    public static final int DATE = 3;
    public static final int CLOB = 4;

    public String key;
    public String name;
    public Integer data_type;
    public String value;
    public String defaultValue;

    public Attribute(){

    }

    public Attribute(String key, String name, Integer data_type, String value, String defaultValue) {
        this.key = key;
        this.name = name;
        this.data_type = data_type;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getData_type() {
        return data_type;
    }

    public void setData_type(Integer data_type) {
        this.data_type = data_type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
