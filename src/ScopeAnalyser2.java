import java.util.*;

public class ScopeAnalyser2 {

    int uniqueId = 0;
    int currentLevel = 0 ;
    int procNewIds = 0 ;
    int varNewId = 0 ;
    ArrayList<ScopeEntry> table=new ArrayList<ScopeEntry>();
    Stack<ScopeEntry> dynamicTable=new Stack<ScopeEntry>();
    ScopeAnalyser2(Node x)
    {
//        DFTprocSearch(x);
        DFTraversal(x);
    }

    public void DFTraversal(Node x)
    {
//        table.add(new ScopeEntry(currentLevel, uniqueId++, x.val, "", false));
        for (int i = 0; i < x.children.size(); i++)
        {
            Node childToVisit = x.children.get(i);

            if (x.val.equals("COND_LOOP") && x.children.get(0).val.equals("for"))
            {
//                if(CheckIfExists(x.children.get(0).getVal(), true, currentLevel+1))   //Means Exists
//                {
                currentLevel++;
                    table.add(new ScopeEntry(currentLevel, uniqueId, x.children.get(1).children.get(0).children.get(0).children.get(0).getVal(), "V"+varNewId, false));
                    dynamicTable.push(new ScopeEntry(currentLevel, uniqueId, x.children.get(1).children.get(0).children.get(0).children.get(0).getVal(), "V"+varNewId, false));
                    dynamicTable.push(new ScopeEntry(-2, -1, "MARKER", "", false));
//                }
//                else
//                {
//                    table.add(new ScopeEntry(currentLevel, uniqueId, childToVisit.getVal(), "P"+procNewIds++, false));
//                    dynamicTable.push(new ScopeEntry(currentLevel, uniqueId, childToVisit.getVal(), "REDEF ", false));
//                    dynamicTable.push(new ScopeEntry(-2, -1, "MARKER", "", false));
//                }
                varNewId++;
                uniqueId++;

                for (int j = 1; j < x.children.size()-1; j++)
                {
                    DFTraversal(x.children.get(j));
                }
//                DFTraversal(childToVisit);
                clearStackTillMarker();
                currentLevel--;
                break;
            }
            else if(x.val.equals("DECL"))
            {
                if(CheckIfExists(x.children.get(1).children.get(0).children.get(0).getVal(), false, currentLevel))   //Means Exists
                {
                    table.add(new ScopeEntry(currentLevel, uniqueId, x.children.get(1).children.get(0).children.get(0).getVal(), "REDEF ", false));
                }
                else
                {
                    table.add(new ScopeEntry(currentLevel, uniqueId, x.children.get(1).children.get(0).children.get(0).getVal(), "V"+varNewId, false));
                }
                varNewId++;
                uniqueId++;
                currentLevel++;
                DFTraversal(x.children.get(1));       // So you skip the name and go straight to the -CODE child path


                currentLevel--;
//                break;
            }
            else
            {
                DFTraversal(childToVisit);
            }
        }
    }

    public void DFTprocSearch(Node x)
    {
//        System.out.println(x.val);
        for (int i = 0; i < x.children.size(); i++)
        {
            Node childToVisit = x.children.get(i);
            if(x.val.equals("PROC"))
            {
                if(CheckIfExists(x.children.get(0).getVal(), true, currentLevel+1))   //Means Exists
                {
                    table.add(new ScopeEntry(currentLevel, uniqueId, childToVisit.getVal(), "REDEF ", true));
                    dynamicTable.push(new ScopeEntry(currentLevel, uniqueId, childToVisit.getVal(), "REDEF ", true));
                    dynamicTable.push(new ScopeEntry(-2, -1, "MARKER", "", true));
                }
                else
                {
                    table.add(new ScopeEntry(currentLevel, uniqueId, childToVisit.getVal(), "P"+procNewIds++, true));
                    dynamicTable.push(new ScopeEntry(currentLevel, uniqueId, childToVisit.getVal(), "REDEF ", true));
                    dynamicTable.push(new ScopeEntry(-2, -1, "MARKER", "", true));
                }

                uniqueId++;
                currentLevel++;
                DFTprocSearch(x.children.get(1));       // So you skip the name and go straight to the -CODE child path

                clearStackTillMarker();
                currentLevel--;
                break;
            }
            else
            {
                DFTprocSearch(childToVisit);
            }
        }
    }

    public boolean CheckIfExists(String val, boolean isProc, Integer lvl)
    {
//        Stack<ScopeEntry> clone = dynamicTable.get();
        for (int i = 0; i < dynamicTable.size(); i++)
        {
            ScopeEntry entryInFocus = dynamicTable.get(i);
            if (entryInFocus.oldName.equals(val) && entryInFocus.scopeLevel <= lvl && isProc )
            {
                return true;
            }
        }
        return false;
    }

    public void clearStackTillMarker()
    {
        while( dynamicTable.size() > 0)
        {
            if (dynamicTable.pop().scopeLevel == -2)
                break;
        }
    }

    public String tableToString()
    {
        String toReturn = "";
        while (table.isEmpty() == false)
        {
            toReturn += table.remove(0).toProperOutput();
        }
        return toReturn;
    }
}
