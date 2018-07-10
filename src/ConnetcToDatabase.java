import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnetcToDatabase {

    public static void main(String[] args)
    {
        Parameter parameter = new Parameter();
        String url = parameter.DB_URL;
        String user = parameter.USER;
        String passwd = parameter.PASS;
        connectMysql(url, user, passwd);
    }

    public static void connectMysql(String url, String username, String passwd)
    {
        // construct a connection object
        Connection conn;

        try
        {
            Parameter parameter = new Parameter();
            String driver = parameter.JDBC_DRIVER;
            Class.forName(driver);
            System.out.println("connecting to the database");
            conn = DriverManager.getConnection(url,username,passwd);

            if(!conn.isClosed())
                System.out.println("connect to remote database successfully");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean createfile(String fileName, String fileContent)
    {
        Parameter parameter = new Parameter();
        String path = parameter.inverted_index_path;
        Boolean bool = false;
        String fileNameTemp = fileName + ".txt";
        File file = new File(path);
        return bool;
    }

}
