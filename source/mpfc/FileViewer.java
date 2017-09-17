package mpfc;

import java.awt.*;
import java.io.*;

import javax.swing.*;


public class FileViewer extends JDialog
{
    private File file = null;
    private Frame frame = null;

    private JTextArea viewer = null;
    private JScrollPane scroller = null;
    
    
    public FileViewer(Frame aFrame, File aFile)
    {
        super(aFrame, "Viewer", false);
        
        frame = aFrame;
        file = aFile;
        setName("file_viewer");
        setSize(800, 600);
        
        scroller = new JScrollPane();
        scroller.setName("file_viewer_scroller");
        
        viewer = new JTextArea();
        viewer.setName("file_viewer_viewer");
        scroller.setViewportView(viewer);
        
        getContentPane().add(scroller, BorderLayout.CENTER);
        readContent();
    }
    
    private void readContent()
    {
        try
        {
            FileReader fr = new FileReader(file);
            viewer.read(fr, null);
        }
        catch (Exception exc)
        {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(frame, "Error while reading file "+file,
                                          "Viewer",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
