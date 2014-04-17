/*	Notify Me!, an app to enhance Android(TM)'s abilities to show notifications.
	Copyright (C) 2013 Tom Kranz
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
	
	Android is a trademark of Google Inc.
*/
package com.anandbibek.notifyme;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.WindowManager.LayoutParams;

public class LightUp extends Activity implements SensorEventListener {

	Prefs prefs;
	boolean countdown;
    boolean mCovered;
    private SensorManager mSensorManager;
    private Handler handler;

    private class screenOnListener extends BroadcastReceiver{
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if( ((TelephonyManager)arg0.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != 0 ){
                arg0.unregisterReceiver(this);
                return;
            }
            startActivity(new Intent(arg0, NotificationActivity.class ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            arg0.unregisterReceiver(this);
        }
    }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        countdown = false;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null && new Prefs(this).isProximityEnabled()) {
            //sensor present AND enabled
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
                    , mSensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            //Proximity sensor not present OR disabled
            mCovered = false;
            getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
            getWindow().addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((TemporaryStorage)getApplicationContext()).storeStuff( true );

                    startActivity( new Intent(getApplicationContext(), NotificationActivity.class )
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("screenWasOff", true)
                            .putExtra("lightUp", true) );
                    finish();
                }
            },100);
        }
	}

	@Override
	protected void onNewIntent(Intent intent){
        try{
            mSensorManager.unregisterListener(this);
        }catch (Exception e){
            //just in case
        }
		if(countdown) {
            handler.removeCallbacksAndMessages(null);
			finish();
			startActivity(intent);
		}else
			super.onNewIntent(intent);
        //TODO check
	}

    @Override
    protected void onDestroy() {
        try{
            mSensorManager.unregisterListener(this);
        }catch (Exception e){
            //just in case
        }
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.d("Sensor value ", sensorEvent.values[0]+"");
        if( sensorEvent.values[0] < 2 /*cm*/) {
            //device in pocket
            //Log.d("In pocket ", System.currentTimeMillis()+"");
            if(!countdown){
                mCovered=true;
                countdown=true;
                handlePocket(this);
                //Log.d("Handler started ", System.currentTimeMillis()+"");
            }
        }
        else {
            //Log.d("Out of pocket ", System.currentTimeMillis()+"");
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
            if(countdown) {
                handler.removeCallbacksAndMessages(null);
                //Log.d("Handler stopped ", System.currentTimeMillis()+"");
            }
            mCovered = false;
            getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
            getWindow().addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);

            //prefs = new Prefs(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //if( prefs.isPopupAllowed(((TemporaryStorage)getApplicationContext()).getFilter())){
                    //let's not honor this obvious pref check TODO remove code
                        ((TemporaryStorage)getApplicationContext()).storeStuff( true );

                        startActivity( new Intent(getApplicationContext(), NotificationActivity.class )
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("screenWasOff", true)
                                .putExtra("lightUp", true) );
                    //}
                    finish();
                }
            },200); //TODO verify if delay is enough
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //unused
    }

    public void handlePocket(final Context context){
        handler = new Handler();
        prefs = new Prefs(this);
        long timeout = prefs.getProximityTimeout();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSensorManager.unregisterListener((SensorEventListener) context);
                mSensorManager = null;
                //Log.d("Unregistered ", System.currentTimeMillis()+"");
                IntentFilter iFilter = new IntentFilter();
                iFilter.addAction(Intent.ACTION_SCREEN_ON);
                registerReceiver(new screenOnListener(), iFilter);
                countdown = false;
                //Log.d("Registered  broadcast receiver",System.currentTimeMillis()+"");
            }
        }, timeout
        );
    }
}
