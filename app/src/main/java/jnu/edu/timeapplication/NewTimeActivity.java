package jnu.edu.timeapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.print.PrinterId;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewTimeActivity extends AppCompatActivity {
    private EditText editTextAddTitle,editTextAddDescription;
    private ListView listViewTime1;
    private Button buttonCancel,buttonOK;
    private List<NewTime> newTime =new ArrayList<>();
    private MessageAdapter adapter;
    private int insertPosition;
    private  int Position;
    private String date,down;  //定义所选日期，剩余日期,易于传值
    private int mYear,mMonth,mDay,mHour,mMinute,countdown;  //定义当前时间，计算后的剩余日期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_time);
        init();
        editTextAddTitle=(EditText)this.findViewById(R.id.edit_text_add_title);
        editTextAddDescription=(EditText)this.findViewById(R.id.edit_text_add_description);
        listViewTime1=(ListView)this.findViewById(R.id.list_view_itime2);
        buttonCancel=(Button)this.findViewById(R.id.button_cancel);
        buttonOK=(Button)this.findViewById(R.id.button_ok);

        adapter =new MessageAdapter(NewTimeActivity.this,R.layout.list_view_item_time2, newTime);
        listViewTime1.setAdapter(adapter);
        listViewTime1.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //给每个item添加响应事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  //给每一个item添加点击事件，position是item在listview中的位置
                final int currentPosition=position;
                switch(currentPosition){
                    case 0:  //设置日期
                        Calendar calendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(NewTimeActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                NewTime newTime0 = newTime.get(currentPosition);
                                date=year + "年" + (monthOfYear+1) + "月" + dayOfMonth+"日";
                                newTime0.setMessage("日期\n"+date);
                                adapter.notifyDataSetChanged();

                                //计算剩余时间
                                Calendar c = Calendar.getInstance();
                                mYear = c.get(Calendar.YEAR); // 获取当前年份
                                mMonth = c.get(Calendar.MONTH) ;// 获取当前月份
                                mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
                                int x=year-mYear;  //年份差
                                int y=monthOfYear-mMonth;  //月份差
                                int z=dayOfMonth-mDay;  //天数差
                                int count=x*365+y*30+z;   //剩余天数
                                down=count+"";
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                        break;

                    case 1:  //选择周期
                        final String[] cycles = new String[]{"每周", "每月","每年", "自定义"};
                        AlertDialog builder1 = new AlertDialog.Builder(NewTimeActivity.this).setTitle("周期")
                                .setItems(cycles, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NewTime newTime1 = newTime.get(currentPosition);
                                        newTime1.setMessage("重复设置\n"+cycles[which]);
                                        adapter.notifyDataSetChanged();
                                    }
                                }).create();
                        builder1.show();
                        break;

                    case 2:
                        break;

                    case 3:  //添加标签,多选
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(NewTimeActivity.this);
                        builder3.setTitle("标签");
                        final String[] labels = new String[]{"生日", "学习","工作", "节假日"};
                        final boolean[] checks = new boolean[]{false, false, false, false, false};
                        builder3.setMultiChoiceItems(labels, checks, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            }
                        });
                        builder3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                StringBuffer sb=new StringBuffer();
                                for(int i=0;i<checks.length;i++){
                                    if(checks[i]){
                                        String label=labels[i];
                                        sb.append(label+" ");
                                    }
                                }
                                NewTime newTime3 = newTime.get(currentPosition);
                                newTime3.setMessage("标签\n"+sb.toString());
                                adapter.notifyDataSetChanged();
                            }
                        });
                        builder3.setNegativeButton("取消", null);
                        builder3.show();
                        break;
                }
            }
        });

        editTextAddTitle.setText(getIntent().getStringExtra("title"));  //获取修改时传过来的数据
        editTextAddDescription.setText(getIntent().getStringExtra("description"));
        insertPosition=getIntent().getIntExtra("insert_position",0);
        Position=getIntent().getIntExtra("position",0);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("title",editTextAddTitle.getText().toString());
                intent.putExtra("description",editTextAddDescription.getText().toString());
                intent.putExtra("date",date.toString());
                intent.putExtra("countdown",down);
                intent.putExtra("insert_position",insertPosition);  //新建位置
                intent.putExtra("position",Position);  //修改位置
                setResult(RESULT_OK,intent);
                NewTimeActivity.this.finish();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewTimeActivity.this.finish();
            }
        });
    }

    private void init() {
        newTime.add(new NewTime("日期",R.drawable.a7));
        newTime.add(new NewTime("重复设置",R.drawable.a8));
        newTime.add(new NewTime("图片",R.drawable.a9));
        newTime.add(new NewTime("标签",R.drawable.a10));
    }

    private class MessageAdapter extends ArrayAdapter<NewTime> {

        private int source;

        public MessageAdapter(Context context, int resource, List<NewTime> objects) {
            super(context, resource, objects);
            source = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NewTime mes = getItem(position);//获取当前项的实例
            View view = LayoutInflater.from(getContext()).inflate(source, parent, false);
            ((ImageView) view.findViewById(R.id.image_view)).setImageResource(mes.getSource());
            ((TextView) view.findViewById(R.id.text_view_description)).setText(mes.getMessage());
            return view;
        }
    }

}

