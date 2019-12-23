package jnu.edu.timeapplication;

import android.content.Context;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class TimeSaver implements Serializable {
    public TimeSaver(Context context) {  //生成构造函数
        this.context = context;
    }

    Context context;   //用于读写内部文件


    public ArrayList<Time> getTime() {
        return time;
    }

    ArrayList<Time> time=new ArrayList<Time>();  //用于保存数据  //数据初始化一下

    public void save(){
        try{
            //序列化
            ObjectOutputStream outputStream = new ObjectOutputStream(context.openFileOutput("Serializable.txt",Context.MODE_PRIVATE));
            outputStream.writeObject(time);
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Time> load(){  //返回读入的数据
        try{
            //反序列化
            ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput("Serializable.txt"));
            time = (ArrayList<Time>) inputStream.readObject();
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return time; //返回读入的数据
    }
}
