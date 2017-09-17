package mpfc;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.text.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

public class MultiColumnListCellRenderer extends JPanel
        implements ListCellRenderer
            , LayoutManager
{
    protected static Border emptyBorder = null;
    private static SimpleDateFormat formatter = new SimpleDateFormat ("dd.MM.yy hh:mm");
    protected static int HGAP = 0;

//    private transient String[] fields = null;
    private transient int fieldsNumber = 1;
    private transient ArrayList fieldSizes = null;
    private transient int[] mapping = null;

    public MultiColumnListCellRenderer()
    {
        this(1);
    }

    public MultiColumnListCellRenderer(int numberOfFields)
    {
        super();
        if ( emptyBorder == null )
       	    emptyBorder = new EmptyBorder(1, 1, 1, 1);
//	    setOpaque(true);
	    setBorder(emptyBorder);
//	    setBorder(null);
        setLayout(this);
        fieldsNumber = numberOfFields;
        init();
    }


    protected void init()
    {
        JLabel l = null;
        
        mapping = new int[fieldsNumber];
        
        for (int i=0; i<fieldsNumber; i++)
        {
            l = new JLabel(""+i);
            l.setOpaque(true);
            l.setBorder(null);
            add( l, i );
            if ( fieldSizes == null )
                l.setSize(l.getPreferredSize());
            else
                l.setSize((Dimension)fieldSizes.get(i));
                
            mapping[i] = i;
        }
    }
     
    public void setColumnsDimension(Dimension[] dims)
    {
        if ( dims == null)
            setFieldSizes(null);
        else
            setFieldSizes(new ArrayList(Arrays.asList(dims)));
    }

    public Component getListCellRendererComponent( JList list
                                                , Object value
                                                , int index
                                                , boolean isSelected
                                                , boolean cellHasFocus
                                                 )
    {
        StringBuffer sb = new StringBuffer();
        Color bgColor = null;
        Color fgColor = null;
        Icon icon = null;
        String text = null;
        boolean enabled = list.isEnabled();
        Font font = list.getFont();
        MCLItem item = null;
        Object[] fields = null;

        item = (MCLItem)value;

    	if ( item.isSelected() )
    	{
    	    bgColor = list.getSelectionBackground();
    	    fgColor = list.getSelectionForeground();
    	}
    	else
    	{
    	    bgColor = list.getBackground();
    	    fgColor = list.getForeground();
    	}
        
        icon = item.getIcon();
        if ( icon == null )
        {
            if ( item.isFolder() )
                icon = UIManager.getIcon("Tree.closedIcon");
            else
                icon = UIManager.getIcon("Tree.leafIcon");
    	}

        fields = item.getFields();    	
        if ( fields != null )
        {
            JLabel l = (JLabel)this.getComponent(0);
            l.setOpaque(true);
            l.setIcon(icon);            
            l.setText((String)item.getField(0));
            l.setFont(font);
            l.setEnabled(enabled);
            l.setBackground(bgColor);
            l.setForeground(fgColor);
            
            l = (JLabel)this.getComponent(1);
            l.setOpaque(true);
            l.setText((String)item.getField(1));
            l.setFont(font);
            l.setEnabled(enabled);
            l.setBackground(bgColor);
            l.setForeground(fgColor);
            
            l = (JLabel)this.getComponent(2);
            l.setOpaque(true);
            if ( item.isFolder() )
                l.setText("<DIR>");
            else
            {
                MultiColumnList.numberFormatter.format((Long)item.getField(2), sb, MultiColumnList.fieldPosition);
                if ( (sb.charAt(0) == '0') && (sb.length() > 1) )
                    l.setText(sb.substring(1));
                else
                    l.setText(sb.toString());
//                l.setText(((Long)item.getField(2)).toString());
            }
            l.setFont(font);
            l.setEnabled(enabled);
            l.setBackground(bgColor);
            l.setForeground(fgColor);
            l.setHorizontalAlignment(SwingUtilities.RIGHT);
            
            l = (JLabel)this.getComponent(3);
            l.setOpaque(true);
            l.setText(formatter.format((Date)item.getField(3)));
            l.setFont(font);
            l.setEnabled(enabled);
            l.setBackground(bgColor);
            l.setForeground(fgColor);
            l.setHorizontalAlignment(SwingUtilities.RIGHT);
            
            l = (JLabel)this.getComponent(4);
            l.setOpaque(true);
            l.setText((String)item.getField(4));
            l.setFont(font);
            l.setEnabled(enabled);
            l.setBackground(bgColor);
            l.setForeground(fgColor);
            l.setHorizontalAlignment(SwingUtilities.CENTER);
        }
    	
        setBackground(bgColor);
        setForeground(fgColor);
        
    	setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : emptyBorder);
//    	setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : null);
    
    	return this;
    }
    
    public void setMapping(int[] newMapping)
    {
        mapping = newMapping;
    }

    public int[] getMapping()
    {
        return mapping;
    }
    
    public void setFieldSizes(ArrayList newSizes)
    {
        fieldSizes = newSizes;
        this.invalidate();
        this.validate();
    }

    public ArrayList getFieldSizes()
    {
        return fieldSizes;
    }
    

    public void addLayoutComponent(String name, Component comp)
    {
    }

    public void removeLayoutComponent(Component comp)
    {
    }

    public Dimension preferredLayoutSize(Container parent)
    {
        Component[] comps = parent.getComponents();
        Insets ins = parent.getInsets();
        Dimension d = null;
        int h = 0;
        int w = 0;
        
        int cnt = comps.length;
        Component c = null;
        
        for (int i=0; i<cnt; i++)
        {
            d = comps[i].getPreferredSize();
            if ( i == 0 )
                h = d.height;
            w += d.width;
        }

        if ( ins != null )
        {
            h += ins.top + ins.bottom;
            w += ins.left + ins.right;
        }
        
        w += (cnt-1)*HGAP;
        
        return (new Dimension(w,h));
    }

    public Dimension minimumLayoutSize(Container parent)
    {
        Component[] comps = parent.getComponents();
        Insets ins = parent.getInsets();
        Dimension d = null;
        int h = 0;
        int w = 0;
        
        int cnt = comps.length;
        Component c = null;
        
        for (int i=0; i<cnt; i++)
        {
            d = comps[i].getMinimumSize();
            if ( i == 0 )
                h = d.height;
            w += d.width;
        }

        if ( ins != null )
        {
            h += ins.top + ins.bottom;
            w += ins.left + ins.right;
        }
        
        w += (cnt-1)*HGAP;
        
        return (new Dimension(w,h));
    }

    public void layoutContainer(Container parent)
    {
        Dimension size = parent.getSize();
        Insets ins = parent.getInsets();
        int x = 0;
        int y = 0;
        int h = parent.getComponent(0).getPreferredSize().height;
        Dimension d = null;

        if ( ins != null )
        {
            size.width -= ins.left + ins.right;
            size.height -= ins.top + ins.bottom;

            x += ins.left;
            y += ins.top;
        }


        Component[] comps = parent.getComponents();
        Object[] dims = fieldSizes.toArray();
        int cnt = comps.length;
        if ( cnt != dims.length )
            throw new RuntimeException( "MultiColumnListCellRenderer::layoutContainer(): Field number must be equal with component number!");
            
        Component c = null;
        for (int i=0; i<cnt; i++)
        {
            d = comps[i].getSize();
            if ( i == cnt-1 )
                comps[i].setBounds(x, y, size.width - x, h);
            else
                comps[i].setBounds(x, y, ((Dimension)dims[i]).width, h);
            x += d.width + HGAP;
        }
    }

}