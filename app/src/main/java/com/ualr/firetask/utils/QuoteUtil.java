package com.ualr.firetask.utils;

import android.content.Context;

import com.ualr.firetask.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteUtil {

    private static Random r = new Random();

    public static int randInt(int max) {
        int min = 0;
        return r.nextInt((max - min) + 1) + min;
    }

    public static String getRandomQuote(Context ctx) {
        String quotesArr[] = ctx.getResources().getStringArray(R.array.quotes);
        int quoteIdx = randInt(quotesArr.length - 1);
        return quotesArr[quoteIdx];
    }
}
