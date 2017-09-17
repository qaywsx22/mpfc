package mpfc.actions;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import mpfc.CommandListener;
import mpfc.CommandEvent;

public class CustomAction extends AbstractAction
{
    public static String title = "MultiPlatform File Commander";
    private transient CommandListener comListener = null;
    
    public CustomAction()
    {
        this.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent e)
    {
    }
    
    public static void setDialogTitle(String aTitle)
    {
        title = aTitle;
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
    
    protected void fireCommandEvent(CommandEvent e)
    {
        if ( comListener != null )
            comListener.executeCommand(e);
    }
}
