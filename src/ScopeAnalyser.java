import java.util.*;
import java.io.*;


public class ScopeAnalyser{

  int scopeCount=0;
  int currentScope=0;
  int IDcount=0;
  int varCount=0;
  int countToVar;
  int countToFor=-1;
  int persistSizeDiff=0;
  int tabCount=0;
  int numForLoops=0;
  //Node root;
  String forCounter="";
  boolean checkScope=false;
  boolean checkVar=false;
  boolean procNext=false;
  boolean checkFor=false;
  boolean activeForLoop=false;
  Stack<ArrayList<String>> table=new Stack<ArrayList<String>>();
  ArrayList<ArrayList<String>> persistentTable=new ArrayList<ArrayList<String>>();
  ArrayList<String> declaredProcs=new ArrayList<String>();
  ArrayList<String> declaredProcsScopes=new ArrayList<String>();
  ArrayList<ArrayList<String>> subScopeList= new ArrayList<ArrayList<String>>();
  Stack<String> forLoopCounters= new Stack<String>();
  Stack<String> forLoopCountersID= new Stack<String>();
  ArrayList<String> redeclaredVariables=new ArrayList<String>();
  public ScopeAnalyser(){

  }

  public void analyse(Node root){
    ArrayList<String> temp= new ArrayList<String>();
    temp.add(String.valueOf(IDcount));
    temp.add("ScopeMarker");
    temp.add(String.valueOf(currentScope));
    persistentTable.add(temp);
    table.push(temp);
    addSubScope();
    transverseTree(root);
    checkProcs();
    int n=0;
    /*System.out.println("Undeclared Variables and Procedures: ");
    for(int i=0;i<persistentTable.size();i++){
      if(persistentTable.get(i).size()==4&&persistentTable.get(i).get(3).equals("U")){
        n++;
        System.out.println(persistentTable.get(i).get(1)+" in scope "+persistentTable.get(i).get(2));
      }
    }

    if(n==0){
      System.out.println("None");
    }*/

    System.out.println("\nRedeclared Variables:");
    for(int i=0;i<redeclaredVariables.size();i++)
      System.out.println(redeclaredVariables.get(i));
    if(redeclaredVariables.size()==0)
      System.out.println("None");

      /*System.out.println("\nRedeclared procedures:");

    n=0;
    for(int i=0;i<declaredProcs.size();i++){
      for(int b=i+1;b<declaredProcs.size();b++){
        if(declaredProcs.get(i).equals(declaredProcs.get(b)))
          n++;
          System.out.println("Proc: "+declaredProcs.get(i));
      }
    }
    if(n==0){
      System.out.println("None");
    }*/
    try{
      writeToFile();
    }catch(IOException e){
      System.out.println(e);
    }

  }

  private int checkForLoopCounters(String s){
    for(int i=0;i<forLoopCounters.size();i++){
      if(s.equals(forLoopCounters.get(i))){
        return i;
      }
    }
    return -1;
  }

  private void writeToFile() throws IOException{
    String write="";
    int numTabs=-1;
    for(int i=0;i<persistentTable.size();i++){
      if(persistentTable.get(i).get(1).equals("ScopeMarker")){
        numTabs++;
      }else if(persistentTable.get(i).get(1).equals("EndScopeMarker"))
        numTabs--;
      else{
      for(int t=0;t<numTabs;t++)
        write+="\t";
      if(persistentTable.get(i).size()==4){
        write+=persistentTable.get(i).get(0)+" ["+persistentTable.get(i).get(1)+" = "+persistentTable.get(i).get(3)+" ] "+persistentTable.get(i).get(2);
      }else{
        write+=persistentTable.get(i).get(0)+" "+persistentTable.get(i).get(1)+" "+persistentTable.get(i).get(2);
      }
      write+="\n";
    }
    }

      BufferedWriter writer= new BufferedWriter(new FileWriter("SymbolTable.txt"));
      writer.write(write);
      writer.close();
    }


  public void printTable(){
    int numTabs=-1;
    for(int i=0;i<persistentTable.size();i++){
      if(persistentTable.get(i).get(1).equals("ScopeMarker")){
        numTabs++;
      }else if(persistentTable.get(i).get(1).equals("EndScopeMarker"))
        numTabs--;
      else{
      for(int t=0;t<numTabs;t++)
        System.out.print("\t");
      if(persistentTable.get(i).size()>3){
        System.out.println(persistentTable.get(i).get(0)+" ["+persistentTable.get(i).get(1)+" = "+persistentTable.get(i).get(3)+" ] "+persistentTable.get(i).get(2));
      }else{
        System.out.println(persistentTable.get(i).get(0)+" "+persistentTable.get(i).get(1)+" "+persistentTable.get(i).get(2));
      }
    }
    }
  }

  private boolean checkSubScope(String inner,String outer){
    //System.out.println("SubScope: "+inner+" "+outer);

    for(int i=0;i<subScopeList.size();i++){
      if(subScopeList.get(i).get(0).equals(inner)){
        for(int b=0;b<subScopeList.get(i).size();b++){
          if(subScopeList.get(i).get(b).equals(outer)){
            //System.out.println("Returning true");
            return true;

          }
        }
        return false;
      }
    }
    return false;
  }

