package com.anandbibek.notifyme;

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
import android.annotation.SuppressLint;
import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.*;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.WindowManager.LayoutParams;
import android.widget.RemoteViews;

public class NotificationActivity extends Activity {

	Prefs prefs;
	int filter;
	boolean big, showPopup, touchValid, triggers, screenWasOff;
	Notification notif;
	RemoteViews remViews;
	View nView;
	ViewGroup pView;
	SliderSurfaceView sView;
	float X, lastX;
    boolean lHaptic,rHaptic;
	GestureDetector geDet;
	AlertDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		screenWasOff = getIntent().getBooleanExtra("screenWasOff", false);
        if(getIntent().getBooleanExtra("lightUp", true))
            getWindow().setFlags(LayoutParams.FLAG_TURN_SCREEN_ON, LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().setFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED, LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		prefs = new Prefs(this);
		if( prefs.isOrientationFixed() ){
			if( android.os.Build.VERSION.SDK_INT < 9 ){
				if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE )
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}else{
				setRequestedOrientation(getRealOrientation());
			}
		}
		filter = ((TemporaryStorage)getApplicationContext()).getFilter();
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	private int getRealOrientation(){
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		int format = getResources().getConfiguration().orientation;
		Log.d("Tpm", String.valueOf(format));
		Log.d("Tpm", String.valueOf(rotation));
		if( format == Configuration.ORIENTATION_LANDSCAPE ){
			if( rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90 )
				return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			else
				return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
		}else{
			if( rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270 )
				return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			else
				return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume(){
		super.onResume();
		notif = (Notification) ((TemporaryStorage)getApplicationContext()).getParcelable();
		if( prefs.expandByDefault(filter) && android.os.Build.VERSION.SDK_INT >= 16 ){
			try{
				notif.bigContentView.hashCode();
				big = true;
			}catch(Exception e){
				
			}
		}
		((TemporaryStorage)getApplicationContext()).storeStuff(screenWasOff);
		try{
			if( ((TemporaryStorage)getApplicationContext()).getTimeout() == 0L && prefs.getScreenTimeout() != 0L && screenWasOff ){
				((TemporaryStorage)getApplicationContext()).storeStuff(Settings.System.getLong(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT));
				Settings.System.putLong(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, prefs.getScreenTimeout());
			}
		}catch(Exception e){
			
		}

        if( !new Prefs(this).isPopupAllowed(filter)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },300);
            return;
        }

		if( !preparePopup() )
			return;
		try{
			if( prefs.isInterfaceSlider() )
				showPopupSlider();
			else
				showPopupButton();
		}catch(Exception e){
			finish();
		}
	}
	
