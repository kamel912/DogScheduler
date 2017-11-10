package com.teamvii.dogscheduler.background;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.teamvii.dogscheduler.connection.ApiClient;
import com.teamvii.dogscheduler.connection.ApiInterface;
import com.teamvii.dogscheduler.models.ResponseFromServer;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MK on 10/11/2017.
 */

public class SentSMSObserver extends ContentObserver {

    ResponseFromServer fromServer;
    static String lastId ;
    Context context;
    Date now;
    public SentSMSObserver(Context context, Handler handler,Date now) {
        super(handler);
        this.context = context;
        this.now = now;
    }

    @Override
    public void onChange(boolean selfChange) {

        super.onChange(selfChange);
        // save the message to the SD card here
            Activity activity = new Activity();
            Uri mSmsinboxQueryUri = Uri.parse("content://sms/sent");
            Cursor cursor1 = context.getContentResolver().query(mSmsinboxQueryUri,
                    new String[]{"_id", "thread_id", "address", "person", "date",
                            "body", "type"}, null, null, null);
            activity.startManagingCursor(cursor1);
            String[] columns = new String[]{"address", "person", "date", "body", "type","_id"};
            if (cursor1.getCount() > 0) {
                String count = Integer.toString(cursor1.getCount());
                Log.e("Count", count);
                while (cursor1.moveToNext()) {
                    String address = cursor1.getString(cursor1.getColumnIndex(columns[0]));
                    String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                    String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                    Long timestamp = Long.parseLong(date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestamp);
                    Date finaldate = calendar.getTime();
                    String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                    int type = cursor1.getInt(cursor1.getColumnIndex(columns[4]));
                    String id = cursor1.getString(cursor1.getColumnIndex(columns[5]));
                    if (finaldate.after(now) && !id.equals(lastId)) {
                        forward(context,"outgoing",address,msg);
                        now = finaldate;
                        lastId = id;
                    }
                    break;
                }
                Log.d("sent sms", "one text send");

            }

    }


    void forward(final Context context, String type, String number, String message){
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseFromServer> call = apiInterface.getMessages("testmohamed",type,number,message);
        call.enqueue(new Callback<ResponseFromServer>() {
            @Override
            public void onResponse(Call<ResponseFromServer> call, Response<ResponseFromServer> response) {
                fromServer = response.body();
                String result = fromServer.getRespons();
                Toast.makeText(context,result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseFromServer> call, Throwable t) {
                Log.d("my error",t.getMessage());
            }
        });
    }
}
