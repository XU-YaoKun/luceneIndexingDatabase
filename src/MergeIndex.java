import com.mysql.cj.protocol.Resultset;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MergeIndex {

    public static void main(String[] args)
    {
        HashMap pedia_1 = new HashMap();
        HashMap pedia_2 = new HashMap();

        //read in file
        File file_1 = new File("D:\\JAVA\\INDEX\\HUDONG_WIKI\\685_HUDONG.txt");
        File file_2 = new File("D:\\JAVA\\INDEX\\HUDONG_WIKI\\685_WIKI.txt");

        
        String vd = getVD("汉语", "vd_zhwiki");
        HashSet<String> res = ConvertToHashset(vd);
    }


    private static String getVD(String subject, String table)
    {
        HashSet res = new HashSet();
        Parameter parameter = new Parameter();
        String DB_URL = parameter.DB_URL;
        String USER = parameter.USER;
        String PASSWD = parameter.PASS;
        String DRIVER = parameter.JDBC_DRIVER;



        Connection conn = null;
        Statement statement = null;

        try
        {
            String subject_url = URLEncoder.encode(subject, "utf-8");
            String sql_query = "SELECT vd FROM " + table + " WHERE sbj = " + '"'+ subject_url + '"';
            System.out.println(sql_query);

            Class.forName(DRIVER);
            System.out.println("connecting to the database");
            conn = DriverManager.getConnection(DB_URL,USER,PASSWD);

            if(!conn.isClosed())
                System.out.println("connect to database successfully");
            else
                System.out.println("fail to connect to database");

            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql_query);

            rs.next();
            String item = rs.getString("VD");
            return item;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static HashSet<String> ConvertToHashset(String vd)
    {
        HashSet<String> res = new HashSet<String>();
        vd = vd.substring(1,vd.length() - 1);
        String[] vd_item = vd.split(",");
        String virtual_document;
        for(String item : vd_item)
        {
            virtual_document = item.split(":")[0];
            System.out.println(virtual_document);
            res.add(virtual_document);
        }
        return res;
    }
}
