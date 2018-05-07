package com.example.alnik.examquiz;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.text.DecimalFormat;


public class MyDemicalFormater implements IValueFormatter, IAxisValueFormatter
{

    private DecimalFormat mFormat;

    public MyDemicalFormater() {
        mFormat = new DecimalFormat("###,###,##0");
    }

    /**
     * Allow a custom decimalformat
     *
     * @param format
     */
    public MyDemicalFormater(DecimalFormat format) {
        this.mFormat = format;
    }

    // IValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value);
    }

    // IAxisValueFormatter
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        //return mFormat.format(value);
        return "";
    }

    public int getDecimalDigits() {
        return 0;
    }
}