package mpfc;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

public class SplitButtonBar extends JPanel
    implements ActionListener
{
    private transient ArrayList buttons = null;
    private transient CustomMouseAdapter cma = null;
    private transient boolean resizeMode = false;
    private transient Point resizeAnchor = null;
    private transient Component resizableComponent = null;

    public static int HGAP = 0;

    public SplitButtonBar()
    {
        this(1);
    }

    public SplitButtonBar(int numberOfButtons)
    {
        this(numberOfButtons, null);
    }

    public SplitButtonBar(String[] columns)
    {
        this(columns.length, columns);
    }
    
    public SplitButtonBar(int numOfBut, String[] columns)
    {
        super();
        setLayout(new MultiColumnPanelLayout());
        setBorder(new LineBorder(Color.black));
        cma = new CustomMouseAdapter();
        buttons = createButtons(columns.length, columns);
    }

    private ArrayList createButtons(int numOfBut, String[] namens)
    {
        ArrayList arr = new ArrayList();
        JLabel b = null;

        for (int i=0; i<numOfBut; i++)
        {
            if ( namens != null )
                b = new JLabel(namens[i]);
            else
                b = new JLabel("Button "+i);
            b.setName("Button "+i);
            b.setOpaque(true);
            b.setForeground(Color.black);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setHorizontalAlignment(JLabel.CENTER);
            b.setEnabled(true);
        	b.setBorder(ThinBorder.getThinBorder());
            arr.add(b);
            add(b);
            b.setSize(b.getPreferredSize());
            b.addMouseListener(cma);
            b.addMouseMotionListener(cma);
            if ( i == 0 )
            {
                b.putClientProperty("ascending", new Boolean(true));
                b.setText("+ "+b.getText());
                b.putClientProperty("selected", new Boolean(true));
                b.setBackground(getBackground().brighter());
            }
        }
        return arr;
    }

    public void actionPerformed(ActionEvent ev)
    {
    }


    public int getActiveColumnIndex()
    {
        JLabel l = null;
        int ind = 0;
        
        for (Iterator i = buttons.iterator(); i.hasNext(); )
        {
            l = (JLabel)i.next();
            if ( l.getClientProperty("selected") != null )
                break;
            ind++;
        }
        
        return ind;
    }

    public int getColumnsNumber()
    {
        return buttons.size();
    }

    public ArrayList getSizes()
    {
        int cnt = buttons.size();
        ArrayList sizes = new ArrayList(cnt);
        for (int i=0; i<cnt; i++)
        {
            sizes.add(((Component)buttons.get(i)).getSize());
        }
        return sizes;
    }

    public void setSizes(ArrayList newSizes)
    {
        int cnt = buttons.size();
        int h = 0;
        int w = 0;
//        Dimension sz = null;
        Component c = null;
        
        for (int i=0; i<cnt; i++)
        {
            c = (Component)buttons.get(i);
            h = c.getSize().height;
            w = ((Dimension)newSizes.get(i)).width;
            c.setSize(w, h);
//            ((Component)buttons.get(i)).getSize().width = ((Dimension)newSizes.get(i)).width;
        }
//        invalidate();
        validate();
//        repaint();
    }

    protected void fireSizesChanged()
    {
        firePropertyChange("sizes", null, getSizes());
    }

    protected void fireSortOrderChanged(JLabel l)
    {
        int ind = buttons.indexOf(l);
        firePropertyChange("sort_order", null, new Integer(ind));
    }


    public boolean isSortOrderAscending(int index)
    {
        boolean result = false;
        
        JLabel l = (JLabel)buttons.get(index);
        if ( (l != null) && (l.getClientProperty("ascending") != null) )
            result = true;
        return result;
    }

    public Dimension getPreferredSize()
    {
        int w = 0;
        int h = 0;
        Dimension d = null;

        int cnt = buttons.size();
        Component c = null;
        for (int i=0; i<cnt; i++)
        {
            c = (Component)buttons.get(i);
            d = c.getSize();
            w += d.width;
            h = Math.max(h, d.height);
        }

        Insets ins = getInsets();
        if ( ins != null )
        {
            w += ins.left + ins.right;
            h += ins.top + ins.bottom;
        }

        return new Dimension(w,h);
    }


    class CustomMouseAdapter implements MouseListener, MouseMotionListener
    {
        public void mousePressed(MouseEvent evt)
        {
            Component source = (Component)evt.getSource();
            if ( resizeMode )
            {
                resizeAnchor = SwingUtilities.convertPoint(source, evt.getPoint(), SplitButtonBar.this);
                evt.consume();
            }
                
        }

        public void mouseReleased(MouseEvent evt)
        {
            Component source = (Component)evt.getSource();
            Point p = evt.getPoint();

            if ( resizeMode )
            {
                source.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                resizeMode = false;
                resizeAnchor = null;
                evt.consume();
            }
            
        }

        public void mouseClicked(MouseEvent evt)
        {
            if ( resizeMode )
            {
                evt.consume();
            }
            else
            {
                JLabel l = (JLabel)evt.getSource();
                if ( l.getClientProperty("selected") != null )
                {
                    String contrChar = null;
//                    boolean contrFlag = false;
                    
                    if ( l.getClientProperty("ascending") != null )
                    {
                        contrChar = "- ";
                        l.putClientProperty("ascending", null);
                    }
                    else
                    {
                        contrChar = "+ ";
                        l.putClientProperty("ascending", new Boolean(true));
                    }
                    l.setText(contrChar + l.getText().substring(2));
                }
                else
                {
                    Color desel = SplitButtonBar.this.getBackground();
                    Color sel = SplitButtonBar.this.getBackground().brighter();
                    JLabel c = null;
                    for (Iterator i = buttons.iterator(); i.hasNext(); )
                    {
                        c = (JLabel)i.next();
                        if ( c.getClientProperty("selected") != null )
                        {
                            c.putClientProperty("selected", null);
                            c.setBackground(desel);
                            c.setText(c.getText().substring(2));
                            break;
                        }
                    }
                    if ( l.getClientProperty("ascending") != null )
                        l.setText("+ " + l.getText());
                    else
                        l.setText("- " + l.getText());
                    l.putClientProperty("selected", new Boolean(true));
                    l.setBackground(sel);
                }
                SplitButtonBar.this.fireSortOrderChanged(l);
                SplitButtonBar.this.repaint();
            }
        }

        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
            Component source = (Component)e.getSource();
            if ( resizeMode && (e.getModifiers() == 0) )
            {
                source.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                resizeMode = false;
                resizableComponent = null;
            }
        }

        public void mouseDragged(MouseEvent e)
        {
            Component source = (Component)e.getSource();
            if ( resizeMode && (resizeAnchor != null) && (resizableComponent != null))
            {
                Point p = SwingUtilities.convertPoint(source, e.getPoint(), SplitButtonBar.this);
                int delta = p.x - resizeAnchor.x;
                Dimension newSize = resizableComponent.getSize();
                newSize.width += delta;
                if ( newSize.width < 6 )
                    newSize.width = 6;
                resizableComponent.setSize(newSize);
                SplitButtonBar.this.validate();
                resizeAnchor = p;
                SplitButtonBar.this.fireSizesChanged();
            }
        }

        public void mouseMoved(MouseEvent e)
        {
            Component source = (Component)e.getSource();
            Point p = e.getPoint();
            int lb = 3;
            int rb = source.getSize().width-3;
            int last = buttons.size()-1;

            if ( (p.x < lb) && (source != buttons.get(0)) )
            {
                source.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                resizeMode = true;
                resizableComponent = (Component)buttons.get(buttons.indexOf(source)-1);
            }
            else if ( (p.x > rb)  && (source != buttons.get(last)) )
            {
                source.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                resizeMode = true;
                resizableComponent = source;
            }
            else
            {
                source.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                resizeMode = false;
                resizableComponent = null;
            }
        }
    }

        // Only for test
    public static void main(String argv[])
    {
        JFrame fr = new JFrame("SplitButtonBar test")
        {
            public void dispose()
            {
                this.setVisible(false);
                super.dispose();
                System.exit(0);
            }
        };
        fr.setBounds(100,100, 400, 300);
        fr.getContentPane().add( new SplitButtonBar(4), BorderLayout.NORTH);
	    fr.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        fr.setVisible(true);
    }

// End of SplitButtonBar class
}
