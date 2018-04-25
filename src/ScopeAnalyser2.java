import java.util.*;

public class ScopeAnalyser2 {

    int uniqueId = 0;
    int currentLevel = 0 ;
    int procNewIds = 0 ;
    int varNewId = 0 ;
    Node root = null;
    ArrayList<ScopeEntry> table;
    Stack<ScopeEntry> dynamicTable=new Stack<ScopeEntry>();
    ArrayList<Linkages> linkages=new ArrayList<Linkages>();

    ScopeAnalyser2(Node x, ArrayList<Linkages> linkages, ArrayList<ScopeEntry> scopeEntries)
    {
        root = x;
        table = scopeEntries;
        this.linkages = linkages;
        DFTraversal(x);
    }

    public void DFTraversal(Node x)
    {
        for (int i = 0; i < x.children.size(); i++)
        {
            Node childToVisit = x.children.get(i);
//            System.out.println(childToVisit.idNode);

            if (childToVisit.getVal().equals("input"))
            {
                x.children.get(i+1).children.get(0).hasBeenSet = "YES";
                markTablePositive(x.children.get(i+1).children.get(0).idNode);
            }
            else if (childToVisit.getVal().equals("output"))
            {
                if (checkVal(x.children.get(i+1).children.get(0).idNode))
                {
//                    System.out.println("True output: "+ x.children.get(i+1).children.get(0).idNode);
                    x.children.get(i+1).children.get(0).hasBeenSet = "YES";
                    markTablePositive(x.children.get(i+1).children.get(0).idNode);
                }
                else
                {
//                    System.out.println("False output: "+ x.children.get(i+1).children.get(0).idNode);
                    x.children.get(i+1).children.get(0).hasBeenSet = "ERROR";
                    markTableError(x.children.get(i+1).children.get(0).idNode);
                }
            }
            else if (childToVisit.getVal().equals("ASSIGN"))
            {
                func_ASSIGN(childToVisit);
            }
            else if (x.getVal().equals("COND_LOOP") && x.children.get(0).getVal().equals("while") )
            {
                func_BOOL(x.children.get(1));
                DFTraversal(x.children.get(2));
            }
            else if(x.children.get(0) != null && x.children.get(0).getVal().equals("for") && i == 2)
            {
                if ( checkVal(x.children.get(2).children.get(0).idNode))
                {
                    x.children.get(2).children.get(0).hasBeenSet = "YES";
                    markTablePositive(x.children.get(2).children.get(0).idNode);
                }
                else
                {
                    x.children.get(2).children.get(0).hasBeenSet = "ERROR";
                    markTableError(x.children.get(2).children.get(0).idNode);
                }
                if ( checkVal(x.children.get(4).children.get(0).idNode))
                {
                    x.children.get(4).children.get(0).hasBeenSet = "YES";
                    markTablePositive(x.children.get(4).children.get(0).idNode);
                }
                else
                {
                    x.children.get(4).children.get(0).hasBeenSet = "ERROR";
                    markTableError(x.children.get(4).children.get(0).idNode);
                }
            }
            else if (childToVisit.getVal().equals("COND_BRANCH"))
            {
                func_BOOL(childToVisit.children.get(0));
                for (int j =1; j < childToVisit.children.size(); j++)
                {
                    if (j == 2)
                    {
                        RefractorChildren(childToVisit.children.get(j));
                    }
                    DFTraversal(childToVisit.children.get(j));
                }
            }
            else
            {
                DFTraversal(childToVisit);
            }
        }
    }

    //TODO: Have this fixed, 26 April Quick Fix
    private void RefractorChildren(Node x)
    {
        int xId = x.idNode;
        int oldScopeId = -1;

        for (int i = 0; i < table.size(); i++)
        {
            if (table.get(i).nodeId == xId)
                oldScopeId = table.get(i).scopeID;
        }
        for (int i = xId-1; i > 0 && table.get(i).scopeID == oldScopeId; i--)
        {
            if (table.get(i).hasBeenAssigned != null && table.get(i).hasBeenAssigned.equals("YES"))
            {
                table.get(i).hasBeenAssigned = null;
            }
        }
    }

