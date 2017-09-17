package mpfc;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class MultiColumnPanelLayout implements LayoutManager
{
    public static int HGAP = 0;

    public MultiColumnPanelLayout()
    {
        super();
    }

    public void addLayoutComponent(String name, Component comp)
    {
    }

    public void removeLayoutComponent(Component comp)
    {
    }

    public Dimension preferredLayoutSize(Container parent)
    {
        return parent.getPreferredSize();
    }

    public Dimension minimumLayoutSize(Container parent)
    {
        return new Dimension(0,0);
    }

    public void layoutContainer(Container parent)
    {
        Dimension size = parent.getSize();
        Insets ins = parent.getInsets();
        int x = 0;
        int y = 0;
        Dimension d = null;

        if ( ins != null )
        {
            size.width -= ins.left + ins.right;
            size.height -= ins.top + ins.bottom;

            x += ins.left;
            y += ins.top;
        }


        Component[] comps = parent.getComponents();
        
        int cnt = comps.length;
        Component c = null;
        for (int i=0; i<cnt; i++)
        {
            d = comps[i].getSize();
            if ( i == cnt-1 )
                comps[i].setBounds(x, y, size.width - x, d.height);
            else
                comps[i].setBounds(x, y, d.width, d.height);
            x += d.width + HGAP;
        }
    }
    
    
}