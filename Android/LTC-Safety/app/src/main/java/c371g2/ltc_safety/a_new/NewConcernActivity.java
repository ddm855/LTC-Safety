package c371g2.ltc_safety.a_new;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import c371g2.ltc_safety.R;

/**
 * This activity displays a form with various fields for specifying a safety concern. A concern with
 * sufficient details can be submitted to the database.
 * Activity: ~ View-Controller
 * @Invariants none
 * @HistoryProperties none
 */
public class NewConcernActivity extends AppCompatActivity {

    Button submitConcernButton;

    EditText nameField;
    EditText phoneNumberField;
    EditText emailAddressField;
    EditText actionsTakenField;

    Spinner concernNatureSpinner;
    Spinner facilitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_concern);
        //getActionBar().setHomeAsUpIndicator(R.drawable.ic); // TODO: Change up-nav to "cancel"

        nameField = (EditText) findViewById(R.id.nameTextField);
        assert(nameField!=null);
        phoneNumberField = (EditText) findViewById(R.id.phoneNumberField);
        assert(phoneNumberField!=null);
        emailAddressField = (EditText) findViewById(R.id.emailTextField);
        assert(emailAddressField!=null);
        actionsTakenField = (EditText) findViewById(R.id.actionsTakenTextField);
        assert(actionsTakenField!=null);

        submitConcernButton = (Button) findViewById(R.id.submit_concern_button);
        submitConcernButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitConcernButtonAction();
            }
        });

        concernNatureSpinner = (Spinner) findViewById(R.id.concernTypeSpinner);
        assert(concernNatureSpinner!=null);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.concern_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        concernNatureSpinner.setAdapter(adapter);
        assert(concernNatureSpinner.getAdapter()!=null);

        facilitySpinner = (Spinner) findViewById(R.id.facilitySpinner);
        assert(facilitySpinner!=null);
        adapter = ArrayAdapter.createFromResource(this, R.array.longtermcare_facilities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facilitySpinner.setAdapter(adapter);
        assert(facilitySpinner.getAdapter()!=null);
    }



    private void submitConcernButtonAction() {
        submitConcernButton.setEnabled(false);
        NewConcernViewModel newConcernViewModel = new NewConcernViewModel(NewConcernActivity.this);

        ReturnCode response = newConcernViewModel.submitConcern(
                concernNatureSpinner.getSelectedItem().toString(),
                actionsTakenField.getText().toString(),
                facilitySpinner.getSelectedItem().toString(),
                nameField.getText().toString(),
                emailAddressField.getText().toString(),
                phoneNumberField.getText().toString()
        );

        if(response!=ReturnCode.SUCCESS) {

            switch (response.id) {
                case 1: // No Concern type
                    //Toast.makeText(getBaseContext(), "Choose a concern type", Toast.LENGTH_SHORT).show();
                    displayInfoDialogue(null,"Please choose a concern type");
                    break;
                case 2: // No Facility
                    //Toast.makeText(getBaseContext(), "Choose a facility / location", Toast.LENGTH_SHORT).show();
                    displayInfoDialogue(null,"Please choose a facility / location");
                    break;
                case 3: // No name
                    //Toast.makeText(getBaseContext(), "Provide name", Toast.LENGTH_SHORT).show();
                    displayInfoDialogue(null,"Please provide your name");
                    break;
                case 4: // No contact info
                    //Toast.makeText(getBaseContext(), "Provide a contact method", Toast.LENGTH_SHORT).show();
                    displayInfoDialogue(null,"Please provide a contact method");
                    break;
            }

            submitConcernButton.setEnabled(true);
        }
    }

    /**
     * Display a popup with the given title and message
     * @param title Title of the popup
     * @param message Text in the popup
     */
    void displayInfoDialogue(String title, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NewConcernActivity.this);
        alertBuilder.setCancelable(true);

        if(title != null) {
            alertBuilder.setTitle(title);
        }
        if(message != null) {
            alertBuilder.setMessage(message);
        }
        alertBuilder.create().show();
    }

    private void exitActivity() {}
}
