package crux;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static String studentName = "Gabriel Blanco";
    public static String studentID = "81443241";
    public static String uciNetID = "gblanco1";
    
    public static void main(String[] args) throws IOException
    {
        String sourceFilename = args[0];
        String outName = args[1];
        String text = new String(Files.readAllBytes(Paths.get(outName)), StandardCharsets.UTF_8);
        
        Scanner s = null;
        try {
            s = new Scanner(new FileReader(sourceFilename));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error accessing the source file: \"" + sourceFilename + "\"");
            System.exit(-2);
        }

        Parser p = new Parser(s);
        p.parse();
        if (p.hasError()) {
            System.out.println("Error parsing file.");
            System.out.println(p.errorReport());
//            if (text.substring(0, text.length() - 1).equals("Error parsing file.\n" + p.errorReport()))
//            {
//                System.out.println("ERROR PASS");
//            }
            System.exit(-3);
        }
        System.out.println(p.parseTreeReport());


//        if (text.substring(0, text.length() - 1).equals(p.parseTreeReport()))
//            System.out.println("PASS");

    }
}
    
