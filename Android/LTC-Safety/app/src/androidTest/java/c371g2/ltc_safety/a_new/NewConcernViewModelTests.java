package c371g2.ltc_safety.a_new;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import c371g2.ltc_safety.R;

import static junit.framework.Assert.assertTrue;

/**
 * This class runs tests on the methods in the NewConcernViewModel class. Requires device to run.
 */
@RunWith(AndroidJUnit4.class)
public class NewConcernViewModelTests {

    @Rule
    public ActivityTestRule<NewConcernActivity> activityRule = new ActivityTestRule<>(NewConcernActivity.class);

    private NewConcernActivity activity;
    private NewConcernViewModel newConcernViewModel;

    @Before
    public void beforeEach() {
        activity = activityRule.launchActivity(new Intent());
        newConcernViewModel = new NewConcernViewModel(activity);
    }

    @After
    public void tearDown() {
        activity.finish();
    }

    @Test
    public void test_ConcernSubmission_allFields() throws InterruptedException{

        String concernType = activity.getResources().getStringArray(R.array.concern_types)[2];
        String actionsTaken = "Stopped wearing nerve-gear";
        String facultyName = activity.getResources().getStringArray(R.array.longtermcare_facilities)[2];
        String roomName = "b101";
        String reporterName = "All Fields";
        String emailAddress = "Kayaki@Aincrad.net";
        String phoneNumber = "3063063066";
        ReturnCode[] rCode = newConcernViewModel.submitConcern(concernType,actionsTaken,facultyName,roomName,reporterName,emailAddress,phoneNumber);

        // Check input return code
        assertTrue("Expecting VALID_INPUT return code but was "+rCode[0],ReturnCode.VALID_INPUT.equals(rCode[0]));

        // Wait for network thread to finish
        newConcernViewModel.signalLatch.await(20, TimeUnit.SECONDS);

        // Check submission return code
        assertTrue("Expecting SUCCESS return code but was "+newConcernViewModel.submissionReturnCode, ReturnCode.SUCCESS.equals(newConcernViewModel.submissionReturnCode));
    }

    @Test
    public void test_ConcernSubmission_NoEmail() throws InterruptedException {
        NewConcernViewModel newConcernViewModel = new NewConcernViewModel(activity);
        String concernType = activity.getResources().getStringArray(R.array.concern_types)[1];
        String actionsTaken = "Ate lots of pumpkin pie";
        String facultyName = activity.getResources().getStringArray(R.array.longtermcare_facilities)[1];
        String roomName = "b101";
        String reporterName = "No Email";
        String emailAddress = "";
        String phoneNumber = "3063063066";

        ReturnCode[] rCode = newConcernViewModel.submitConcern(concernType,actionsTaken,facultyName,roomName,reporterName,emailAddress,phoneNumber);

        // Check input return code
        assertTrue(ReturnCode.VALID_INPUT.equals(rCode[0]));

        // Wait for network thread to finish
        newConcernViewModel.signalLatch.await(20, TimeUnit.SECONDS);

        // Check submission return code
        assertTrue(ReturnCode.SUCCESS.equals(newConcernViewModel.submissionReturnCode));
    }

    @Test
    public void test_ConcernSubmission_NoPhone() throws InterruptedException {
        NewConcernViewModel newConcernViewModel = new NewConcernViewModel(activity);
        String concernType = activity.getResources().getStringArray(R.array.concern_types)[0];
        String actionsTaken = "Had a picnic";
        String facultyName = activity.getResources().getStringArray(R.array.longtermcare_facilities)[0];
        String roomName = "b101";
        String reporterName = "No Phone";
        String emailAddress = "IchiKuro@soulsociety.com";
        String phoneNumber = "";

        ReturnCode[] rCode = newConcernViewModel.submitConcern(concernType,actionsTaken,facultyName,roomName,reporterName,emailAddress,phoneNumber);

        // Check input return code
        assertTrue(ReturnCode.VALID_INPUT.equals(rCode[0]));

        // Wait for network thread to finish
        newConcernViewModel.signalLatch.await(20, TimeUnit.SECONDS);

        // Check submission return code
        assertTrue(ReturnCode.SUCCESS.equals(newConcernViewModel.submissionReturnCode));
    }

    @Test
    public void test_ConcernSubmission_noConcernType() {
        NewConcernViewModel newConcernViewModel = new NewConcernViewModel(activity);
        String concernType = "";
        String actionsTaken = "none";
        String facultyName = activity.getResources().getStringArray(R.array.longtermcare_facilities)[3];
        String roomName = "b101";
        String reporterName = "No LocalConcern Type";
        String emailAddress = "Kayaki@Aincrad.net";
        String phoneNumber = "3063063066";

        ReturnCode[] rCode = newConcernViewModel.submitConcern(concernType,actionsTaken,facultyName,roomName,reporterName,emailAddress,phoneNumber);
        assertTrue(ReturnCode.NO_CONCERN_TYPE.equals(rCode[0]));
    }

    @Test
    public void test_ConcernSubmission_noContactInfo() {
        NewConcernViewModel newConcernViewModel = new NewConcernViewModel(activity);
        String concernType = activity.getResources().getStringArray(R.array.concern_types)[4];
        String actionsTaken = "none";
        String facultyName = activity.getResources().getStringArray(R.array.longtermcare_facilities)[4];
        String roomName = "b101";
        String reporterName = "Kayaba Akihiko";
        String emailAddress = "";
        String phoneNumber = "";

        ReturnCode[] rCode = newConcernViewModel.submitConcern(concernType,actionsTaken,facultyName,roomName,reporterName,emailAddress,phoneNumber);
        assertTrue(ReturnCode.INVALID_PHONE_AND_EMAIL.equals(rCode[0]));
    }

    @Test
    public void test_ConcernSubmission_noName() {
        NewConcernViewModel newConcernViewModel = new NewConcernViewModel(activity);
        String concernType = activity.getResources().getStringArray(R.array.concern_types)[5];
        String actionsTaken = "none";
        String facultyName = activity.getResources().getStringArray(R.array.longtermcare_facilities)[5];
        String roomName = "b101";
        String reporterName = "";
        String emailAddress = "Kayaki@Aincrad.net";
        String phoneNumber = "3063063066";

        ReturnCode[] rCode = newConcernViewModel.submitConcern(concernType,actionsTaken,facultyName,roomName,reporterName,emailAddress,phoneNumber);
        assertTrue(ReturnCode.INVALID_NAME.equals(rCode[0]));
    }
}
