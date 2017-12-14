package com.freeankit.ageracalculator;

import android.widget.SeekBar;

import com.google.android.agera.MutableRepository;

/**
 * @author Ankit Kumar (ankitdroiddeveloper@gmail.com) on 14/12/2017 (MM/DD/YYYY )
 */

public class RepositorySeekBarListener implements SeekBar.OnSeekBarChangeListener {

    private MutableRepository<Integer> mRepository;

    public RepositorySeekBarListener(MutableRepository<Integer> repository) {
        mRepository = repository;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean userInitiated) {
        mRepository.accept(i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
