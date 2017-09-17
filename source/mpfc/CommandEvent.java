package mpfc;

import java.util.*;


public class CommandEvent extends EventObject
{
    String _command;
    int _modifiers = 0;
    Object _parameter;
    
    
    public CommandEvent(Object source, String command, int modifiers, Object parameter)
    {
        super(source);
        _command = command;
        _modifiers = modifiers;
        _parameter = parameter;
    }
    
    
    public int getModifiers()
    {
        return _modifiers;
    }
    
    public String getCommand()
    {
        return _command;
    }
    
    public Object getParameter()
    {
        return _parameter;
    }
}