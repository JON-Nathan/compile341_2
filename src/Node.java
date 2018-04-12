import java.util.ArrayList;

public class Node {
    public Token token;
    public int idNode;
    private String val;
    public ArrayList<Node> children;

    private static int id = 0;
    public Node()
    {
        children = new ArrayList<Node>();

    }

    public String getVal()
    {
        return val;
    }

    public Node(String val)
    {
        children = new ArrayList<Node>();
        this.val = val;
        setVal(val);
    }

    public void addChild(Node newChild)
    {
        children.add(newChild);
    }

    public void setVal(String val)
    {
        idNode = id++;
        this.val = val;
    }

    public Node(String x1, String x2, String x3, String x4)
    {
//        idNode = id++;
        children = new ArrayList<>();
        val = x1;
        setVal(x1);
        Node newNode1 = new Node();
        newNode1.setVal(x2);
        Node newNode2 = new Node();
        newNode2.setVal(x3);
        Node newNode3 = new Node();
        newNode3.setVal(x4);

        newNode2.addChild(newNode3);
        newNode1.addChild(newNode2);
        children.add(newNode1);

    }

    public Node(String x1, String x2, String x3)
    {
//        idNode = id++;
        if (x1.equals("PROC_DEFS"))
            id--;
        children = new ArrayList<>();
        val = x1;
        setVal(x1);
        Node newNode1 = new Node();
        newNode1.setVal(x2);
        Node newNode2 = new Node();
        newNode2.setVal(x3);
        newNode1.addChild(newNode2);
        children.add(newNode1);
    }

    public Node(String x1, String x2)
    {
//        idNode = id++;
        children = new ArrayList<>();
        val = x1;
        setVal(x1);
        Node newNode1 = new Node();
        newNode1.setVal(x2);
//        Node newNode2 = new Node();
        children.add(newNode1);

    }

}
