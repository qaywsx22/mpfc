package mpfc;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import mpfc.actions.*;

public class Mpfc extends JFrame
        implements ActionListener
        , PropertyChangeListener
        , CommandListener
{
    public final static int LEFT = 0;
    public final static int RIGHT = 1;
    private final static int NUMBER_OF_FUNKTION = 7;
    
	protected transient JMenuBar menuBar = null;
	protected transient JToolBar driveBar = null;
    private transient JSplitPane mainPanel = null;
    private transient MultiColumnList left = null;
    private transient MultiColumnList right = null;
    private transient JSplitPane jsp = null;
    
    private transient CommandLinePanel comPan = null;
	protected transient JPanel fp = null;
    private transient JPanel bottom = null;
    
    private transient ComponentListener icl = null;
    private transient WindowListener wl = null;
    
    private transient ButtonGroup drivesGroup = null;
    private transient ButtonGroup lnfGroup = null;
    private transient Hashtable drives = null;
    private transient MultiColumnList activeList = null;
    private transient LoadAction loadAction = null;
    
    private transient AbstractButton leftListButton = null;
    private transient AbstractButton rightListButton = null;
    
    private transient ListLoader leftLoader = null;
    private transient ListLoader rightLoader = null;

    private transient CustomFile leftFile = null;
    private transient CustomFile rightFile = null;
    
    private transient Hashtable actions = null;

    private transient double divider = 0.5d;

    static int instCount = 0;
    private boolean _init = true;
    
    public static void main(String argv[])
    {
        try
        {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        }  
	    catch ( Exception e )
	    {
	        e.printStackTrace();
	        System.exit(1);
	    }

        Mpfc frame=null;
        
        try
        {
            URL url = ClassLoader.getSystemResource("images/harddisk20.gif");
            Icon icon = new ImageIcon(url);
            UIManager.put("mpfc.driveIcon", icon);
        
            url = ClassLoader.getSystemResource("images/homeDir.gif");
            icon = new ImageIcon(url);
            UIManager.put("mpfc.homeDirIcon", icon);

            if ( "Windows".equals(UIManager.getLookAndFeel().getID()) )
            {
                Border focusCellHighlightBorder = new BorderUIResource.LineBorderUIResource( 
                                                        UIManager.getColor("activeCaptionBorder"));
                UIManager.put("List.focusCellHighlightBorder", focusCellHighlightBorder);
            }
            
            frame = new Mpfc();
            frame.setVisible(true);
            frame.initLoad();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public Mpfc()
    {
        super("MultiPlatform File Commander");
	    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel = createPanels();
        
        actions = new Hashtable();
        createActions();        
        
        menuBar = createMenu();
        setJMenuBar(menuBar);

        Action a = (Action)actions.get("left_select");
        left.registerKeyboardAction ( a
                                , KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, 0)
                                , JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                               );
        a = (Action)actions.get("right_select");
        right.registerKeyboardAction ( a
                                , KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, 0)
                                , JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                               );
                               
        mainPanel.setSize(getContentPane().getSize());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        pack();
        icl = new InternalComponentAdapter();
        addComponentListener(icl);
        drivesGroup = new ButtonGroup();
        
        
        driveBar = createDriveBar();
        getContentPane().add(driveBar, BorderLayout.NORTH);
        wl = new InternalWindowAdapter();
        addWindowListener(wl);
        
        createBottomPanel();
        getContentPane().add(bottom, BorderLayout.SOUTH);

	    setBounds(100,100, 700, 500);
	    invalidate();
	    validate();
        mainPanel.setDividerLocation(divider);
    }

    private void divideWindows()
    {
        mainPanel.setDividerLocation(divider);
    }
    
    private JMenuBar createMenu()
    {
        Action a = null;
        
        JMenuBar mb = new JMenuBar();
        mb.setBorderPainted(false);
        
        JMenu m = new JMenu("Files");
        
        JMenuItem mi = new JMenuItem("Properties...");
        mi.setActionCommand("PROP");
        mi.addActionListener(this);
        m.add(mi);
        
        m.addSeparator();
        
        mi = new JMenuItem("Exit");
        mi.setActionCommand("EXIT");
        mi.addActionListener(this);
        m.add(mi);
        
        mb.add(m);

//---------------------------
        m = new JMenu("Mark");
        
        mi = new JMenuItem("Select All");
        mi.setActionCommand("SEL_ALL");
        mi.addActionListener(this);
        m.add(mi);
        
        mb.add(m);

        
        m = new JMenu("View");

//        mi = new JMenuItem("");
//        mi.setActionCommand("");
//        mi.addActionListener(this);
//        m.add(mi);
        
        mb.add(m);
        
        m = new JMenu("Commands");
        
//        mi = new JMenuItem("");
//        mi.setActionCommand("");
//        mi.addActionListener(this);
//        m.add(mi);
        
        mb.add(m);
        
        m = new JMenu("Options");

        JMenu sm = new JMenu("Look & Feel");

        lnfGroup = new ButtonGroup();
        
        mi = new JRadioButtonMenuItem("Java L&F");
        lnfGroup.add(mi);
        mi.setActionCommand("L&F_JAVA");
        a = (Action)actions.get("lnf_metal");
        mi.addActionListener(a);
        sm.add(mi);
        
        mi = new JRadioButtonMenuItem("Motif L&F");
        lnfGroup.add(mi);
        mi.setActionCommand("L&F_MOTIF");
        a = (Action)actions.get("lnf_motif");
        mi.addActionListener(a);
        sm.add(mi);
        
        mi = new JRadioButtonMenuItem("Windows L&F");
        lnfGroup.add(mi);
        lnfGroup.setSelected(mi.getModel(), true);
        mi.setActionCommand("L&F_WIN");
        a = (Action)actions.get("lnf_windows");
        mi.addActionListener(a);
        sm.add(mi);

        m.add(sm);
        
        mi = new JMenuItem("Configuration...");
        mi.setActionCommand("DLG_CONFIG");
        mi.addActionListener(this);
        m.add(mi);
        
        mb.add(m);
        
        m = new JMenu("Start");
        
//        mi = new JMenuItem("");
//        mi.setActionCommand("");
//        mi.addActionListener(this);
//        m.add(mi);
        
        mb.add(m);
        
        m = new JMenu("Help");
        
        mi = new JMenuItem("About...");
        mi.setActionCommand("DLG_ABOUT");
        mi.addActionListener(this);
        m.add(mi);
        
        mb.add(m);
//---------------------------
        
        return mb;
    }

    private JToolBar createDriveBar()
    {
        File[] roots = File.listRoots();
        
        JToolBar tb = new JToolBar();
        tb.setBorderPainted(false);
        tb.setMargin(new Insets(0,2,0,2));
        drives = new Hashtable();
        drivesGroup = new ButtonGroup();
        tb.setFloatable(false);
        String txt = null;
        AbstractButton b = null;
        int ind = -1;
        Icon icon = (Icon)UIManager.get("mpfc.driveIcon");
        Action a = null;
        
        for ( int i=0; i<roots.length; i++ )
        {
            // for toolbar create toggle buttons
            b = createToolbarButton(true);
            b.setIcon(icon);
            txt = roots[i].toString();
            b.setName(txt);
            b.setToolTipText(txt);
            ind = txt.indexOf(":");
            if ( ind > 0 )
            {
                // search drive letter under Windows
                txt = txt.substring(0, ind);
                b.setText(txt.toUpperCase());
                b.setHorizontalTextPosition(SwingConstants.RIGHT);
            }
            else
            {
                // search root directory name under Linux
                ind = txt.indexOf(File.separator,1);
                if ( ind > 0 )
                {
                    txt = txt.substring(0, ind);
                    b.setText(txt);
                    b.setHorizontalTextPosition(SwingConstants.RIGHT);
                }
            }
            a = (Action)actions.get("load");
            b.addActionListener(a);
            tb.add(b);
            drivesGroup.add(b);
            txt = roots[i].getPath();
            drives.put(b, new FileSystemUnit(txt));
        }
        return tb;
    }


    protected AbstractButton createToolbarButton( boolean toggle )
    {
        AbstractButton b = null;
        if ( toggle )
        {
            b = new JToggleButton()
            {
                public boolean isFocusTraversable()
                { return false; }
            };
        }
        else
        {
            b = new JButton()
            {
                public boolean isFocusTraversable()
                { return false; }
            };
        }
        b.setRequestFocusEnabled(false);
        b.setFocusPainted(false);
        b.setBorder(ToggleBorder.getToggleBorder());
        b.setRolloverEnabled(true);
        b.setMargin(new Insets(1,1,1,1));

        return b;
    }

    private void createActions()
    {
        Action a =  new LoadAction();
        actions.put("load", a);
        
        a = new DeleteAction(left);
        actions.put("left_delete", a);
        
        a = new DeleteAction(right);
        actions.put("right_delete", a);
        
        a = new LNFAction("javax.swing.plaf.metal.MetalLookAndFeel", this);
        actions.put("lnf_metal", a);

        a = new LNFAction("com.sun.java.swing.plaf.motif.MotifLookAndFeel", this);
        actions.put("lnf_motif", a);

        a = new LNFAction("com.sun.java.swing.plaf.windows.WindowsLookAndFeel", this);
        actions.put("lnf_windows", a);

        a = new SelectAction(left);
        actions.put("left_select", a);

        a = new SelectAction(right);
        actions.put("right_select", a);
        
        a = new MakeDirectoryAction();
        actions.put("make_dir", a);
    }

    public void initLoad()
    {
        int cnt = driveBar.getComponentCount();
        AbstractButton b = null;
        FileSystemUnit fsu = null;

        activeList = left;
        if ( cnt > 1 )
            b = (AbstractButton)driveBar.getComponentAtIndex(1);
        else
            b = (AbstractButton)driveBar.getComponentAtIndex(0);
        if ( b != null )
        {
            leftListButton = b;
            fsu = (FileSystemUnit)drives.get(b);
            leftFile = new CustomFile(fsu.getCurrentDirectory());
            left.setActive(true);
            right.setActive(false);
System.out.println( "Mpfc.initLoad try to load left panel with "+leftFile);
            b.doClick();
        
            activeList = right;
            rightListButton = b;
            fsu = (FileSystemUnit)drives.get(b);
            rightFile = new CustomFile(fsu.getCurrentDirectory());
            left.setActive(false);
            right.setActive(true);
            comPan.setDirectoryName(fsu.getCurrentDirectory());
System.out.println( "Mpfc.initLoad try to load right panel with "+leftFile);
            b.doClick();
        }
        _init = false;
    }
    
    private JSplitPane createPanels()
    {
        String[] names = {"Name", "Ext", "Size", "Date", "Attr"};
        Dimension[] sar = { new Dimension(120,0),
                            new Dimension(30,0),
                            new Dimension(60,0),
                            new Dimension(100,0),
                            new Dimension(30,0)
                        };
        ArrayList sizes = new ArrayList(Arrays.asList(sar));
        left = new MultiColumnList(names);
        left.setName("left_list");
        left.setColumnSizes(sizes);
        left.addPropertyChangeListener(this);
        left.addCommandListener(this);

        right = new MultiColumnList(names);
        right.setName("right_list");
        right.setColumnSizes(sizes);
        right.addPropertyChangeListener(this);
        right.addCommandListener(this);
        
        jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, left, right)
        {
            public boolean isFocusCycleRoot()
            { return true;  }
        };
        jsp.setDividerSize(2);
        jsp.setDividerLocation(divider);
        jsp.addPropertyChangeListener(this);
        return jsp;
    }

    private void createBottomPanel()
    {
        AbstractButton b = null;
        
        comPan = new CommandLinePanel();
        
        fp = new JPanel();
        fp.setName("function_panel");
        fp.setLayout( new GridLayout(0, NUMBER_OF_FUNKTION));
        String txt = null;
        String comm = null;
        Action a = null;
        
        for (int i=0; i<NUMBER_OF_FUNKTION; i++)
        {
            // for function bar create normal buttons
            b = createToolbarButton(false);
            switch ( i )
            {
                case 0: txt = "F3 View";
                        comm = "VIEW";
                        b.addActionListener(this);
                    break;
                case 1: txt = "F4 Edit";
                        comm = "EDIT";
                    break;
                case 2: txt = "F5 Copy";
                        comm = "COPY";
                    break;
                case 3: txt = "F6 RenMove";
                        comm = "RENMOVE";
                    break;
                case 4: txt = "F7 MkDir";
                        comm = "F7_mkdir";
                        a = (Action)actions.get("make_dir");
                        b.addActionListener(a);
//                        ((JComponent)getContentPane()).registerKeyboardAction ( a
                        left.registerKeyboardAction ( a
                                                , KeyStroke.getKeyStroke( KeyEvent.VK_F7, 0)
                                                , JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                                               );
                        right.registerKeyboardAction ( a
                                                , KeyStroke.getKeyStroke( KeyEvent.VK_F7, 0)
                                                , JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                                               );
                        comm = "MKDIR";
                    break;
                case 5: txt = "F8 Delete";
                        b.addActionListener(this);
                        b.setActionCommand("F8_delete");
                        a = (Action)actions.get("left_delete");
                        left.registerKeyboardAction ( a
                                                , KeyStroke.getKeyStroke( KeyEvent.VK_F8, 0)
                                                , JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                                               );
                        left.registerKeyboardAction ( a
                                                , KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0)
                                                , JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                                               );
                        a = (Action)actions.get("right_delete");
                        right.registerKeyboardAction ( a
                                                , KeyStroke.getKeyStroke( KeyEvent.VK_F8, 0)
                                                , JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                                               );
                        right.registerKeyboardAction ( a
                                                , KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0)
                                                , JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                                               );
                        comm = "DELETE";
                    break;
                case 6: txt = "F10 Exit";
                        comm = "EXIT";
                        b.addActionListener(this);
                    break;
            }
            
            b.setText(txt);
            b.setName(txt);
            b.setActionCommand(comm);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
