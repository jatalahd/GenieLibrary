package org.robotframework.genielibrary;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeywordOverload;

import com.adobe.genie.executor.Genie;
import com.adobe.genie.executor.LogConfig;
import com.adobe.genie.executor.components.GenieMovieClip;
import com.adobe.genie.genieCom.SWFApp;


@RobotKeywords
public class GenieKeywords {

    /* Class internal variables */
    private Genie g;
    private SWFApp app;
    private LogConfig l;
    private int waitTimeout = 0;
  
    /* Constructor with initializers */
    public GenieKeywords() {
        l = new LogConfig();
        l.setLogFolder("./GenieLogs");
        g = null;
        Genie.EXIT_ON_FAILURE = true;
        Genie.EXIT_ON_TIMEOUT = true;
        Genie.CAPTURE_SCREENSHOT_ON_FAILURE = false;
    }
    
    /* Customised Exception handling class */
    private class StepFailed extends Exception {  
        public StepFailed(String msg) {
            super(msg);
        }
    }
    

    @RobotKeyword("Sets a time delay between executed Genie steps. "
                   + "Delay is given in seconds, for example 1 or 2.5\n\n"
                   + "Example:\n"
                   + "| SetGenieExecutionDelay | 2 |\n")
    @ArgumentNames({"timeout"})
    public void setGenieExecutionDelay(final String delay) throws Exception {
        Genie.EXECUTION_DELAY_BEFORE_STEP = (int)(Float.parseFloat(delay)*1000);
    }

    @RobotKeyword("Sets a timeout value, which is used for waiting Genie objects to become visible on Stage. "
                   + "Suggested usage would reset this value before each test case. Default internal value is currently 0 seconds. "
                   + "The argument is given in full seconds, that is, 1 or 3 or 21.\n\n"
                   + "Example:\n"
                   + "| SetGenieObjectTimeout | 10 |\n")
    @ArgumentNames({"timeout"})
    public void setGenieObjectTimeout(final String timeout) {
        this.waitTimeout = Integer.parseInt(timeout);
    }

    @RobotKeyword("Initializes Genie connection to the GenieServer. "
                   + "Must be used before connecting to the SWF application. "
                   + "Every opened socket needs to be closed after each test "
                   + "case or test suite (see CloseGenieSocket).\n\n"
                   + "Example:\n"
                   + "| OpenGenieSocket |\n")
    public void openGenieSocket() throws Exception {
        try {
            g = Genie.init(l);
        } catch (Exception e) {
            throw new StepFailed("Error: Genie initialization failed!");
        }
    }

    @RobotKeyword("Closes Genie connection to the GenieServer and the SWF application. "
                   + "Must be used in test or suite teardown!\n\n"
                   + "Example:\n"
                   + "| CloseGenieSocket |\n")
    public void closeGenieSocket() throws Exception {
        try {
            g.stop();
        } catch (Exception e) {
            throw new StepFailed("Error: Genie failed to close socket!");
        }
    }

    @RobotKeyword("Connects to the SWF application of the given name. "
                   + "Must be used before sending any commands to the application.\n\n"
                   + "Example:\n"
                   + "| ConnectToApplication | applicationName |\n")
    @ArgumentNames({"appName"})
    public void connectToApplication(final String appName) throws Exception {
        try {
            app = g.connectToApp(appName);
        } catch (Exception e) {
            throw new StepFailed("Error: Could not connect to application: " + appName);
        }
    }

    @RobotKeyword("Clicks a GenieMovieClip object with a given genieID.\n\n"
                   + "Example:\n"
                   + "| ClickGenieMovieClipObject | genieID |\n")
    @ArgumentNames({"genieID"})
    public void clickGenieMovieClipObject(final String genieID) throws Exception {
        try {
            (new GenieMovieClip(genieID, app)).waitFor(this.waitTimeout);
            (new GenieMovieClip(genieID, app)).click();
        } catch (Exception e) {
            throw new StepFailed("Error: Click on genieID " + genieID + " failed!");
        }
    }

    @RobotKeyword("Reads and returns the text of the given GenieMovieClip object.\n\n"
                   + "Example:\n"
                   + "| ReadGenieMovieClipObject | genieID |\n")
    @ArgumentNames({"genieID"})
    public String readGenieMovieClipObject(final String genieID) throws Exception {
        boolean bVisible=false;
        String retVal = "";
        try {
            (new GenieMovieClip(genieID, app)).waitFor(this.waitTimeout);
            bVisible = (new GenieMovieClip(genieID, app)).isVisible();
            if (bVisible) { retVal = (new GenieMovieClip(genieID, app)).getValueOf("text"); }
        } catch (Exception e) {
            throw new StepFailed("Error: reading text from genieID " + genieID + " failed!");
        }
        return retVal;
    }

    @RobotKeyword("Waits for the given time for genieID object to have a property with given value.\n\n"
                   + "Examples:\n"
                   + "| WaitGenieMovieClipProperty | genieID | visible | true  | 5  |\n"
                   + "| WaitGenieMovieClipProperty | genieID | visible | false | 10 |\n")
    @ArgumentNames({"genieID","property","value","timeout"})
    public void waitGenieMovieClipProperty(final String genieID, final String prop, 
                                           final String val, final String tout) throws Exception {
        boolean outcome = false;
        try {
            if (new GenieMovieClip(genieID, app).waitForPropertyValue(prop,val,Integer.parseInt(tout))) {
                outcome = true;
            }
        } catch (Exception e) {
            throw new StepFailed("Error: wait for property of genieID " + genieID + " failed!");
        }
        if (!outcome) {
            throw new StepFailed("Error: timeout " + tout + " seconds when waiting genieID " 
                                  + genieID + " to have property " + prop + " with value " + val);
        }
    }
    


} // End Of GenieKeywords
