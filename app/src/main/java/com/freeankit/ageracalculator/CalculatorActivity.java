package com.freeankit.ageracalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.util.Pair;

import com.google.android.agera.MutableRepository;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.RepositoryConfig;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ankit Kumar (ankitdroiddeveloper@gmail.com) on 14/12/2017 (MM/DD/YYYY )
 */

public class CalculatorActivity extends AppCompatActivity {
    private static final String VALUE_2 = "value2";
    private static final String VALUE_1 = "value1";
    private static final String OPERATION_KEY = "operation_key";
    public static final String ANIMATIONS_ENABLED_KEY = "animations_enabled_key";

    private MutableRepository<Integer> mValue1Repo = Repositories.mutableRepository(0);
    private MutableRepository<Integer> mValue2Repo = Repositories.mutableRepository(0);
    private Repository<Result<String>> mResultRepository;

    private Updatable mValue1TVUpdatable;
    private Updatable mValue2TVUpdatable;
    private Updatable mResultUpdatable;
    private MutableRepository<Result<Integer>> mOperationSelector = Repositories.mutableRepository(Result.absent());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);
        if (savedInstanceState != null && savedInstanceState.containsKey(VALUE_1)) {
            mValue1Repo.accept(savedInstanceState.getInt(VALUE_1));
            mValue2Repo.accept(savedInstanceState.getInt(VALUE_2));
            mOperationSelector.accept(Result.present(savedInstanceState.getInt(OPERATION_KEY)));

        }
        // For testing, the animation can be disabled via an Intent.
        if (getIntent().hasExtra(ANIMATIONS_ENABLED_KEY)) {
            boolean mAnimationEnabled = getIntent().getBooleanExtra(ANIMATIONS_ENABLED_KEY, true);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();

        ExecutorService mExecutor = Executors.newSingleThreadExecutor();

        mResultRepository = Repositories.repositoryWithInitialValue(Result.<String>absent())
                .observe(mValue1Repo, mValue2Repo, mOperationSelector)
                .onUpdatesPerLoop()
                .goTo(mExecutor)
                .attemptTransform(CalculatorOperations::keepCpuBusy).orEnd(Result::failure)
                .getFrom(mValue1Repo)
                .mergeIn(mValue2Repo, Pair::create)
                .attemptMergeIn(mOperationSelector, CalculatorOperations::attemptOperation)
                .orEnd(Result::failure)
                .thenTransform(input -> Result.present(input.toString()))
                .onConcurrentUpdate(RepositoryConfig.SEND_INTERRUPT)
                .compile();


        ((SeekBar) findViewById(R.id.seekBar1)).setOnSeekBarChangeListener(
                new RepositorySeekBarListener(mValue1Repo));

        ((SeekBar) findViewById(R.id.seekBar2)).setOnSeekBarChangeListener(
                new RepositorySeekBarListener(mValue2Repo));

        mValue1TVUpdatable = () -> ((TextView) findViewById(R.id.value1)).setText(
                mValue1Repo.get().toString());

        mValue2TVUpdatable = () -> ((TextView) findViewById(R.id.value2)).setText(
                mValue2Repo.get().toString());

        TextView resultTextView = findViewById(R.id.textViewResult);
        mResultUpdatable = () -> mResultRepository
                .get()
                .ifFailedSendTo(t -> Toast.makeText(this, t.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show())
                .ifFailedSendTo(t -> {
                    if (t instanceof ArithmeticException) {
                        resultTextView.setText("DIV#0");
                    } else {
                        resultTextView.setText("N/A");
                    }
                })
                .ifSucceededSendTo(resultTextView::setText);

        setUpdatables();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.startAnimation(findViewById(R.id.imageView));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mOperationSelector.get().isPresent()) {
            outState.putInt(VALUE_1, mValue1Repo.get());
            outState.putInt(VALUE_2, mValue2Repo.get());
            outState.putInt(OPERATION_KEY, mOperationSelector.get().get());
        }
        super.onSaveInstanceState(outState);
    }

    private void setUpdatables() {
        mValue1Repo.addUpdatable(mValue1TVUpdatable);
        mValue2Repo.addUpdatable(mValue2TVUpdatable);
        mResultRepository.addUpdatable(mResultUpdatable);

        mValue1TVUpdatable.update();
        mValue2TVUpdatable.update();
        mResultUpdatable.update();
    }

    @Override
    protected void onStop() {
        removeUpdatables();
        super.onStop();
    }

    private void removeUpdatables() {
        mValue1Repo.removeUpdatable(mValue1TVUpdatable);
        mValue2Repo.removeUpdatable(mValue2TVUpdatable);
        mResultRepository.removeUpdatable(mResultUpdatable);
    }

    public void onRadioButtonClicked(View view) {
        mOperationSelector.accept(Result.present(view.getId()));
    }
}
