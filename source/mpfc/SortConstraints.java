package mpfc;

public class SortConstraints
{
    public static final int UNDEFINED = 0;
    public static final int STRING = 1;
    public static final int LONG = 2;
    public static final int DATE = 3;
    
    private boolean ascending = true;
    private int index = 0;
    private int itemType = STRING;
    
    public SortConstraints( boolean sortOrderAsc, int position, int anItemType)
    {
        ascending = sortOrderAsc;
        index = position;
        itemType = anItemType;
    }
        
    public void setAscending(boolean asc)
    {
        ascending = asc;
    }
    
    public boolean isAscending()
    {
        return ascending;
    }

    public void setIndex(int ind)
    {
        index = ind;
    }
    
    public int getIndex()
    {
        return index;
    }

    public void setItemType(int name)
    {
        itemType = name;
    }
    
    public int getItemType()
    {
        return itemType;
    }
}