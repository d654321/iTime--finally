package jnu.edu.timeapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

public class EventActivity extends AppCompatActivity {
    private TextView textViewEvent,textViewDate,textViewCountdown;
    private Button buttonReturn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        textViewEvent=(TextView)this.findViewById(R.id.text1);
        textViewDate=(TextView)this.findViewById(R.id.text2);
        textViewCountdown=(TextView)this.findViewById(R.id.text3);
        buttonReturn=(Button)this.findViewById(R.id.button_return);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventActivity.this.finish();
            }
        });
        new TimeThread().start();
        textViewEvent.setText(getIntent().getStringExtra("event"));  //获取修改时传过来的数据
        textViewDate.setText(getIntent().getStringExtra("date"));

    }
    public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long getCountDown=getCount();
                    long days = getCountDown / (1000 * 60 * 60 * 24); //换算成天数
                    long hours = (getCountDown - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);//换算成小时
                    long minutes = (getCountDown - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60); //换算成分钟
                    long seconds = (getCountDown - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (60 * 1000)) / 1000;
                    textViewCountdown.setText(days + "天" + hours + "小时" + minutes + "分" + seconds + "秒");
                    break;
                default:
                    break;
            }
        }
    };

    private long getCount(){
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int mYear=c.get(Calendar.YEAR);
        int mMonth=c.get(Calendar.MONTH)+1;
        int mDay =c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        int mHour =c.get(Calendar.HOUR_OF_DAY);//时
        int mMinute =c.get(Calendar.MINUTE);//分
        int mSecond =c.get(Calendar.SECOND);//秒

        String date1=getIntent().getStringExtra("date");
        String s1=date1.substring(0,4);
        String s2=date1.substring(5,7);
        String s3=date1.substring(8,10);
        int w1=Integer.parseInt(s1);
        int w2=Integer.parseInt(s2);
        int w3=Integer.parseInt(s3);

        int day=(w1-mYear)*365+(w2-mMonth)*30+(w3-mDay);
        long count=(60-mSecond)*1000+(59-mMinute)*60*1000+(23-mHour)*60*60*1000+(day-1)*24*60*60*1000;
        return count;
    }
}

