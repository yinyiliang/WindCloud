package yyl.windcloud;

import android.app.Application;

import com.orhanobut.logger.Logger;

/**
 * Created by yinyiliang on 2016/7/28 0028.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init("测试");
    }
}
