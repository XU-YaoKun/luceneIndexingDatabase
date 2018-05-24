import org.apache.lucene.analysis.Analyzer;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class indexDatabase {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://192.168.2.247:3306/zhishi";

    static final String USER = "xyk";
    static final String PASS = "123";

    public static void main(String[] args)
    {
        try {
            indexDatabase index = new indexDatabase();
          //  index.indexDatabase(index.getResult("SELECT sbj, vd FROM vd_zhwiki"));
          //  index.searcher("\\u4f9b\\u7535");
            index.MatchAllDocs();
            index.searchIndex();
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
            System.out.println("connecting the database......");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

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
            final Path path = Paths.get("D:\\JAVA\\luceneIndexingDatabase\\INDEX");
            Directory directory = FSDirectory.open(path);

            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);

            IndexWriter writer = new IndexWriter(directory, config);

            int counter = 0;

            while(rs.next())
            {
                Document doc = new Document();

                String sbj = rs.getString("sbj");
                Field sbjField = new TextField("sbj", sbj, Field.Store.YES);
                doc.add(sbjField);

                String vd = rs.getString("vd");
                Field vdField = new TextField("vd", vd, Field.Store.YES);
                doc.add(vdField);

                writer.addDocument(doc);
                System.out.println("index " + counter + " has been created");
                counter++;
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
       String field = "vd";
       String queryStr = queryString;
       final Path path = Paths.get("D:\\JAVA\\luceneIndexingDatabase\\INDEX");
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
            final Path path = Paths.get("D:\\JAVA\\luceneIndexingDatabase\\INDEX");
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
                 System.out.println(document.get("vd"));
                 System.out.println("-------------------------------------");
             }
             reader.close();
        }
       catch(IOException ie)
    {
        ie.printStackTrace();
    }
        }

        public void searchIndex()
        {
            try {
                final Path path = Paths.get("D:\\JAVA\\luceneIndexingDatabase\\INDEX");
                Directory directory = FSDirectory.open(path);
                IndexReader reader = DirectoryReader.open(directory);
                IndexSearcher searcher = new IndexSearcher(reader);
                Analyzer analyzer = new StandardAnalyzer();

                QueryParser parser = new QueryParser("vd",analyzer);
                Query query = parser.parse("iPod");
                ScoreDoc[] hits = searcher.search(query, 1000).scoreDocs;
                System.out.println(hits.length + " total has been found");
                for(int i = 0; i < hits.length; i++)
                {
                    Document hitDoc = searcher.doc(hits[i].doc);
                    System.out.println(hitDoc.get("sbj"));
                    System.out.println(hitDoc.get("vd"));
                    System.out.println("-------------------------------");
                }
                reader.close();
                directory.close();
            }
            catch(IOException ie)
            {
                ie.printStackTrace();
            }
            catch (ParseException pe)
            {
                pe.printStackTrace();
            }
        }


    }

