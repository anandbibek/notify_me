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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class EditFilterActivity extends Activity {

	Prefs prefs;
	ImageView appChooserButton;
	TextView appNameView;
	ImageView appIconView;
	CheckBox keywordsCheckbox;
	EditText keywordsEditor;
	LinearLayout keywordsCaption;
	View blacklistCaption;
	RadioButton blacklistRadiobutton;
	View whitelistCaption;
	RadioButton whitelistRadiobutton;
	CheckBox popupCheckbox;
	LinearLayout popupCaption;
	CheckBox aggressiveCheckbox;
	LinearLayout aggressiveCaption;
	CheckBox expansionCheckbox;
	LinearLayout expansionCaption;
	CheckBox expandedCheckbox;
	LinearLayout expandedCaption;
	CheckBox lightUpCheckbox;
	LinearLayout lightUpCaption;
	CheckBox duringCallCheckbox;
	LinearLayout duringCallCaption;
	int filter;
	String app;
	PackageManager packMan;
	boolean changed = false;
	long lastBackPress = 0L;
	Toast leaveToast;
	
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_filter);
		prefs = new Prefs(this);
		appChooserButton = (ImageView) findViewById(R.id.editor_app_chooser_button);
		appNameView = (TextView) findViewById(R.id.editor_chosen_app_name);
		appIconView = (ImageView) findViewById(R.id.editor_chosen_app_icon);
		keywordsCheckbox = (CheckBox) findViewById(R.id.editor_keywords_checkbox);
		keywordsEditor = (EditText) findViewById(R.id.editor_keywords_edittext);
		keywordsCaption = (LinearLayout) findViewById(R.id.editor_keywords_caption);
		blacklistCaption = findViewById(R.id.editor_keywords_blacklist_caption);
		blacklistRadiobutton = (RadioButton) findViewById(R.id.editor_keywords_blacklist_radio);
		whitelistCaption = findViewById(R.id.editor_keywords_whitelist_caption);
		whitelistRadiobutton = (RadioButton) findViewById(R.id.editor_keywords_whitelist_radio);
		popupCheckbox = (CheckBox) findViewById(R.id.editor_popup_checkbox);
		popupCaption = (LinearLayout) findViewById(R.id.editor_popup_caption);
		aggressiveCheckbox = (CheckBox) findViewById(R.id.editor_aggressive_checkbox);
		aggressiveCaption = (LinearLayout) findViewById(R.id.editor_aggressive_caption);

        //not suitable for common user
        aggressiveCaption.setVisibility(View.GONE);
        aggressiveCheckbox.setVisibility(View.GONE);

		expansionCheckbox = (CheckBox) findViewById(R.id.editor_expansion_checkbox);
		expansionCaption = (LinearLayout) findViewById(R.id.editor_expansion_caption);
		expandedCheckbox = (CheckBox) findViewById(R.id.editor_expanded_checkbox);
		expandedCaption = (LinearLayout) findViewById(R.id.editor_expanded_caption);
		lightUpCheckbox = (CheckBox) findViewById(R.id.editor_lightup_checkbox);
		lightUpCaption = (LinearLayout) findViewById(R.id.editor_lightup_caption);
		duringCallCheckbox = (CheckBox) findViewById(R.id.editor_duringcall_checkbox);
		duringCallCaption = (LinearLayout) findViewById(R.id.editor_duringcall_caption);

        //not suitable for common user
        duringCallCaption.setVisibility(View.GONE);
        duringCallCheckbox.setVisibility(View.GONE);

		if( getIntent().getAction().equals("edit") ){
			filter = getIntent().getIntExtra("filter", 0);
			app = prefs.getFilterApp(filter);
		}
        else{
			filter = prefs.getNumberOfFilters();
		}
		keywordsEditor.addTextChangedListener(
			new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if( count != 0 )
						changed();
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}
				@Override
				public void afterTextChanged(Editable s) {
				}
			}
		);
		packMan = this.getPackageManager();
		leaveToast = Toast.makeText(this, R.string.editor_leave_toast, Toast.LENGTH_LONG);
	}

	@Override
	protected void onResume(){
		super.onResume();
		if( android.os.Build.VERSION.SDK_INT < 16 ){
			findViewById(R.id.editor_expansion).setVisibility(View.GONE);
			findViewById(R.id.editor_expanded).setVisibility(View.GONE);
		}
		if( app != null ){
			try {
                if(filter==9999){
                    appChooserButton.setVisibility(View.GONE);
                    keywordsCheckbox.setEnabled(false);
                    keywordsCheckbox.setChecked(false);
                    keywordsCaption.setEnabled(false);
                    keywordsCaption.getChildAt(0).setEnabled(false);
                    keywordsCaption.getChildAt(1).setEnabled(false);
                }
                else {
                    ApplicationInfo appInfo = packMan.getApplicationInfo(app, 0);
                    appNameView.setText(packMan.getApplicationLabel(appInfo));
                    appIconView.setImageDrawable(packMan.getApplicationIcon(appInfo));

                    keywordsCheckbox.setEnabled(true);
                    keywordsCheckbox.setChecked(prefs.hasFilterKeywords(filter));
                    keywordsEditor.setText("");
                    if( keywordsCheckbox.isChecked() ){
                        String[] keywords = prefs.getFilterKeywords(filter);
                        for( int i = 0 ; i < keywords.length - 1 ; i++ )
                            keywordsEditor.setText(keywordsEditor.getText()+keywords[i]+"\n");
                        keywordsEditor.setText(keywordsEditor.getText()+keywords[keywords.length-1]);
                        keywordsEditor.setEnabled(true);
                        keywordsEditor.setFocusableInTouchMode(true);
                    }else{
                        keywordsEditor.setEnabled(false);
                        keywordsEditor.setFocusable(false);
                    }
                    keywordsCaption.setEnabled(true);
                    keywordsCaption.getChildAt(0).setEnabled(true);
                    keywordsCaption.getChildAt(1).setEnabled(true);
                }
				appChooserButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_edit));
				popupCheckbox.setEnabled(true);
				popupCheckbox.setChecked(prefs.isPopupAllowed(filter));
				popupCaption.setEnabled(true);
				popupCaption.getChildAt(0).setEnabled(true);
				popupCaption.getChildAt(1).setEnabled(true);
				aggressiveCheckbox.setChecked(prefs.isAggressive(filter));
				expansionCheckbox.setChecked(prefs.isExpansionAllowed(filter));
				expandedCheckbox.setChecked(prefs.expandByDefault(filter));
				lightUpCheckbox.setEnabled(true);
				lightUpCheckbox.setChecked(prefs.isLightUpAllowed(filter));
				lightUpCaption.setEnabled(true);
				lightUpCaption.getChildAt(0).setEnabled(true);
				lightUpCaption.getChildAt(1).setEnabled(true);
				duringCallCheckbox.setEnabled(true);
				duringCallCheckbox.setChecked(prefs.isDuringCallAllowed(filter));
				duringCallCaption.setEnabled(true);
				duringCallCaption.getChildAt(0).setEnabled(true);
				duringCallCaption.getChildAt(1).setEnabled(true);
			}catch(Exception e){
				finish();
			}
		}else{
			keywordsCheckbox.setEnabled(false);
			keywordsCheckbox.setChecked(false);
			keywordsEditor.setEnabled(false);
			keywordsEditor.setFocusable(false);
			keywordsEditor.setText("");
			keywordsCaption.setEnabled(false);
			keywordsCaption.getChildAt(0).setEnabled(false);
			keywordsCaption.getChildAt(1).setEnabled(false);
			popupCheckbox.setEnabled(false);
			popupCheckbox.setChecked(false);
			popupCaption.setEnabled(false);
			popupCaption.getChildAt(0).setEnabled(false);
			popupCaption.getChildAt(1).setEnabled(false);
			aggressiveCheckbox.setChecked(false);
			expansionCheckbox.setChecked(false);
			expandedCheckbox.setChecked(false);
			lightUpCheckbox.setEnabled(false);
			lightUpCheckbox.setChecked(false);
			lightUpCaption.setEnabled(false);
			lightUpCaption.getChildAt(0).setEnabled(false);
			lightUpCaption.getChildAt(1).setEnabled(false);
			duringCallCheckbox.setEnabled(false);
			duringCallCheckbox.setChecked(false);
			duringCallCaption.setEnabled(false);
			duringCallCaption.getChildAt(0).setEnabled(false);
			duringCallCaption.getChildAt(1).setEnabled(false);
			appNameView.setText(R.string.editor_app_chooser_title);
		}
		aggressiveCheckbox.setEnabled(popupCheckbox.isChecked());
		aggressiveCaption.setEnabled(popupCheckbox.isChecked());
		aggressiveCaption.getChildAt(0).setEnabled(popupCheckbox.isChecked());
		aggressiveCaption.getChildAt(1).setEnabled(popupCheckbox.isChecked());
		expansionCheckbox.setEnabled(popupCheckbox.isChecked());
		expansionCaption.setEnabled(popupCheckbox.isChecked());
		expansionCaption.getChildAt(0).setEnabled(popupCheckbox.isChecked());
		expansionCaption.getChildAt(1).setEnabled(popupCheckbox.isChecked());
		expandedCheckbox.setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCaption.setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCaption.getChildAt(0).setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCaption.getChildAt(1).setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		blacklistCaption.setEnabled(keywordsCheckbox.isChecked());
		blacklistRadiobutton.setEnabled(keywordsCheckbox.isChecked());
		blacklistRadiobutton.setChecked(!prefs.isFilterWhitelist(filter));
		whitelistCaption.setEnabled(keywordsCheckbox.isChecked());
		whitelistRadiobutton.setEnabled(keywordsCheckbox.isChecked());
		whitelistRadiobutton.setChecked(prefs.isFilterWhitelist(filter));
	}
	
	@Override
	public void onBackPressed(){
		if( app == null || !changed || (System.currentTimeMillis()-lastBackPress < 3500L) ){
			leaveToast.cancel();
			finish();
			return;
		}
		leaveToast.show();
		lastBackPress = System.currentTimeMillis();
	}
	
	public void checkTheKeywordsBox(View view){
		if( view.equals(keywordsCaption) )
			keywordsCheckbox.toggle();
		keywordsEditor.setEnabled(keywordsCheckbox.isChecked());
		keywordsEditor.setFocusable(keywordsCheckbox.isChecked());
		keywordsEditor.setFocusableInTouchMode(keywordsCheckbox.isChecked());
		blacklistCaption.setEnabled(keywordsCheckbox.isChecked());
		blacklistRadiobutton.setEnabled(keywordsCheckbox.isChecked());
		whitelistCaption.setEnabled(keywordsCheckbox.isChecked());
		whitelistRadiobutton.setEnabled(keywordsCheckbox.isChecked());
		changed();
	}

	public void checkTheBlacklistRadiobutton(View view){
		if( view.equals(blacklistCaption) )
			blacklistRadiobutton.toggle();
		whitelistRadiobutton.setChecked(!blacklistRadiobutton.isChecked());
		changed();
	}

	public void checkTheWhitelistRadiobutton(View view){
		if( view.equals(whitelistCaption) )
			whitelistRadiobutton.toggle();
		blacklistRadiobutton.setChecked(!whitelistRadiobutton.isChecked());
		changed();
	}
	
	public void checkThePopupBox(View view){
		if( view.equals(popupCaption) )
			popupCheckbox.toggle();
		aggressiveCheckbox.setEnabled(popupCheckbox.isChecked());
		aggressiveCheckbox.setChecked(false);
		aggressiveCaption.setEnabled(popupCheckbox.isChecked());
		aggressiveCaption.getChildAt(0).setEnabled(popupCheckbox.isChecked());
		aggressiveCaption.getChildAt(1).setEnabled(popupCheckbox.isChecked());
		expansionCheckbox.setEnabled(popupCheckbox.isChecked());
		expansionCheckbox.setChecked(popupCheckbox.isChecked());
		expansionCaption.setEnabled(popupCheckbox.isChecked());
		expansionCaption.getChildAt(0).setEnabled(popupCheckbox.isChecked());
		expansionCaption.getChildAt(1).setEnabled(popupCheckbox.isChecked());
		expandedCheckbox.setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCheckbox.setChecked(false);
		expandedCaption.setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCaption.getChildAt(0).setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCaption.getChildAt(1).setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		changed();
	}

	public void checkTheAggressiveBox(View view){
		if( view.equals(aggressiveCaption) )
			aggressiveCheckbox.toggle();
		changed();
	}

	public void checkTheExpansionBox(View view){
		if( view.equals(expansionCaption) )
			expansionCheckbox.toggle();
		expandedCheckbox.setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCheckbox.setChecked(false);
		expandedCaption.setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCaption.getChildAt(0).setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		expandedCaption.getChildAt(1).setEnabled(popupCheckbox.isChecked() && expansionCheckbox.isChecked());
		changed();
	}

	public void checkTheExpandedBox(View view){
		if( view.equals(expandedCaption) )
			expandedCheckbox.toggle();
		changed();
	}
	
	public void checkTheLightUpBox(View view){
		if( view.equals(lightUpCaption) )
			lightUpCheckbox.toggle();
		changed();
	}
	
	public void checkTheDuringCallBox(View view){
		if( view.equals(duringCallCaption) )
			duringCallCheckbox.toggle();
		changed();
	}
	
	public void chooseApp(View view){
        if(filter==9999)
            return;
		leaveToast.cancel();
		startActivityForResult(new Intent(this, AppPicker.class).putExtra("filter", filter), 42);
	}

	protected void onActivityResult(int reqCode, int resCode, Intent result){
		if( resCode == Activity.RESULT_OK && reqCode == 42 ){
			app = result.getAction();
			changed();
		}
	}
	
	void apply(){
		if( filter == prefs.getNumberOfFilters() ){
			if( !popupCheckbox.isChecked() && !lightUpCheckbox.isChecked() ){
				Toast.makeText(this, "This app is now blacklisted", Toast.LENGTH_LONG).show();
				//return;
			}
			if( keywordsCheckbox.isChecked() && !keywordsEditor.getText().toString().equals("") ){
				prefs.addFilter(app, popupCheckbox.isChecked(), aggressiveCheckbox.isChecked(), expansionCheckbox.isChecked(), expandedCheckbox.isChecked(), lightUpCheckbox.isChecked(), duringCallCheckbox.isChecked(), produceKeywords(), whitelistRadiobutton.isChecked());
			}else{
				prefs.addFilter(app, popupCheckbox.isChecked(), aggressiveCheckbox.isChecked(), expansionCheckbox.isChecked(), expandedCheckbox.isChecked(), lightUpCheckbox.isChecked(), duringCallCheckbox.isChecked());
			}
		}else{
			if( !popupCheckbox.isChecked() && !lightUpCheckbox.isChecked() ){
				Toast.makeText(this, "This app is now blacklisted", Toast.LENGTH_LONG).show();
				//return;
			}
			if( keywordsCheckbox.isChecked() && !keywordsEditor.getText().toString().equals("") ){
				prefs.editFilter(filter, app, popupCheckbox.isChecked(), aggressiveCheckbox.isChecked(), expansionCheckbox.isChecked(), expandedCheckbox.isChecked(), lightUpCheckbox.isChecked(), duringCallCheckbox.isChecked(), produceKeywords(), whitelistRadiobutton.isChecked());
			}else{
				prefs.editFilter(filter, app, popupCheckbox.isChecked(), aggressiveCheckbox.isChecked(), expansionCheckbox.isChecked(), expandedCheckbox.isChecked(), lightUpCheckbox.isChecked(), duringCallCheckbox.isChecked());
			}
		}
		finish();
	}
	
	private String[] produceKeywords(){
		String keywordString = keywordsEditor.getText().toString();
		int numberOfBreaks = 0;
		for( int i = 0 ; i < keywordString.length() ; i++ ){
			if( String.valueOf(keywordString.charAt(i)).equals("\n") ){
				numberOfBreaks++;
			}
		}
		String[] keywords = new String[numberOfBreaks+1];
		int prevBreak = 0;
		int j = 0;
		for( int i = 0 ; i < keywordString.length() ; i++ ){
			if( String.valueOf(keywordString.charAt(i)).equals("\n") ){
				keywords[j] = keywordString.substring(prevBreak, i);
				prevBreak = i + 1;
				j++;
			}
		}
		keywords[numberOfBreaks] = keywordString.substring(prevBreak);
		return keywords;
	}
	
	@SuppressLint("NewApi")
	private void changed(){
		changed = true;
		if( android.os.Build.VERSION.SDK_INT >= 11 )
			invalidateOptionsMenu();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.edit_filter_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.findItem(R.id.editor_menu_apply).setTitle( (filter == prefs.getNumberOfFilters() ? R.string.editor_menu_apply0 : R.string.editor_menu_apply) ).setEnabled(changed);
		menu.findItem(R.id.editor_menu_discard).setTitle( (filter == prefs.getNumberOfFilters() ? R.string.editor_menu_discard0 : R.string.editor_menu_discard) );
		menu.findItem(R.id.editor_menu_remove).setVisible(prefs.getFilterApp(filter).equals(app));
		leaveToast.cancel();
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		leaveToast.cancel();
		switch( item.getItemId() ){
			case R.id.editor_menu_apply:
				apply();
				return true;
			case R.id.editor_menu_discard:
				finish();
				return true;
			case R.id.editor_menu_remove:
                if(filter==9999)
                    return true;
				prefs.removeFilter(filter);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
