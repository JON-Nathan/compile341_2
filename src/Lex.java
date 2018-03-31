/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ngobale
 */
public class Lex {
    // Alphabet
    ArrayList<Character> letters = new ArrayList<>();
    // Numbers
    ArrayList<Character> numbers = new ArrayList<>();

    List<String> fileContent;

    // Some chars are stored as Strings for same types createToken(String)
    ArrayList<String> compSymbols = new ArrayList<>();
    ArrayList<String> boolOperators = new ArrayList<>();
    ArrayList<String> numOperators = new ArrayList<>();
    ArrayList<String> sepSymbols = new ArrayList<>();
    ArrayList<String> grpSymbols = new ArrayList<>();
    ArrayList<String> ctrlStrucKeywords = new ArrayList<>();
    ArrayList<String> ioCommands = new ArrayList<>();
    String special = "halt";
    ArrayList<String> typeKeywords = new ArrayList<>();
    // To be used while scanning for a string
    char strIndicator = '"';
    char negative = '-';
    String assign = "=";
    String procedure = "proc";

    public Token head;

    Lex() {
        Character[] let = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z','T','F'};
        Character[] num = {'1','2','3','4','5','6','7','8','9','0'};
        String[] compSym = {"eq", "<", ">"};
        String[] boolOp = {"and", "or", "not"};
        String[] numOp = {"add", "sub", "mult"};
        String[] sepSym = {" "};
        String[] groupSym = {"(",")","{", "}", ",", ";"};
        String[] conStr = {"if", "then", "else", "while", "for"};
        String[] io = {"input", "output"};
        String[] type = {"num", "bool", "string"};

        letters.addAll(Arrays.asList(let));
        numbers.addAll(Arrays.asList(num));
        compSymbols.addAll(Arrays.asList(compSym));
        boolOperators.addAll(Arrays.asList(boolOp));
        numOperators.addAll(Arrays.asList(numOp));
        sepSymbols.addAll(Arrays.asList(sepSym));
        grpSymbols.addAll(Arrays.asList(groupSym));
        ctrlStrucKeywords.addAll(Arrays.asList(conStr));
        ioCommands.addAll(Arrays.asList(io));
//        so far away
        typeKeywords.addAll(Arrays.asList(type));
    }

    void setFileContent(List<String> x)
    {
        fileContent = x;
    }




