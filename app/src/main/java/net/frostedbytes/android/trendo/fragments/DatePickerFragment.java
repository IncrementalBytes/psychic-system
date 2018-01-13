package net.frostedbytes.android.trendo.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import net.frostedbytes.android.trendo.R;

public class DatePickerFragment extends DialogFragment {

  private static final String TAG = "DatePickerFragment";

  private static final String ARG_DATE = "date";

  private OnMatchDateSetListener mCallback;

  public interface OnMatchDateSetListener {

    void onMatchDateSet(long dateInMilliseconds);
  }

  private DatePicker mDatePicker;

  public static DatePickerFragment newInstance(long date) {

    Log.d(TAG, "++newInstance(long)");
    Bundle args = new Bundle();
    args.putSerializable(ARG_DATE, date);

    DatePickerFragment fragment = new DatePickerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    try {
      mCallback = (OnMatchDateSetListener) getTargetFragment();
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling Fragment must implement OnMatchDateSetListener.");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    super.onCreateDialog(savedInstanceState);

    Log.d(TAG, "++onCreateDialog(Bundle)");
    Calendar calendar = Calendar.getInstance();
    Bundle bundle = getArguments();
    if (bundle != null) {
      Serializable value = bundle.getSerializable(ARG_DATE);
      if (value != null && (long) value > 0) {
        calendar.setTimeInMillis((long) value);
      }
    }

    View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date_picker, null);

    mDatePicker = v.findViewById(R.id.dialog_date_picker);
    mDatePicker.init(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        null);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setView(v)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

          private static final String TAG = "setPositiveButton";

          @Override
          public void onClick(DialogInterface dialog, int which) {

            Log.d(TAG, "++onClick(DialogInterface, int)");
            int year = mDatePicker.getYear();
            int month = mDatePicker.getMonth();
            int day = mDatePicker.getDayOfMonth();
            Date date = new GregorianCalendar(year, month, day).getTime();
            sendResult(date.getTime());
          }
        });

    return builder.create();
  }

  private void sendResult(long date) {

    Log.d(TAG, "++sendResult(long)");
    mCallback.onMatchDateSet(date);
    dismiss();
  }
}