package com.xylon.tipjar.service;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class GPSTracker extends AsyncTask<Object, String, String> implements LocationListener {
	private Location mLocation;
	private Address mAddress;
	private Context mContext;
	private EditText mLocationView;
	private CheckBox mExcludeTaxView;
	private static String TAG =GPSTracker.class.getSimpleName();

	
	@Override
	protected String doInBackground(Object... arg0) {
		Log.v(TAG, "GPS cal");
		mContext = (Context) arg0[0];
		LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE); 
		mLocationView = (EditText) arg0[1];
		mExcludeTaxView = (CheckBox) arg0[2];
		
		// Request GPS updates. The third param is the looper to use, which
		// defaults the the one for
		// the current thread.
		
        // getting GPS status
        boolean isGPSEnabled = lm
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = lm
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.v(TAG, "GPS enabled "+ isGPSEnabled);
        Log.v(TAG, "Network enabled "+ isNetworkEnabled);
        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        	System.out.println("GPS/network not enabled");
        	return null;
        }
        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, false);
        mLocation = lm.getLastKnownLocation(provider);
        if (mLocation != null)
        	Log.v(TAG,"Location " + mLocation.getLatitude() + " :" + mLocation.getLongitude() + " time " + mLocation.getTime());
        if (System.currentTimeMillis() - mLocation.getTime() > 50000 ) {
        	Looper.prepare();
        	lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
        	Looper.loop(); // start waiting...when this is done, we'll have the
						// location in this.location
        }
		// Now use the location coordinates for reverse geocoding
		Geocoder gc = new Geocoder(mContext);

		if (Geocoder.isPresent()) { // check if the methods are implemented
			List<Address> list;
			try {
				//list = gc.getFromLocation(37.42279, -122.08506, 1);
				list = gc.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
				mAddress = list.get(0);

				StringBuffer str = new StringBuffer();
				str.append("Name: " + mAddress.getLocality() + "\n");
				str.append("Sub-Admin Ares: " + mAddress.getSubAdminArea()
						+ "\n");
				str.append("Admin Area: " + mAddress.getAdminArea() + "\n");
				str.append("Country: " + mAddress.getCountryName() + "\n");
				str.append("Country Code: " + mAddress.getCountryCode() + "\n");
				str.append("Thorough Fare: " + mAddress.getThoroughfare() + "\n");

				String strAddress = str.toString();
				Log.v(TAG,strAddress);
				return mAddress.getLocality();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		Log.v(TAG, "in post execute");
		if ( result != null) {
			Typeface font = Typeface.createFromAsset(mContext.getAssets(), "JennaSue.ttf");  
			mLocationView.setTypeface(font); 
			mLocationView.setText(result);
			mExcludeTaxView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// Store the location, then get the current thread's looper and tell it
		// to
		// quit looping so it can continue on doing work with the new location.
		this.mLocation = location;
		Looper.myLooper().quit();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}