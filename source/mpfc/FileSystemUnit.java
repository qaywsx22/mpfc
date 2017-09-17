package mpfc;

import javax.swing.*;

public class FileSystemUnit
{
    private transient AbstractButton driveButton = null;
    private transient String systemRoot = null;
    private transient String currentDirectory = null;
    
    public FileSystemUnit( String initPath )
    {
        systemRoot = initPath;
        currentDirectory = initPath;
    }
    
    public void setDriveButton(AbstractButton b)
    {
        driveButton = b;
    }
    
    public AbstractButton getDriveButton()
    {
        return driveButton;
    }
            
    public void setSystemRoot(String r)
    {
        systemRoot = r;
    }
    
    public String getSystemRoot()
    {
        return systemRoot;
    }
    
    public void setCurrentDirectory(String cd)
    {
        currentDirectory = cd;
    }
    
    public String getCurrentDirectory()
    {
        return currentDirectory;
    }
}