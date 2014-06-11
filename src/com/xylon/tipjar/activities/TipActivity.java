package com.xylon.tipjar.activities;

import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.xylon.tipjar.R;
import com.xylon.tipjar.fragment.TipPercentDialogFragment;
import com.xylon.tipjar.model.Bill;
import com.xylon.tipjar.service.GPSTracker;
import com.xylon.tipjar.utils.ResourceUtils;
import com.xylon.tipjar.utils.SharedPreferencesUtils;

public class TipActivity extends Activity implements OnSeekBarChangeListener{
	
	private static String TAG = TipActivity.class.getSimpleName();
	EditText tipAmtView;
	EditText totalAmtView;
	EditText billAmtView;
	SeekBar tipPercentBarView;
	EditText tipPercentView;
	SeekBar splitByBarView;
	EditText splitByView;
	RadioGroup radioGroupView;
	EditText locationView;
	
	Map<String, String> cityTaxTable;
	boolean excludedServiceTax = false;
	CheckBox excludeServiceTaxView;
	EditText billAmtLabel;
	Bill bill;

	
//	//Seekbar min value is 0, it cannot be changed.
//	// I want the tip min = 10 and splitby min = 1; This is achieved with the offset
//	private static int tipOffset = 0;
	private static int splitByOffset = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip);
		
		// Get the views
		billAmtView = (EditText)findViewById(R.id.billAmount);
		tipAmtView = (EditText) findViewById(R.id.tipAmount);
		totalAmtView = (EditText)findViewById(R.id.finalAmount);
		tipPercentBarView = (SeekBar)findViewById(R.id.tipPercentageSlider);
		splitByBarView = (SeekBar)findViewById(R.id.splitbySlider);
		radioGroupView = (RadioGroup)findViewById(R.id.radioGroup);
		tipPercentView = (EditText) findViewById(R.id.tipPercent);
		splitByView = (EditText)findViewById(R.id.splitBy);
		locationView = (EditText) findViewById(R.id.location);
		excludeServiceTaxView = (CheckBox)findViewById(R.id.excludeTax);
		billAmtLabel = (EditText)findViewById(R.id.billAmountLabel);
		bill = new Bill();
		// Get default values
		bill.setSplitBy(Integer.parseInt(splitByView.getText().toString()));
		bill.setTipPercent(Integer.parseInt(tipPercentView.getText().toString()));
		
		// Set the action bar color
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
		
		// set typeface
		Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"); 
		int count = radioGroupView.getChildCount();
        for (int i=0;i<count;i++) {
            View o = radioGroupView.getChildAt(i);
            if (o instanceof RadioButton) {
                ((RadioButton) o).setTypeface(tf);
                // also load if values present in the preferences
                SharedPreferencesUtils.LoadRadioPreferences(getApplicationContext(),"tip" + (i + 1), (RadioButton)o);
                if (((RadioButton) o).isChecked()) {
                	int val = getRadioValue((RadioButton) o);
                	bill.setTipPercent(val);
                	tipPercentView.setText(String.valueOf(val));
                	tipPercentBarView.setProgress(bill.getTipPercent());
                }
                
            }
        }
		
        // Add listeners
		tipPercentBarView.setOnSeekBarChangeListener(this); 
		splitByBarView.setOnSeekBarChangeListener(this);
		addListenerOnRadioButton();
		addTextWatchToBillAmount();
		setOnEditActionListenerOnTotalAmount();
		setOnEditActionListenerOnTipAmount();
		
		// TODO Turn GPS/off and on from Settings instead.
		// Has issues on emulator
