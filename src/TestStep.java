import io.appium.java_client.AppiumDriver;

import java.util.Map;
import java.util.function.Supplier;

public class TestStep {
    private final String description;
    private final Supplier<Boolean> action;
    private final boolean isCritical;

    public TestStep(String description, AppiumDriver driver, String AIComamnd, boolean isCritical) {
        this.description = description;
        this.action = () -> executeAINavigation(driver, AIComamnd, description);
        this.isCritical = isCritical;
    }

    // Constructor for custom steps (e.g., raw Selenium commands)
    public TestStep(String description, Supplier<Boolean> customAction, boolean isCritical) {
        this.description = description;
        this.isCritical = isCritical;
        this.action = customAction;
    }

    public boolean execute() {
        return action.get();
    }

    public boolean isCritical() {
        return isCritical;
    }

    public String getDescription() {
        return description;
    }

    public static Boolean executeAINavigation(AppiumDriver driver, String AICommand, String command){
        Boolean result;
        if (AICommand.equalsIgnoreCase("nav")) {
            result = (Boolean) driver.executeScript("perfecto:ai:user-action"
                    , Map.of("action"
                            , command));
        } else {
            result = (Boolean) driver.executeScript("perfecto:ai:validation"
                    , Map.of("validation",
                            command));
        }
        return result;
    }
}
