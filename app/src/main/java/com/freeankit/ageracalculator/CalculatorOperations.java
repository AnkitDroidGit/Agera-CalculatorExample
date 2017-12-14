package com.freeankit.ageracalculator;

import static com.google.android.agera.Result.present;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.google.android.agera.Result;


/**
 * @author Ankit Kumar (ankitdroiddeveloper@gmail.com) on 14/12/2017 (MM/DD/YYYY )
 */

public class CalculatorOperations {
    @NonNull
    public static Result<Result<String>> keepCpuBusy(Result<String> input) {
        String stringCounter = "0";
        for (int i = 0; i < 600_000; i++) {
            if (Thread.currentThread().isInterrupted()) {

                return Result.failure();
            }
            // Show no love for our CPU and GC.
            Integer intCounter = Integer.valueOf(stringCounter);
            intCounter++;
            stringCounter = intCounter.toString();
        }
        return present(input);
    }

    public static Result<Integer> attemptOperation(@NonNull Pair<Integer, Integer> operands,
                                                   @NonNull Result<Integer> operation) {
        return operation.ifSucceededAttemptMerge(operands,
                (presentOperation, operandsInner) -> {
                    try {
                        return present(performOperation(operandsInner, presentOperation));
                    } catch (RuntimeException e) {
                        return Result.failure(e);
                    }
                });
    }

    private static Integer performOperation(Pair<Integer, Integer> pair1, Integer operation1)
            throws ArithmeticException {
        switch (operation1) {
            case R.id.radioButtonAdd:
                return pair1.first + pair1.second;
            case R.id.radioButtonSub:
                return pair1.first - pair1.second;
            case R.id.radioButtonMult:
                return pair1.first * pair1.second;
            case R.id.radioButtonDiv:
                return (pair1.first / pair1.second);
            default:
                throw new RuntimeException("Invalid operation");
        }
    }
}
