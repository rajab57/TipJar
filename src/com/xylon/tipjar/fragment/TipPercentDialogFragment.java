package com.xylon.tipjar.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.xylon.tipjar.R;
import com.xylon.tipjar.utils.SharedPreferencesUtils;

public class TipPercentDialogFragment extends DialogFragment implements
		OnSeekBarChangeListener {

	Button saveButton;
	Button cancelButton;
	EditText tip1;
	EditText tip2;
	EditText tip3;
	RadioGroup radioGroup;
	SeekBar tipslider1;
	SeekBar tipslider2;
	SeekBar tipslider3;
	int selTipPercent = 0;

	// TODO how to do notifyAll That should be the way to go
	public TipPercentDialogFragment(RadioGroup rd, int currentSelectedTipPercent) {
		radioGroup = rd;
		selTipPercent = currentSelectedTipPercent;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.edit_tax_percent);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
		tip1 = (EditText) dialog.findViewById(R.id.tip1);
		tip2 = (EditText) dialog.findViewById(R.id.tip2);
		tip3 = (EditText) dialog.findViewById(R.id.tip3);
		saveButton = (Button) dialog.findViewById(R.id.saveBtn);
		cancelButton = (Button) dialog.findViewById(R.id.cancelBtn);
		tipslider1 = (SeekBar) dialog.findViewById(R.id.tipslider1);
		tipslider2 = (SeekBar) dialog.findViewById(R.id.tipslider2);
		tipslider3 = (SeekBar) dialog.findViewById(R.id.tipslider3);
		tipslider1.setOnSeekBarChangeListener(this);
		tipslider2.setOnSeekBarChangeListener(this);
		tipslider3.setOnSeekBarChangeListener(this);

		loadPreferencesFromRadio(radioGroup);

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Save the preset values

				SharedPreferencesUtils.SavePreferences(getActivity(), "tip1",
						tip1.getText().toString());
				SharedPreferencesUtils.SavePreferences(getActivity(), "tip2",
						tip2.getText().toString());
				SharedPreferencesUtils.SavePreferences(getActivity(), "tip3",
						tip3.getText().toString());
				radioGroup.clearCheck();
				// load the preferences from the file
				for (int i = 0; i < radioGroup.getChildCount(); i++) {
					RadioButton rd = ((RadioButton) radioGroup.getChildAt(i));
					SharedPreferencesUtils.LoadRadioPreferences(getActivity(),
							"tip" + (i + 1), rd);
					int rdTip = Integer.parseInt(extractValue(rd.getText().toString()) );
					if ( rdTip == selTipPercent ) {
						radioGroup.check(rd.getId());
					}
				}
				// Change the existing view
				dismiss();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return dialog;

	}

	private String extractValue(String tipString) {
		String tipP = tipString.substring(0,
				tipString.length() - 1);
		return tipP;
	}

	// initial values on the dialog
	private void loadPreferencesFromRadio(final RadioGroup rdg) {
		// Load the preferences from the Radio to The slider and the text
		RadioButton rd1 = ((RadioButton) rdg.getChildAt(0));
		RadioButton rd2 = ((RadioButton) rdg.getChildAt(1));
		RadioButton rd3 = ((RadioButton) rdg.getChildAt(2));
		tip1.setText(extractValue(rd1.getText().toString()));
		tip2.setText(extractValue(rd2.getText().toString()));
		tip3.setText(extractValue(rd3.getText().toString()));

		tipslider1.setProgress(Integer.parseInt(extractValue(rd1.getText().toString())));
		tipslider2.setProgress(Integer.parseInt(extractValue(rd2.getText().toString())));
		tipslider3.setProgress(Integer.parseInt(extractValue(rd3.getText().toString())));

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {

		case R.id.tipslider1:
			tip1.setText(String.valueOf(progress));
			break;
		case R.id.tipslider2:
			tip2.setText(String.valueOf(progress));
			break;
		case R.id.tipslider3:
			tip3.setText(String.valueOf(progress));
			break;
		}
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
