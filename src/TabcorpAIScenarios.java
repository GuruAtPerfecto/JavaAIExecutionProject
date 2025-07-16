import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class TabcorpAIScenarios {
    private AppiumDriver driver;
    ReportiumClient reportiumClient;
    private static String PERFECTO_SECURITY_TOKEN = "<<SECURITY_TOKEN>>";

    @BeforeClass
    public void setUp() throws Exception {
        System.out.println("Run started");

        String browserName = "";
        DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
        String host = "tabcorp.perfectomobile.com";

        Map<String, Object> cloudOptions = new HashMap<>();
        cloudOptions.put("securityToken", PERFECTO_SECURITY_TOKEN);
        cloudOptions.put("appiumVersion", "latest");
        cloudOptions.put("enableAppiumBehavior", true);
        cloudOptions.put("platformName", "iOS");
        cloudOptions.put("app", "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        cloudOptions.put("iOSResign",true);
        cloudOptions.put("fullReset", true);
        cloudOptions.put("bundleId", "au.com.tabcorp.flutterTab.rc");
        cloudOptions.put("autoLaunch", "true");
        capabilities.setCapability("perfecto:options", cloudOptions); // Mandatory

        // Use the automationName capability to define the required framework - Appium (this is the default) or PerfectoMobile.
        capabilities.setCapability("automationName", "Appium");

        driver = new AppiumDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
        // IOSDriver driver = new IOSDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        // Reporting client. For more details, see http://developers.perfectomobile.com/display/PD/Reporting
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withProject(new Project("My Project", "1.0"))
                .withJob(new Job("TABCORP_AI_POC_SMOKE", 5))
                .withContextTags("tag1")
                .withWebDriver(driver)
                .build();
       reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
    }

    @Test(priority = 1)
    public void LogOutIfAlreadyLogin() {
        //Testcase 1
        Boolean result = true;
        reportiumClient.testStart("Logout_Application_if_already_logged_In_and_chose_UAT1_env", new TestContext("tag2", "tag3"));
        List<TestStep> steps = Arrays.asList(
                new TestStep("launch TAB RC app and accept all permission pop up. Stop after clicking \"Share My Location\" button", driver, "nav", true),
                //handle Appium click in between
                new TestStep("Click on Location popup", () -> {
                    try {
                        driver.findElement(By.xpath("//*[@label=\"Allow While Using App\"]")).click();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, false),  // You can mark it as critical if needed
                new TestStep("Proceed with accepting anymore popups. If you are on the app, proceed till you reach the page with login option. If you see \"Whats New\" banner then close it.", driver, "nav", true),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("Scroll down to Developer option, click on it and click on the environment UAT1", driver, "nav", true),
                new TestStep("Launch TAB RC app.  If you see \"Whats New\" banner then close it.", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 2)
    public void testDepositLimitUpdate() {
        //Testcase 2
        Boolean result = true;
        reportiumClient.testStart("Verify_Deposit_limit_update_DQA-3348", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) { e.printStackTrace(); return false;}
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1501134/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Responsible gambling button", driver, "nav", true),
                new TestStep("click on Deposit limit button", driver, "nav", true),
                new TestStep("Is this the Deposit Limit screen?", driver, "val", true),
                new TestStep("click on edit option", driver, "nav", false),
                //new TestStep("long press on the amount field then click on select all and then perform click on \"cut\"", driver, "nav", true),
                new TestStep("set the amount value of 9999991 in limit amount field", driver, "nav", true),
                new TestStep("click on update Deposit limit button", driver, "nav", true),
                new TestStep("Is the error message displayed equal to \"Please enter a valid amount.\"?", driver, "val", true),
                new TestStep("Click on Cancel button", driver, "nav", true),
                new TestStep("click on edit button", driver, "nav", true),
                new TestStep("capture the current value of amount in ${currentValue}", driver, "nav", false),
                //new TestStep("long press on the amount field then click on select all and then perform click on \"cut\"", driver, "nav", true),
                new TestStep("Add 1 to 99999 and result should be entered to amount field", driver, "nav", true),
                new TestStep("click on update Deposit limit button", driver, "nav", true),
                new TestStep("Is the deposit limit confirmation banner displayed showing a limit of $100000 for days?", driver, "val", true),
                new TestStep("click on confirm button", driver, "nav", true),
                new TestStep("Verify the confirmation message", driver, "val", true),
                new TestStep("click on edit option", driver, "nav", true),
                //new TestStep("long press on the amount field then click on select all and then perform click on \"cut\"", driver, "nav", true),
                new TestStep("Add the amount as 99998", driver, "nav", true),
                new TestStep("click on update Deposit limit button", driver, "nav", true),
                new TestStep("click on confirm button", driver, "nav", true),
                new TestStep("click on remove button", driver, "nav", true),
                //new TestStep("click on remove button", driver, "nav", false),
                new TestStep("Navigate back to account screen and scroll down to bottom of screen and perform logout. Use larger swipe to speed up the process", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 3)
    public void testCreateTeamsforBestFriends() {
        Boolean result = true;
        //Testcase 3
        reportiumClient.testStart("Validate_Create_Team_for_bets_friends_DQA-1747", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) { e.printStackTrace(); return false;}
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1425499/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Bets Friends", driver, "nav", true),
                new TestStep("click on Teams", driver, "nav", true),
                new TestStep("click on create a team", driver, "nav", true),
                new TestStep("create a team name as \"Dummyy\" and submit on Create Team button", driver, "nav", false),
                new TestStep("copy the Team code by clicking on clipboard", driver, "nav", true),
                new TestStep("Is the success message displayed as \"Your Team has been created\"?", driver, "val", true),
                new TestStep("close the dialog option", driver, "nav", true),
                new TestStep("Navigate back until you reach accounts screen", driver, "nav", true),
                new TestStep("scroll down to bottom of screen and perform logout", driver, "nav", true),
                new TestStep("click on login button", driver, "nav", true),
                new TestStep("click on user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02", driver, "nav", true),
                new TestStep("Close the Dialog box", driver, "nav", true),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("click on Bets Friends", driver, "nav", true),
                new TestStep("click on Teams", driver, "nav", true),
                new TestStep("click on Join Team", driver, "nav", true),
                new TestStep("Long Press on Team code edit field and click on paste option and stop", driver, "nav", true),
                new TestStep("click on Join button", driver, "nav", true),
                new TestStep("click on Join button", driver, "nav", false),
                new TestStep("Is the number of members in the Demo team equal to 2?", driver, "val", true),
                new TestStep("Navigate back until you reach accounts screen", driver, "nav", true),
                new TestStep("scroll down to bottom of screen and perform logout. Use larger swipe to speed up the process", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 4)
    public void testVerifyPayPalDepositFromBetSlip() {
        Boolean result = true;
        //Testcase 4
        reportiumClient.testStart("Verify_paypal_deposit_from_Betslip_DQA-2955", new TestContext("tag2", "tag3"));
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");

        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) { e.printStackTrace(); return false;}
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on the \"betSlipButton\" button in Global Header", driver, "nav", true),
                new TestStep("click on the \"depositButton\" button in Betslip Header", driver, "nav", true),
                new TestStep("Is the Deposit and Play screen displayed?", driver, "nav", true),
                new TestStep("Enter the minimum  deposit amount as $1.6 and click on Pay button", driver, "nav", true),
                new TestStep("Is the error message displayed exactly \"Minimum deposit of AUD $2.00\"?", driver, "nav", true),
                new TestStep("Clear the \"Deposit Amount\" text field by using Keyboard delete key. Make sure text field is fully cleared.", driver, "nav", true),
                new TestStep("Clear the \"Deposit Amount\" text field by using Keyboard delete key. Make sure text field is fully cleared.", driver, "nav", false),
                new TestStep("Enter the new amount as $2 and click on Pay button", driver, "nav", true),
                new TestStep("Navigate back and click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("scroll down to transactionsAndStatements and perform click operation on it", driver, "nav", true),
                new TestStep("Was the latest transaction a $2.00 credit via PayPal?", driver, "nav", true),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("scroll down to bottom of screen and perform logout. Use larger swipe to speed up the process", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }

    }


    @Test(priority = 5)
    public void testVerifyPayPalDepositFlowForAnExistingUser() {
        Boolean result = true;
        //Testcase 5
        reportiumClient.testStart("Verify_PayPal_Deposit_Flow_for_an_existing_user_for_Main_Deposit_Page_DQA-2316", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) { e.printStackTrace(); return false;}
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on the \"deposit\" button in Global Header. Scroll up if not button is not visible", driver, "nav", true),
                new TestStep("skip the PayID tutorial", driver, "nav", true),
                new TestStep("Click \"Skip\" button if avaialable", driver, "nav", false),
                new TestStep("click on the \"payPal\" button in Deposit section", driver, "nav", true),
                new TestStep("Is the PayPal account associated with yarra100@yopmail.com?", driver, "val", true),
                new TestStep("Enter the minimum deposit amount as $1.6 and click on Pay button", driver, "nav", true),
                new TestStep("Is the error message displayed exactly \"Minimum deposit of AUD $2.00\"?", driver, "nav", true),
                new TestStep("Clear the \"Deposit Amount\" text field by using Keyboard delete key. Make sure text field is fully cleared.", driver, "nav", true),
                new TestStep("Clear the \"Deposit Amount\" text field by using Keyboard delete key. Make sure text field is fully cleared.", driver, "nav", false),
                new TestStep("Enter the new amount as $2 and click on Pay button", driver, "nav", true),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("scroll down to transactionsAndStatements and perform click operation on it", driver, "nav", true),
                new TestStep("Was the latest transaction a $2.00 credit via PayPal?", driver, "nav", true),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("scroll down to bottom of screen and perform logout. Use larger swipe to speed up the process", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }

    }

    @Test(priority = 6)
    public void testAddCardDepositAndVerifyInTransactions() {
        Boolean result = true;
        //Testcase 6
        reportiumClient.testStart("Add_Card_deposit_and_verify_in_Transactions_DQA-2320", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) { e.printStackTrace(); return false;}
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500826/payment01 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("Click on Deposit button on top of the page", driver, "nav", true),
                new TestStep("click on Debit card item under Deposit list", driver, "nav", true),
                new TestStep("Remove the card ending with \"2346\" if present", driver, "nav", false),
                new TestStep("add credit card number as:5123456789012346", driver, "nav", true),
                new TestStep("add card expiry details as: MM to 01 and YY as 39 and CVV as 100", driver, "nav", false),
                new TestStep("enter the name on card as App tester", driver, "nav", true),
                new TestStep("enter the deposit amount as 10", driver, "nav", true),
                new TestStep("click on confirm button. Remove the keyboard if it opened. Verify successful card addition and user User verifies the Toast Message on successful deposit", driver, "nav", true),
                new TestStep("User taps on \"Account\" tab", driver, "nav", true),
                new TestStep("scroll down to Transaction and Statements under More Section", driver, "nav", true),
                new TestStep("Is the screen title \"Transactions & Statements\"?", driver, "val", true),
                new TestStep("Is the latest transaction showing Credit of $10.00 using Deposit type as Card?", driver, "val", true),
                new TestStep("Navigate back and scroll down to bottom of screen and perform logout. Use larger swipe to speed up the process", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }

    }

    @Test(priority = 7)
    public void testEFTWithdrawalUsingAnExistingBankAccountInTheList() {
        Boolean result = true;
        //Testcase 7
        reportiumClient.testStart("EFT Withdrawal using an existing Bank Account in the list DQA-391", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) { e.printStackTrace(); return false;}
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("Scroll to top of screen and click on the withdrawMenu button", driver, "nav", true),
                new TestStep("click on the skip button on Withdraw tutorial if exists", driver, "nav", true),
                new TestStep("click on the Withdrawal amount field", driver, "nav", true),
                new TestStep("enter $0 in Withdrawal Amount field", driver, "nav", false),
                new TestStep("close the keyboard", driver, "nav", true),
                new TestStep("Is the error message \"Please enter a withdrawal amount\" displayed on the screen?", driver, "val", true),
                new TestStep("close the cross icon button in withdrawal field", driver, "nav", true),
                new TestStep("enter $1 in Withdrawal Amount field", driver, "nav", true),
                new TestStep("close the keyboard", driver, "nav", true),
                new TestStep("Scroll above the bottom navigation bar to see if there's more content that contains the \"Withdraw Now\" button and click on it", driver, "nav", true),
                new TestStep("click on \"Withdraw\" button", driver, "nav", true),
                new TestStep("$1.00 withdrawal successful", driver, "nav", true),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("scroll down to Transaction and Statements under More Section", driver, "nav", true),
                new TestStep("click on Transaction and Statements", driver, "nav", true),
                new TestStep("Is the screen title \"Transactions & Statements\"?", driver, "nav", true),
                new TestStep("Is the current transaction showing a debit of $1.00 using EFT?", driver, "nav", true),
                new TestStep("Navigate back to Accounts screen and scroll down to bottom of screen and perform logout. Use larger swipe to speed up the process", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }

    }

    @Test(priority = 8)
    public void testWinPlaceToteBetAndVerifyOnMyBets() {
        Boolean result = true;
        //Testcase 8
        reportiumClient.testStart("Win_Place_Tote_Bet_and_verify_on_My_Bets_DQA-993", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("Users Taps on \"Today\" carousel in Racing Tab and select the race that is going to jump after 10 mins or more", driver, "nav", true),
                new TestStep("Choose the race that is going to jump after 10 mins or more. Stop if race is already chosen and if you see \"tote\" and \"fixed\" odds. Manage the scroll to find the race.", driver, "nav", true),
                new TestStep("Click on the Odds Button under Tote column which is right of \"fixed\" column. Don't chose from \"Fixed\"", driver, "nav", true),
                new TestStep("Place bet with '2' as Tote Win and '3' as Tote Place and proceed. If you see confirm bet button click it too", driver, "nav", true),
                new TestStep("Is a check mark shown next to Bet placed text on a green banner?", driver, "val", true),
                new TestStep("Click on Done Button to finish the process and display the Bet details", driver, "nav", true),
                new TestStep("User clicks on \"My Bets\" and User should see Pending tab selected", driver, "nav", true),
                new TestStep("User taps on 'showDetails' button on last Bet placed from top-most bet", driver, "nav", true),
                new TestStep("User should able to see following element on My Bets page: labelTSN, labelBetDate, labelEventDate and Property field", driver, "nav", true),
                new TestStep("User verify Total Stake Amount in the latest topmost bet is sum of '2' and '3'", driver, "val", true),
                new TestStep("Click on Account option and scroll to the bottom of the screen to logout from the app", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 9)
    public void testVerifyResultsTabAndFormDQA() {
        Boolean result = true;
        //Testcase 9
        reportiumClient.testStart("Verify_Results_Tab_and_Form_DQA-1086", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("User navigates to Racing Home page", driver, "nav", true),
                new TestStep("User taps on 'Results' carousel", driver, "nav", true),
                new TestStep("User taps on first race", driver, "nav", true),
                new TestStep("User taps on first meeting", driver, "nav", true),
                new TestStep("Is the user currently on the Placings tab?", driver, "val", true),
                new TestStep("Are the first, second and third place winners displayed in the race results?", driver, "val", true),
                new TestStep("Are the Fixed and Tote odds labels visible on the screen?", driver, "val", true),
                new TestStep("Are the Winners Placings odds visible for the horses in the race?", driver, "val", true),
                new TestStep("Are the runner details displayed under Placings showing horse names, barrier numbers, jockey names, weights, trainers and form information?", driver, "val", true),
                new TestStep("Is the Add to Blackbook button displayed on the screen?", driver, "nav", true),
                new TestStep("User taps on 'firstWinner' button", driver, "nav", true),
                new TestStep("Are the odds tables displayed above the \"Add to Blackbook\" disabled and not clickable?", driver, "nav", true),
                new TestStep("Navigate back to placing tab", driver, "nav", true),
                new TestStep("Is the Exotic Dividends tab present in the navigation menu?", driver, "val", true),
                new TestStep("Is the Deductions tab present? find the tab, it may hidden in the screen", driver, "nav", true),
                new TestStep("Is the Substitutes tab present? find the tab, it may hidden in the screen", driver, "nav", true),
                new TestStep("User taps on the Sky Live TV button", driver, "nav", true),
                new TestStep("Are the Feature replays section and Racing/Sports tabs available on the screen?", driver, "val", true),
                new TestStep("Click on Account option and scroll down to bottom of screen to logout. Use larger swipe to speed up the process", driver, "nav", true)
                );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 10)
    public void testWinPlaceFixedOddsBetFromPreBetPSlip() {
        Boolean result = true;
        //Testcase 10
        reportiumClient.testStart("Win_Place_Fixed_odds_bet_from_Pre_BetP_Slip_DQA-980", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("Users Taps on \"Today\" carousel in Racing Tab", driver, "nav", true),
                new TestStep("Choose the race that is going to jump after 10 mins or more. Stop if race is already chosen. Manage the scroll to find the race. If Odd shown for 'Fixed' column is 'N/A', go back to Racing page and chose the new race going back all the way to \"Racing\" page", driver, "nav", true),
                new TestStep("Place bet with '3' as fixed Win and '4' as fixed Place and proceed. If you see confirm bet button click it too", driver, "nav", true),
                new TestStep("Is a check mark shown next to Bet placed text on a green banner", driver, "val", true),
                new TestStep("Click on Done Button to finish the process and display the Bet details", driver, "nav", true),
                new TestStep("User clicks on \"My Bets\" and User should see Pending tab selected", driver, "nav", true),
                new TestStep("User taps on 'showDetails' button on last Bet placed from top-most bet", driver, "nav", true),
                new TestStep("User should able to see following element on My Bets page: labelTSN, labelBetDate, labelEventDate and Property field", driver, "nav", true),
                new TestStep("User verify Total Stake Amount as sum of '3' and '4'", driver, "nav", true),
                new TestStep("Click on Account option and scroll to the bottom of the screen to logout from the app", driver, "nav", true)

                );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 11)
    public void testVenueHubFeaturesWithVMOFF() {
        Boolean result = true;
        //Testcase 11
        reportiumClient.testStart("VenueHub_features_with_VM_OFF_DQA-2155", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Set lcoation on the device", () -> {
                    try {
                        setCoordinates(driver, "-37.820075, 144.9499219");
                        CloseApp(driver, "com.apple.Preferences");
                        StartApp(driver, "com.apple.Preferences");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Goto top of the screen to locate search bar and search for Tab RC. Set location permission as \"Allow while using app\" and disable \"Precise Tracking\" if it enabled.  Then press the home button of the device", driver, "nav", true),
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("Click \"Venue\" option", driver, "nav", true),
                new TestStep("Click on \"Turn on\" button", driver, "nav", true),
                new TestStep("Enable \"Precise Tracking\" in the Location services.", driver, "nav", true),
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Click on Accounts tab located at bottom navigation bar", driver, "nav", true),
                new TestStep("navigates to the \"VenueHub\" section from the Account page", driver, "nav", true),
                new TestStep("Are the elements Exclusive Deals, Exclusive Markets, Venue Promotions, and TAB Digital Card visible on the VenueHub page?", driver, "val", true),
                new TestStep("User click on \"VenueModeBenefits\" \"link\" on \"VenueHub\" page ", driver, "nav", true),
                new TestStep("User click on \"FindVenue\" \"link\" on \"VenueModeBenefits\" page then Then the application should navigate the user to the \"TabLocatorPage\"", driver, "nav", true),
                new TestStep("User navigate back to venue hub page", driver, "nav", true),
                new TestStep("User click on \"VenuePromotions\" link on Venue Hub page", driver, "nav", true),
                new TestStep("Are the PromotionsPage, AllChip and RacingChip elements visible on the Promotions page?", driver, "val", true),
                new TestStep("User navigate back to venue hub page", driver, "nav", true),
                new TestStep("User click on \"TabLocator\" link on Venue Hub page", driver, "nav", true),
                new TestStep(" User should able to see following element on \"TabLocator\" page.  TabLocatorPage, Map, List and  TABATMLocationsChip", driver, "val", true),
                new TestStep("User click on \"List\" \"Tab\" on \"TabLocator\" page", driver, "nav", true),
                new TestStep("User should able to see following element on \"List\" page. TABLocationsTitle, VenueNameOnList", driver, "val", true),
                new TestStep("User click on \"ViewOnMap\" \"link\" on \"List\" page. Remember the respective locations you clicked. verify the locations you remembered in the map. Stop if partial matching is available", driver, "nav", true),
                new TestStep("User navigate back to venue hub page", driver, "nav", true),
                new TestStep("User click on \"TABDigitalCard\" link on Venue Hub page", driver, "nav", true),
                new TestStep("login with 1500820/password02 and User should see his account number in the Digital Card page", driver, "nav", true),
                new TestStep("Close the Digital card page", driver, "nav", true),
                new TestStep("User click on \"CheckAndCollect\" \"link\" on \"VenueHub\" page. The application should navigate the user to the \"Check and Collect\" page. Just check the heading", driver, "nav", true),
                new TestStep("User navigate back to venue hub page", driver, "nav", true),
                new TestStep("click on Account Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("scroll down to bottom of screen and perform logout. Use larger swipe to speed up the process", driver, "nav", true)
                );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 12)
    public void testToteBetPlacementFromTomorrowAndVerifyOnMyBets() {
        Boolean result = true;
        //Testcase 12
        reportiumClient.testStart("Tote_Bet_Placement_from_Tomorrow_and_verify_on_My_Bets_DQA-5132", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("Users Taps on \"Racing\" tab", driver, "nav", true),
                new TestStep("Users Taps on \"Tomorrow\" carousel ", driver, "nav", true),
                new TestStep("Choose any race. Manage the scroll to find the race. If \"Tote\" Odd not available, go back to all the way to \"Racing\" page to choose the new race, without further looking in the current page. Ignore Rounds if \"Tote\" not avaialble in the first round", driver, "nav", true),
                new TestStep("Click on the Odds Button under Tote column which is right of \"fixed\" column. Don't chose from \"Fixed\"", driver, "nav", true),
                new TestStep("Place bet with '4' as Tote Win and '5' as Tote Place and proceed. If you see confirm bet button click it too", driver, "nav", true),
                new TestStep("Is a check mark shown next to Bet placed text on a green banner", driver, "val", true),
                new TestStep("Click on Done Button to finish the process and display the Bet details", driver, "nav", true),
                new TestStep("User clicks on \"My Bets\" and User should see Pending tab selected", driver, "nav", true),
                new TestStep("User taps on 'showDetails' button on last Bet placed from top-most bet", driver, "nav", true),
                new TestStep("User should able to see following element on My Bets page: labelTSN, labelBetDate, labelEventDate and Property field", driver, "nav", true),
                new TestStep("User verify Total Stake Amount as sum of ${tote_win} and ${tote_place}", driver, "nav", true),
                new TestStep("Click on Account option and scroll to the bottom of the screen to logout from the app", driver, "nav", true)
                );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 13)
    public void testMultiplierAndBonusBetOnBetSlip() {
        Boolean result = true;
        //Testcase 13
        reportiumClient.testStart("Multiplier_and_Bonus_Bet_on_Bet_Slip_DQA-4946", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1501998/tabcorp01 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("Users Taps on \"Racing\" tab", driver, "nav", true),
                new TestStep("Users Taps on \"Today\" carousel ", driver, "nav", true),
                new TestStep("Choose the race that is going to jump after 10 mins or more. Stop if race is already chosen. Manage the scroll to find the race. If Odd shown for 'Fixed' column is 'N/A', go back to Racing page and chose the new race going back all the way to \"Racing\" page", driver, "nav", true),
                new TestStep("Place bet with '4' as fixed Win and '5' as fixed Place and click \"Add to Beet Slip\" button", driver, "nav", true),
                new TestStep("User taps on Bet Slip button on the header And User should navigate to Bet Slip page", driver, "nav", true),
                new TestStep("Is the Fixed Win amount equal to '4' and Fixed Place amount equal to '9' in the bet slip?", driver, "val", true),
                new TestStep("User clicks on Multipliers promotion icon then User should see Bonus Bet promotion icon as disabled", driver, "nav", true),
                new TestStep("User clicks on Place Bet button with multipliers promotion", driver, "nav", true),
                new TestStep("User clicks on EditBet button from Bet Slip screen", driver, "nav", true),
                new TestStep("User removes Multipliers promotion from that bet", driver, "nav", true),
                new TestStep("User clicks on Bonus Bet promotion icon", driver, "nav", true),
                new TestStep("User clicks on Done button from Bonus Bet bottom sheet", driver, "nav", true),
                new TestStep("Verify the \"multipliers\" icon is in a disabled state", driver, "nav", true),
                new TestStep("Click on \"Clear Bet Slip\" button", driver, "nav", true),
                new TestStep("Click on Account option and scroll down to bottom of screen to logout. Use larger swipe to speed up the process", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 14)
    public void testBoxedTrifectaBetWithField() {
        Boolean result = true;
        //Testcase 14
        reportiumClient.testStart("Boxed_Trifecta_Bet_with_Field_DQA-1015", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1501998/tabcorp01 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("Users Taps on \"Racing\" tab", driver, "nav", true),
                new TestStep("Users Taps on \"Today\" carousel ", driver, "nav", true),
                new TestStep("Choose the race that is going to jump after 10 mins or more. Manage the scroll to find the race. ", driver, "nav", true),
                new TestStep("User Click on \"Trifecta\" option", driver, "nav", true),
                new TestStep("User selects First, second, or third option for 'First runner'", driver, "nav", true),
                new TestStep("User selects First, second, or third option for 'Second runner", driver, "nav", true),
                new TestStep("User taps on 'Bet Now ' button and Verify the toast  message indicating '\"Select at least 3 runners for the bet\" ", driver, "nav", true),
                new TestStep("User selects First, second, or third option for 'Third runner'", driver, "nav", true),
                new TestStep("User taps on 'Bet Now ' button", driver, "nav", true),
                new TestStep("User enters bet amount as 3", driver, "nav", true),
                new TestStep("User taps on 'placeBet' button and Confirm bet screen should be launched", driver, "nav", true),
                new TestStep("User taps on 'confirm Bet' button", driver, "nav", true),
                new TestStep("User taps on \"Done\" button", driver, "nav", true),
                new TestStep("User clicks on \"My Bets\" on the header and Then User should navigate to My Bets page", driver, "nav", true),
                new TestStep("User taps on 'ShowSelectionAndDetails' button on top most bet and verify the presense of elements labelTSN, labelBetDate and labelEventDate. Scroll down if all elements are not visible", driver, "nav", true),
                new TestStep("Is the TSN is in digits, BetDate showing proper date format, and EventDate matching today's date", driver, "val", true),
                new TestStep("Click on Account option and scroll down to bottom of screen to logout. Use larger swipe to speed up the process", driver, "nav", true)
                );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 15)
    public void testVerificationOfAppFooter() {
        Boolean result = true;
        //Testcase 15
        reportiumClient.testStart("Verification_of_app_footer_DQA-712", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("scroll the top bar to the Left direction until you see \"Racing\" option", driver, "nav", true),
                new TestStep("Scroll to the bottom of the page", driver, "nav", true),
                new TestStep("click on Safe Gambling", driver, "nav", true),
                new TestStep("Is the current screen showing Safer Gambling content?", driver, "val", true),
                new TestStep("Navigate Back", driver, "nav", true),
                new TestStep("Click on Deposit Limits", driver, "nav", true),
                new TestStep("Is the screen title \"Deposit Limits\"?", driver, "val", true),
                new TestStep("Navigate Back", driver, "nav", true),
                new TestStep("Click on Close Account", driver, "nav", true),
                new TestStep("Navigate Back", driver, "nav", true),
                new TestStep("click on SA code of Practice Button", driver, "nav", true),
                new TestStep("Navigate Back", driver, "nav", true),
                new TestStep("click on ACT code of Practice Button", driver, "nav", true),
                new TestStep("Is the screen title \"ACT Code of Practice\"?", driver, "val", true),
                new TestStep("Navigate Back", driver, "nav", true),
                new TestStep("Click on Betting Rules Button", driver, "nav", true),
                new TestStep("Is the current screen titled \"Betting Rules\"?", driver, "val", true),
                new TestStep("Navigate Back", driver, "nav", true),
                new TestStep("Click on \"terms of Rules\" button", driver, "nav", true),
                new TestStep("Is the current screen titled \"Terms of Use\"?", driver, "val", true),
                new TestStep("Navigate Back", driver, "nav", true),
                new TestStep("Click on Tabcorp Holdings limited button", driver, "nav", true),
                new TestStep("Is this the Tabcorp Holdings Limited screen?", driver, "val", true),
                new TestStep("Navigate Back", driver, "nav", true),
                new TestStep("click on \"Tabcorp code of conduct\" link", driver, "nav", false),
                new TestStep("Is the screen displaying the Tabcorp Code of Conduct?", driver, "val", false),
                new TestStep("Navigate Back", driver, "nav", false)
                );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 16)
    public void testVerifySpendLimitFeatureEditAndIncrease() {
        Boolean result = true;
        //Testcase 16
        reportiumClient.testStart("Verify_Spend_limit_feature_edit_and_increase_DQA-3349", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1501998/tabcorp01 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("Click on Accounts tab located at bottom navigation bar", driver, "nav", true),
                new TestStep("click on Responsible gambling button", driver, "nav", true),
                new TestStep("User clicks on the \"spendLimit\" button", driver, "nav", true),
                new TestStep("Is this the Spend Limit screen?", driver, "val", true),
                new TestStep("set the amount value of 9999991 in limit amount field", driver, "nav", true),
                new TestStep("Is the error message displayed equal to \"Please enter a valid amount.\"?", driver, "val", false),
                new TestStep("Navigate Back", driver, "nav", false),
                new TestStep("User clicks on the \"spendLimit\" button", driver, "nav", false),
                new TestStep("Add 1 to 99999 and result should be entered to amount field", driver, "nav", true),
                new TestStep("User clicks on the \"Update spendLimit\" button", driver, "nav", false),
                new TestStep("Is the confirm spend limit message displayed?", driver, "val", false),
                new TestStep("Click on Confirm button", driver, "nav", false),
                new TestStep("Is the spend limit increase notification banner displayed at the top of the screen?", driver, "val", true),
                new TestStep("Navigate Back to Account option and scroll down to bottom of screen to logout. Use larger swipe to speed up the process", driver, "nav", true)

                );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }
    }

    @Test(priority = 17)
    public void testQuickDepositWithCardFromBetSlipDuringBetPlacement() {
        Boolean result = true;
        //Testcase 17
        reportiumClient.testStart("Quick_Deposit_with_card_from_betSlip_during_bet_placement_DQA-173", new TestContext("tag2", "tag3"));
        result = true;
//            try {
//                UninstallApplication(driver, "au.com.tabcorp.flutterTab.rc");
//            } catch (Exception e) {
//            }
//            InstallApplication(driver, "GROUP:MobileApp/Native/ios/TABFlutter-release/candidate/1.28.0.ipa");
        List<TestStep> steps = Arrays.asList(
                new TestStep("Start Application", () -> {
                    try {
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        CloseApp(driver, "au.com.tabcorp.flutterTab.rc");
                        StartApp(driver, "au.com.tabcorp.flutterTab.rc");
                        return true;
                    } catch (Exception e) { e.printStackTrace(); return false;}
                }, true),  // You can mark it as critical if needed
                new TestStep("Stop if you see \"Log In\" button.  If not available log out the application by going in the \"Account\" section, go to bottom of the page to \"Logout\". Use larger swipe to speed up the process.", driver, "nav", false),
                new TestStep("click on Log in button and clear the user name field if it is not empty, use the delete button on keypad", driver, "nav", false),
                new TestStep("Login to the application with credentials,1500820/password02 and \"No, Thank you\" button from PIN setup dialog if appears", driver, "nav", true),
                new TestStep("clicks on the \"Got It\" button if present", driver, "nav", false),
                new TestStep("click on Sports Button on the bottom navigation bar", driver, "nav", true),
                new TestStep("Is this the Sports landing page?", driver, "val", true),
                new TestStep("taps on item 4 under featured competition", driver, "nav", true),
                new TestStep("Tap any random competition", driver, "nav", true),
                new TestStep("Select any random option under line section", driver, "nav", false),
                new TestStep("Tap on betslip icon on the top header", driver, "nav", true),
                new TestStep("Enter the stake value as $60000 and place the Bet", driver, "nav", true),
                new TestStep("Is the error message \"Insufficient Funds. Adjust your stake or make a deposit to place your bet.\" displayed on screen?", driver, "val", true),
                new TestStep("Click on Change payment mode option at bottom of screen", driver, "nav", true),
                new TestStep("Tap on Add a Debit card option", driver, "nav", true),
                new TestStep("Are the card number, expiry date, and CVV input fields displayed on the debit card form?", driver, "val", false),
                new TestStep("close the dialog box and navigate back until you reach account section screen", driver, "nav", true),
                new TestStep("scroll down to bottom of screen and perform logout. Use larger swipe to speed up the process ", driver, "nav", true)
        );

        if (result) {
            executeStepsWithOptionalFailures(reportiumClient, steps);
        }

    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void StartApp(AppiumDriver driver, String appName) {
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", appName);
        driver.executeScript("mobile:application:open", params);
    }

    private void CloseApp(AppiumDriver driver, String appName) {
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", appName);
        driver.executeScript("mobile:application:close", params);
    }

    private void setCoordinates(AppiumDriver driver, String coordinates){
        Map<String, Object> params = new HashMap<>();
        params.put("coordinates", coordinates);
        driver.executeScript("mobile:location:set", params);
    }

    public static boolean executeStepsWithOptionalFailures(ReportiumClient reportiumClient, List<TestStep> steps) {
        for (TestStep step : steps) {
            boolean stepResult = step.execute();

            if (!stepResult && step.isCritical()) {
                reportiumClient.testStop(TestResultFactory.createFailure("Test is failed at: \" " +  step.getDescription() + " \" Step. Please Check", null));
                return false;
            }
        }
        reportiumClient.testStop(TestResultFactory.createSuccess());
        return true;
    }
}
