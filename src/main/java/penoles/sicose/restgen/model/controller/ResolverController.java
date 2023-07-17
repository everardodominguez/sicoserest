package penoles.sicose.restgen.model.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.core.MultivaluedMap;
import penoles.sicose.restgen.model.db.DataBaseManager;
import penoles.sicose.restgen.model.entity.Attribute;
import penoles.sicose.restgen.model.entity.Entity;
import penoles.sicoseutil.database.controller.DatabaseController;

public class ResolverController extends DatabaseController {
    private static final Logger LOGGER = Logger.getLogger(ResolverController.class.getName());

    public static String resolveSearch(String table, MultivaluedMap<String, String> params) {
        String json = null;
        List<Entity> entities = new ArrayList<Entity>();
        if (table != null) {
            Entity entity = getMetadata(table);
            if (entity == null) {
                return null;
            }
            StringBuilder sqlSelect = new StringBuilder("Select ");
            StringBuilder sqlWhere = new StringBuilder(" Where 1=1 ");
            // Configuracion de parametros
            for (Attribute attr : entity.getAttributes()) {
                if (params != null) {
                    for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(attr.getName())) {
                            attr.setValue(entry.getValue().get(0));
                            if (attr.getData_type() == Attribute.VARCHAR || attr.getData_type() == Attribute.CLOB) {
                                sqlWhere.append("AND ").append(attr.getName()).append(" like ").append("'%'||?||'%'");
                            } else if (attr.getData_type() == Attribute.NUMBER
                                    || attr.getData_type() == Attribute.DATE) {
                                sqlWhere.append("AND ").append(attr.getName()).append(" = ").append("?");
                            }
                        }
                    }
                }
                if ("Select ".equalsIgnoreCase(sqlSelect.toString())) {
                    sqlSelect.append(attr.getName());
                } else {
                    sqlSelect.append(",").append(attr.getName());
                }
            }
            // Creacion de SQL
            String sql = sqlSelect.toString() + " from " + table + sqlWhere.toString();
            Connection conn = DataBaseManager.buildConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql);
                if (params != null) {
                    int i = 1;
                    for (Attribute attr : entity.getAttributes()) {
                        if (attr.getValue() != null) {
                            if (attr.getData_type() == Attribute.DATE) {
                                Long dateLong = Long.valueOf(attr.getValue());
                                Date dateFilter = new Date(dateLong);
                                ps.setDate(i++, dateFilter);
                            } else {
                                ps.setObject(i++, attr.getValue());
                            }
                        }
                    }
                }
                rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    Entity ent = new Entity();
                    ent.setKey(table);
                    ent.setName(table);
                    List<Attribute> attributes = new ArrayList<Attribute>();
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        Attribute attr = new Attribute();
                        Object value = null;
                        int dataType = Attribute.VARCHAR;
                        int typeMD = rsmd.getColumnType(i);
                        if (typeMD == Types.DATE || typeMD == Types.TIMESTAMP) {
                            dataType = Attribute.DATE;
                            value = rs.getDate(i);
                            if (value != null) {
                                value = ((Date) value).getTime();
                            }
                        } else if (typeMD == Types.NUMERIC) {
                            dataType = Attribute.NUMBER;
                            if (rsmd.getScale(i) > 0) {
                                value = rs.getDouble(i);
                            } else {
                                value = rs.getLong(i);
                            }
                        } else if (typeMD == Types.CLOB) {
                            dataType = Attribute.CLOB;
                            value = rs.getString(i);
                        } else if (typeMD == Types.VARCHAR || typeMD == Types.CHAR) {
                            dataType = Attribute.VARCHAR;
                            value = rs.getString(i);
                        }
                        attr.setValue(String.valueOf(value));
                        attr.setData_type(dataType);
                        attr.setName(rsmd.getColumnName(i));
                        attr.setKey(rsmd.getColumnName(i));

                        attributes.add(attr);
                    }
                    ent.setAttributes(attributes);
                    entities.add(ent);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, sql, e);
            } finally {
                DataBaseManager.closeThings(conn, ps, null, rs);
            }
        }
        json = JSONBuilder.buildJSON(entities);
        return json;
    }

    public static String resolveInsert(String table, String json) throws Exception {
        String result = "No Result";
        if (json != null) {
            Entity entity = JSONBuilder.buildEntity(json);
            if (entity != null && entity.getDetail() != null) {
                Entity entMeta = getMetadata(table);
                if (entMeta == null) {
                    return "La tabla que a donde intenta insertar no existe";
                }
                Object pk = getPK(table, entMeta, entity);
                StringBuilder insert = new StringBuilder("Insert into ");
                StringBuilder cols = new StringBuilder();
                StringBuilder values = new StringBuilder();
                for (Attribute attr : entMeta.getAttributes()) {
                    for (Map.Entry entry : entity.getDetail().entrySet()) {
                        if (attr.getName().equalsIgnoreCase((String) entry.getKey())) {
                            if (cols.toString().isEmpty()) {
                                cols.append(attr.getName());
                                values.append("?");
                            } else {
                                cols.append(",").append(attr.getName());
                                values.append(",").append("?");
                            }
                        }
                    }
                }
                insert.append(table).append("(").append(cols).append(") values (").append(values).append(")");
                Connection conn = DataBaseManager.buildConnection();
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement(insert.toString());
                    int i = 1;
                    for (Attribute attr : entMeta.getAttributes()) {
                        for (Map.Entry entry : entity.getDetail().entrySet()) {
                            if (attr.getName().equalsIgnoreCase((String) entry.getKey())) {
                                Object value = entry.getValue();
                                if (attr.getData_type() == Attribute.DATE && entry.getValue() != null) {
                                    value = new Date((Long) entry.getValue());
                                }
                                if (attr.getName().equalsIgnoreCase(entity.getKey_col())) {
                                    value = pk;
                                }
                                ps.setObject(i++, value);
                            }
                        }
                    }
                    ps.executeUpdate();
                    result = "success";
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, insert.toString(), e);
                    result = e.getMessage();
                } finally {
                    DataBaseManager.closeThings(conn, ps, null, null);
                }
            }
        } else {
            throw new Exception("No se puede insertar sin contenido");
        }
        return result;
    }

    public static String resolveUpdate(String table, String json) throws Exception {
        String result = "No Result";
        if (json != null) {
            Entity entity = JSONBuilder.buildEntity(json);
            if (entity != null && entity.getDetail() != null) {
                Entity entMeta = getMetadata(table);
                if (entMeta == null) {
                    return "La tabla que a donde intenta actualizar no existe";
                }
                StringBuilder update = new StringBuilder(" Update ");
                StringBuilder cols = new StringBuilder();
                StringBuilder where = new StringBuilder(" Where 1=1 ");
                for (Map.Entry detail : entity.getDetail().entrySet()) {
                    for (Attribute attr : entMeta.getAttributes()) {
                        if (attr.getName().equalsIgnoreCase((String) detail.getKey()) &&
                                !attr.getName().equalsIgnoreCase(entity.getKey_col()) &&
                                detail.getValue() != null) {
                            if (cols.toString().isEmpty()) {
                                cols.append(attr.getName()).append("=?");
                            } else {
                                cols.append(",").append(attr.getName()).append("=?");
                            }
                        }
                    }
                    if (((String) detail.getKey()).equalsIgnoreCase(entity.getKey_col()) &&
                            detail.getValue() != null) {
                        where.append(" And ").append(detail.getKey()).append(" = ?");
                    }

                }
                update.append(table).append(" set ").append(cols).append(where);
                Connection conn = DataBaseManager.buildConnection();
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement(update.toString());
                    int i = 1;
                    // COLS
                    for (Map.Entry detail : entity.getDetail().entrySet()) {
                        for (Attribute attr : entMeta.getAttributes()) {
                            if (attr.getName().equalsIgnoreCase((String) detail.getKey()) &&
                                    !attr.getName().equalsIgnoreCase(entity.getKey_col()) &&
                                    detail.getValue() != null) {
                                ps.setObject(i++, detail.getValue());
                            }
                        }
                    }
                    // WHERE
                    for (Map.Entry detail : entity.getDetail().entrySet()) {
                        if (((String) detail.getKey()).equalsIgnoreCase(entity.getKey_col()) &&
                                detail.getValue() != null) {
                            ps.setObject(i++, detail.getValue());
                        }
                    }
                    ps.executeUpdate();
                    result = "success";
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, update.toString(), e);
                    result = e.getMessage();
                } finally {
                    DataBaseManager.closeThings(conn, ps, null, null);
                }
            } else {
                throw new Exception("El contenido esta malformado");
            }
        } else {
            throw new Exception("No se puede insertar sin contenido");
        }
        return result;
    }

    private static Object getPK(String table, Entity entMetadata, Entity entity) {
        Object pk = null;
        int dataType = Attribute.NUMBER;
        for (Attribute attr : entMetadata.getAttributes()) {
            if (attr.getName().equalsIgnoreCase(entity.getKey())) {
                dataType = attr.getData_type();
                break;
            }
        }
        if (dataType == Attribute.NUMBER) {
            StringBuilder sql = new StringBuilder("Select MAX(NVL(");
            sql.append(entity.getKey_col()).append(",0)) + 1  from ").append(table);
            if (entity.getKey_sec_col() != null) {
                sql.append(" Where 1=1 ");
                for (String secCol : entity.getKey_sec_col()) {
                    sql.append(" And ").append(secCol).append(" = ? ");
                }
            }
            Connection conn = DataBaseManager.buildConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql.toString());
                if (entity.getKey_sec_col() != null) {
                    int i = 1;
                    for (String secCol : entity.getKey_sec_col()) {
                        for (Map.Entry entry : entity.getDetail().entrySet()) {
                            if (secCol.equalsIgnoreCase((String) entry.getKey())) {
                                ps.setObject(i++, entry.getValue());
                            }
                        }
                    }
                }
                rs = ps.executeQuery();
                if (rs.next()) {
                    pk = rs.getObject(1);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, sql.toString(), e);
            } finally {
                DataBaseManager.closeThings(conn, ps, null, rs);
            }
        } else {
            for (Map.Entry entry : entity.getDetail().entrySet()) {
                if (((String) entry.getKey()).equalsIgnoreCase(entity.getKey())) {
                    pk = entry.getValue();
                    break;
                }
            }
        }
        return pk;
    }

    private static Entity getMetadata(String tableName) {
        Entity ent = new Entity();
        List<Attribute> lattr = null;
        String sql = "Select column_name," +
                "data_type," +
                "data_length," +
                "data_precision," +
                "data_scale," +
                "nullable" +
                " from user_tab_columns cols" +
                " join (SELECT " +
                " table_name as obj_name" +
                " FROM" +
                "  user_tables  " +
                " union" +
                " SELECT" +
                "   view_name as obj_name" +
                " FROM" +
                "   user_views) tvw on tvw.obj_name = cols.table_name" +
                " Where upper(cols.table_name) = upper(?)";
        Connection conn = DataBaseManager.buildConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableName);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (lattr == null) {
                    lattr = new ArrayList<>();
                }
                Integer data_type = 0;
                if (rs.getString(2).startsWith("VARCHAR")) {
                    data_type = Attribute.VARCHAR;
                } else if (rs.getString(2).startsWith("NUMBER")) {
                    data_type = Attribute.NUMBER;
                } else if (rs.getString(2).startsWith("DATE")) {
                    data_type = Attribute.DATE;
                } else if (rs.getString(2).startsWith("CLOB")) {
                    data_type = Attribute.CLOB;
                }
                Attribute attr = new Attribute(rs.getString(1), rs.getString(1), data_type, null, null);
                lattr.add(attr);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, sql, e);
        } finally {
            DataBaseManager.closeThings(conn, ps, null, rs);
        }
        ent.setAttributes(lattr);
        ent.setKey(tableName);
        ent.setName(tableName);
        return ent;
    }
}
