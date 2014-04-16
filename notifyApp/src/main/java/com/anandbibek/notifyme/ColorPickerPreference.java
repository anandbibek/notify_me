package com.anandbibek.notifyme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * Created by Anand on 16-Apr-14.
 */
public class ColorPickerPreference extends Preference {

    Context mContext;

    public ColorPickerPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onClick() {
        final Prefs prefs = new Prefs(getContext());
        final View view = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.main_menu_popup, null);

        ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_r)).setMax(255);
        ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_r)).setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if( fromUser ){
                            ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_r)).setText(( progress == 0 ? "" : String.valueOf(progress) ));
                        }

                        ((ImageView)view.findViewById(R.id.main_menu_popup_color_preview)).setImageDrawable(new ColorDrawable(Color.rgb(progress, ((SeekBar) view.findViewById(R.id.main_menu_popup_color_slider_g)).getProgress(), ((SeekBar) view.findViewById(R.id.main_menu_popup_color_slider_b)).getProgress())));
                    }
                }
        );
        ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_r)).addTextChangedListener(
                new TextWatcher(){
                    @Override
                    public void afterTextChanged(Editable s) {
                        try{
                            if( Integer.parseInt(s.toString()) > 255 ){
                                s.replace(0, s.length(), "255");
                                return;
                            }else if( Integer.parseInt(s.toString()) < 0 ){
                                s.replace(0, s.length(), "0");
                                return;
                            }
                            ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_r)).setProgress(Integer.parseInt(s.toString()));
                        }catch(Exception e){
                            ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_r)).setProgress(0);
                            s.clear();
                        }
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                }
        );
        ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_g)).setMax(255);
        ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_g)).setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if( fromUser ){
                            ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_g)).setText(( progress == 0 ? "" : String.valueOf(progress) ));
                        }
                        ((ImageView)view.findViewById(R.id.main_menu_popup_color_preview)).setImageDrawable(new ColorDrawable(Color.rgb(((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_r)).getProgress(), progress, ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_b)).getProgress())));
                    }
                }
        );
        ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_g)).addTextChangedListener(
                new TextWatcher(){
                    @Override
                    public void afterTextChanged(Editable s) {
                        try{
                            if( Integer.parseInt(s.toString()) > 255 ){
                                s.replace(0, s.length(), "255");
                                return;
                            }else if( Integer.parseInt(s.toString()) < 0 ){
                                s.replace(0, s.length(), "0");
                                return;
                            }
                            ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_g)).setProgress(Integer.parseInt(s.toString()));
                        }catch(Exception e){
                            ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_g)).setProgress(0);
                            s.clear();
                        }
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                }
        );
        ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_b)).setMax(255);
        ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_b)).setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if( fromUser ){
                            ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_b)).setText(( progress == 0 ? "" : String.valueOf(progress) ));
                        }
                        ((ImageView)view.findViewById(R.id.main_menu_popup_color_preview)).setImageDrawable(new ColorDrawable(Color.rgb(((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_r)).getProgress(), ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_g)).getProgress(), progress)));
                    }
                }
        );
        ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_b)).addTextChangedListener(
                new TextWatcher(){
                    @Override
                    public void afterTextChanged(Editable s) {
                        try{
                            if( Integer.parseInt(s.toString()) > 255 ){
                                s.replace(0, s.length(), "255");
                                return;
                            }else if( Integer.parseInt(s.toString()) < 0 ){
                                s.replace(0, s.length(), "0");
                                return;
                            }
                            ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_b)).setProgress(Integer.parseInt(s.toString()));
                        }catch(Exception e){
                            ((SeekBar)view.findViewById(R.id.main_menu_popup_color_slider_b)).setProgress(0);
                            s.clear();
                        }
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                }
        );
        ((ImageView)view.findViewById(R.id.main_menu_popup_color_preview)).setImageDrawable(new ColorDrawable(Color.rgb(prefs.getSliderBackgroundR(), prefs.getSliderBackgroundG(), prefs.getSliderBackgroundB())));
        ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_r)).setText(String.valueOf(prefs.getSliderBackgroundR()));
        ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_g)).setText(String.valueOf(prefs.getSliderBackgroundG()));
        ((EditText)view.findViewById(R.id.main_menu_popup_color_edit_b)).setText(String.valueOf(prefs.getSliderBackgroundB()));

        new AlertDialog.Builder(mContext).setView(view).setPositiveButton(R.string.main_menu_popup_save_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefs.setSliderBackground(((SeekBar) view.findViewById(R.id.main_menu_popup_color_slider_r)).getProgress(), ((SeekBar) view.findViewById(R.id.main_menu_popup_color_slider_g)).getProgress(), ((SeekBar) view.findViewById(R.id.main_menu_popup_color_slider_b)).getProgress());
                    }
                }
        ).setNegativeButton(R.string.main_menu_popup_cancel_button, null).show();
    }
}
