package mpfc;

import java.util.*;

public class MCLItemComparator
    implements Comparator
{
    private transient boolean asc = false;
    private transient int ind = 0;
    private transient int itemType = SortConstraints.STRING;
    
    public MCLItemComparator(int index, boolean ascending)
    {
        asc = ascending;
        ind = index;
    }
    
    public MCLItemComparator(SortConstraints sc)
    {
        asc = sc.isAscending();
        ind = sc.getIndex();
        itemType = sc.getItemType();
    }

    public int compare(Object ob1, Object ob2)
    {
        int result = 0;
        
        MCLItem o1 = (MCLItem)ob1;
        MCLItem o2 = (MCLItem)ob2;
        
        boolean isFolder1 = o1.isFolder();
        boolean isFolder2 = o2.isFolder();
        // Folder is allways smaller as leaf
        if (isFolder1 && !isFolder2)
            result = -1;
        else if (!isFolder1 && isFolder2)
            result = 1;
        // If both are leaf or folder, then compare dispaly names.
        else
        {
            Comparable s1 = (Comparable)o1.getField(ind);
            Comparable s2 = (Comparable)o2.getField(ind);
            
            switch ( itemType )
            {
                case SortConstraints.UNDEFINED:
                case SortConstraints.STRING:
                            result = ((String)s1).compareToIgnoreCase((String)s2);
                        break;
                default:
                            result = s1.compareTo(s2);
            }
            
            if ( !asc )
                result = -result;
        }
        return result;
    }
}
