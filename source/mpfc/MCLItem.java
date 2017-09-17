package mpfc;

import javax.swing.*;

public interface MCLItem
{
    public String getName();
    public String getDisplayName();
    public Icon getIcon();
    
    public boolean isFolder();
    public boolean isSuperFolder();
    
    public int getNumberOfAttributes();
    public void setNumberOfAttributes(int num);

    public boolean isSelected();
    public void setSelected(boolean selected);
    
    public Object[] getFields();
    public Object getField(int index);
}