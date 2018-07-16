import com.mysql.cj.protocol.Resultset;
import javafx.beans.binding.ObjectExpression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        //pedia_1 store information from HUDONG PEDIA
        HashMap pedia_1 = new HashMap();
        //pedia_2 store information from WIKI PEDIA
        HashMap pedia_2 = new HashMap();

        //read in file
        File file_1 = new File("D:\\JAVA\\INDEX\\HUDONG_WIKI\\685_HUDONG.txt");
        File file_2 = new File("D:\\JAVA\\INDEX\\HUDONG_WIKI\\685_WIKI.txt");

        //now all information in the file stored into the Hashmap
        read_file(file_1, pedia_1);
        read_file(file_2, pedia_2);

        for(Object item : pedia_1.keySet())
            System.out.println(item);


        HashSet<String> res_1 = new HashSet<String>();
        HashSet<String> res_2 = new HashSet<String>();
        HashSet<String> res = new HashSet<String>();

        int intersection = 0;
        int block_number = 1;


        for (Object item_1 : pedia_1.keySet()) {
            String vd_1 = getVD(item_1.toString(), "vd_hudongbaike");
            res_1 = ConvertToHashset(vd_1);
            for (Object item_2 : pedia_2.keySet()) {
                String vd_2 = getVD(item_2.toString(), "vd_zhwiki");
                res_2 = ConvertToHashset(vd_2);

                //get the number of common item in two set
                res.clear();
                res.addAll(res_1);
                res.retainAll(res_2);
                intersection = res.size();

                if (Integer.valueOf(pedia_1.get(item_1).toString()) == 0 && Integer.valueOf(pedia_2.get(item_2).toString()) == 0 && intersection > 2) {
                    pedia_1.put(item_1, block_number);
                    pedia_2.put(item_2, block_number);
                    block_number++;
                } else if (Integer.valueOf(pedia_1.get(item_1).toString()) != 0 && Integer.valueOf(pedia_2.get(item_2).toString()) == 0 && intersection > 2) {
                    pedia_1.put(item_1, pedia_2.get(item_2));
                } else if (Integer.valueOf(pedia_1.get(item_1).toString()) == 0 && Integer.valueOf(pedia_2.get(item_2).toString()) != 0 && intersection > 2) {
                    pedia_2.put(item_2, pedia_1.get(item_1));
                } else
                    continue;
            }
        }


        for(Object item : pedia_1.keySet())
        {
            System.out.print(pedia_1.get(item));
        }
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
            //System.out.println(virtual_document);
            res.add(virtual_document);
        }
        return res;
    }

    public static void read_file(File file, HashMap pedia){
        BufferedReader reader=null;
        String temp=null;
        int line=1;
        try{
            reader=new BufferedReader(new FileReader(file));
            //temp is subject for each line
            while((temp=reader.readLine())!=null){
                if(line == 1) {
                    line++;
                    continue;
                }
                pedia.put(temp, 0);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(reader!=null){
                try{
                    reader.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
