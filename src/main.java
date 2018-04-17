import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Integer algo_selected = 0;
        Integer file_num = 0;
        Scanner S=new Scanner(System.in);

        System.out.println("========================================================");
        System.out.println("  Welcome to COS341 project_2a 2018\n\tcompiled by Kyle and Nathan");
        System.out.println("========================================================");
        System.out.println("\n");
        File curDir = new File("./TestFiles");

        if (curDir.exists() == false)
        {
            System.out.println("'TestFiles' Folder not found, searching in current Directory for resource files");
            curDir = new File(".");
        }
        do{
            System.out.println("========================================================");
            System.out.println("Please select a testFile: ");
            getAllFiles(curDir);
            file_num=S.nextInt();
        }
        while (file_num > curDir.listFiles().length || file_num < 1);

        File[] filesList = curDir.listFiles();
        File selectedFile = null;
        int count = 1;
        for(File f : filesList){
            if(f.isFile() || f.isDirectory()){
                if (count == file_num)
                {
                    selectedFile = f;
                    break;
                }
                count++;
            }
        }


        Lex lex = new Lex();
//        ;
        lex.setFileContent(readFile(selectedFile.getAbsolutePath()));
        lex.beginAnaylsis();
        //System.out.println(lex.getLexList());
        /*
        InsertLexer Here
        */
        Parser pars = new Parser(lex.head);
        pars.start();

        ScopeAnalyser scope= new ScopeAnalyser();
        System.out.println("______Scope Analyser_____");
        Scanner sc=new Scanner(System.in);
        System.out.println("Would you like to display the AST? [Y/N]");
        String r=sc.next();
        if(r.equals("Y"))
          System.out.println(pars.treeToString());
        System.out.println("\n_____Feedback:_____");
        scope.analyse(pars.getRoot());


        System.out.println("Would you like to display the Symbol table? [Y/N]");
        r=sc.next();
        if(r.equals("Y")){
          System.out.println("\n_____Symbol table:_____");
          scope.printTable();
        }


    }

    private static void getAllFiles(File curDir) {
        File[] filesList = curDir.listFiles();
        int count = 1;
        for(File f : filesList){
            if(f.isFile() || f.isDirectory()){
                System.out.println("\t("+ (count++) +") "+f.getName());
            }
        }
    }

    private static List<String> readFile(String filename) throws IOException
    {
        System.out.println("========================================================");
        System.out.println("Reading Contents from File\n");

        List<String> lines = Files.readAllLines(Paths.get(filename));
        StringBuilder sb = new StringBuilder();

        if (lines.size() == 0)
        {
            System.out.println("Something went wrong while reading the files, structure is different");
            System.exit(0);
        }
        else
            System.out.println(lines.toString());

        return lines;
    }
}
