package penoles.sicose.restgen.model.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import penoles.sicose.restgen.model.entity.Attribute;
import penoles.sicose.restgen.model.entity.Entity;

public class JSONBuilder {
    
    public static String buildJSON(List<Entity> entities){
        StringBuilder json = new StringBuilder("[");
        if(entities != null){
            for(Entity ent : entities){
                StringBuilder stbAttrs = new StringBuilder("{");
                for(Attribute attr : ent.getAttributes()){
                    if(!stbAttrs.toString().equalsIgnoreCase("{")){
                        stbAttrs.append(",");
                    }
                    stbAttrs.append("\"").append(attr.getName().toLowerCase()).append("\":");
                    if(attr.getData_type() == Attribute.NUMBER || attr.getData_type() == Attribute.DATE){
                        stbAttrs.append(attr.getValue());
                    } else {
                        stbAttrs.append("\"").append(attr.getValue()).append("\"");
                    }
                }
                stbAttrs.append("}");
                if(!json.toString().equalsIgnoreCase("[")){
                    json.append(",").append(stbAttrs.toString());
                } else {
                    json.append(stbAttrs.toString());
                }
            }
        }
        json.append("]");
        return json.toString();
    }


    /**
     * Ejemplo de JSON de como vamos a recibir el objeto para formarlo
     * {
            "name": "pais",
            "detail":
            {
                "reg_id": 7,
                "pai_id": 146,
                "pai_clave": "SEN",
                "pai_prefijo": "SEN",
                "pai_nombre": "SENEGAL",
                "estatus": 1,
                "fec_mod": 1684870980000,
                "usu_mod": "CARGA"
            }
        }
     */
    public static Entity buildEntity(String json) {
        ObjectMapper mapper = new ObjectMapper();
        Entity entity = null;
        try {
            entity = mapper.readValue(json, Entity.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return entity;
    }
}
