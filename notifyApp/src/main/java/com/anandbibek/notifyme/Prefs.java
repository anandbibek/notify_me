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
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

@SuppressLint("CommitPrefEdits")
public class Prefs {
	private final SharedPreferences prefs;
	private final Editor edit;
	
	protected Prefs(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		edit = prefs.edit();
	}
	
	protected int getPrevVersion(){
		return prefs.getInt("PreviousVersion", 0);
	}
	
	protected void setPrevVersion(int newVersion){
		edit.putInt("PreviousVersion", newVersion);
		edit.commit();
	}
	
	protected boolean isBackgroundColorInverted(){
		return prefs.getBoolean("BackgroundInverted", true);
	}
	
	protected void setBackgroundColorInverted(boolean inverted){
		edit.putBoolean("BackgroundInverted", inverted);
		edit.commit();
	}
	
	protected long getScreenTimeout(){
		return prefs.getLong("ScreenTimeout", 0L);
	}
	
	protected void setScreenTimeout(long timeout){
		edit.putLong("ScreenTimeout", timeout);
		edit.commit();
	}

    protected long getProximityTimeout(){
        return prefs.getLong("ProximityTimeout", 10L);
    }

    protected void setProximityTimeout(long timeout){
        edit.putLong("ProximityTimeout", timeout);
        edit.commit();
    }
	
	protected boolean isOrientationFixed(){
		return prefs.getBoolean("OrientationFixed", true);
	}
	
	protected void setOrientationFixed(boolean orientationFixed){
		edit.putBoolean("OrientationFixed", orientationFixed);
		edit.commit();
	}
	
	protected boolean isInterfaceSlider(){
		return prefs.getBoolean("InterfaceSlider", true);
	}
	
	protected void setInterfaceSlider(boolean slider){
		edit.putBoolean("InterfaceSlider", slider);
		edit.commit();
	}
	
	protected int getSliderBackgroundR(){
		return prefs.getInt("SliderBackgroundR", ( android.os.Build.VERSION.SDK_INT > 10 ? 24 : 255 ));
	}
	protected int getSliderBackgroundG(){
		return prefs.getInt("SliderBackgroundG", ( android.os.Build.VERSION.SDK_INT > 10 ? 24 : 255 ));
	}
	protected int getSliderBackgroundB(){
		return prefs.getInt("SliderBackgroundB", ( android.os.Build.VERSION.SDK_INT > 10 ? 24 : 255 ));
	}
	
	protected void setSliderBackground(int r, int g, int b){
		edit.putInt("SliderBackgroundR", r);
		edit.putInt("SliderBackgroundG", g);
		edit.putInt("SliderBackgroundB", b);
		edit.commit();
	}
	
	protected int getNumberOfFilters(){
		return prefs.getInt("numberOfFilters", 0);
	}
	
	protected String getFilterApp(int filter){
		return prefs.getString("filter"+String.valueOf(filter)+"App", "");
	}
	
	protected boolean hasFilterKeywords(int filter){
		return ( prefs.getInt("filter"+String.valueOf(filter)+"numberOfKeywords", 0) > 0 );
	}
	
	protected boolean isFilterWhitelist(int filter){
		return prefs.getBoolean("filter"+String.valueOf(filter)+"Whitelist", true);
	}
	
	protected boolean isPopupAllowed(int filter){
		return prefs.getBoolean("filter"+String.valueOf(filter)+"Popup", true);
	}
	
	protected boolean isAggressive(int filter){
		return prefs.getBoolean("filter"+String.valueOf(filter)+"Aggressive", false);
	}
	
	protected boolean isDuringCallAllowed(int filter){
		return prefs.getBoolean("filter"+String.valueOf(filter)+"Call", false);
	}
	
	protected boolean isExpansionAllowed(int filter){
		return prefs.getBoolean("filter"+String.valueOf(filter)+"Expansion", true);
	}
	
	protected boolean expandByDefault(int filter){
		return prefs.getBoolean("filter"+String.valueOf(filter)+"Expanded", false);
	}
	
	protected boolean isLightUpAllowed(int filter){
		return prefs.getBoolean("filter"+String.valueOf(filter)+"Light", true);
	}
	
	protected String[] getFilterKeywords(int filter){
		int numberOfKeywords = prefs.getInt("filter"+String.valueOf(filter)+"numberOfKeywords", 0);
		String[] keywords = new String[numberOfKeywords];
		for(int i = 0 ; i < numberOfKeywords ; i++ ){
			keywords[i] = prefs.getString("filter"+String.valueOf(filter)+"Keyword"+String.valueOf(i), "");
		}
		return keywords;
	}
	