	@SuppressLint("NewApi")
	private boolean preparePopup(){
		try{
			if( big )
				remViews = notif.bigContentView;
			else
				remViews = notif.contentView;
		}catch(Exception e){
			finish();
			return false;
		}
		dialog = new AlertDialog.Builder(this).setView(pView).setInverseBackgroundForced(prefs.isBackgroundColorInverted()).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(
			new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			}
		);
		return true;
	}
	
	@SuppressWarnings("deprecation")
	private void showPopupSlider(){
		pView = (ViewGroup) ((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.slider_popup, null);
		nView = remViews.apply(this, pView);
		pView.addView(nView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		sView = (SliderSurfaceView) pView.getChildAt(1);
		geDet = new GestureDetector( new UnlockListener() );
		sView.setOnTouchListener(
			new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if( event.getAction() == MotionEvent.ACTION_UP ){
                        if( event.getX() <= sView.leftX && triggers ){
                            dialog.cancel();
                            finish();
                            //overridePendingTransition(0, 0); TODO better animations
                            return true;
                        }
                        else if( event.getX() >= sView.rightX && triggers ){
                            unlock();
                            dialog.cancel();
                            return true;
                        }

						touchValid = false;
                        triggers = false;
                        X=sView.centerX;
                        sView.doDraw(X,false);
					}
					return geDet.onTouchEvent(event);
				}
			}
		);
		sView.setOnClickListener(null);
		sView.setZOrderOnTop(true);
		dialog.setView(pView);
        setLayoutListener();
		dialog.show();
    }
	
	@SuppressLint("NewApi")
	private void showPopupButton(){
		nView = remViews.apply(this, null);
		dialog.setView(nView);
		dialog.setButton(Dialog.BUTTON_POSITIVE, this.getText(R.string.notification_view_button), 
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
                    unlock();
					dialog.cancel();
				}
			}
		);
		dialog.setButton(Dialog.BUTTON_NEGATIVE, this.getText(R.string.notification_dismiss_button),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}
		);
		if( android.os.Build.VERSION.SDK_INT >= 16 && prefs.isExpansionAllowed(filter) ){
			try{
				notif.bigContentView.hashCode();
				dialog.setButton(Dialog.BUTTON_NEUTRAL, getText( big ? R.string.notification_collapse_button : R.string.notification_expand_button ),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							big = !big;
							if( preparePopup() )
								try{
									showPopupButton();
								}catch(Exception e){
									finish();
								}
						}
					}
				);
			}catch(Exception e){
				
			}
		}
		dialog.show();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if( !prefs.isPopupAllowed(filter) )
			return;
		if( prefs.getScreenTimeout() != 0L && screenWasOff ){
			Settings.System.putLong(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, ((TemporaryStorage)getApplicationContext()).getTimeout());
			((TemporaryStorage)getApplicationContext()).storeStuff(false);
		}
		((TemporaryStorage)getApplicationContext()).storeStuff(0L);
		if( dialog != null )
			dialog.dismiss();
	}
	
	@Override
	protected void onNewIntent(Intent intent){
		//if( !getIntent().equals(intent) ) {
        if( dialog != null )
            dialog.dismiss();
        finish();
        startActivity(intent);
        //}
		//}else
		//	super.onNewIntent(intent);
        //TODO verify double stuff
	}

    public void setLayoutListener(){
        try{
        sView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
                            sView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        else
                            sView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        sliderInit();
                    }
                });
        }catch (Exception e){
            sliderInit();
        }

    }


    private void sliderInit(){
        sView.setDimensions((float) sView.getWidth(),(float) sView.getHeight());
        Resources res = getResources();
        sView.setBitmaps(BitmapFactory.decodeResource(res, R.drawable.ic_lock_lock),
                BitmapFactory.decodeResource(res, R.drawable.ic_lock_handle),
                BitmapFactory.decodeResource(res, R.drawable.ic_lock_view),
                BitmapFactory.decodeResource(res, R.drawable.ic_lock_view0),
                BitmapFactory.decodeResource(res, R.drawable.ic_lock_dismiss),
                BitmapFactory.decodeResource(res, R.drawable.ic_lock_dismiss0));
        X = sView.centerX;
        lastX = X / 2;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sView.doDraw(X,false);
            }
        },100); //delay first time drawing, otherwise not drawn properly
    }

    //unlock handler makes unlock.java redundant
    public void unlock(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    if (notif.contentIntent != null) {
                        notif.contentIntent.send();
                    }
                }catch(PendingIntent.CanceledException e){}
                finish();
                //overridePendingTransition(0, 0); TODO better animations
            }
        }, 100 /* We need this delay to get new flags applied */
        );
    }


    //gesture listener
	private class UnlockListener extends SimpleOnGestureListener{
		float[] a = new float[2];
		float[] b = new float[2];
		
		@Override
		public boolean onDown(MotionEvent ev){
			a[0] = ev.getX();
			a[1] = ev.getY();
			b[0] = sView.centerX;
			b[1] = sView.centerY;
			if( sView.dist(a, b) < sView.offsetY ){
				touchValid = true;
				triggers = true;
                sView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                lHaptic = false;
                rHaptic = false;
			}

            sView.doDraw(X, touchValid);
			return touchValid;
		}
		
		@Override
		public boolean onScroll(MotionEvent ev1, MotionEvent ev2, float dX, float dY){

			if( Math.abs(ev2.getY() - sView.centerY)/(Math.abs(ev2.getX() - sView.centerX) + sView.offsetX)  >= 1 ){
				touchValid = false;
				triggers = false;
			}
			if( touchValid ){
				lastX = X;
				X = ev2.getX();
			}
            if( lastX != X ){
                if(lastX>sView.leftX && X<=sView.leftX && !lHaptic) {
                    sView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    lHaptic=true;
                }

                if(lastX<sView.rightX && X>=sView.rightX && !rHaptic) {
                    sView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    rHaptic=true;
                }

                if(lHaptic && X>sView.leftX )
                    lHaptic=false;
                if(rHaptic && X<sView.rightX)
                    rHaptic=false;

                if( !touchValid ){
                    lastX = X;
                    X = sView.centerX;
                }
            }
            sView.doDraw(X, touchValid);
            return false;
		}

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(android.os.Build.VERSION.SDK_INT >= 16) {
                dialog.dismiss();
                big = !big;
                if (preparePopup())
                    try {
                        showPopupSlider();
                    } catch (Exception ex) {
                        finish();
                    }
            }
            return super.onDoubleTap(e);
        }
	}
}
