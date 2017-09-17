package mpfc;


import javax.swing.SwingUtilities;

/**
 * An abstract class that you subclass to perform
 * GUI-related work in a dedicated thread.
 * For instructions on using this class, see 
 * http://java.sun.com/products/jfc/swingdoc/threads.html
 */

public abstract class SwingWorker
{
    private Object value;
    private Thread thread;

    /**
     * Start a thread that will call the <code>construct</code> method
     * and then exit.
     */
    public SwingWorker(Object arg)
    {
	    final Object _arg = arg;
        final Runnable doFinished = new Runnable()
        {
           public void run() { finished(); }
        };

	    Runnable doConstruct = new Runnable()
	    { 
	        public void run()
	        {
		        synchronized(SwingWorker.this)
		        {
		            value = construct(_arg);
		            thread = null;
		        }
		        SwingUtilities.invokeLater(doFinished);
	        }
	    };

	    thread = new Thread(doConstruct);
	    thread.start();
    }

    /** 
     * Compute the value to be returned by the <code>get</code> method. 
     */
    public abstract Object construct(Object arg);

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished()
    {
    }

    /**
     * Return the value created by the <code>construct</code> method.  
     */
    public Object get()
    {
	    while (true)
	    {  // keep trying if we're interrupted
	        Thread t;
	        synchronized (SwingWorker.this)
	        {
		        t = thread;
		        if (t == null)
		        {
		            return value;
		        }
	        }
	        try
	        {
		        t.join();
	        }
	        catch (InterruptedException e)
	        {
	        }
	    }
    }
}