//            b.addActionListener();
            fp.add(b);
        }
        
        bottom = new JPanel();
        bottom.setName("bottom_panel");
        bottom.setLayout(new GridLayout(2,0));
        bottom.add(comPan);
        bottom.add(fp);
    }

    public void registerDefaultAccelerators()
    {
        
    }

    private DefaultListModel loadDirectory(CustomFile root, MultiColumnList list)
//    private void loadDirectory(CustomFile root, MultiColumnList list)
    {
        if ( (root == null) || (list == null) )
            return null;
        MultiColumnList panel = list;
        DefaultListModel model = null;
        CustomFile cf = null;
        
        if ( panel == left )
        {
            cf = leftFile;
        }
        else
        {
            cf = rightFile;
        }
        
	    long t0 = System.currentTimeMillis();
        String[] ennames = root.getOrigin().list();
//        File[] entries = root.listFiles();
//        if ( entries == null )
        if ( ennames == null )
            throw new RuntimeException("Directory "+root+" - Access denied.");
	    long t1 = System.currentTimeMillis();
        String path = root.getPath()+File.separator;
        CustomFile item = null;
        File f = null;

	    long t2 = System.currentTimeMillis();
  	    long size = 0;
  	    int count = 0;
//        Object[] modArr = new Object[entries.length];
        Object[] modArr = new Object[ennames.length];
//        for (int i=0; i<entries.length; i++)
        for (int i=0; i<ennames.length; i++)
        {
            f = new File(path+ennames[i]);
//            item = new CustomFile(entries[i]);
            item = new CustomFile(f);
            modArr[i] = item;
//            size += entries[i].length();
            size += f.length();
//            if ( !entries[i].isDirectory() )
            if ( !item.isFolder() )
                count++;
        }
	    long t3 = System.currentTimeMillis();

        int index = panel.getActiveColumnIndex();
        boolean ascending = panel.isSortOrderAscending(index);
        
	    long t4 = System.currentTimeMillis();
        SortConstraints sc = new SortConstraints(ascending, index, panel.getColumnType(index));
        Arrays.sort(modArr, new MCLItemComparator(sc));
	    long t5 = System.currentTimeMillis();
        
            
        model = new DefaultListModel();
        File par = root.getParentFile();
  	    long t6=0;
        synchronized ( panel.getTreeLock() )
        {
            if ( par != null )
            {
                item = new CustomFile(par);
                item.setSuperFolder(true);
                model.addElement(item);
            }
            
    	    t6 = System.currentTimeMillis();
            for (int i=0; i<modArr.length; i++)
            {
                model.addElement(modArr[i]);
            }
            
            panel.setFileCount(count);
            panel.setFileSize(size);
        }
    	long t7 = System.currentTimeMillis();
String time = "Time for "+root+" directory load:\n";
time += "\tget file list -> "+(t1-t0)+" ms\n";
time += "\tcreate custom files array -> "+(t3-t2)+" ms\n";
time += "\tsort list -> "+(t5-t4)+" ms\n";
time += "\tfill list model-> "+(t7-t6)+" ms\n";
time += "\tall together -> "+(t7-t0)+" ms\n";
System.out.println( time );
System.out.println( " Insgesamt "+ modArr.length+" files");
        return model;
    }


    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if ( "EXIT".equals(command) )
        {
            setVisible(false);
            dispose();
        }
        else if ( "F8_delete".equals(command) )
        {
            Action a = (Action)actions.get("left_delete");
            
            if ( activeList == right )
                a = (Action)actions.get("right_delete");
                
            a.actionPerformed(e);
        }
        else if ( "DLG_ABOUT".equals(command) )
        {
        }
        else if ( "VIEW".equals(command) )
        {
            CustomFile cf = (CustomFile)activeList.getFocusedValue();
        System.out.println( "MPFC::actionPerformed(view): focused custom file is "+cf);
            File f = cf.getOrigin();
        System.out.println( "MPFC::actionPerformed(view): focused file is "+f);
            FileViewer viewer = new FileViewer(this, f);
            viewer.setLocationRelativeTo(this);
            viewer.setVisible(true);
        }
        else if ( "PROP".equals(command) )
        {
        }
        else if ( "PROP".equals(command) )
        {
        }
        else if ( "PROP".equals(command) )
        {
        }

        if ( e.getSource() instanceof MenuElement )
            menuBar.setSelected(null);
    
    }

    public void dispose()
    {
        super.dispose();
        System.exit(0);
    }
    
    public void propertyChange(PropertyChangeEvent e)
    {
        String propertyName = e.getPropertyName();
        Object source = e.getSource();
        
        if ( "sizes".equals(propertyName) )
        {
            ArrayList sz = (ArrayList)e.getNewValue();
            if ( left == e.getSource() )
                right.setColumnSizes(sz);
            else
                left.setColumnSizes(sz);
        }
        else if ( JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY.equals(propertyName) )
        {
            double l = left.getWidth();
            double w = jsp.getWidth()-jsp.getDividerSize();
            long tmp = Math.round((l/w)*1000d);
            divider = tmp;
            divider /= 1000d;
        }
        else if ( "active_list".equals(propertyName) )
        {
            if ( source == activeList )
                return;
                
            activeList = (MultiColumnList)source;
            if ( left == activeList )
            {
                left.setActive(true);
                right.setActive(false);
                
                FileSystemUnit fsu = (FileSystemUnit)drives.get(leftListButton);
                comPan.setDirectoryName(fsu.getCurrentDirectory());
//                CustomFile cf = new CustomFile(fsu.getCurrentDirectory());
//                if ( leftFile.compareTo(cf) != 0 )
                    leftListButton.doClick();
            }
            else
            {
                left.setActive(false);
                right.setActive(true);
                FileSystemUnit fsu = (FileSystemUnit)drives.get(rightListButton);
                comPan.setDirectoryName(fsu.getCurrentDirectory());
//                CustomFile cf = new CustomFile(fsu.getCurrentDirectory());
//                if ( rightFile.compareTo(cf) != 0 )
                    rightListButton.doClick();
            }
        }
        else if ( "sort_order".equals(propertyName) )
        {
            SortConstraints sc = (SortConstraints)e.getNewValue();
            MultiColumnList list = (MultiColumnList)source;
            DefaultListModel model = (DefaultListModel)list.getModel();
            
	    long t0 = System.currentTimeMillis();
            CustomFile parentDirectory = (CustomFile)model.getElementAt(0);
            if ( parentDirectory.isSuperFolder() )
                model.remove(0);
            else
                parentDirectory = null;
	    long t1 = System.currentTimeMillis();
                
            Object[] selection = list.getSelectedValues();
            
	    long t2 = System.currentTimeMillis();
            Object[] arr = model.toArray();
	    long t3 = System.currentTimeMillis();
            Arrays.sort(arr, new MCLItemComparator(sc));
	    long t4 = System.currentTimeMillis();

            model = new DefaultListModel();
            
            if ( parentDirectory != null )
                model.addElement(parentDirectory);
                
	    long t5 = System.currentTimeMillis();
            for (int i=0; i<arr.length; i++)
            {
                model.addElement(arr[i]);
            }
	    long t6 = System.currentTimeMillis();
            
            if ( selection != null )
            {
                for (int i=0; i<selection.length; i++)
                {
                    list.setSelectedValue(selection[i]);
                }
            }
            
	    long t7 = System.currentTimeMillis();
            list.setModel(model);
	    long t8 = System.currentTimeMillis();
String time = "Time for sort:\n";
time += "\tparent directory search -> "+(t1-t0)+" ms\n";
time += "\tmodel to array -> "+(t3-t2)+" ms\n";
time += "\tsort array -> "+(t4-t3)+" ms\n";
time += "\tfill model-> "+(t6-t5)+" ms\n";
time += "\tset new model-> "+(t8-t7)+" ms\n";
time += "\tall together -> "+(t8-t0)+" ms\n";
System.out.println( time );
System.out.println( " Insgesamt "+ arr.length+" items");
        }
    }


    public void executeCommand( CommandEvent e )
    {
        Object par = e.getParameter();
        Object source = e.getSource();
        String command = e.getCommand();
        
        if ( "reload".equals(command) )
        {
            if ( left == par )
            {
                leftListButton.doClick();
            }
            else
            {
                rightListButton.doClick();
            }
        }
        else if ( "execute".equals(command) )
        {
            if ( (source == left) || (source == right) )
            {
                CustomFile cf = (CustomFile)par;
                if ( cf.isFolder() )
                {
                    MultiColumnList list = (MultiColumnList)source;
                    FileSystemUnit fsu = null;
                    ListLoader ll = null;
                    
                    if ( list == left )
                    {
                        if ( leftLoader != null )
                        {
                            getToolkit().beep();
                            return;
                        }
                        fsu = (FileSystemUnit)drives.get(leftListButton);
                        fsu.setCurrentDirectory(cf.getPath());
                        leftFile = cf;
                        leftLoader = new ListLoader(cf, list);
                    }
                    else
                    {
                        if ( rightLoader != null )
                        {
                            getToolkit().beep();
                            return;
                        }
                        fsu = (FileSystemUnit)drives.get(rightListButton);
                        fsu.setCurrentDirectory(cf.getPath());
                        rightFile = cf;
                        rightLoader = new ListLoader(cf, list);
                    }
                }
            }
        }
    }
    
    
    class InternalComponentAdapter extends ComponentAdapter
    {
        public void componentResized(ComponentEvent e)
        {
            Mpfc.this.jsp.setDividerLocation(Mpfc.this.divider);
        }
    }


    class MakeDirectoryAction extends AbstractAction
    {
        public MakeDirectoryAction()
        {
            this.setEnabled(true);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String name = null;
            String newDirName = null;
            FileSystemUnit fsu = null;
            File newDir = null;
            boolean leftAct = false;

            name = JOptionPane.showInputDialog(Mpfc.this, "Directory name:",
                                                        Mpfc.this.getTitle(),
                                                        JOptionPane.PLAIN_MESSAGE);
            if ( name == null )   // dialog was canceled, return...
                return;
                
            if ( left == activeList )
            {
                fsu = (FileSystemUnit)drives.get(leftListButton);
                leftAct = true;
            }
            else
            {
                fsu = (FileSystemUnit)drives.get(rightListButton);
            }
            
            newDirName = fsu.getCurrentDirectory()+File.separator+name;
            newDir = new File(newDirName);
            if ( newDir.exists() || !newDir.mkdir() )  // directory exists or error. Now simple cancel action...
            {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(Mpfc.this, "Directory "+newDirName+" can not be created!",
                                              Mpfc.this.getTitle(),
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if ( leftAct )
                leftListButton.doClick();
            else
                rightListButton.doClick();
        }
    }


    
    class LoadAction extends AbstractAction
    {
        public LoadAction()
        {
            this.setEnabled(true);
        }
        
        public void actionPerformed(ActionEvent e)
        {
System.out.println( "LoadAction: actionPerformed called");
            Object source = e.getSource();
            FileSystemUnit fsu = (FileSystemUnit)drives.get(source);
            MultiColumnList list = Mpfc.this.left;
            boolean right = false;
            CustomFile cf = new CustomFile(fsu.getCurrentDirectory());
            
            synchronized (Mpfc.this.activeList)
            {
                if ( Mpfc.this.activeList == Mpfc.this.right )
                {
System.out.println( "LoadAction: try to load right panel with "+cf);
                    if ( rightLoader != null )
                    {
System.out.println( "LoadAction: right loader is busy!");
                        getToolkit().beep();
                        return;
                    }
                    rightListButton = (AbstractButton)source;
                    list = Mpfc.this.right;
                    right = true;
                    rightFile = cf;
                }
                else
                {
System.out.println( "LoadAction: try to load left panel with "+cf);
                    if ( leftLoader != null )
                    {
System.out.println( "LoadAction: left loader is busy!");
                        getToolkit().beep();
                        return;
                    }
                    leftListButton = (AbstractButton)source;
                    leftFile = cf;
                }
            }
            if ( right )
                rightLoader = new ListLoader( cf, list);
            else
                leftLoader = new ListLoader(cf, list);
        }
    }

    class InternalWindowAdapter extends WindowAdapter
    {
        public void windowActivated(WindowEvent e)
        {
            if ( _init )
                return;
            CustomFile cf = null;
            if ( (leftListButton != null) && (leftFile != null) )
            {
                if ( leftLoader != null )
                    return;
                FileSystemUnit fsu = (FileSystemUnit)drives.get(leftListButton);
                cf = new CustomFile(fsu.getCurrentDirectory());
//                if ( leftFile.compareTo(cf) != 0 )
                    leftLoader = new ListLoader( cf, left);
            }
            
            if ( (rightListButton != null) && (rightFile != null) )
            {
                if ( rightLoader != null )
                    return;
                FileSystemUnit fsu = (FileSystemUnit)drives.get(rightListButton);
                cf = new CustomFile(fsu.getCurrentDirectory());
//                if ( rightFile.compareTo(cf) != 0 )
                    rightLoader = new ListLoader( cf, right);
            }
        }
    }

    class ListLoader extends SwingWorker
    {
        MultiColumnList panel = null;
        String _dir = null;
        
        public ListLoader(CustomFile dir, MultiColumnList list)
        {
            super(dir);
            panel = list;
            Mpfc.instCount++;
//System.out.println( "ListLoader "+Mpfc.instCount+" CREATED for "+dir+", panel "+panel.getName());
    }
    
        public void finalize() throws Throwable
        {
            super.finalize();
            Mpfc.instCount--;
//System.out.println( "ListLoader "+Mpfc.instCount+" DESTROYED.");
        }
    
        public Object construct(Object dir)
        {
            try
            {
                CustomFile f = (CustomFile)dir;
                _dir = f.getPath();
                return Mpfc.this.loadDirectory(f, panel);
//                Mpfc.this.loadDirectory(f, panel);
//                return null;
            }
            catch (Throwable t)
            {
                return t;
            }
        }

        public void finished()
        {
            Object o = get();
    
            if ( o instanceof Throwable )
            {
                System.out.println( "Mpfc: Error while read directory " + _dir);
                ((Throwable)o).printStackTrace();
//                throw new RuntimeException();
            }
//System.out.println( "ListLoader for "+_dir+", panel "+panel.getName()+" FINISHED");
            else if ( o != null )
            {
                DefaultListModel mod = (DefaultListModel)o;
                panel.setTitleBarText(_dir);
//                if ( panel == Mpfc.this.left )
//                    leftModel = mod;
//                else
//                    rightModel = mod;
                    
                panel.setModel(mod);
                panel.updateInfoLine();
                if ( Mpfc.this.activeList == panel )
                    Mpfc.this.comPan.setDirectoryName(_dir);
//                panel.refreshList();
            }
                if ( panel == Mpfc.this.left )
                {
                    leftLoader = null;
System.out.println( "ListLoader:finished() set left loader to null");
                }
                else
                {
                    rightLoader = null;
System.out.println( "ListLoader:finished() set right loader to null");
                }
        }
    }
}