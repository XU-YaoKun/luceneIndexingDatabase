import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class GenarateInvertedIndex {
    static String sql_query = "SELECT inverted FROM inverted WHERE ID = ";

    public static void main(String[] args)
    {
        Parameter parameter = new Parameter();
        int max = parameter.inverted_max_id;
        String item = new String();
        Set<String> item_set = new HashSet<String>();
        for(int i = 1; i < max; i++)
        {
            item = getItem(i, sql_query);
            System.out.println(item);
            item_set = searchIndex(item);
        }
        System.out.println("the item result is " + item);
    }

    public static Set<String> searchIndex(String inverted_index)
    {
        indexDatabase searcher = new indexDatabase();
        Set<String> resultList;
        resultList = indexDatabase.searchIndex(inverted_index);
        return resultList;
    }

    public static String getItem(int id, String sql_query)
    {
        Connection conn = null;
        Statement statement = null;
        Parameter parameter = new Parameter();
        String url = parameter.DB_URL;
        String user = parameter.USER;
        String passwd = parameter.PASS;

        String res;

        try
        {
            String driver = parameter.JDBC_DRIVER;
            Class.forName(driver);
            System.out.println("connecting to the database");
            conn = DriverManager.getConnection(url, user, passwd);

            if(!conn.isClosed())
                System.out.println("connect to remote database successfully");

            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql_query + String.valueOf(id));
            resultSet.next();

            res = resultSet.getString("inverted");

            return res;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
