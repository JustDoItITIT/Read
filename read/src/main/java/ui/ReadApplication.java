package ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/4/18.
 */
public class ReadApplication extends Application {

    private static ReadApplication instance;
    private Boolean isLogined;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        instance = this;
        sp=getSharedPreferences("login", Context.MODE_PRIVATE);
        editor=sp.edit();
        isLogined = sp.getBoolean("logined",false);
        super.onCreate();
    }

    public static ReadApplication getInstance(){
        return instance;
    }

    public boolean getFlag(){
        return isLogined;
    }

    public void setFlag(Boolean flag){
        isLogined = flag;
        editor.putBoolean("logined",isLogined);
        editor.commit();
    }


}
