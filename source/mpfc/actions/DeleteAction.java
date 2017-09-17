package mpfc.actions;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import mpfc.CommandEvent;
import mpfc.CustomFile;
import mpfc.MCLItem;
import mpfc.MultiColumnList;

public class DeleteAction extends CustomAction
{
    MultiColumnList list = null;
    
    public DeleteAction( MultiColumnList mcList )
    {
        list = mcList;
        this.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        File file = null;
        File[] children = null;
        Object[] sel = list.getSelectedValues();
        boolean changed = false;
                
        if ( (sel == null) || (sel.length == 0) ) // nothing is selected, return...
        {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(list, "No files selected!",
                                          title,
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String fn = null;
        MCLItem cf = null;
        
        if ( sel.length == 1 )
        {
            cf = (MCLItem)sel[0];
            fn = cf.getName();
        }
        else
            fn = "" + sel.length + " selected files/directories";
        int res = JOptionPane.showConfirmDialog(list, "Do you really want to delete "+fn+"?",
                                                title,
                                                JOptionPane.OK_CANCEL_OPTION);
        if ( (res == JOptionPane.CANCEL_OPTION) || (res == JOptionPane.CLOSED_OPTION) )
            return;

        for ( int i=0; i<sel.length; i++)
        {
            cf = (MCLItem)sel[i];
            file = ((CustomFile)cf).getOrigin();
            if ( cf.isFolder() )
                children = file.listFiles();
            if ( (children != null) && (children.length > 0) )
            {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(list, "Directory "+file+" is not empty. It can not be deleted.",
                                              title,
                                              JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if ( !file.delete() )
            {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(list, "File "+file+" was not be deleted!",
                                              title,
                                              JOptionPane.ERROR_MESSAGE);
            }
            else
                changed = true;
        }

        if ( changed )
        {
            CommandEvent ce = new CommandEvent(this, "reload", 0, list);
            fireCommandEvent(ce);
        }
    }
}