    void beginAnaylsis()
    {  
        Token tail = new Token("Head","");
        head = tail;
        String line = "";
        for(String x : this.fileContent)
            line += x;
//        for(String line : this.fileContent)
        {
            while (line.length() > 0)
            {
                int i = 1;
                if (letters.contains(line.charAt(0)))  //if it's a letter
                {
                    //String mess;
                    //int wordsize = 0;
                    for ( ;i < line.length() && (letters.contains(line.charAt(i)) || numbers.contains(line.charAt(i))) ; i++)
                    {

                    }
                    //mess = line.substring(0, i);
//                    for (int c = 0; c < compSymbols.size(); c++) {
//                        if (mess.contains(compSymbols.get(c))) {
//                            if (mess.indexOf(compSymbols.get(c)) < i) {
//                                i = mess.indexOf(compSymbols.get(c));
//                                wordsize = compSymbols.get(c).length();
//                            }
//                        }
//                    }
//                    for (int c = 0; c < boolOperators.size(); c++) {
//                        if (mess.contains(boolOperators.get(c))) {
//                            if (mess.indexOf(boolOperators.get(c)) < i) {
//                                i = mess.indexOf(boolOperators.get(c));
//                                wordsize = boolOperators.get(c).length();
//                            }
//                        }
//                    }
//                    for (int c = 0; c < numOperators.size(); c++) {
//                        if (mess.contains(numOperators.get(c))) {
//                            if (mess.indexOf(numOperators.get(c)) < i) {
//                                i = mess.indexOf(numOperators.get(c));
//                                wordsize = numOperators.get(c).length();
//                            }
//                        }
//                    }
//                    for (int c = 0; c < ctrlStrucKeywords.size(); c++) {
//                        if (mess.contains(ctrlStrucKeywords.get(c))) {
//                            if (mess.indexOf(ctrlStrucKeywords.get(c)) < i) {
//                                i = mess.indexOf(ctrlStrucKeywords.get(c));
//                                wordsize = ctrlStrucKeywords.get(c).length();
//                            }
//                        }
//                    }
//                    for (int c = 0; c < ioCommands.size(); c++) {
//                        if (mess.contains(ioCommands.get(c))) {
//                            if (mess.indexOf(ioCommands.get(c)) < i) {
//                                i = mess.indexOf(ioCommands.get(c));
//                                wordsize = ioCommands.get(c).length();
//                            }
//                        }
//                    }
//                    for (int c = 0; c < typeKeywords.size(); c++) {
//                        if (mess.contains(typeKeywords.get(c))) {
//                            if (mess.indexOf(typeKeywords.get(c)) < i) {
//                                i = mess.indexOf(typeKeywords.get(c));
//                                wordsize = typeKeywords.get(c).length();
//                            }
//                        }
//                    }
//                    if (mess.contains(special)) {
//                        if (mess.indexOf(special) < i) {
//                            i = mess.indexOf(special);
//                            wordsize = special.length();
//                        }
//                    }
//                    if (mess.contains(procedure)) {
//                        if (mess.indexOf(procedure) < i) {
//                            i = mess.indexOf(procedure);
//                            wordsize = procedure.length();
//                        }
//                    }
                    // Found special word at start of mess
                    //if (i == 0) {
                     //   i += wordsize;
                    //}
                    tail.next = createWordToken(line.substring(0, i));
                }
                else if( (numbers.contains(line.charAt(0)) || line.charAt(0) == '-'))
                {
                    if ((line.charAt(0) == '-') && line.length() == 1)
                    {
                        System.out.println("Lexical Error: '-' found without any following input. Scanning Aborted");
                        System.exit(0);
                    }
                    if ((line.charAt(0) == '-') && (!(numbers.contains(line.charAt(i)))))
                    {
                        System.out.println("Lexical Error: '-' symbol found but followed by non numeric value '"+ line.charAt(i) +"'. Scanning Aborted");
                        System.exit(0);
                    }
                    if ((line.charAt(0) == '-') && (line.charAt(i)=='0'))
                    {
                        System.out.println("Lexical Error: '-' symbol found but followed by 0. Scanning Aborted");
                        System.exit(0);
                    }
//                    if ((line.charAt(0) == '0'))
//                    {
//                        System.out.println("Lexical Error: '0' symbol found and is not a valid number. Scanning Aborted");
//                        System.exit(0);
//                    }
                    for ( ;i < line.length() && (numbers.contains(line.charAt(i))) ; i++)
                    {
                    }
                    tail.next = createIntToken(line.substring(0, i));
                }
                else if (grpSymbols.contains(line.substring(0,1))
                        || compSymbols.contains(line.substring(0,1))
                        || sepSymbols.contains(line.substring(0,1)))
                {
                    if (line.charAt(0) != ' ')
                        tail.next = createSymbolToken(line.substring(0,1));
                }
                else if (assign.contains(line.substring(0,1)))
                {
                    tail.next = new Token("Assignment Operator","=");
                }
                else if(strIndicator == line.charAt(0))
                {

                    for ( ;i < line.length() && line.charAt(i) != strIndicator; i++)
                    {
                        if (!(letters.contains(line.charAt(i)) || sepSymbols.contains(line.substring(i,i+1)) || numbers.contains(line.charAt(i))) )
                        {
                            System.out.println("Lexical Error: Foreign character found: '"+line.substring(i,i+1)+"' while constructing a string. Scanning Aborted");
                            System.exit(0);
                        }
                        else if(i == 9)
                        {
                            System.out.println("Lexical Error: String length exceeded maximum. Scanning Aborted");
                            System.exit(0);
                        }
                    };
                    if(i == line.length())
                    {
                        System.out.println("Lexical Error: End of file reached without closing '\"'. Scanning Aborted");
                        System.exit(0);
                    }
                    tail.next = createStringToken(line.substring(0, ++i));
                }
                else if (line.charAt(0) == '\t')
                {

                }
                else // Invalid char
                {
                    System.out.println("Lexical Error: Foreign character found: "+line.charAt(0)+". Scanning Aborted");
                    System.exit(0);
                }

//                System.out.println("pos: " + line.substring(0, i));
                line = line.substring(i);

                if (tail.next != null)
                    tail = tail.next;
            }

        }
    }

