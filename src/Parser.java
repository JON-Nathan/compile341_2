public class Parser {
    Node head;
    Token tHead;

    public Parser(Token head) {
        this.tHead = head;
    }

    public void start() {
        head = new Node();
        head.setVal("PROG");
//        head.val = ;
        func_PROG(tHead.next, head);

//        head.
    }

    public void func_PROG(Token x, Node parent)
    {

        if (x.symbolClass.equals("Procedure Definition Keyword"))
        {
            //TODO: Check for this
            System.out.println("Syntax Error: Cannot start application with proc");
            System.exit(0);
        }
        Token root = x;
        while (x.next != null)
        {
//            if (x.next.inputSnippet.equals("proc"))
//            {
//                Token tknPtr = x.next;
//                x.next = null;
//                func_CODE(root, parent, 0);
//                func_PROC(tknPtr, parent);
//                return ;
//            }
            x = x.next;
        }
        if (x.next == null)
            func_CODE(root, parent, 0);
    }

    public void SynError(String x)
    {
        System.out.println();
        System.out.println();
        System.out.println("=======================================");
        System.out.println(x);
        System.exit(0);
    }

    String priorParent = "";
    String priorCond = "";
    public void check(int size, String[] arr, Token xCode)
    {

    }
    // allowProcs: - 0 means first time; 1 means allow; 2 means don't allow
    public void func_CODE(Token xCode, Node parent, int allowProcs)
    {
        //TODO: Should not allow for procedures to exist

        boolean procException= false;
        if (xCode == null)
            return;

//        System.out.println("PRINTING func_CODE");
//        System.out.println(xCode.getInputSnippet());
        Node nde_Code = new Node("CODE");

        Token root = xCode;

        if (!xCode.symbolClass.equals("Procedure Definition Keyword") && allowProcs == 3)
        {
            SynError("Syntax Error: found token '"+xCode.inputSnippet+"', expected 'PROC_DEFS' or nothing");
        }
        else if (xCode.symbolClass.equals("Special Command") == true)
        {
            xCode = xCode.next;
            root.next = null;
            func_HALT(root, nde_Code);
        }
        else if (xCode.symbolClass.equals("Procedure Definition Keyword") )
        {
            if (allowProcs == 0 || allowProcs == 2)
            {
                if (allowProcs == 0)
                    SynError("Syntax Error: Found a proc definition at start of PROG"+priorParent);
                if (allowProcs == 2)
                    SynError("Syntax Error: Found a proc within a code segment");
            }
            else
            {
                priorParent = " inside procedure "+xCode.next.inputSnippet;
                Node proc_nde = new Node("PROC_DEFS", "PROC", xCode.next.inputSnippet);
                Token tknB4Curl = findCurly(xCode);
                xCode = findCurly(xCode).next.next;
                tknB4Curl.next = null;

                func_CODE(root.next.next.next, proc_nde.children.get(0),0);
                allowProcs = 3;
                procException = true;
//                nde_Code.addChild(proc_nde);
                nde_Code = proc_nde;
            }
        }
        else if (xCode.inputSnippet.equals("for"))
        {
            Token tknOnCurve = findCurve(xCode);
            xCode = findCurve(xCode).next;
            tknOnCurve.next = null;

            Node nde_COND = new Node("INSTR", "COND_LOOP ","for");//*****
            //TODO: Add the condition for a for head

//            nde_COND.children.get(0).addChild(new Node("VAR",root.next.next.inputSnippet));

            Token dummy = root.next.next.next.next.next;
            root.next.next.next.next.next = null;
            func_ASSIGN(root.next.next,nde_COND.children.get(0));
            root = dummy;
            System.out.println("XXXXXXXXXXXXXXXX "+root.inputSnippet);
            //TODO: check
            //TODO: add 'T' and 'F'
            Token root2 = xCode;

            Token tknBfCurly = findCurly(xCode);
            xCode = findCurly(xCode).next.next;
            tknBfCurly.next = null;
            func_CODE(root2.next, nde_COND.children.get(0), 2);

            nde_Code.addChild(nde_COND);
        }
        else if (xCode.inputSnippet.equals("while"))
        {
            Token tknB4Curve = findCurve(xCode);
            xCode = findCurve(xCode).next;
            tknB4Curve.next = null;
            Node nde_COND = new Node("INSTR", "COND_LOOP","while");//*****
            func_COND_BRANCH(root, nde_COND.children.get(0));
            if (xCode != null && xCode.inputSnippet.equals("{"))
            {
                xCode = xCode;
                Token root2 = xCode;
                Token tknBfCurly = findCurly(xCode);
                xCode = findCurly(xCode).next.next;
                tknBfCurly.next = null;
                System.out.println("root2: " + tknBfCurly.inputSnippet);
                func_CODE(root2.next, nde_COND.children.get(0), 2);
            }
            else
            {
                System.out.println("Syntax Error: 'while' not followed by '{' token\t"+xCode.inputSnippet);
            }



            nde_Code.addChild(nde_COND);
        }
        else if (xCode.inputSnippet.equals("if"))
        {
            Token tknB4Curve = findCurve(xCode);
            xCode = findCurve(xCode).next;
            tknB4Curve.next = null;
            Node nde_COND = new Node("INSTR", "COND_BRANCH");
            func_COND_BRANCH(root, nde_COND.children.get(0));
            //TODO: Continue with the other two cases
            if (xCode != null && xCode.inputSnippet.equals("then"))
            {
                xCode = xCode.next;
                Token root2 = xCode;
                Token tknBfCurly = findCurly(xCode);
                xCode = findCurly(xCode).next.next;
                tknBfCurly.next = null;
                System.out.println("root2: "+tknBfCurly.inputSnippet);
                func_CODE(root2.next, nde_COND.children.get(0), 2);

                if (xCode != null && xCode.inputSnippet.equals("else"))
                {
                    if (xCode != null && xCode.inputSnippet.equals("else"))
                    {
                        Token root3 = xCode;
                        Token tknBfCurly2 = findCurly(xCode);
                        xCode = findCurly(xCode).next.next;
                        tknBfCurly2.next = null;
                        System.out.println("root3: "+root3.inputSnippet);
                        func_CODE(root3.next.next, nde_COND.children.get(0), 2);
                    }

                }
                else
                {
//                    System.out.println("Syntax Error: 'ELSE' token " );
                }
            }
            else
            {
                System.out.println("Syntax Error: If not followed by 'then' token\t"+xCode.inputSnippet);
            }
            nde_Code.addChild(nde_COND);
//            System.out.println("printing x: \t"+xCode.inputSnippet);
        }
        else if (xCode.symbolClass.equals("Type Declaration Keyword") == true)
        {
            Token tknPtr = forwardLookAhead(xCode, 1);
            xCode = forwardLookAhead(xCode, 2);
            tknPtr.next = null;

            func_DECL(root, nde_Code);
//
        }
        else if (xCode.symbolClass.equals("User-Defined Name") == true && xCode.next != null && xCode.next.symbolClass.equals("Assignment Operator"))
        {
            Token assignment = forwardLookAhead(xCode, 1);
            Token afta_assignment = forwardLookAhead(xCode, 2);
            Token tknPtr = null;

            if (afta_assignment != null)
            {
                if (afta_assignment.symbolClass.equals("String") == true)
                {
                    tknPtr = forwardLookAhead(xCode, 2);
                    xCode = forwardLookAhead(xCode, 3);
                    tknPtr.next = null;
                }
                else if (afta_assignment.symbolClass.equals("User-Defined Name"))
                {
                    tknPtr = forwardLookAhead(xCode, 2);
                    xCode = forwardLookAhead(xCode, 3);
                    tknPtr.next = null;
                }
                else if (afta_assignment.symbolClass.equals("Integer") || afta_assignment.symbolClass.equals("Number Operator"))
                {
                    if (afta_assignment.symbolClass.equals("Integer"))
                    {
                        tknPtr = forwardLookAhead(xCode, 2);
                        xCode = forwardLookAhead(xCode, 3);
                        tknPtr.next = null;
                    }
                    else if (afta_assignment.symbolClass.equals("Number Operator"))
                    {
                        tknPtr = findCurve(xCode);
                        xCode = findCurve(xCode).next;
                        tknPtr.next = null;
                    }
                }
                else if (afta_assignment.symbolClass.equals("Boolean Operator")|| afta_assignment.inputSnippet.equals("eq") || afta_assignment.inputSnippet.equals("("))
                {
                    /*
                        TODO: Fix this in the instance that 'T' becomes a recognized character
                     */
                    //TODO: Fix the Checks
                    if (xCode.next.next.inputSnippet.equals("not"))
                    {
                        tknPtr = findCurveNot(xCode);
                        xCode = findCurveNot(xCode).next;
                        tknPtr.next = null;
                    }
                    else if(afta_assignment.inputSnippet.equals("eq"))
                    {
                        tknPtr = findCurve(xCode);
                        xCode = findCurve(xCode).next;
                        tknPtr.next = null;
                    }
                    else if (afta_assignment.symbolClass.equals("Boolean Operator"))
                    {
                        tknPtr = findCurve(xCode);
                        xCode = findCurve(xCode).next;
                        tknPtr.next = null;
                    }
                    else if (afta_assignment.inputSnippet.equals("("))
                    {
                        tknPtr = findCurve(xCode);
                        xCode = findCurve(xCode).next;
                        tknPtr.next = null;
                    }
                }
                else
                {
                    System.out.println("Syntax Error: found this after assignment operator: " + afta_assignment.inputSnippet);
                    System.exit(0);
                }
                func_ASSIGN(root, nde_Code);
            }
            else if (afta_assignment == null)
            {
//                System.out.println("Syntax Error: Expected token after assignment operator in line"+ root.inputSnippet + "=");
                System.out.println("SOMETHING WENT WRONG CODE SHOULD NEVER GET HERE");
                System.exit(0);
            }
            else
            {
                System.out.println("SOMETHING WENT WRONG CODE SHOULD NEVER GET HERE");
                System.exit(0);
            }


        }
        else if (xCode.symbolClass.equals("User-Defined Name") == true)
        {
            Token tknPtr = xCode;
            xCode = forwardLookAhead(xCode, 1);
            tknPtr.next = null;

            func_CALL(root, nde_Code);
        }
        else if (xCode.symbolClass.equals("I/O Command") == true)
        {
            Token tknPtr = findCurve(xCode);
            xCode = findCurve(xCode).next;
            tknPtr.next = null;
            Node IO = new Node("INSTR", root.inputSnippet);
            IO.addChild(new Node("VAR", root.next.next.inputSnippet));
            nde_Code.addChild(IO);
        }

        if (allowProcs == 0)
        {
            allowProcs = 1;
        }

        if ((xCode!=null && procException))
        {
            func_CODE(xCode, nde_Code, allowProcs);
        }
        else if ( (xCode!=null && xCode.inputSnippet.equals(";") && xCode.next != null))
        {
            xCode = xCode.next;
            func_CODE(xCode, nde_Code, allowProcs);
        }
        else if (xCode != null)
        {
            //TODO: Missing Return statement
            System.out.println("Syntax Error: Expected ';', found " + xCode.inputSnippet);
            System.exit(0);
        }
        parent.addChild(nde_Code);
    }

    public void func_COND_BRANCH(Token xCode, Node parent)
    {
//        parent.addChild(new Node("if"));
        func_BOOL(xCode.next.next, parent);
    }
    public void func_ASSIGN(Token xCode, Node parent)
    {
        Node lvl1 = new Node("INSTR", "ASSIGN", "VAR", xCode.inputSnippet);
        Node assPar = lvl1.children.get(0);
//        System.out.println("SPECIAL SPOT");
        Token afta_assignment = forwardLookAhead(xCode, 2);

        if (afta_assignment != null)
        {
            if (afta_assignment.symbolClass.equals("String") == true)
            {
                assPar.addChild(new Node(afta_assignment.inputSnippet));
            }
            else if (afta_assignment.symbolClass.equals("User-Defined Name"))
            {
                assPar.addChild(new Node("VAR", afta_assignment.inputSnippet));
            }
            else if (afta_assignment.symbolClass.equals("Integer") || afta_assignment.symbolClass.equals("Number Operator"))
            {
                if (afta_assignment.symbolClass.equals("Integer"))
                {
                    assPar.addChild(new Node("NUMEXPR", afta_assignment.inputSnippet));
                }
                else if (afta_assignment.symbolClass.equals("Number Operator"))
                {
                    func_CALC(afta_assignment, assPar);
                }
            }
            else if (afta_assignment.symbolClass.equals("Boolean Operator") || afta_assignment.inputSnippet.equals("(") || afta_assignment.inputSnippet.equals("eq"))
            {
                //TODO this stuff
                func_BOOL(afta_assignment, assPar);
            }
            else
            {
                System.out.println("Syntax Error: found this after assignment operator: " + afta_assignment.inputSnippet);
                System.exit(0);
            }
        }

        parent.addChild(lvl1);
    }

    public void func_BOOL(Token xCode, Node parent)
    {
//        (afta_assignment.symbolClass.equals("Boolean Operator") || afta_assignment.inputSnippet.equals("(") || afta_assignment.inputSnippet.equals("eq"))

        Token root = xCode;
        if (xCode.inputSnippet.equals("not"))
        {
            Node not_node = new Node("BOOL","not");
            func_BOOL(xCode.next, not_node.children.get(0));
            parent.addChild(not_node);
        }
        else if (xCode.symbolClass.equals("Boolean Operator") || xCode.inputSnippet.equals("eq")) //and  or
        {
            Token beforeComma = findBeforeComa(xCode);
            xCode = beforeComma.next.next;
            beforeComma.next = null;

            Node boolNode = new Node("BOOL", root.inputSnippet);

            root = root.next.next;
            func_BOOL(root, boolNode);
            func_BOOL(xCode, boolNode);
            parent.addChild(boolNode);
        }
        else if (xCode.inputSnippet.equals("("))
        {
            //TODO: Error Check and Null Creation

            Node newNode = new Node("BOOL");
//            if (xCode.next == null || xCode.next.symbolClass.equals("User-Defined Name") == false)
//            {
//                SynError("Syntax Error: Expected a User-Defined Name but found '"+xCode.next.inputSnippet+"'");
//            }
            newNode.addChild(new Node("VAR", xCode.next.inputSnippet));
//            if (xCode.next.next == null ||  (xCode.next.next.inputSnippet.equals("<")==false && xCode.next.next.inputSnippet.equals(">")==false) == false)
//            {
//                SynError("Syntax Error: Expected a '<' or '>' but found '"+xCode.next.inputSnippet+"'");
//            }
            newNode.addChild(new Node(xCode.next.next.inputSnippet));
//            if (xCode.next.next.next == null || xCode.next.next.next.symbolClass.equals("User-Defined Name") == false)
//            {
//                SynError("Syntax Error: Expected a User-Defined Name but found '"+xCode.next.inputSnippet+"'");
//            }
            newNode.addChild(new Node("VAR", xCode.next.next.next.inputSnippet));

            parent.addChild(newNode);
        }
        else if (xCode.inputSnippet.equals("T") || xCode.inputSnippet.equals("F"))
        {
            parent.addChild(new Node("BOOL", xCode.inputSnippet));
        }
        else if (xCode.symbolClass.equals("User-Defined Name"))
        {
            parent.addChild(new Node("VAR", xCode.inputSnippet));
        }
        else
        {
            SynError("Syntax Error: unexpected token '"+xCode.inputSnippet+"' found in boolean expression");
        }
    }

    public void func_CALC(Token xCode, Node parent)
    {
        // TODO : test this thing, for nested Calculations
        Token root = xCode;
        if (xCode.symbolClass.equals("Integer"))
        {
            parent.addChild(new Node("NUMEXPR", xCode.inputSnippet));
        }
        else if (xCode.symbolClass.equals("Number Operator") == true)
        {

            Token beforeComma = findBeforeComa(xCode);
            xCode = beforeComma.next.next;
            beforeComma.next = null;

            Node numexpr = new Node("NUMEXPR", "CALC");
            Node calc_nde = numexpr.children.get(0);
            calc_nde.addChild(new Node(root.inputSnippet));

            root = root.next.next;
            func_CALC(root, calc_nde);
            func_CALC(xCode, calc_nde);
            parent.addChild(numexpr);
        }
        else if (xCode.symbolClass.equals("User-Defined Name"))
        {
            parent.addChild(new Node("NUMEXPR","VAR", xCode.inputSnippet));
        }
        else
        {
            SynError("Unexpected Token found, expected NUMEXPR but found '"+xCode.inputSnippet+"'");
        }
    }

    public void func_DECL(Token xCode, Node parent)
    {
        System.out.println("PRINTING func_DECL");

        Node lvl1 = new Node("INSTR", "DECL", "TYPE", xCode.inputSnippet);
        lvl1.children.get(0).addChild(new Node("NAME", "User-Defined Name", xCode.next.inputSnippet));
        parent.addChild(lvl1);
    }

    public void func_HALT(Token xCode, Node parent)
    {
        parent.addChild(new Node("INSTR","halt"));
    }
    public void func_CALL(Token xCode, Node parent)
    {
        parent.addChild(new Node("INSTR","CALL", xCode.inputSnippet));//Change from "User"
    }

    public void func_IO(Token xCode, Node parent)
    {
        Node lvl1 = new Node("INSTR", "IO", xCode.inputSnippet);
        lvl1.children.get(0).addChild(new Node("VAR", "User-Defined Name"));
        parent.addChild(lvl1);
    }

    public void func_PROC(Token xCode, Node parent)
    {
//        System.out.println("PRINTING func_PROC");
        while (xCode != null)
        {
            System.out.println(xCode.getInputSnippet());
            xCode = xCode.next;
        }
    }



    public String treeToString()
    {
        String toReturn = "";

        toReturn = print("", head, true);

        return toReturn;
    }

    private String print(String pre , Node x, boolean isTail)
    {
        String toReturn = "";
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

    /*
        NODE must be on the Brace
        returns token on the correct closing brace
     */
    Token findCurve(Token x)
    {
        int difference = 0;
        String startOff = "";
        if (x.inputSnippet.equals("(") == false)
        {
            startOff = " after token '"+x.inputSnippet+"' ";
        }
        while (x != null)
        {
            if (x.inputSnippet.equals(")"))
                difference--;
            if (x.inputSnippet.equals("("))
                difference++;

            if (x.inputSnippet.equals(")") == true && difference == 0)
                return x;
            x = x.next;
        }
        SynError("Syntax Error: Missing closing Brace" + startOff);
        return x;
    }

    Token findCurly(Token x)
    {
        int difference = 0;
        Token prev = null;
        String startOff = "";
        if (x.inputSnippet.equals("{") == false)
        {
            startOff = " after token '"+x.inputSnippet+"' ";
        }
        while (x != null)
        {
            if (x.inputSnippet.equals("}"))
                difference--;
            if (x.inputSnippet.equals("{"))
                difference++;

            if (x.inputSnippet.equals("}") == true && difference == 0)
                return prev;
            prev = x;
            x = x.next;
        }
        SynError("Syntax Error:  Missing closing Brace" + startOff);
        return x;
    }

    Token findCurveNot(Token x)
    {
        int difference = 0;
        boolean foundNot = false;
        while (x != null)
        {
            if (x.inputSnippet.equals("not"))
                foundNot = true;
            if (x.inputSnippet.equals(")"))
                difference--;
            if (x.inputSnippet.equals("("))
                difference++;

            if (foundNot && (x.inputSnippet.equals(")") == true && difference == 0 || x.symbolClass.equals("User-Defined Name") ) )
                return x;
            x = x.next;
        }
        System.out.println("Syntax Error 'not', is not followed by a boolean");
        return x;
    }

    Token forwardLookAhead(Token x, int length)
    {
//        System.out.println();
        for (int i = 0; i < length; i++)
        {
            if (x == null)
            {
//                x.symbolClass = "0";
                SynError("Incomplete expression expected a greater length of tokens");
            }
//            System.out.println("\t fLA: " + x.inputSnippet);
            x = x.next;
        }
        return x;
    }

    /*
    reutns the one right Before the Comma
     */
    Token findBeforeComa(Token x)
    {
//        System.out.println("\tFindBeforeComa");
        int difference = 0;
        while (x != null)
        {
//            System.out.println(x.inputSnippet);
            if (x.inputSnippet.equals(")"))
                difference--;
            if (x.inputSnippet.equals("("))
                difference++;
            if (x.next != null && x.next.inputSnippet.equals(",") == true && difference == 1)
                return x;
            x = x.next;
        }
        System.out.println("POTENTIAL ERROR MISSING COMMA TOKEN");
        return x;
    }

    Token findBeforeComea(Token x)
    {
//        System.out.println("\tFindBeforeComa");
        int difference = 0;
        while (x != null)
        {
//            System.out.println(x.inputSnippet);
            if (x.inputSnippet.equals(")"))
                difference--;
            if (x.inputSnippet.equals("("))
                difference++;
            if (x.next != null && x.next.inputSnippet.equals(",") == true && difference == 1)
                return x;
            x = x.next;
        }
        System.out.println("POTENTIAL ERROR MISSING COMMA TOKEN");
        return x;
    }

    public Node getRoot(){
        return head;
    }
//    private void print(String prefix, boolean isTail) {
//        if (true)
//            writer.println(prefix + (isTail ? "'--- " : "|---- ") + symbolName);
//
//        if (children != null) {
//            for (int i = 0; i < children.size() - 1; i++)
//            {
//                children.get(i).print(prefix + (isTail ? "     " : "|    "), false);
//            }
//            if (children.size() > 0)
//            {
//                children.get(children.size() - 1).print(prefix + (isTail ?"     " : "|    "), true);
//            }
//        }
//    }

}