//		// Get location info
//		GPSTracker runner = new GPSTracker();
//	    runner.execute(this, locationView, excludeServiceTaxView);
		
	    // Get city sales tax table
	    cityTaxTable =  ResourceUtils.getHashMapResource(this, R.xml.cityservicetax);
	    Log.v(TAG, "Size of the city table " + cityTaxTable.size());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_options, menu);
		return true;
	}

	/**
	 * ACTION BAR ACTIONS for each Item on the todo list
	 * 1. Edit the preset the tip percentages and persist
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_editTip) {
			DialogFragment dialogFragment = new TipPercentDialogFragment(radioGroupView,bill.getTipPercent());
			dialogFragment.show(getFragmentManager(), "edittippercentage");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void addTextWatchToBillAmount() {
		billAmtView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				bill.calculateTipFromBillAmount(billAmtView.getText().toString());	
				displayValues();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.v(TAG, "Before text changed");
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.v(TAG, "On Text changed");	
			}
		});
	}
	
	/**
	 * 
	 * Add listener to TotalAmount. When the total amount changes, calculate tip %
	 * and tip amount
	 */
	public void setOnEditActionListenerOnTotalAmount() {
		totalAmtView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_NEXT) {
					InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					bill.calculateTipFromTotalAmount(totalAmtView.getText().toString());
					displayValues();
					return true;
				} else {
					return false;
				}
			}
		});
	}
	
	/**
	 * 
	 * Add listener to Tip Amount. When the tip amount is input by the user,
	 * compute the Tip % and the Total amount
	 */
	public void setOnEditActionListenerOnTipAmount() {
		tipAmtView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_NEXT) {
					InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					bill.calculateTotalFromTipAmount(tipAmtView.getText().toString());
					displayValues();
					return true;
				} else {
					return false;
				}
			}
		});
	}
	
	public void excludeServiceTaxFromBillAmount(View v) {
		if (((CheckBox) v).isChecked()) {
			String cityName = locationView.getText().toString(); 					
			String taxRate = cityTaxTable.get(cityName);
			if (taxRate != null) {
				bill.removeTaxFromBillAmount(taxRate);
				billAmtLabel.setText("PreTax Bill Amount");
				excludedServiceTax = true;
			}
		} else {
			excludedServiceTax = false;
			bill.addTaxToBillAmount();
			billAmtLabel.setText("Bill Amount");
			
		}
		billAmtView.setText(String.valueOf(bill.getBillAmt()));
		bill.calculateTipFromBillAmount(billAmtView.getText().toString());
		
		displayValues();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {

		case R.id.tipPercentageSlider:
			bill.setTipPercent(progress);
			//updateRadioStates(progress);
			bill.calculateTipFromBillAmount(billAmtView.getText().toString());
			displayValues();
			break;
		case R.id.splitbySlider:
			bill.setSplitBy(progress+splitByOffset);
			bill.calculateTipFromBillAmount(billAmtView.getText().toString());
			displayValues();
			break;
		}
	}
	
	private void updateRadioStates(int val) {
		boolean isOneSelected = false;
		for(int i = 0; i < radioGroupView.getChildCount(); i++){
		    RadioButton rd = ((RadioButton)radioGroupView.getChildAt(i));
		    int tip = getRadioValue(rd);
		    if ( tip == val ) {
		    	isOneSelected = true;
		    	radioGroupView.check(rd.getId());
		    }
		}
		if (isOneSelected == false) radioGroupView.clearCheck();		
	}
	
	private int getRadioValue(RadioButton rd) {
		String tipP = rd.getText().toString().substring(0,rd.getText().toString().length() - 1);
	    int tip = Integer.parseInt(tipP);
	    return tip;
	}
	
	public void addListenerOnRadioButton() {
		radioGroupView
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup rdgroup, int arg1) {
						int selectedId = rdgroup.getCheckedRadioButtonId();

						if (selectedId == -1)
							Log.w(TAG, "Radio Button returned -1");
						else {
							// find the radiobutton by returned id
							RadioButton rd = (RadioButton) findViewById(selectedId);
							String tipString = rd.getText().toString();
							int tipP = Integer.parseInt(tipString.substring(0, tipString.length()-1));
							bill.setTipPercent(tipP);
							bill.calculateTipFromBillAmount(billAmtView.getText().toString());
							tipPercentBarView.setProgress(bill.getTipPercent());
						}
					}
				});
	}
	
	public void displayValues() {
		updateRadioStates((bill.getTipPercent()));
		//tipPercentBarView.setProgress(bill.getTipPercent()); // should be updated when we choose one from the preset from the radio group.
		tipPercentView.setText(String.valueOf(bill.getTipPercent()));
		splitByView.setText(String.valueOf(bill.getSplitBy()));
		tipAmtView.setText(String.valueOf(roundOff(bill.getTipAmt())));
		totalAmtView.setText(String.valueOf(roundOff(bill.getTotalAmt())));		
	}
		
	private static double roundOff(double a) {
		return ( Math.round(a * 100.0) / 100.0 );
	}
	

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	

	
}