    public String getLexList()
    {
        String toReturn = "";
        Token ptr = head;
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter("tokenfile.txt", false));
            while (ptr != null) {
                String line = ptr.id + ": " + ptr.getSymbolClass() + ": " + ptr.getInputSnippet();
                toReturn += line;
                file.append(line);
                file.newLine();
                toReturn += "\n\t|\n\tV\n";
                ptr = ptr.next;
            }
            file.close();
        }
        catch (IOException ex) {

        }
        return toReturn;
    }

    private Token createWordToken(String word) {
        Token token;

        /* Find the appropriate class for word */
        if (compSymbols.contains(word)) {
            token = new Token("Comparison Symbol", word);
        }
        else if (boolOperators.contains(word)) {
            token = new Token("Boolean Operator", word);
        }
        else if (numOperators.contains(word)) {
            token = new Token("Number Operator", word);
        }
        else if (ctrlStrucKeywords.contains(word)) {
            token = new Token("Control Structure Keyword", word);
        }
        else if (ioCommands.contains(word)) {
            token = new Token("I/O Command", word);
        }
        else if (typeKeywords.contains(word)) {
            token = new Token("Type Declaration Keyword", word);
        }
        else if (word.compareTo(procedure) == 0) {
            token = new Token("Procedure Definition Keyword", word);
        }
        else if (word.compareTo(special) == 0) {
            token = new Token("Special Command",word);
        }
        else {
            token = createUserDefName(word);
        }

        return token;
    }

    /* Takes a string as parameter and returns a User defined name
       (e.g. variable name) token.
     */
    Token createUserDefName(String word) {
        // Lett(rom) + (Lett(rom) | D(null)*
        // D(null) = (0 | D(pos))
        // D(pos) = (1|2|3|4|5|6|7|8|9)

        // Incorrect length
        if (word.length() == 0) {
            return null;
        }

        // Check if word is special keyword
        if (compSymbols.contains(word) || boolOperators.contains(word) ||
                numOperators.contains(word) || ctrlStrucKeywords.contains(word) ||
                ioCommands.contains(word) || typeKeywords.contains(word) ||
                special.compareTo(word) == 0 || procedure.compareTo(word) == 0) {
            return null;
        }

        // Check if chars are valid
        // First char must be a letter
        if (!letters.contains(word.charAt(0))) {
            return null;
        }

        // Letter or number
        for (int i = 1; i < word.length(); i++) {
            if (!letters.contains(word.charAt(i)) &&
                    !numbers.contains(word.charAt(i))) {
                return null;
            }
        }

        return new Token("User-Defined Name", word);
    }

    public Token createIntToken(String word) {
        // (- | ε) + D(pos) + D(null)
        // D(null) = (0 | D(pos))
        // D(pos) = (1|2|3|4|5|6|7|8|9)

        int i = 0;

        // Negative
        if (word.charAt(0) == negative) {
            // Number is only '-'
            if (word.length() == '1') {
                return null;
            }
            i++;
        }

        // Check if first character is 0
//        if (word.charAt(i) == '0') {
//            return null;
//        }

        // Check if all characters are numbers
        while (i < word.length()) {
            if (!numbers.contains(word.charAt(i++))) {
                return null;
            }
        }

        return new Token("Integer", word);
    }

    /* Takes a string as parameter and returns String Token */
    public Token createStringToken(String string) {
        // Check length
        if (string.length() < 2 || string.length() > 10) {
            return null;
        }

        // Check ""
        if (string.charAt(0) != strIndicator && string.charAt(string.length() - 1) != strIndicator) {
            return null;
        }

        for (int i = 1; i < string.length() - 1; i++) {
            if (!letters.contains(string.charAt(i)) &&
                    !numbers.contains(string.charAt(i)) &&
                    !(string.charAt(i) == ' ')) {
                return null;
            }
        }

        return new Token("String", string);
    }

    public Token createSymbolToken(String character)
    {
        if (grpSymbols.contains(character))
        {
            return new Token("Group Symbol", character);
        }
        else if (compSymbols.contains(character))
        {
            return new Token("Comparison Symbol", character);
        }
        return null;
    }

    /*
    inputnot,eq<and>or,not,add,sub,mult,"", ,(,),{,},;,=,if,
then,else,while,for,
input,output,halt,num,bool,string,proc,-1234567890,123456789,aw3,hf0,"df"
     */
}
