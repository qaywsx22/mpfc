package mpfc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class LNFAction extends AbstractAction
{
    String lnfName = null;
    Component _host = null;
    
    public LNFAction( String aLNFName, Component host)
    {
        
        lnfName = aLNFName;
        _host = host;
        putValue(Action.NAME, "lnfAction");

        if ( (lnfName != null) && (_host != null) )
            this.setEnabled(true);
        else
            this.setEnabled(false);
    }

    public void actionPerformed(ActionEvent evt)
    {
        try
        {
            UIManager.setLookAndFeel(lnfName);
            
            if ( "Windows".equals(UIManager.getLookAndFeel().getID()) )
            {
                Border focusCellHighlightBorder = new BorderUIResource.LineBorderUIResource( 
                                                        UIManager.getColor("activeCaptionBorder"));
                UIManager.put("List.focusCellHighlightBorder", focusCellHighlightBorder);
            }
            else
                UIManager.put("List.focusCellHighlightBorder", null);
            
            SwingUtilities.updateComponentTreeUI(_host);
        }  
    	catch ( Exception ex )
    	{
    	    ex.printStackTrace();
            ((AbstractButton)evt.getSource()).setEnabled(false);
    	}
    }
}