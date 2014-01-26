import org.robotframework.javalib.library.AnnotationLibrary;

public class GenieLibrary extends AnnotationLibrary {
    public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
    public static final String ROBOT_LIBRARY_VERSION = "1.0";
    
    public GenieLibrary() {
        super("org/robotframework/genielibrary/GenieKeywords.class");
    }
}
