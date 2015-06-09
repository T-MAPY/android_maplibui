/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android.
 * Author:   Dmitry Baryshnikov (aka Bishop), bishop.dev@gmail.com
 * Author:   NikitaFeodonit, nfeodonit@yandex.com
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * *****************************************************************************
 * Copyright (c) 2012-2015. NextGIS, info@nextgis.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextgis.maplibui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.nextgis.maplib.api.IGISApplication;
import com.nextgis.maplib.api.ILayer;
import com.nextgis.maplib.display.TMSRenderer;
import com.nextgis.maplib.map.LocalTMSLayer;
import com.nextgis.maplib.map.MapBase;
import com.nextgis.maplib.util.Constants;
import com.nextgis.maplibui.R;


/**
 * TMS layer settings activity. Include common settings (layer name) and renderer settings.
 */
public class LocalTMSLayerSettingsActivity
        extends AppCompatActivity
{
    public final static String LAYER_ID_KEY = "layer_id";
    protected LocalTMSLayer mRasterLayer;

    protected int     mCacheSizeMult;
    protected float   mContrast;
    protected float   mBrightness;
    protected boolean mForceToGrayScale;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_localrasterlayer_settings);

        short layerId = Constants.NOT_FOUND;
        if (savedInstanceState != null) {
            layerId = savedInstanceState.getShort(LAYER_ID_KEY);
        } else {
            layerId = getIntent().getShortExtra(LAYER_ID_KEY, layerId);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.getBackground().setAlpha(255);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        IGISApplication application = (IGISApplication) getApplication();

        MapBase map = application.getMap();
        if (null != map) {
            ILayer layer = map.getLayerById(layerId);
            if (null != layer && layer.getType() == Constants.LAYERTYPE_LOCAL_TMS) {
                mRasterLayer = (LocalTMSLayer) layer;
            }
        }

        if (null != mRasterLayer) {
            EditText editText = (EditText) findViewById(R.id.layer_name);
            editText.setText(mRasterLayer.getName());

            Spinner cacheSizeMult = (Spinner) findViewById(R.id.spinner);
            if (null != cacheSizeMult) {
                cacheSizeMult.setSelection(mRasterLayer.getCacheSizeMultiply());

                cacheSizeMult.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener()
                        {
                            @Override
                            public void onItemSelected(
                                    AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id)
                            {
                                mCacheSizeMult = position;
                            }


                            @Override
                            public void onNothingSelected(AdapterView<?> parent)
                            {

                            }
                        });
            }

            // set color
            TMSRenderer tmsRenderer = (TMSRenderer) mRasterLayer.getRenderer();
            if (null != tmsRenderer) {
                SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.make_grayscale);
                mForceToGrayScale = tmsRenderer.isForceToGrayScale();
                switchCompat.setChecked(mForceToGrayScale);

                switchCompat.setOnCheckedChangeListener(
                        new CompoundButton.OnCheckedChangeListener()
                        {
                            @Override
                            public void onCheckedChanged(
                                    CompoundButton buttonView,
                                    boolean isChecked)
                            {
                                mForceToGrayScale = isChecked;
                            }
                        });

                mContrast = tmsRenderer.getContrast();

                final TextView mContrastLabel = (TextView) findViewById(R.id.contrast_seek);
                mContrastLabel.setText(getString(R.string.contrast) + ": " + mContrast);

                SeekBar contrastPicker = (SeekBar) findViewById(R.id.contrastSeekBar);
                contrastPicker.setProgress((int) mContrast * 10);
                contrastPicker.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener()
                        {
                            @Override
                            public void onProgressChanged(
                                    SeekBar seekBar,
                                    int progress,
                                    boolean fromUser)
                            {
                                if (fromUser) {
                                    float fProgress = progress;
                                    mContrast = fProgress / 10;
                                    mContrastLabel.setText(
                                            getString(R.string.contrast) + ": " + mContrast);
                                }
                            }


                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar)
                            {

                            }


                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar)
                            {

                            }
                        });

                mBrightness = tmsRenderer.getBrightness();

                final TextView mBrightnessLabel = (TextView) findViewById(R.id.brightness_seek);
                mBrightnessLabel.setText(getString(R.string.brightness) + ": " + mBrightness);

                SeekBar brightnessPicker = (SeekBar) findViewById(R.id.brightnessSeekBar);
                brightnessPicker.setProgress((int) mBrightness + 255);
                brightnessPicker.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener()
                        {
                            @Override
                            public void onProgressChanged(
                                    SeekBar seekBar,
                                    int progress,
                                    boolean fromUser)
                            {
                                if (fromUser) {
                                    mBrightness = progress - 255;
                                    mBrightnessLabel.setText(
                                            getString(R.string.brightness) + ": " + mBrightness);
                                }
                            }


                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar)
                            {

                            }


                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar)
                            {

                            }
                        });
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void saveSettings()
    {
        if (null == mRasterLayer) {
            return;
        }
        EditText editText = (EditText) findViewById(R.id.layer_name);
        mRasterLayer.setName(editText.getEditableText().toString());
        mRasterLayer.setCacheSizeMultiply(mCacheSizeMult);

        TMSRenderer tmsRenderer = (TMSRenderer) mRasterLayer.getRenderer();
        if (null != tmsRenderer) {
            tmsRenderer.setContrastBrightness(mContrast, mBrightness, mForceToGrayScale);
        }

        mRasterLayer.save();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        saveSettings();
    }
}