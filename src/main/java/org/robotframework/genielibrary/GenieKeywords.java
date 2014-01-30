package org.robotframework.genielibrary;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeywordOverload;

import com.adobe.genie.executor.Genie;
import com.adobe.genie.executor.LogConfig;
import com.adobe.genie.executor.components.GenieComponent;
import com.adobe.genie.executor.components.GenieDisplayObject;
import com.adobe.genie.executor.components.GenieTextInput;
//import com.adobe.genie.executor.uiEvents.UIGenieID;
//import com.adobe.genie.executor.uiEvents.UIKeyBoard;
import com.adobe.genie.genieCom.SWFApp;

import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    @RobotKeyword("Saves a screenshot of the current Genie application. "
                   + "The screenshot files are saved in a folder './scrshots' in a date-format MMddHHmmss.png.\n\n"
                   + "Example:\n"
                   + "| GetGenieScreenshot |\n")
    public void getGenieScreenshot() throws Exception {
        String fileName = new SimpleDateFormat("MMddHHmmss'.png'").format(new Date());
        g.captureAppImage(app, "./scrshots/"+fileName);
        System.out.println("*HTML* <img src='./scrshots/"+fileName+"'></img>");
    }

    @RobotKeyword("Clicks a GenieComponent object with a given genieID.\n\n"
                   + "Example:\n"
                   + "| ClickGenieComponent | genieID |\n")
    @ArgumentNames({"genieID"})
    public void clickGenieComponent(final String genieID) throws Exception {
        try {
            (new GenieComponent(genieID,app)).waitFor(this.waitTimeout);
            (new GenieComponent(genieID,app)).click();
        } catch (Exception e) {
            throw new StepFailed("Error: Click on GenieComponent " + genieID + " failed!");
        }
    }

    @RobotKeyword("Reads and returns the text of the given GenieComponent object.\n\n"
                   + "Example:\n"
                   + "| ${text}= | ReadGenieComponent | genieID |\n")
    @ArgumentNames({"genieID"})
    public String readGenieComponent(final String genieID) throws Exception {
        String retVal = "";
        try {
            (new GenieComponent(genieID,app)).waitForPropertyValue("visible","true",this.waitTimeout);
            retVal = (new GenieComponent(genieID,app)).getValueOf("text");
        } catch (Exception e) {
            throw new StepFailed("Error: reading text from GenieComponent " + genieID + " failed!");
        }
        return retVal;
    }

    @RobotKeyword("Waits for the given time for given GenieComponent property to obtain the given value.\n\n"
                   + "Examples:\n"
                   + "| WaitForGenieComponentProperty | genieID | visible | true          | 5  |\n"
                   + "| WaitForGenieComponentProperty | genieID | enabled | true          | 3  |\n"
                   + "| WaitForGenieComponentProperty | genieID | text    | dynamic_text  | 6  |\n"
                   + "| WaitForGenieComponentProperty | genieID | visible | false         | 10 |\n")
    @ArgumentNames({"genieID","property","value","timeout"})
    public void waitForGenieComponentProperty(final String genieID, final String prop, 
                                              final String val, final String tout) throws Exception {
        boolean outcome = false;
        try {
            outcome = (new GenieComponent(genieID,app)).waitForPropertyValue(prop,val,Integer.parseInt(tout));
        } catch (Exception e) {
            throw new StepFailed("Error: error occurred when waiting GenieComponent " 
                                  + genieID + " to have property " + prop + " with value " + val);
        }
        if (!outcome) {
            throw new StepFailed("Error: timeout " + tout + " seconds when waiting GenieComponent " 
                                  + genieID + " to have property " + prop + " with value " + val);
        }
    }
    
    @RobotKeyword("Takes a screenshot of the given GenieComponent object "
                   + "and saves it as a .png file to the given directory.\n\n"
                   + "Examples:\n"
                   + "| CaptureGenieComponentImage | genieID | ./myImage.png         |\n"
                   + "| CaptureGenieComponentImage | genieID | C:\\myfolder\\img.png |\n")
    @ArgumentNames({"genieID","imagePath"})
    public void captureGenieComponentImage(final String genieID, final String imagePath) throws Exception {
        try {
            (new GenieComponent(genieID,app)).captureComponentImage(imagePath);
        } catch (Exception e) {
            throw new StepFailed("Error: screenshot of GenieComponent " + genieID + " failed!");
        }
    }

    @RobotKeyword("Returns the local coordinates of the given GenieComponent object.\n\n"
                   + "Example:\n"
                   + "| ${coords}= | GetGenieLocalCoordinates | genieID |\n")
    @ArgumentNames({"genieID"})
    public String getGenieLocalCoordinates(final String genieID) throws Exception {
        Point xy = new Point();
        try {
            xy = (new GenieComponent(genieID,app)).getLocalCoordinates();
        } catch (Exception e) {
            throw new StepFailed("Error: getting local coordinates of GenieComponent " + genieID + " failed!");
        }
        return xy.toString();
    }

    @RobotKeyword("Writes text to the given GenieTextInput object.\n\n"
                   + "Example:\n"
                   + "| WriteToGenieTextInput | genieID | some_text |\n")
    @ArgumentNames({"genieID","text"})
    public void writeToGenieTextInput(final String genieID, final String text) throws Exception {
        try {
            (new GenieTextInput(genieID,app)).waitFor(this.waitTimeout);
            (new GenieTextInput(genieID,app)).input(text,"false");
        } catch (Exception e) {
            throw new StepFailed("Error: writing text to GenieTextInput " + genieID + " failed!");
        }
    }

    @RobotKeyword("Presses a key on the given GenieTextInput object.\n\n"
                   + "Examples:\n"
                   + "| PressKeyOnGenieTextInput | genieID | ENTER  |\n"
                   + "| PressKeyOnGenieTextInput | genieID | ESCAPE |\n")
    @ArgumentNames({"genieID","key"})
    public void pressKeyOnGenieTextInput(final String genieID, final String key) throws Exception {
        try {
            (new GenieTextInput(genieID,app)).waitFor(this.waitTimeout);
            (new GenieTextInput(genieID,app)).input(key,"false");
        } catch (Exception e) {
            throw new StepFailed("Error: pressing key on GenieTextInput " + genieID + " failed!");
        }
    }

    @RobotKeyword("Executes a double click on the given GenieTextInput object.\n\n"
                   + "Example:\n"
                   + "| DoubleClickOnGenieTextInput | genieID |\n")
    @ArgumentNames({"genieID"})
    public void doubleClickOnGenieTextInput(final String genieID) throws Exception {
        try {
            (new GenieTextInput(genieID,app)).waitFor(this.waitTimeout);
            (new GenieTextInput(genieID,app)).doubleClick();
        } catch (Exception e) {
            throw new StepFailed("Error: double click on GenieTextInput " + genieID + " failed!");
        }
    }

    @RobotKeyword("Executes a native mouse click on the given GenieDisplayObject. "
                   + "Only accepts click parameters of the format: int1,int2,int3,int4,int5,int6,int7,boolean "
                   + "where the last boolean argument can have values 'true' or 'false'. "
                   + "If 'true' is given, the mouse is physically moved to the location of the click.\n\n"
                   + "Example:\n"
                   + "| ClickGenieDisplayObject | genieID | 69,20,298,122,575,360,3,false |\n")
    @ArgumentNames({"genieID","clickArgumentString"})
    public void clickGenieDisplayObject(final String genieID, final String args) throws Exception {
        String[] buf = args.split(","); 
        boolean bool = buf[(buf.length - 1)].equals("true");
        try {
            (new GenieDisplayObject(genieID,app)).waitFor(this.waitTimeout);
            (new GenieDisplayObject(genieID,app)).click(Integer.parseInt(buf[0]),
                                                        Integer.parseInt(buf[1]),
                                                        Integer.parseInt(buf[2]),
                                                        Integer.parseInt(buf[3]),
                                                        Integer.parseInt(buf[4]),
                                                        Integer.parseInt(buf[5]),
                                                        Integer.parseInt(buf[6]), bool );
        } catch (Exception e) {
            throw new StepFailed("Error: native click on GenieTextInput " + genieID + " failed!");
        }
    }

    @RobotKeyword("Executes a double click on the given GenieDisplayObject. "
                   + "The x and y arguments define the coordinates to click upon.\n\n"
                   + "Example:\n"
                   + "| DoubleClickOnGenieDisplayObject | genieID | x | y |\n")
    @ArgumentNames({"genieID"})
    public void doubleClickOnGenieDisplayObject(final String genieID, final String x, final String y) throws Exception {
        try {
            (new GenieDisplayObject(genieID,app)).waitFor(this.waitTimeout);
            (new GenieDisplayObject(genieID,app)).doubleClick(Integer.parseInt(x),Integer.parseInt(y));
        } catch (Exception e) {
            throw new StepFailed("Error: double click on GenieDisplayObject " + genieID + " failed!");
        }
    }

    @RobotKeyword("Executes a native key press action on the given GenieDisplayObject. "
                   + "The duration of the keypress is hardcoded as 0.5 seconds.\n\n"
                   + "Examples:\n"
                   + "| PressKeyOnGenieDisplayObject | genieID | UP   |\n"
                   + "| PressKeyOnGenieDisplayObject | genieID | DOWN |\n")
    @ArgumentNames({"genieID","key"})
    public void pressKeyOnGenieDisplayObject(final String genieID, final String key) throws Exception {
        try {
            (new GenieDisplayObject(genieID,app)).waitFor(this.waitTimeout);
            (new GenieDisplayObject(genieID,app)).performKeyAction(key,500);
        } catch (Exception e) {
            throw new StepFailed("Error: pressing key " + key + " on GenieDisplayObject " + genieID + " failed!");
        }
    }


} // End Of GenieKeywords
