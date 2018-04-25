public class ScopeEntry {
    int scopeID = -1;
    int nodeId = -1;
    String oldName;
    String newName;
    boolean isProc;
    String hasBeenAssigned;

    public ScopeEntry(int scopeID, int nodeId, String oldName, String newName, String hasBeenAssigned) {
        this.scopeID = scopeID;
        this.nodeId = nodeId;
        this.oldName = oldName;
        this.newName = newName;
        this.hasBeenAssigned = hasBeenAssigned;
    }

    public String toProperOutput()
    {
        String toReturn = "";
        if (hasBeenAssigned != null && hasBeenAssigned.equals("ERROR"))
            toReturn += "\u001B[33m";
        for (int i = 0; i < scopeID; i++)
        {
            toReturn += "\t";
        }
        toReturn += "["+scopeID+"] id: "+nodeId+ " ";

        if (newName != null)
            toReturn += " [ "+oldName+ " -> "+ newName +" ]";
        else
            toReturn += oldName;
        if (hasBeenAssigned != null && hasBeenAssigned.equals("YES"))
        toReturn += "\t\t" + "";
        toReturn+="\u001B[0m";
        toReturn += "\n";
        return  toReturn;
    }
}