    private void func_ASSIGN(Node x)
    {
        boolean allowPositiveMark = false;
//        System.out.println(x.idNode+" "+x.getVal()+" child in focus");
        Node rightExpression = x.children.get(1);
        Node leftVariable = x.children.get(0).children.get(0);
        int totalTally = 0;
        if (rightExpression.getVal().contains("\""))
            allowPositiveMark = true;
        else if (rightExpression.getVal().equals("VAR"))
        {
//            System.out.println("We are here");
            rightExpression = rightExpression.children.get(0);
            if (checkVal(rightExpression.idNode))
            {
//                System.out.println("It was Found "+rightExpression.idNode);
                rightExpression.hasBeenSet = "YES";
                allowPositiveMark = true;
                markTablePositive(rightExpression.idNode);
            }
            else
            {
                rightExpression.hasBeenSet = "ERROR";
                markTableError(rightExpression.idNode);
            }
        }
        else if (rightExpression.getVal().equals("BOOL"))
        {
//            System.out.println(rightExpression.children.get(0).getVal()+" XDSD");
//            Node leftOperand = rightExpression.children.get(1).children.get(0);
//            totalTally = ;
            if (func_BOOL(rightExpression))
            {
                allowPositiveMark = true;
            }
        }
        else
        {

            rightExpression = rightExpression.children.get(0);
            if (Character.isDigit(rightExpression.getVal().charAt(0)))
            {
                allowPositiveMark = true;
            }
            else if (rightExpression.getVal().equals("T") || rightExpression.getVal().equals("F"))
            {
                allowPositiveMark = true;
            }
            else if (rightExpression.getVal().equals("CALC"))
            {
                Node leftOperand = rightExpression.children.get(1).children.get(0);
                Node rightOperand = rightExpression.children.get(2).children.get(0);
//                totalTally = func_CALC(leftOperand);
//                System.out.println("TotalTally: "+totalTally);
//                totalTally += func_CALC(rightOperand);
                if (func_CALC(leftOperand) && func_CALC(rightOperand))
                {
                    allowPositiveMark = true;
                }
            }

        }


        if (allowPositiveMark)
        {
            leftVariable.hasBeenSet = "YES";
            markTablePositive(leftVariable.idNode);
        }
        else
        {
            leftVariable.hasBeenSet = "ERROR";
            markTableError(leftVariable.idNode);
        }
    }

    private boolean func_BOOL(Node bool)
    {
//        System.out.println("Dies here "+bool.getVal());
        if (bool.children.get(0).getVal().equals("not"))
            return func_BOOL(bool.children.get(0).children.get(0));
        else if (bool.children.get(0).getVal().equals("and") || bool.children.get(0).getVal().equals("or") || bool.children.get(0).getVal().equals("eq"))
        {
            if (func_BOOL(bool.children.get(1)) && func_BOOL(bool.children.get(2)))
                return true;
            else
                return false;
        }
        else if (bool.children.get(0).getVal().equals("VAR") && bool.children.size() > 1)
        {
            if (func_BOOL(bool.children.get(0)) && func_BOOL(bool.children.get(2)))
                return true;
            else
                return false;
        }
        else if (bool.getVal().equals("VAR"))
        {
            if ( checkVal(bool.children.get(0).idNode) )
            {
                bool.children.get(0).hasBeenSet = "YES";
                markTablePositive(bool.children.get(0).idNode);
                return true;
            }
            else
            {
                bool.children.get(0).hasBeenSet = "ERROR";
                markTableError(bool.children.get(0).idNode);
                return false;
            }
        }
        else if (bool.getVal().equals("BOOL") && ( bool.children.get(0).getVal().equals("T") || bool.children.get(0).getVal().equals("F")))
        {
                return true;
        }
        else if (bool.getVal().equals("BOOL"))
        {
            return func_BOOL(bool.children.get(0));
        }
//        Node x = null;
        System.out.println("SOMETHING WENT WRONG" + bool.idNode +" "+bool.getVal());
//        x.getVal();
//        System.exit(1);
        return  false;
    }

