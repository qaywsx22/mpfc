package mpfc.actions;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import mpfc.MultiColumnList;

public class SelectAction extends AbstractAction
{
    MultiColumnList list = null;
    
    public SelectAction( MultiColumnList mcList )
    {
        list = mcList;
        this.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if ( list.getModel().getSize() == 0 )
            return;
            
        boolean last = false;
        int index = list.getFocusedIndex();
        int maxIndex = list.getModel().getSize() - 1;
        if ( index < 0 )
            index = 0;
        else if ( index > maxIndex )
        {
            index = maxIndex;
            last = true;
        }
        boolean sel = !list.isSelectedIndex(index);
        list.setSelectedIndex(index, sel);
        index++;
        if ( last )
        {
            index = maxIndex;
//            list.repaintList();
        }
    
        list.setFocusedIndex(index);
        list.ensureIndexIsVisible(index);
    }
}
