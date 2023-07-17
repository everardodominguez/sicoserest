package penoles.sicose.restgen.secrets.client;

import java.util.HashMap;
import java.util.Map;

public class SecretsClient {
    //JDBC Key names for a connection
    public static final String DB_HOST_KEY_NAME = "db_host_name";
    public static final String DB_PORT_KEY_NAME = "db_port";
    public static final String DB_SID_KEY_NAME = "db_sid_name";
    public static final String DB_USU_KEY_NAME = "db_user_name";
    public static final String DB_PASS_KEY_NAME = "db_password";
    public static final String DB_TYPE_KEY_NAME = "db_type";

    public static Map<String, String> getDBSecretsFromService(String serviceKey){
        Map<String, String> secrets = new HashMap<String, String>();
        
        // TODO implement a call for a service where the secrets it is
        
        secrets.put(DB_HOST_KEY_NAME, "spdibd19cdv.siteplata.penoles");
        secrets.put(DB_PORT_KEY_NAME, "60001");
        secrets.put(DB_SID_KEY_NAME, "SICOVE");
        secrets.put(DB_USU_KEY_NAME, "sicove");
        secrets.put(DB_PASS_KEY_NAME, "Sic0v32023");
        secrets.put(DB_TYPE_KEY_NAME, "oracle");

        return secrets;
    }

    
    
}
