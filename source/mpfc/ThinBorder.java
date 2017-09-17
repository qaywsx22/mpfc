package mpfc;

import java.awt.*;

import javax.swing.*;

import javax.swing.border.*;


public class ThinBorder extends AbstractBorder
{
    private static Border thinBorder = new ThinBorder();
        //top, left, bottom, right
    private static Insets borderInsetsWithoutText = new Insets(1, 1, 0, 1);
    private static Insets borderInsetsWithText = new Insets(1, 2, 0, 6);

    public static Border getThinBorder()
    {
        return thinBorder;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
	    Color oldColor;
        boolean isPressed  = true;

        if ( c instanceof JComponent )
        {
            Boolean selected = (Boolean)((JComponent)c).getClientProperty("selected");
            if ( selected != null )
                isPressed  = selected.booleanValue();
            else
                isPressed  = false;
        }	

        oldColor = g.getColor();

        if ( !isPressed )
    	    g.setColor(Color.white);
	    else
		    g.setColor(Color.black);

	    g.drawLine(0, 0, width-1, 0);
	    g.drawLine(0, 0, 0, height-1);

	    if ( !isPressed )
		    g.setColor(Color.black);
	    else
		    g.setColor(Color.white);

	    g.drawLine(width-1, 0, width-1, height-1);
//	    g.drawLine(0, height-1, width-1, height-1);

	    g.setColor(oldColor);
    }

    public Insets getBorderInsets(Component c)
    {
        boolean withText = false;

        try
        {
            String text = ((AbstractButton)c).getText();
            withText = text.length() > 0;
        }
        catch (Exception exc)
        {
            return borderInsetsWithoutText;
        }

        if ( withText )
            return borderInsetsWithText;
        else
            return borderInsetsWithoutText;
    }

}
