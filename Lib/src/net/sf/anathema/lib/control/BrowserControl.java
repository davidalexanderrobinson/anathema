package net.sf.anathema.lib.control;

import java.io.IOException;
import java.net.URL;

import net.sf.anathema.lib.logging.Logger;

/**
 * A simple, static class to display a URL in the system browser.
 * 
 * Under Unix, the system browser is hard-coded to be 'netscape'. Netscape must
 * be in your PATH for this to work. This has been tested with the following
 * platforms: AIX, HP-UX and Solaris.
 * 
 * Under Windows, this will bring up the default browser under windows, usually
 * either Netscape or Microsoft IE. The default browser is determined by the
 * OS. This has been tested under Windows 95/98/NT.
 * 
 * Examples: BrowserControl.displayURL("http://www.javaworld.com")
 * BrowserControl.displayURL("file://c:\\docs\\index.html")
 * BrowserContorl.displayURL("file:///user/joe/index.html");
 * 
 * Note - you must include the url type -- either "http://" or "file://".
 */
public class BrowserControl {

  private static final Logger logger = Logger.getLogger(BrowserControl.class);
  
  public static void displayUrl(URL url) {
    displayUrl(url.toExternalForm());
  }
  
  /**
   * Display a file in the system browser. If you want to display a file, you
   * must include the absolute path name.
   * 
   * @param url
   *                the file's url (the url must start with either "http://" or
   *                "file://").
   */
  public static void displayUrl(String url) {
    boolean windows = WindowsUtilities.isWindows();
    String cmd = null;

    try {
      if (windows) {
        cmd = WIN_PATH + " " + WIN_FLAG + " \"" + url + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        WindowsUtilities.executeMsdosCommand(cmd);
      }
      else {
        // Under Unix, Netscape has to be running for the "-remote" command to work. So, we try sending the command and
        // check for an exit value. If the exit command is 0, it worked, otherwise we need to start the browser.
        // cmd = 'netscape -remote openURL(http://www.javaworld.com)'
        cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Process p = Runtime.getRuntime().exec(cmd);

        try {
          // wait for exit code -- if it's 0, command worked, otherwise we need to start the browser up.
          int exitCode = p.waitFor();

          if (exitCode != 0) {
            // Command failed, start up the browser
            // cmd = 'netscape http://www.javaworld.com'
            cmd = UNIX_PATH + " " + url; //$NON-NLS-1$
            p = Runtime.getRuntime().exec(cmd);
          }
        }
        catch (InterruptedException x) {
          logger.error("Error bringing up browser, cmd='" + cmd + "'", x); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
    catch (IOException x) {
      // couldn't exec browser
      logger.error("Could not invoke browser, command=" + cmd, x); //$NON-NLS-1$
    }
  }

  /**
   * Simple example.
   */
  public static void main(String[] args) {
    displayUrl("http://www.javaworld.com"); //$NON-NLS-1$
  }

  // The default system browser under windows.
  private static final String WIN_PATH = "start"; //$NON-NLS-1$

  // The flag to display a url.
  private static final String WIN_FLAG = "\"\""; //$NON-NLS-1$

  // The default browser under unix.
  private static final String UNIX_PATH = "netscape"; //$NON-NLS-1$

  // The flag to display a url.
  private static final String UNIX_FLAG = "-remote openURL"; //$NON-NLS-1$
}