    private boolean func_CALC(Node calc)
    {
//        Node leftOperand = calc.children.get(1).children.get(0);
//        Node rightOperand = calc.children.get(2).children.get(0);
        int upGrade = 0;

//        System.out.println("ENtering CALC with "+ calc.getVal());
        if ( Character.isDigit(calc.getVal().charAt(0)))
        {
            return true;
        }
        else if (calc.getVal().equals("VAR"))
        {
            if (checkVal(calc.children.get(0).idNode))
            {
                calc.children.get(0).hasBeenSet = "YES";
                markTablePositive(calc.children.get(0).idNode);
                return true;
            }
            else
            {
                calc.children.get(0).hasBeenSet = "ERROR";
                markTableError(calc.children.get(0).idNode);
                return false;
            }
        }
        else if (calc.getVal().equals("CALC"))
        {
            boolean toReturn1 = func_CALC(calc.children.get(1).children.get(0));
            boolean toReturn2 = func_CALC(calc.children.get(2).children.get(0));
            return toReturn1 && toReturn2;
        }
        else
        {
            System.out.println("SOMETHING WENT WRONG 21___________________________");
        }

        return false;
    }

    private void markTablePositive(int x)
    {
        for (int i = 0; i < table.size(); i++)
        {
            if (table.get(i).nodeId == x)
            {
                table.get(i).hasBeenAssigned = "YES";
                return;
            }
        }
        System.out.println("SOMETHING WENT WRONG");
    }

    private void markTableError(int x)
    {
        for (int i = 0; i < table.size(); i++)
        {
            if (table.get(i).nodeId == x)
            {
                table.get(i).hasBeenAssigned = "ERROR";
                return;
            }
        }
        System.out.println("SOMETHING WENT WRONG");
    }

    private boolean checkVal(int x)
    {
        String variableUniqueName = null;
        Integer lockedView = null;
        for (int i = 0; i < table.size(); i++)
        {
            if (table.get(i).nodeId == x)
            {
                variableUniqueName = table.get(i).newName;
                lockedView = table.get(i).scopeID;
                break;
            }
        }

        for (int i = 0; i < x; i++)
        {
//            if (table.get(i) == null)
//                System.out.println( "Experienced a null at: "+x);
            if (table.get(i).newName != null && table.get(i).newName.equals(variableUniqueName) == true && allowScopeComparision( table.get(i).scopeID, lockedView) )
            {
//                System.out.println("value of x: "+x+variableUniqueName + "== " + table.get(i).newName );
                if ( table.get(i).hasBeenAssigned != null && table.get(i).hasBeenAssigned.equals("YES") )
                {
                    return true;
                }
            }
        }

        return  false;
    }

    private boolean allowScopeComparision(Integer openView, Integer lockedView)
    {//TODO Make it work for an infinate scoping depth
        if (openView == lockedView)
            return true;
        for( int i = 0; i < linkages.size() ; i++)
        {
            if (linkages.get(i).lockedView == lockedView && linkages.get(i).openView == openView)
            {
                return true;
            }
        }
        if (checkIfLinkPathExists(openView, lockedView))
            return true;
        return false;
    }

    private boolean checkIfLinkPathExists(Integer openView, Integer lockedView)
    {
        if (lockedView == 0)
            return false;
        for( int i = 0; i < linkages.size() ; i++)
        {
            if (linkages.get(i).lockedView == lockedView && linkages.get(i).openView == openView)
            {
                return true;
            }
            if (linkages.get(i).lockedView == lockedView)
            {
                return checkIfLinkPathExists(openView, linkages.get(i).openView);
            }
        }
        return false;

//        0 <- 1
//        1 <- 2
//
//        (0, 2)
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

    public void print2cTree()
    {
        String toReturn = "";

        toReturn = print("", root, true);

        System.out.println(toReturn);
    }

    private String print(String pre , Node x, boolean isTail)
    {
        String toReturn = "";
        if (x.hasBeenSet != null && x.hasBeenSet.equals("YES") == false)
        {
            toReturn += pre +"\u001B[33m"+ (x.children.size()==0 ? "'--- " : "|---- ") +x.idNode+"."+ x.getVal() + "\n";
            toReturn+="\u001B[0m";
        }
        else
            toReturn += pre + (x.children.size()==0 ? "'--- " : "|---- ") +x.idNode+"."+ x.getVal() + "\n";
        if (x.children.size() != 0)
        {
            for (int i = 0; i < x.children.size()-1; i++)
            {
                toReturn += print( pre+(isTail ? "     " : "|    "), x.children.get(i) , false);
            }
            if (x.children.size() > 0)
            {
                toReturn += print(pre+(isTail ? "     " : "|    "), x.children.get(x.children.size()-1), true);
            }
        }
        return toReturn;
    }
}
