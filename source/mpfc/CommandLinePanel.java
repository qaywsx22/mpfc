package mpfc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class CommandLinePanel extends JPanel
    implements LayoutManager
{
    private transient JLabel dirName = null;
    private transient JTextField commandField = null;
    
    
    public CommandLinePanel()
    {
        setLayout(this);
        setName("command_line_panel");
        setBorder(new EtchedBorder());
        dirName = new JLabel();
        dirName.setName("dir_name_label");
        dirName.setHorizontalAlignment(SwingConstants.RIGHT);
        commandField = new JTextField();
        commandField.setName("command_line_entry_field");
        add(dirName);
        add(commandField);
    }

    public void setDirectoryName( String name )
    {
        dirName.setText(name);
        dirName.repaint();
    }
    
    public String getDirectoryName()
    {
        return dirName.getText();
    }

    public void setCommand( String command )
    {
        commandField.setText(command);
    }
    
    public String getCommand()
    {
        return commandField.getText();
    }

    public void addActionListener(ActionListener l)
    {
        commandField.addActionListener(l);
    }
    
    public void removeActionListener(ActionListener l)
    {
        commandField.removeActionListener(l);
    }


    //---------------------------------------
    //  Layout manager staff
    //---------------------------------------
    
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
            h = Math.max(h, d.height);
            w += d.width;
        }

        if ( ins != null )
        {
            h += ins.top + ins.bottom;
            w += ins.left + ins.right;
        }
        
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
            h = Math.max(h, d.height);
            w += d.width;
        }

        if ( ins != null )
        {
            h += ins.top + ins.bottom;
            w += ins.left + ins.right;
        }
        
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

        int w = size.width / 4;
        h = Math.max(h, parent.getComponent(1).getPreferredSize().height);
        Component c = parent.getComponent(0);
        c.setBounds(x, y, w, h);
        c = parent.getComponent(1);
        c.setBounds(x+w+2, y, size.width-w-2, h);
    }
}