	protected void addFilter(String app, boolean popupAllowed, boolean aggressive, boolean expansionAllowed, boolean expanded, boolean lightUpAllowed, boolean duringCall){
		int filter = getNumberOfFilters();
		edit.putString("filter"+String.valueOf(filter)+"App", app);
		edit.putBoolean("filter"+String.valueOf(filter)+"Popup", popupAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Aggressive", aggressive);
		edit.putBoolean("filter"+String.valueOf(filter)+"Expansion", expansionAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Expanded", expanded);
		edit.putBoolean("filter"+String.valueOf(filter)+"Light", lightUpAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Call", duringCall);
		edit.putInt( "numberOfFilters", filter + 1 );
		edit.commit();
	}

	protected void addFilter(String app, boolean popupAllowed, boolean aggressive, boolean expansionAllowed, boolean expanded, boolean lightUpAllowed, boolean duringCall, String[] keywords, boolean filterWhitelist){
		int filter = getNumberOfFilters();
		edit.putString("filter"+String.valueOf(filter)+"App", app);
		edit.putBoolean("filter"+String.valueOf(filter)+"Popup", popupAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Aggressive", aggressive);
		edit.putBoolean("filter"+String.valueOf(filter)+"Expansion", expansionAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Expanded", expanded);
		edit.putBoolean("filter"+String.valueOf(filter)+"Light", lightUpAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Call", duringCall);
		int pos = 0;
		for( int i = 0 ; i < keywords.length ; i++ ){
			if( !isUseless(keywords, i) ){
				edit.putString("filter"+String.valueOf(filter)+"Keyword"+String.valueOf(pos++), cleaned(keywords[i]));
			}
		}
		edit.putInt("filter"+String.valueOf(filter)+"numberOfKeywords", pos);
		edit.putBoolean("filter"+String.valueOf(filter)+"Whitelist", filterWhitelist);
		edit.putInt( "numberOfFilters", filter + 1 );
		edit.commit();
	}
	
	protected void editFilter(int filter, String app, boolean popupAllowed, boolean aggressive, boolean expansionAllowed, boolean expanded, boolean lightUpAllowed, boolean duringCall){
		for( int i = 0 ; i < prefs.getInt("filter"+String.valueOf(filter)+"numberOfKeywords", 0) ; i++ ){
			edit.remove("filter"+String.valueOf(filter)+"Keyword"+String.valueOf(i));
		}
		edit.remove("filter"+String.valueOf(filter)+"numberOfKeywords");
		edit.putString("filter"+String.valueOf(filter)+"App", app);
		edit.putBoolean("filter"+String.valueOf(filter)+"Popup", popupAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Aggressive", aggressive);
		edit.putBoolean("filter"+String.valueOf(filter)+"Expansion", expansionAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Expanded", expanded);
		edit.putBoolean("filter"+String.valueOf(filter)+"Light", lightUpAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Call", duringCall);
		edit.commit();
	}
	
	protected void editFilter(int filter, String app, boolean popupAllowed, boolean aggressive, boolean expansionAllowed, boolean expanded, boolean lightUpAllowed, boolean duringCall, String[] keywords, boolean filterWhitelist){
		for( int i = 0 ; i < prefs.getInt("filter"+String.valueOf(filter)+"numberOfKeywords", 0) ; i++ ){
			edit.remove("filter"+String.valueOf(filter)+"Keyword"+String.valueOf(i));
		}
		edit.remove("filter"+String.valueOf(filter)+"numberOfKeywords");
		edit.putString("filter"+String.valueOf(filter)+"App", app);
		edit.putBoolean("filter"+String.valueOf(filter)+"Popup", popupAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Aggressive", aggressive);
		edit.putBoolean("filter"+String.valueOf(filter)+"Expansion", expansionAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Expanded", expanded);
		edit.putBoolean("filter"+String.valueOf(filter)+"Light", lightUpAllowed);
		edit.putBoolean("filter"+String.valueOf(filter)+"Call", duringCall);
		int pos = 0;
		for( int i = 0 ; i < keywords.length ; i++ ){
			if( !isUseless(keywords, i) ){
				edit.putString("filter"+String.valueOf(filter)+"Keyword"+String.valueOf(pos++), cleaned(keywords[i]));
			}
		}
		edit.putInt("filter"+String.valueOf(filter)+"numberOfKeywords", pos);
		edit.putBoolean("filter"+String.valueOf(filter)+"Whitelist", filterWhitelist);
		edit.commit();
	}
	
	private String cleaned(String keyword) {
		boolean startsWithCharacter = false;
		while( !startsWithCharacter ){
			if( keyword.indexOf(" ") == 0 )
				keyword = keyword.substring(1);
			else
				startsWithCharacter = true;
		}
		if( keyword.equals("") )
			return "";
		boolean endsWithCharacter = false;
		while( !endsWithCharacter ){
			if( keyword.lastIndexOf(" ") == keyword.length()-1 )
				keyword = keyword.substring(0, keyword.length()-1);
			else
				endsWithCharacter = true;
		}
		return keyword;
	}

	private boolean isUseless(String[] keywords, int position){
		String keyword = cleaned(keywords[position]);
		if( keyword.equals("") )
			return true;
		for( int i = 0 ; i < position ; i++ ){
			if( cleaned(keywords[i]).equals(keyword) )
				return true;
		}
		return false;
	}
	
	protected void removeFilter(int filter){
		edit.remove("filter"+String.valueOf(filter)+"App");
		edit.remove("filter"+String.valueOf(filter)+"Popup");
		edit.remove("filter"+String.valueOf(filter)+"Aggressive");
		edit.remove("filter"+String.valueOf(filter)+"Expansion");
		edit.remove("filter"+String.valueOf(filter)+"Expanded");
		edit.remove("filter"+String.valueOf(filter)+"Light");
		edit.remove("filter"+String.valueOf(filter)+"Call");
		for( int i = 0 ; i < prefs.getInt("filter"+String.valueOf(filter)+"numberOfKeywords", 0) ; i++ ){
			edit.remove("filter"+String.valueOf(filter)+"Keyword"+String.valueOf(i));
		}
		edit.remove("filter"+String.valueOf(filter)+"numberOfKeywords");
		edit.remove("filter"+String.valueOf(filter)+"Whitelist");
		for( int i = filter + 1 ; i < getNumberOfFilters() ; i++ ){
			edit.putString("filter"+String.valueOf(i-1)+"App", getFilterApp(i));
			edit.remove("filter"+String.valueOf(i)+"App");
			edit.putBoolean("filter"+String.valueOf(i-1)+"Popup", isPopupAllowed(i));
			edit.remove("filter"+String.valueOf(i)+"Popup");
			edit.putBoolean("filter"+String.valueOf(i-1)+"Aggressive", isAggressive(i));
			edit.remove("filter"+String.valueOf(i)+"Aggressive");
			edit.putBoolean("filter"+String.valueOf(i-1)+"Expansion", isExpansionAllowed(i));
			edit.remove("filter"+String.valueOf(i)+"Expansion");
			edit.putBoolean("filter"+String.valueOf(i-1)+"Expanded", expandByDefault(i));
			edit.remove("filter"+String.valueOf(i)+"Expanded");
			edit.putBoolean("filter"+String.valueOf(i-1)+"Light", isLightUpAllowed(i));
			edit.remove("filter"+String.valueOf(i)+"Light");
			edit.putBoolean("filter"+String.valueOf(i-1)+"Call", isDuringCallAllowed(i));
			edit.remove("filter"+String.valueOf(i)+"Call");
			for( int j = 0 ; j < prefs.getInt("filter"+String.valueOf(i)+"numberOfKeywords", 0) ; j++ ){
				edit.putString("filter"+String.valueOf(i-1)+"Keyword"+String.valueOf(j), prefs.getString("filter"+String.valueOf(i)+"Keyword"+String.valueOf(j), ""));
				edit.remove("filter"+String.valueOf(i)+"Keyword"+String.valueOf(j));
			}
			edit.putInt("filter"+String.valueOf(i-1)+"numberOfKeywords", prefs.getInt("filter"+String.valueOf(i)+"numberOfKeywords", 0));
			edit.remove("filter"+String.valueOf(i)+"numberOfKeywords");
			edit.putBoolean("filter"+String.valueOf(i-1)+"Whitelist", isFilterWhitelist(i));
			edit.remove("filter"+String.valueOf(i)+"Whitelist");
		}
		edit.putInt( "numberOfFilters", getNumberOfFilters() - 1 );
		edit.commit();
	}
}
