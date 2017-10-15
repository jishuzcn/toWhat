package cn.jishuz.towhat;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.jishuz.towhat.bean.SpeechRecognizerTool;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener, SpeechRecognizerTool.ResultsCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private LinearLayout trans_layout;
    private LinearLayout zhuan_layout;
    private LinearLayout dict_layout;
    private LinearLayout robot_layout;

    private ImageButton trans_btn;
    private ImageButton zhuan_btn;
    private ImageButton dict_btn;
    private ImageButton robot_btn;

    private TextView trans_tv;
    private TextView zhuan_tv;
    private TextView dict_tv;
    private TextView robot_tv;

    private Fragment frame1;
    private Fragment frame2;
    private Fragment frame3;
    private Fragment frame4;

    private AudioRecoderDialog recoderDialog;
    private ImageButton say;
    private long downT;

    private SpeechRecognizerTool mSpeechRecognizerTool = new SpeechRecognizerTool(this);
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        // 启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ConnectivityManager mConnectivityManager = (ConnectivityManager) this
                .getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo == null) {
            Toast.makeText(this, "网络不可用,请开启网络", Toast.LENGTH_LONG).show();
        }

        recoderDialog = new AudioRecoderDialog(this);
        recoderDialog.setShowAlpha(0.98f);

        initView();
        initEvent();
        setSelect(0, "");
        onUpdate();
    }

    public void onUpdate() {
        if (null != recoderDialog) {
            recoderDialog.setLevel(8);
            recoderDialog.setTime(System.currentTimeMillis() - downT);
        }
        mHandler.postDelayed(mUpdateStatusTimer, 100);
    }

    private final Handler mHandler = new Handler();

    private Runnable mUpdateStatusTimer = new Runnable() {
        public void run() {
            onUpdate();
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.say_btn:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PermissionUtils.requestPermission(this, PermissionUtils.CODE_RECORD_AUDIO, mPermissionGrant);
                    mSpeechRecognizerTool.startASR(MainActivity.this); //bd
                    downT = System.currentTimeMillis();
                    recoderDialog.showAtLocation(view, Gravity.CENTER, 0, 0);
                    say.setBackgroundResource(R.drawable.shape_recoder_btn_recoding);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mSpeechRecognizerTool.stopASR();//bd
                    recoderDialog.dismiss();
                    say.setBackgroundResource(R.drawable.shape_recoder_btn_normal);
                    return true;
                }
                break;
            case R.id.dict_layout:
                float firstX = 0,lastX;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    firstX = lastX = event.getX();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    lastX = event.getX();
                    if (lastX - firstX < 5) {//不是点击事件
                        ObjectAnimator animator = ObjectAnimator.ofFloat(dict_layout,"scaleX", 0, 2, 1);
                        animator.setDuration(2000);
                        animator.start();
                        return false;
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void initEvent() {
        // TODO Auto-generated method stub
        trans_layout.setOnClickListener(this);
        zhuan_layout.setOnClickListener(this);
        dict_layout.setOnClickListener(this);
        robot_layout.setOnClickListener(this);

        say.setOnTouchListener(this);
        dict_layout.setOnTouchListener(this);
        trans_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(trans_layout,"alpha",1,0,1,0,1);
                animator.setDuration(2000);
                animator.start();
                return false;
            }
        });
    }

    private void initView() {
        // TODO Auto-generated method stub
        trans_layout = (LinearLayout) findViewById(R.id.trans_layout);
        zhuan_layout = (LinearLayout) findViewById(R.id.zhuan_layout);
        dict_layout = (LinearLayout) findViewById(R.id.dict_layout);
        robot_layout = (LinearLayout) findViewById(R.id.robot_layout);

        trans_btn = (ImageButton) findViewById(R.id.trans_btn);
        zhuan_btn = (ImageButton) findViewById(R.id.zhuan_btn);
        dict_btn = (ImageButton) findViewById(R.id.dict_btn);
        robot_btn = (ImageButton) findViewById(R.id.robot_btn);

        trans_tv = (TextView) findViewById(R.id.trans_tv);
        zhuan_tv = (TextView) findViewById(R.id.zhuan_tv);
        dict_tv = (TextView) findViewById(R.id.dict_tv);
        robot_tv = (TextView) findViewById(R.id.robot_tv);

        say = (ImageButton) findViewById(R.id.say_btn);
        say.setBackgroundResource(R.drawable.shape_recoder_btn_normal);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        resetColor();
        switch (v.getId()) {
            case R.id.trans_layout:
                setSelect(0, "");
                break;
            case R.id.zhuan_layout:
                setSelect(1, "");
                ObjectAnimator animator = ObjectAnimator.ofFloat(zhuan_layout,"rotation",0,360,0);
                animator.setDuration(2000);
                animator.start();
                break;
            case R.id.dict_layout:
                setSelect(2, "");
                break;
            case R.id.robot_layout:
                setSelect(3, "");
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(robot_layout,"translationX",0,200,-200,0);
                animator1.setDuration(2000);
                animator1.start();
                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    private void setSelect(int i, String s) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();// 开始事务
        hiddenFragment(transaction);
        switch (i) {
            case 0:
                frame1 = new Frame1();
                if (!s.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("str", s);
                    frame1.setArguments(bundle);
                }
                transaction.add(R.id.content, frame1);
                trans_btn.setImageResource(R.drawable.tran2);
                trans_tv.setTextColor(Color.GREEN);
                break;
            case 1:
                frame2 = new Frame2();
                transaction.add(R.id.content, frame2);
                zhuan_btn.setImageResource(R.drawable.font2);
                zhuan_tv.setTextColor(Color.GREEN);
                break;
            case 2:
                frame3 = new Frame3();
                if (!s.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("str", s);
                    frame3.setArguments(bundle);
                }
                transaction.add(R.id.content, frame3);
                dict_btn.setImageResource(R.drawable.dict2);
                dict_tv.setTextColor(Color.GREEN);
                break;
            case 3:
                frame4 = new Frame4();
                transaction.add(R.id.content, frame4);
                robot_btn.setImageResource(R.drawable.robot2);
                robot_tv.setTextColor(Color.GREEN);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    @SuppressLint("NewApi")
    private void hiddenFragment(FragmentTransaction transaction) {
        // TODO Auto-generated method stub
        if (frame1 != null) {
            // transaction.hide(mTab01);
            transaction.remove(frame1);
        }

        if (frame2 != null) {
            transaction.remove(frame2);
        }

        if (frame3 != null) {
            transaction.remove(frame3);
        }

        if (frame4 != null) {
            transaction.remove(frame4);
        }
    }

    private void resetColor() {
        // TODO Auto-generated method stub
        trans_btn.setImageResource(R.drawable.tran1);
        zhuan_btn.setImageResource(R.drawable.font1);
        dict_btn.setImageResource(R.drawable.dict1);
        robot_btn.setImageResource(R.drawable.robot1);

        trans_tv.setTextColor(this.getResources().getColor(R.color.gray));
        zhuan_tv.setTextColor(this.getResources().getColor(R.color.gray));
        dict_tv.setTextColor(this.getResources().getColor(R.color.gray));
        robot_tv.setTextColor(this.getResources().getColor(R.color.gray));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSpeechRecognizerTool.createTool();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSpeechRecognizerTool.destroyTool();
    }

    @Override
    public void onResults(String result) {
        final String finalResult = result;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mTextView.setText(finalResult);
                Log.e("MainActivity", finalResult);
                String fanyi = "翻译";
                String zd = "查字典";
                int a = finalResult.indexOf(fanyi);
                int b = finalResult.indexOf(zd);
                if (a >= 0) {
//                    System.out.println("morning在字符串中的位置:"+a);
                    String ss = finalResult.substring(a + fanyi.length(), finalResult.length());// 去掉前面的翻译
                    resetColor();
                    setSelect(0, ss);
                } else if (b >= 0) {
                    String ss = finalResult.substring(b + zd.length(), finalResult.length());// 去掉前面的翻译
                    resetColor();
                    setSelect(2, ss);
                } else {
                    Toast.makeText(MainActivity.this, "请把口令说清楚,我没听清楚【" + finalResult + "】", Toast.LENGTH_LONG).show();
                }
//                Toast.makeText(MainActivity.this,"接口测试中 请说命令 如 翻译..",Toast.LENGTH_LONG).show();
            }
        });
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_RECORD_AUDIO:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_GET_ACCOUNTS:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_PHONE_STATE:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CALL_PHONE:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CAMERA:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    //Toast.makeText(MainActivity.this, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

/*
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }*/
}
