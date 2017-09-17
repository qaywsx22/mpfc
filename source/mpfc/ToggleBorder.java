package mpfc;

import java.awt.*;

import javax.swing.*;

import javax.swing.border.*;


public class ToggleBorder extends AbstractBorder
{
    private static Border toggleBorder = new ToggleBorder();
    private static Insets borderInsetsWithoutText = new Insets(1, 1, 1, 1);
    private static Insets borderInsetsWithText = new Insets(1, 2, 1, 6);

    public static Border getToggleBorder()
    {
        return toggleBorder;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
	    Color oldColor;
        boolean isPressed  = true;
        boolean isRollover = true;

        if ( c instanceof AbstractButton )
        {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();

            isPressed  = (model.isPressed() && model.isArmed()) || model.isSelected();
            isRollover = model.isRollover();
        }	

        oldColor = g.getColor();

	    if ( isRollover || isPressed )
        {
            if ( !isPressed )
    		    g.setColor(Color.white);
	        else
		        g.setColor(Color.black);

	        g.drawLine(0, 0, width-1, 0);
	        g.drawLine(0, 0, 0, height-2);

	        if ( !isPressed )
		        g.setColor(Color.black);
	        else
		        g.setColor(Color.white);

	        g.drawLine(width-1, 1, width-1, height-1);
	        g.drawLine(0, height-1, width-1, height-1);

	        g.setColor(oldColor);
	    }
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