  private void checkProcs(){
    String name="P";
    String outer="";
    for(int i=0;i<declaredProcs.size();i++){
      for(int p=persistentTable.size()-1;p>0;p--){
        if(persistentTable.get(p).size()==4&&persistentTable.get(p).get(3).equals("PROC")&&persistentTable.get(p).get(1).equals(declaredProcs.get(i))&&persistentTable.get(p).get(2).equals(declaredProcsScopes.get(i))){
          persistentTable.get(p).remove(3);
          persistentTable.get(p).add(name+i);
          outer=persistentTable.get(p-2).get(2);
        }
      }
      for(int p=persistentTable.size()-1;p>0;p--){
        if(persistentTable.get(p).get(1).equals("CALL")&&persistentTable.get(p+1).get(1).equals(declaredProcs.get(i))&&checkSubScope(persistentTable.get(p+1).get(2),outer)){
          //System.out.println("Adding to "+persistentTable.get(p+1).get(0)+" "+(name+i));
          persistentTable.get(p+1).add(name+i);
        }
      }
    }

    for(int i=0;i<persistentTable.size()-1;i++){
      if(persistentTable.get(i).get(1).equals("CALL")&&persistentTable.get(i+1).size()==3){
        persistentTable.get(i+1).add("U");
      }
    }
  }

  private void addSubScope(){
    subScopeList.add(new ArrayList<String>());
    subScopeList.get(subScopeList.size()-1).add(String.valueOf(currentScope));
    for(int i=0;i<table.size();i++){
      subScopeList.get(subScopeList.size()-1).add(table.get(i).get(2));
    }
  }

  private void transverseTree(Node node){

    if(node.val.equals("PROC")||node.val.equals("COND_BRANCH")||node.val.equals("COND_LOOP")||node.val.equals("COND_LOOP ")){
      ArrayList<String> temp= new ArrayList<String>();
      temp.add(String.valueOf(IDcount));
      temp.add("ScopeMarker");
      temp.add(String.valueOf(currentScope));
      table.push(temp);
      persistentTable.add(temp);

      currentScope=++scopeCount;
      addSubScope();
    }


    if(checkFor){
      countToFor--;
    }

    if(node.val.equals("COND_LOOP ")){
      activeForLoop=true;
      countToFor=5;
      checkFor=true;
    }

    ArrayList<String> temp= new ArrayList<String>();
    temp.add(String.valueOf(IDcount));
    temp.add(node.val);
    temp.add(String.valueOf(currentScope));
    if(checkFor&&countToFor==0){
      forLoopCounters.push(node.val);
      numForLoops++;
      forLoopCountersID.push("V"+varCount++);
      checkFor=false;
    }
    table.push(temp);
    persistentTable.add(temp);

    if(checkVar){
      countToVar--;
      if(countToVar==0){
        checkRedeclare();
        checkVar=false;
        String name="V"+varCount;
        table.peek().add(name);
        varCount++;
      }
    }

    if(node.val.equals("DECL")){
      checkVar=true;
      countToVar=5;
    }



    if(checkScope){
      if(activeForLoop){
        if(checkForLoopCounters(node.val)!=-1){
          persistentTable.get(persistentTable.size()-1).add(forLoopCountersID.get(checkForLoopCounters(node.val)));
        }else{
          checkScope();
        }
      }else{
        checkScope();
      }
      checkScope=false;
    }

    if(node.val.equals("VAR"))
      checkScope=true;

    if(procNext){
      declaredProcs.add(node.val);
      declaredProcsScopes.add(""+currentScope);
      table.peek().add("PROC");
      procNext=false;
    }

    if(node.val.equals("PROC"))
      procNext=true;

    IDcount++;
    for(int i=0;i<node.children.size();i++){
      transverseTree(node.children.get(i));
    }

    if(node.val.equals("COND_LOOP ")){
      numForLoops--;
      if(numForLoops==0)
        activeForLoop=false;
      forLoopCounters.pop();
      forLoopCountersID.pop();
    }

    if(node.val.equals("PROC")||node.val.equals("COND_BRANCH")||node.val.equals("COND_LOOP")||node.val.equals("COND_LOOP ")){
      ArrayList<String> tempExit= new ArrayList<String>();
      tempExit.add(String.valueOf(IDcount));
      tempExit.add("EndScopeMarker");
      tempExit.add(String.valueOf(currentScope));
      table.push(tempExit);
      persistentTable.add(tempExit);
      exitScope();
    }

  }

  private void exitScope(){
    while(!table.peek().get(1).equals("ScopeMarker")){
      table.pop();
    }
    currentScope=Integer.parseInt(table.peek().get(2));
    table.pop();
    Stack<ArrayList<String>> copyTable=(Stack<ArrayList<String>>)table.clone();
  }

  private void checkScope(){
    boolean found=false;
    Stack<ArrayList<String>> copyTable=(Stack<ArrayList<String>>)table.clone();
    ArrayList<ArrayList<String>> tempTable=new ArrayList<ArrayList<String>>();
    ArrayList<String> temp;
    while(!copyTable.empty()){
      tempTable.add(copyTable.peek());
      copyTable.pop();
    }

    int i=1;
    while(i<tempTable.size()){
      if(tempTable.get(i).get(1).equals("DECL")){
        if(tempTable.get(i-5).get(1).equals(tempTable.get(0).get(1))){
          found=true;
          table.peek().add(tempTable.get(i-5).get(3));
          break;
        }
      }
      i++;
    }

    if(!found){
      table.peek().add("U");
    }

  }

  private void checkRedeclare(){
    int i=table.size()-2;
    while(!table.get(i).get(1).equals("ScopeMarker")){
      if(table.get(i).get(1).equals(table.get(table.size()-1).get(1))&&table.get(i-5).get(1).equals("DECL")){
        //System.out.println(table.get(table.size()-1).get(1)+" has been redeclared.");
        redeclaredVariables.add("Variable: "+table.get(table.size()-1).get(1)+" Scope: "+table.get(table.size()-1).get(2));
        break;
      }
      i--;
    }

  }



}
