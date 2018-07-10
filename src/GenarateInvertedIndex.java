import java.sql.*;

public class GenarateInvertedIndex {
    static String sql_query = "SELECT inverted FROM inverted WHERE ID = ";

    public static void main(String[] args)
    {
        String item = getItem(1, sql_query);
        System.out.println("the item result is " + item);
    }

    public static String[] searchIndex(String inverted_index)
    {
        indexDatabase searcher = new indexDatabase();
        String[] resultList;
        return null;
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
