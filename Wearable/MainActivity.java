package xyz.hackbuster.myapplication;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private AVIMConversation conversation;

    private String TAG = "TicWear";
    private final int GRAVITY_ACCELERATION_THRESHOLD = 7;

    private boolean isHandRaisedFlag = false;
    private long timeStamp = 0;

    private SensorManager sensorManager;
    private Sensor mSensor;
    private Sensor gryoscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        AVOSCloud.initialize(this, "pkKqvyfLzVvv9tJAHLeu3EC8-gzGzoHsz", "qRSI1AtYqjqBw6eCN15OBJxB");
        //AVObject testObject = new AVObject("TestObject");
        //testObject.put("foo", "bar");
        //testObject.saveInBackground();

        AVIMClient tom = AVIMClient.getInstance("Jerry");
        tom.open(new AVIMClientCallback(){
            @Override
            public void done(AVIMClient client,AVIMException e){
                if(e==null){
                    //登录成功
                    final AVIMConversation conv = client.getConversation("57777e7f2e958a00558704ae");
                    conv.join(new AVIMConversationCallback(){
                        @Override
                        public void done(AVIMException e){
                            if(e==null){
                                //加入成功
                                conversation = conv;
                            }
                        }
                    });
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gryoscope = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gryoscope, SensorManager.SENSOR_DELAY_UI);

        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_HEART_RATE);
        Log.e("FQ", "sensor ---- " + list.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION) {
                //return;
                if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                    Log.d("haha", "hehee");
                    mTextView.setText(String.valueOf(event.values[0]));
                    send_heartrate(event.values[0]);
                }
                return;
            }
            Long currentTime = System.currentTimeMillis();

            if ((currentTime-timeStamp)>1000 && false == isHandRaisedFlag && event.values[2] < -GRAVITY_ACCELERATION_THRESHOLD) {
                Log.d(TAG, "举起手");
                isHandRaisedFlag = true;
                timeStamp = currentTime;
                send_plus();
            }
            else if ((currentTime-timeStamp)>1000 && true == isHandRaisedFlag && event.values[2] > GRAVITY_ACCELERATION_THRESHOLD) {
                Log.d(TAG, "放下手");
                isHandRaisedFlag = false;
                timeStamp = currentTime;
                send_minus();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void send_plus() {
        //mTextView.setText("clicked!!");
        if (conversation == null) return;
        AVIMTextMessage msg = new AVIMTextMessage();
        msg.setText("+");
        // 发送消息
        conversation.sendMessage(msg, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    Log.d("Tom & Jerry", "发送plus成功！");
                }
            }
        });
    }

    public void send_minus() {
        //mTextView.setText("clicked!!");
        if (conversation == null) return;
        AVIMTextMessage msg = new AVIMTextMessage();
        msg.setText("-");
        // 发送消息
        conversation.sendMessage(msg, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    Log.d("Tom & Jerry", "发送minus成功！");
                }
            }
        });
    }

    public void send_heartrate(float value) {
        if (conversation == null) return;
        AVIMTextMessage msg = new AVIMTextMessage();
        msg.setText(String.valueOf(value));
        // 发送消息
        conversation.sendMessage(msg, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    Log.d("Tom & Jerry", "发送heartrate成功！");
                }
            }
        });
    }

}
