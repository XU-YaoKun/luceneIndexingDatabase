import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.swing.plaf.synth.SynthTextAreaUI;
import javax.xml.transform.Result;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class indexDatabase {
    public static void main(String[] args)
    {
        try {
            Parameter parameter = new Parameter();
            indexDatabase index = new indexDatabase();
            index.indexDatabase(index.getResult(parameter.sql_query)); // index database
            index.searchIndex("人类"); //get search result
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public ResultSet getResult(String sql)
    {
        Connection conn = null;
        Statement stmt = null;
        try
        {
            // register JDBC Driver
            Class.forName("com.mysql.jdbc.Driver");

            //open the url
            Parameter parameter = new Parameter();
            System.out.println("connecting the database......");
            conn = DriverManager.getConnection(parameter.DB_URL,parameter.USER,parameter.PASS);

            if(!conn.isClosed())
                System.out.println("connect to database Successfully");

            System.out.println("initializing the statement");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        }
        catch(SQLException se)
        {
            se.printStackTrace();;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void indexDatabase(ResultSet rs)
    {
        try
        {

            Parameter parameter = new Parameter();
            final Path path = Paths.get(parameter.path);
            Directory directory = FSDirectory.open(path);
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(directory, config);

            int counter = 0;

            while(rs.next())
            {
                try {
                    Document doc = new Document();

                    String sbj = rs.getString("sbj");

                    String sbj_use = URLDecoder.decode(sbj,"UTF-8");
                //   System.out.println(sbj_use);
                    Field sbjField = new TextField("sbj", sbj_use, Field.Store.YES);
                    doc.add(sbjField);

                    String nv = rs.getString("nv");

                    String json = "[" + nv + "]";

                    JSONArray jsonArray = JSONArray.fromObject(json);
                    Object[] os = jsonArray.toArray();
                    JSONObject jsonObject = JSONObject.fromObject(os[0]);
                    //System.out.println(jsonObject.names());
                    String vd_use = jsonObject.names().toString();

                    Field nvField = new TextField("nv", vd_use, Field.Store.YES);
                    doc.add(nvField);


                    writer.addDocument(doc);
                    System.out.println("index " + counter + " has been created");
                    counter++;
                }
                catch(ClassCastException ce)
                {
                    continue;
                }
            }
            writer.close();
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }
    }

    public void searcher(String queryString) throws Exception
    {

        Parameter parameter = new Parameter();
       String field = "nv";
       String queryStr = queryString;
       final Path path = Paths.get(parameter.path);

       Directory directory = FSDirectory.open(path);
       IndexReader reader = DirectoryReader.open(directory);
       IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryStr);
        System.out.println("Searching for: " + query.toString(field));
        TopDocs results = searcher.search(query, 50);
        ScoreDoc[] hits = results.scoreDocs;
        long numTotalHits = results.totalHits;
        System.out.println(numTotalHits + " total matching documents");
        for(ScoreDoc sd:hits)
        {
            Document doc = searcher.doc(sd.doc);
        }
        }

        public void MatchAllDocs() {
        try {

            Parameter parameter = new Parameter();
            final Path path = Paths.get(parameter.path);

            Directory directory = FSDirectory.open(path);
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            Query query = new MatchAllDocsQuery();
             TopDocs topDocs = searcher.search(query,10);
             long num = topDocs.totalHits;
             System.out.println(num + " total matching documents");
             for (ScoreDoc scoreDoc : topDocs.scoreDocs)
             {
                 Document document = searcher.doc(scoreDoc.doc);
                 System.out.println(document.get("sbj"));

                 System.out.println(document.get("nv"));

                 System.out.println("-------------------------------------");
             }
             reader.close();
        }
       catch(IOException ie)
        {
                ie.printStackTrace();
        }
        }

        public static Set<String> searchIndex(String term)
        {
            try {
                int flag = 0;
                int tolerate = 0;
                eliminateNoise excludeNoise = new eliminateNoise();
                Parameter parameter = new Parameter();
                final Path path = Paths.get(parameter.path);

                Directory directory = FSDirectory.open(path);
                IndexReader reader = DirectoryReader.open(directory);
                IndexSearcher searcher = new IndexSearcher(reader);
                Analyzer analyzer = new StandardAnalyzer();
               // Analyzer analyzer = new SmartChineseAnalyzer();

                QueryParser parser = new QueryParser(parameter.field_nv,analyzer);
                Query tq = new TermQuery(new Term(parameter.field_nv,term));
                Query query = parser.parse(term);

                ScoreDoc[] hits = searcher.search(query, parameter.n).scoreDocs;

                Set<String> resultSet = new HashSet<String>();

                System.out.println(hits.length + " total has been found");
                for(int i = 0; i < hits.length; i++)
                {
                    Document hitDoc = searcher.doc(hits[i].doc);

                    String name_vector = hitDoc.get("nv");
                    String[] item = name_vector.split(",");

                    for(int j = 0; j < item.length; j++) {
                        if (excludeNoise.hasTerm(item[j],term)) {
                            flag = 0;
                            break;
                        } else
                            flag = 1;
                    }
                    if(flag == 1) tolerate++;
                    if(flag == 1 && tolerate > 5) break;
                    System.out.println(hitDoc.get("sbj"));
                    resultSet.add(hitDoc.get("sbj"));
                    resultSet.add(hitDoc.get("sbj"));

                    System.out.println(hitDoc.get("nv"));
                    System.out.println("-------------------------------");
                }
                reader.close();
                directory.close();
                return resultSet;
            }
            catch(IOException ie)
            {
                ie.printStackTrace();
            }
            catch (ParseException pe)
            {
                pe.printStackTrace();
            }
            return null;
        }
    }

