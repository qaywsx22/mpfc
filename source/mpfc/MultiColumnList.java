package mpfc;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

public class MultiColumnList extends JPanel
    implements PropertyChangeListener
{
//    private static BevelBorder barsBorder = new BevelBorder(BevelBorder.RAISED);
    private static Border barsBorder = new EtchedBorder();
    private static String[] defaultColumn = { "default" };
    public static DecimalFormat numberFormatter = new DecimalFormat("###,###,###");
    public static FieldPosition fieldPosition = new FieldPosition(NumberFormat.INTEGER_FIELD);

    private transient SplitButtonBar header = null;
    private transient JLabel titleBar = null;
    private transient JList list = null;
    private transient JScrollPane sp = null;
    private transient JLabel infoLine = null;
    
    private transient MultiColumnListCellRenderer renderer = null;
    
    private transient CommandListener comListener = null;
    private transient MouseListener ml = null;

    private transient String infoString = null;
    private transient CustomFile prototypeCell = null;
    private transient int selFileCount = 0;
    private transient long selFileSize = 0;
    private transient int fileCount = 0;
    private transient long fileSize = 0;
    
    public MultiColumnList()
    {
        this(defaultColumn);
    }


    public MultiColumnList(String[] columns)
    {
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(2,0));
        titleBar = new JLabel();
        titleBar.setOpaque(true);
        
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder( barsBorder );
        wrapper.add(titleBar, BorderLayout.CENTER);
        pan.add(wrapper);
//        titleBar.setBorder( barsBorder );
//        pan.add(titleBar);
        header = new SplitButtonBar(columns);
        pan.add(header);
        list = new JList();
//        list.setLeadAnchorNotificationEnabled(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                            ,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        sp.setBorder(null);
        
        infoString = " "+selFileSize+" of "+fileSize+" k in "+selFileCount+" of "+selFileCount+" files selected";
        infoLine = new JLabel(infoString);
        infoLine.setName("infoline");
//        infoLine.setBorder( barsBorder );

        wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder( barsBorder );
        wrapper.add(infoLine, BorderLayout.CENTER);
//        pan.add(wrapper);
        
        setLayout(new BorderLayout());
        add(pan, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
//        add(infoLine, BorderLayout.SOUTH);
        add(wrapper, BorderLayout.SOUTH);
        sp.setViewportView(list);
        
        renderer =  new MultiColumnListCellRenderer(columns.length);
        list.setCellRenderer(renderer);
        renderer.setFieldSizes(header.getSizes());
        header.addPropertyChangeListener(this);
        ml = new MCLMouseAdapter();
        list.addMouseListener(ml);
        titleBar.addMouseListener(ml);
        String javaHome = (String)System.getProperty("java.home");
        prototypeCell = new CustomFile(javaHome+File.separator+"jre"+File.separator+"lib"+File.separator+"rt.jar");
        list.setPrototypeCellValue(prototypeCell);
        Dimension ps = new Dimension(250, 400);
        setPreferredSize(ps);
    }
    
    public void setColumnSizes(ArrayList newSizes)
    {
        header.setSizes(newSizes);
        renderer.setFieldSizes(newSizes);
        list.revalidate();
        list.repaint();
    }

    public int getColumnType( int index )
    {
        int res = SortConstraints.UNDEFINED;
        
        if ( (index < 0) || (index >= header.getColumnsNumber()) )
            return res;
            
        switch ( index )
        {
            case 0: res = SortConstraints.STRING;
                    break;
            case 1: res = SortConstraints.STRING;
                    break;
            case 2: res = SortConstraints.LONG;
                    break;
            case 3: res = SortConstraints.DATE;
                    break;
            case 4: res = SortConstraints.STRING;
                    break;
        }
        
        return res;
    }
    
    public void setModel(DefaultListModel model)
    {
        list.setModel(model);
        list.revalidate();
        list.repaint();
    }

    public void setModelLazy(DefaultListModel model)
    {
        list.setModel(model);
    }
    
    public ListModel getModel()
    {
        return list.getModel();
    }

    // Returns focused item's value.
    // Focused item is item which will be drawn with border.
    // If selection is empty or list has not focus returns null.
    public Object getFocusedValue()
    {
        Object ret = null;
        if ( list.hasFocus() && !list.isSelectionEmpty() )
        {
            int ind = list.getLeadSelectionIndex();
            ret = list.getModel().getElementAt(ind);
        }
        
        return ret;
    }

    // Returns index of focused item.
    // Focused item is item which will be drawn with border.
    // If selection is empty or list has not focus returns -1.
    public int getFocusedIndex()
    {
        int ret = -1;
        if ( list.hasFocus() && !list.isSelectionEmpty() )
        {
            ret = list.getLeadSelectionIndex();
        }
        
        return ret;
    }

    public void setFocusedIndex(int index)
    {
        list.setSelectedIndex(index);
    }

    public void ensureIndexIsVisible(int index)
    {
        list.ensureIndexIsVisible(index);
    }
    
    public Object[] getSelectedValues()
    {
        ListModel model = list.getModel();
        int cnt = model.getSize();
        ArrayList coll = new ArrayList();
        MCLItem item = null;
        
        for (int i=0; i<cnt; i++)
        {
            item = (MCLItem)model.getElementAt(i);
            if ( item.isSelected() )
                coll.add(item);
        }
        
        if ( coll.isEmpty() )
            return null;
        else
            return coll.toArray();
    }

//    public Object getSelectedValue()
//    {
//        return list.getSelectedValue();
//    }

    public boolean isSelectedIndex(int index)
    {
        return ((MCLItem)(list.getModel().getElementAt(index))).isSelected();
    }

    public void setSelectedIndex( int index )
    {
        
        ((MCLItem)(list.getModel().getElementAt(index))).setSelected(true);
    }

    public void setSelectedIndex( int index, boolean select )
    {
        
        ((MCLItem)(list.getModel().getElementAt(index))).setSelected(select);
    }
    
    public void setSelectedValue(Object value)
    {
        DefaultListModel model = (DefaultListModel)list.getModel();
        int index = model.indexOf(value);
        if ( index >= 0 )
            setSelectedIndex(index);
    }

    public boolean isSortOrderAscending(int index)
    {
        return header.isSortOrderAscending(index);
    }
    
    public int getActiveColumnIndex()
    {
        return header.getActiveColumnIndex();
    }

    public void setFileCount(int cnt)
    {
        fileCount = cnt;
    }
    
    public void setSelFileCount(int cnt)
    {
        selFileCount = cnt;
    }

    public void setFileSize(long size)
    {
        fileSize = size / 1024L;
    }
    
    public void setSelFileSize(long size)
    {
        selFileSize = size / 1024L;
    }

    public void updateInfoLine()
    {
        StringBuffer ss = new StringBuffer();
        numberFormatter.format(selFileSize, ss, fieldPosition);
        if ( (ss.charAt(0) == '0') && (ss.length() > 1) )
            ss.deleteCharAt(0);
        infoString = " " + ss.toString();
        numberFormatter.format(fileSize, ss, fieldPosition);
        if ( (ss.charAt(0) == '0') && (ss.length() > 1) )
            ss.deleteCharAt(0);
        infoString += " of "+ss.toString()+" k in "+selFileCount+" of "+fileCount+" files selected";
        infoLine.setText(infoString);
        infoLine.repaint();
    }
        
    public void propertyChange(PropertyChangeEvent evt)
    {
        String propertyName = evt.getPropertyName();
        if ( "sizes".equals(propertyName) )
        {
            ArrayList sizes = (ArrayList)evt.getNewValue();
            renderer.setFieldSizes(sizes);
            firePropertyChange("sizes", null, sizes);
//System.out.println( "MulticolumnList "+this.getName()+" fired sizes PCE" );
            list.revalidate();
            list.repaint();
        }
        else if ( "sort_order".equals(propertyName) )
        {
            int index = ((Integer)evt.getNewValue()).intValue();
            boolean ascending = ((SplitButtonBar)evt.getSource()).isSortOrderAscending(index);
            
            SortConstraints sc = new SortConstraints(ascending, index, getColumnType(index));
            firePropertyChange("sort_order", null, sc);
        }
    }

    public void setActive(boolean active)
    {
        if ( active )
        {
            titleBar.setBackground(UIManager.getColor("activeCaption"));
            titleBar.setForeground(UIManager.getColor("activeCaptionText"));
        }
        else
        {
            titleBar.setBackground(UIManager.getColor("inactiveCaption"));
            titleBar.setForeground(UIManager.getColor("inactiveCaptionText"));
        }
//System.out.println( "MCL "+this.getName()+" setActive "+active+"" );
//System.out.println( "title background is "+titleBar.getBackground()+"" );
//System.out.println( "title foreground is "+titleBar.getForeground()+"" );
        titleBar.repaint();
    }
    
    public void addCommandListener(CommandListener cl)
    {
        comListener = cl;
    }
    
    public void removeCommandListener(CommandListener cl)
    {
        if ( comListener == cl )
            comListener = null;
    }
    
    private void fireCommandEvent(CommandEvent e)
    {
        if ( comListener != null )
            comListener.executeCommand(e);
    }

    private void fireActiveListEvent()
    {
        firePropertyChange("active_list", null, this);
    }

    public void setTitleBarText(String text)
    {
        titleBar.setText(text);
    }

    public String getTitleBarText()
    {
        return titleBar.getText();
    }

    public void refreshList()
    {
        list.revalidate();
        list.repaint();
    }

    public void repaintList()
    {
        list.repaint();
    }
    
    public void setSelectionMode(int selectionMode)
    {
        list.setSelectionMode(selectionMode);
    }

    class MCLMouseAdapter extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            Object o = e.getSource();
            if ( o == titleBar )
            {
                list.requestFocus();
            }
            else if ( (o == list) && (e.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(e) )
            {
                int index = MultiColumnList.this.list.locationToIndex(e.getPoint());
                CommandEvent ce = new CommandEvent(MultiColumnList.this,
                            "execute",
                            e.getModifiers(),
                            MultiColumnList.this.list.getModel().getElementAt(index));
                fireCommandEvent(ce);
            }
            else if ( (o == list) && SwingUtilities.isRightMouseButton(e) )
            {
                int index = MultiColumnList.this.list.locationToIndex(e.getPoint());
                MultiColumnList.this.setSelectedIndex(index, !MultiColumnList.this.isSelectedIndex(index));
                MultiColumnList.this.list.repaint();
            }
            
            fireActiveListEvent();
        }
    }
    
//    class MCLMouseMotionAdapter extends MouseMotionAdapter
//    {
//    }
}