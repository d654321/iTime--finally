package jnu.edu.timeapplication.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import jnu.edu.timeapplication.EventActivity;
import jnu.edu.timeapplication.MainActivity;
import jnu.edu.timeapplication.NewTime;
import jnu.edu.timeapplication.NewTimeActivity;
import jnu.edu.timeapplication.R;
import jnu.edu.timeapplication.Time;
import jnu.edu.timeapplication.TimeSaver;
import jnu.edu.timeapplication.ui.send.SendViewModel;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    public static final int CONTEXT_MENU_ITEM_UPDATE=1;
    public static final int CONTEXT_MENU_ITEM_DELETE=CONTEXT_MENU_ITEM_UPDATE+1;
    public static final int REQUEST_CODE_UPDATE_BOOK = 901;

    private ListView listViewTime;
    private Button buttonAdd;
    private List<Time> itime = new ArrayList<>();
    private TimeAdapter adapter;
    private TimeSaver timeSaver;
    private int insertPosition;//新建插入的位置
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        timeSaver=new TimeSaver(getContext());
        itime=timeSaver.load();
        listViewTime=(ListView)root.findViewById(R.id.list_view_itime);
        buttonAdd=(Button) root.findViewById(R.id.button_add);
        insertPosition=getActivity().getIntent().getIntExtra("insert_position",0);
        adapter =new TimeAdapter(getActivity(),R.layout.list_view_item_time,itime);
        this.registerForContextMenu(listViewTime);  //接受事件
        listViewTime.setAdapter(adapter);

        listViewTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Time time1=itime.get(i);
                Intent intent=new Intent(getActivity(),EventActivity.class);
                String str=time1.getTitle();
                String []temp=null;
                temp=str.split("\n");
                intent.putExtra("event",temp[0]);
                intent.putExtra("date",temp[2]);
                startActivity(intent);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NewTimeActivity.class);
                intent.putExtra("title","");
                intent.putExtra("countdown","");
                startActivityForResult(intent,0);  //启动另一个Activity
            }
        });
        return root;
    }
    public void onStop() {
        super.onStop();
        timeSaver.save();
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v==getActivity().findViewById(R.id.list_view_itime)){
            int itemPosition=((AdapterView.AdapterContextMenuInfo)menuInfo).position;
            menu.setHeaderTitle(itime.get(itemPosition).getTitle());
            menu.add(0,CONTEXT_MENU_ITEM_UPDATE,0,"修改");
            menu.add(0,CONTEXT_MENU_ITEM_DELETE,0,"删除");
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()){
            case CONTEXT_MENU_ITEM_UPDATE:{  //需要把当前属性传递过去
                Time time=itime.get(menuInfo.position);

                Intent intent = new Intent(getActivity(),NewTimeActivity.class);
                String str=time.getTitle();
                String []temp=null;
                temp=str.split("\n");
                intent.putExtra("position",menuInfo.position);
                intent.putExtra("title",temp[0]);
                intent.putExtra("description",temp[1]);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_BOOK);
                break;
            }

            case CONTEXT_MENU_ITEM_DELETE:{
                final int itemPosition=menuInfo.position;
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("注意")
                        .setMessage("是否删除该计时")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itime.remove(itemPosition);  //移走删除
                                adapter.notifyDataSetChanged();    //界面刷新
                                //Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create().show();
                break;
            }
        }
        return super.onContextItemSelected(item);
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_UPDATE_BOOK:
                if (resultCode == RESULT_OK) {
                    int Position = data.getIntExtra("position", 0);
                    String title = data.getStringExtra("title");
                    String description = data.getStringExtra("description");
                    String date = data.getStringExtra("date");
                    String down = data.getStringExtra("countdown");
                    Time time = itime.get(Position);
                    time.setTitle(title + "\n" + description + "\n" + date);
                    time.setCountdown(down);
                    adapter.notifyDataSetChanged();
                    //Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                if (resultCode == RESULT_OK) {
                    int insertPosition = data.getIntExtra("insert_position", 0);
                    String title = data.getStringExtra("title");
                    String description = data.getStringExtra("description");
                    String date = data.getStringExtra("date");
                    String down=data.getStringExtra("countdown");
                    itime.add(insertPosition,new Time(title+"\n"+description+"\n"+date,R.drawable.a3,down));
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private class TimeAdapter extends ArrayAdapter<Time> {
        private int resourceId;
        public TimeAdapter(Context context, int resource, List<Time> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Time time = getItem(position);//获取当前项的实例
            View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            ((ImageView) view.findViewById(R.id.list_view_image)).setImageResource(time.getResourceId());
            ((TextView) view.findViewById(R.id.list_view_text1)).setText(time.getTitle());
            ((TextView) view.findViewById(R.id.list_view_text2)).setText("剩余"+time.getCountdown()+"天");
            return view;
        }
    }
}

