package mpfc;

import java.io.*;
import java.util.*;
import java.text.*;

import javax.swing.*;


public class CustomFile
    implements MCLItem
{
    private static int attributeNumber = 5;
    
    private transient Icon icon = null;
    
    private transient Object[] fields = new Object[attributeNumber];
    private transient boolean superFolder = false;
    private transient boolean selected = false;
    private transient boolean directory = false;
    private transient File origin = null;
    
    public CustomFile(String pathname)
    {
        origin = new File(pathname);
        init();
    }

    public CustomFile(File file)
    {
        origin = file;
        init();
    }

    private void init()
    {
        directory = origin.isDirectory();
//        String extName = origin.getName();
//        
//        displayName = extName;
//        extention = "";
//        
//        if ( !origin.isDirectory() )
//        {
//            int dot = extName.lastIndexOf('.');
//            if ( dot >= 0 )
//            {
//                displayName = extName.substring(0, dot);
//                extention = extName.substring(dot+1);
//            }
//        }
    }


    //-----------------------
    // delegated functions 
    //-----------------------
           
    public long lastModified()
    {
        return origin.lastModified();
    }
    
    public String getPath()
    {
        return origin.getPath();
    }

    public File[] listFiles()
    {
        return origin.listFiles();
    }
    
    public File getParentFile()
    {
        return origin.getParentFile();
    }

    public String toString()
    {
//        return origin+", displayName "+displayName+", time "+origin.lastModified();
        return origin.toString();
    }


    //-----------------------------
    // attribute access functions 
    //-----------------------------
    
    public void setIcon(Icon newIcon)
    {
        icon = newIcon;
    }

    public void setDisplayName(String newName)
    {
        fields[0] = newName;
    }
    
    public File getOrigin()
    {
        return origin;
    }

    public void setSuperFolder(boolean b)
    {
        superFolder = b;
        
        if ( superFolder )
        {
            fields[0] = "..";
            fields[1] = "";
            fields[2] = "<DIR>";
            icon = UIManager.getIcon("mpfc.homeDirIcon");
        }
        else
        {
            parseFullName();
            icon = null;
        }
    }


    //-----------------------------------
    // MCLItem interface implementation 
    //-----------------------------------

    public String getName()
    {
        return origin.getName();
    }
    
    public String getDisplayName()
    {
        if ( fields[0] == null )
            parseFullName();
            
        return (String)fields[0];
    }

    public Icon getIcon()
    {
        return icon;
    }

    public boolean isFolder()
    {
//        return origin.isDirectory();
        return directory;
    }

    public boolean isSuperFolder()
    {
        return superFolder;
    }

    public void setNumberOfAttributes(int num)
    {
    }
    
    public int getNumberOfAttributes()
    {
        return attributeNumber;
    }

    public boolean isSelected()
    {
        return selected;
    }
    
    public void setSelected( boolean sel)
    {
        selected = sel;
    }

    // function returns asked attribute. If attribute was not yet accessed
    // then does lazy access
    public Object getField(int index)
    {
        Object returnValue = null;
        switch ( index )
        {
            case 0: if ( fields[0] == null )    //  file/directory name
                    {
                        parseFullName();
                    }
                    returnValue = fields[0];
                break;
            case 1: if ( fields[1] == null )    //  file extention
                        parseFullName();
                    returnValue = fields[1];
                break;
            case 2: if ( fields[2] == null )    // file length
                        fields[2] = new Long(origin.length());
                    returnValue = fields[2];
                break;
            case 3: if ( fields[3] == null )    // last modification date
                        fields[3] = new Date(origin.lastModified());
                    returnValue = fields[3];
                break;
            case 4: if ( fields[4] == null )    // attribute <readOnly> and <hidden>
                        fields[4] = (origin.canWrite() ? "-" : "r") + (origin.isHidden() ? "h" : "-");
                    returnValue = fields[4];
                break;
            default:
        }
        return returnValue;
    }

    public Object[] getFields()
    {
        for ( int i=0; i<attributeNumber; i++)
        {
            if ( fields[i] == null )
                getField(i);
        }
                
        return fields;
    }


    //-----------------------
    // other functions 
    //-----------------------

    private void parseFullName()
    {
        String s0 = origin.getName();
        String s1 = "";
        
        if ( !isFolder() )
        {
            int dot = s0.lastIndexOf('.');
            if ( dot >= 0 )
            {
                s1 = s0.substring(dot+1);
                s0 = s0.substring(0, dot);
            }
        }
        fields[0] = s0;
        fields[1] = s1;
    }

//    public int compareTo( CustomFile cf )
//    {
//        int ret = -1;
//        File pattern = cf.getOrigin();
//        
//        if ( !origin.getPath().equals(pattern.getPath()) )
//            return ret;
//            
//        if ( origin.length() != pattern.length() )
//            return ret;
//            
//        if ( origin.lastModified() != pattern.lastModified() )
//            return ret;
//            
//        if (   (!origin.isDirectory() && pattern.isDirectory())
//            || (origin.isDirectory() && !pattern.isDirectory())
//           )
//            return ret;
//
//        if (   (origin.isFile() && !pattern.isFile())
//            || (!origin.isFile() && pattern.isFile())
//           )
//            return ret;
//
//        if (   (origin.isHidden() && !pattern.isHidden())
//            || (!origin.isHidden() && pattern.isHidden())
//           )
//            return ret;
//            
//        if (   (origin.canWrite() && !pattern.canWrite())
//            || (!origin.canWrite() && pattern.canWrite())
//           )
//            return ret;
//            
//            ret = 0;
//            return ret;
//    }
}
