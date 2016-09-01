package soexample.umeng.com.myhtml5;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.view.menu.MenuBuilder;

import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity {
    MycustomView mycustomView;

    static long time = 1000*60*10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mycustomView = (MycustomView) findViewById(R.id.timer_view);
        final Date date = new Date(1000*60*10);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Date date1 = new Date(time);
                        Log.d("time","timer:"+date1.getTime());
                        mycustomView.setDate(date1.getMinutes()+":"+date1.getSeconds());
                        time = time-1000;
                    }
                });
            }
        },0,1000);

    }
}
