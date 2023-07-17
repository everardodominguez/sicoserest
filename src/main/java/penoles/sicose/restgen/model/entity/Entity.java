package penoles.sicose.restgen.model.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Entity implements Serializable {
    private String key;
    private String name;
    private String key_col;
    private String[] key_sec_col;
    private List<Attribute> attributes;
    private List<Entity> children;
    private Map<String, Object> detail;

    public Entity(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public Entity() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Entity> getChildren() {
        return children;
    }

    public void setChildren(List<Entity> children) {
        this.children = children;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    public String getKey_col() {
        return key_col;
    }

    public void setKey_col(String key_col) {
        this.key_col = key_col;
    }

    public String[] getKey_sec_col() {
        return key_sec_col;
    }

    public void setKey_sec_col(String[] key_sec_col) {
        this.key_sec_col = key_sec_col;
    }

    @Override
    public String toString() {
        String str = "";
        str += "key=" + this.key;
        str += System.getProperty("line.separator");
        str += "name=" + this.name;
        str += System.getProperty("line.separator");
        str += "detail=";
        str += System.getProperty("line.separator");
        if (detail != null) {
            for (Map.Entry entry : this.detail.entrySet()) {
                str += entry.getKey() +"="+ entry.getValue();
                str += System.getProperty("line.separator");
            }
        }
        return str;
    }

}
