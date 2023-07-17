package penoles.sicose.restgen.model.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import penoles.sicose.restgen.secrets.client.SecretsClient;

public class DataBaseManager {
    private static final Logger LOGGER = Logger.getLogger(DataBaseManager.class.getName());

    private String serviceName;
    private static DataBaseManager _dataBaseManager;

    public static DataBaseManager getInstance(){
        if(_dataBaseManager == null){
            _dataBaseManager = new DataBaseManager();
        }
        return _dataBaseManager;
    }

    private DataBaseManager(){
    }
    
    public static Connection buildConnection(){
        Connection conn = null;
        try{
            String host = SecretsClient.getDBSecretsFromService(DataBaseManager.getInstance().getServiceName()).get(SecretsClient.DB_HOST_KEY_NAME);
            String port = SecretsClient.getDBSecretsFromService(DataBaseManager.getInstance().getServiceName()).get(SecretsClient.DB_PORT_KEY_NAME);
            String sid = SecretsClient.getDBSecretsFromService(DataBaseManager.getInstance().getServiceName()).get(SecretsClient.DB_SID_KEY_NAME);
            String type = SecretsClient.getDBSecretsFromService(DataBaseManager.getInstance().getServiceName()).get(SecretsClient.DB_TYPE_KEY_NAME);
            String user = SecretsClient.getDBSecretsFromService(DataBaseManager.getInstance().getServiceName()).get(SecretsClient.DB_USU_KEY_NAME);
            String pass = SecretsClient.getDBSecretsFromService(DataBaseManager.getInstance().getServiceName()).get(SecretsClient.DB_PASS_KEY_NAME);
            String jdbcUrl = ("oracle".equalsIgnoreCase(type)) ? "jdbc:oracle:thin:@//"+host+":"+port+"/"+sid : null;
            conn = DriverManager.getConnection(jdbcUrl, user, pass);
        } catch(Exception e){
            LOGGER.log(Level.SEVERE, null, e);
        }
        return conn;
    }

    public static void closeThings(Connection conn, PreparedStatement ps, CallableStatement cs, ResultSet rs){
        try{
            ps.close();
        }catch(Exception e){
        }
        try{
            cs.close();
        }catch(Exception e){
        }
        try{
            rs.close();
        }catch(Exception e){
        }
        try{
            conn.close();
        }catch(Exception e){
        }
    }

    public String getServiceName(){
        return serviceName;
    }

    public void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }

}
