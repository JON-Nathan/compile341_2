public class ScopeEntry {
    int scopeLevel = -1;
    int nodeId = -1;
    String oldName;
    String newName;
    boolean isProc;

    public ScopeEntry(int scopeLevel, int nodeId, String oldName, String newName, boolean isProc) {
        this.scopeLevel = scopeLevel;
        this.nodeId = nodeId;
        this.oldName = oldName;
        this.newName = newName;
        this.isProc = isProc;
    }

    public String toProperOutput()
    {
        String toReturn = "";
        for (int i = 0; i < scopeLevel; i++)
        {
            toReturn += "\t";
        }
        toReturn += "["+scopeLevel+"] id: "+nodeId+ " ";

        if (newName.length() > 1)
            toReturn += " [ "+oldName+ " -> "+ newName +" ]";
        else
            toReturn += oldName;

        toReturn += "\n";
        return  toReturn;
    }
}
