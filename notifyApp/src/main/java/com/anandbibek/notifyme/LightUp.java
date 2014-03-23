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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager.LayoutParams;

public class LightUp extends Activity implements SensorEventListener {

	Prefs prefs;
	WaitForLight wFL;
	boolean running, countdown;
    boolean mCovered;
    private SensorManager mSensorManager;
    private Handler handler;

    private BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if( ((TelephonyManager)arg0.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != 0 ){
                arg0.unregisterReceiver(this);
                return;
            }
            startActivity(new Intent(arg0, NotificationActivity.class ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            arg0.unregisterReceiver(this);
        }
    };

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        countdown = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager != null)
            mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
                    ,mSensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onNewIntent(Intent intent){
		if( wFL != null && running){
			running = false;
			wFL.cancel(true);
			finish();
			startActivity(intent);
		}else
			super.onNewIntent(intent);
        //TODO handle Handler
	}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.d("Sensor value ", sensorEvent.values[0]+"");
        if( sensorEvent.values[0] < 2 /*cm*/) {
            //device in pocket
            //Log.d("In pocket ", System.currentTimeMillis()+"");
            if(!countdown){
                mCovered=true;
                handlePocket(this);
                //Log.d("Handler started ", System.currentTimeMillis()+"");
                countdown=true;
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
            prefs = new Prefs(this);
            wFL = new WaitForLight();
            wFL.execute();
            running = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //unused
    }

    public void handlePocket(final Context context){
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSensorManager.unregisterListener((SensorEventListener) context);
                mSensorManager = null;
                //Log.d("Unregistered ", System.currentTimeMillis()+"");
                IntentFilter iFilter = new IntentFilter();
                iFilter.addAction(Intent.ACTION_SCREEN_ON);
                registerReceiver(receiver, iFilter);
                //Log.d("Registered  broadcast receiver",System.currentTimeMillis()+"");
            }
        }, 10000 /* We need this delay to get new flags applied */
        );
    }

	
	private class WaitForLight extends AsyncTask<Void,Void,Boolean>{
		@Override
		protected Boolean doInBackground(Void... params) {

//TODO verify redundancy and cleanup code around here

//            if(!mCovered) {
//            long curTime = System.currentTimeMillis();
//            while( !((PowerManager)getSystemService(POWER_SERVICE)).isScreenOn() ){
//                  if( System.currentTimeMillis() - curTime > 999 ){
//                        return false;
//                  }
//            }
//            }
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			if( prefs.isPopupAllowed(((TemporaryStorage)getApplicationContext()).getFilter()) && result ){
				((TemporaryStorage)getApplicationContext()).storeStuff( true );

				startActivity( new Intent(getApplicationContext(), NotificationActivity.class )
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("screenWasOff", true)
                        .putExtra("screenCovered", mCovered) );
			}
			finish();
			if( !result ) {
                Log.d("starting all over again","");
                startActivity(getIntent());
            }
		}
	}
}
