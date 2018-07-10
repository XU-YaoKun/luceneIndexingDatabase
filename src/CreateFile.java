import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.spec.ECField;
import java.util.HashSet;
import java.util.Set;

public class CreateFile {
    public static void main(String[] args)
    {
        Set<String> index = new HashSet<String>();
        index.add("but");
        index.add("everything");
        index.add("is");
        index.add("sheltering");
        index.add("it's");
        index.add("my");
        index.add("mistake");
        writeToFile(index);
    }

    private static boolean createFile(String fileName, String fileContent)
    {
        Boolean bool = false;
        File file = new File("D:\\JAVA\\INDEX\\inverted_index\\1.txt");
        try
        {
            if(!file.exists())
            {
                file.createNewFile();
                bool = true;
                System.out.println("create file successfully");
            }
        }
        catch (Exception e)
        {
            System.out.println("fail to create file");
            e.printStackTrace();
        }
        return bool;
    }

    private static void writeToFile(Set<String> index)
    {
        File file = new File("D:\\JAVA\\INDEX\\inverted_index\\1.txt");
        for(String str : index)
        {
            FileOutputStream fos = null;
            PrintStream ps = null;
            try
            {
                fos = new FileOutputStream(file, true);
                ps = new PrintStream(fos);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            String string = str + "\r\n";
            ps.print(string);
            ps.close();
        }
    }
}
