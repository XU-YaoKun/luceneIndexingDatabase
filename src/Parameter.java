public class Parameter {
    int flag = 0; //determine whether result should be stopped
    int tolerate = 0; // how many indexes that
    int n = 1000; // the max results returned from research engine
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://192.168.1.101:3306/zhishi";

    static final String USER = "xyk";
    static final String PASS = "123";
    String path = "D:\\JAVA\\INDEX\\namevector_HUDONGBAIKE";

    String sql_query = "SELECT sbj, nv FROM nv_zhwiki";

    String field_nv = "nv";
}
