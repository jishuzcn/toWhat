package cn.jishuz.towhat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class About extends Activity {

	private TextView tv1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		tv1 = (TextView) findViewById(R.id.about_one);
		tv1.setText("语音口令使用方法:\n\n" +
				"翻译某字或一句话---请先说口令【翻译】再加上你想要翻译的字或一句话\n\n" +
				"查字典同上一样---请先说口令【查字典】再加上你想要查的字");
//		Toast.makeText(this,"注意应用必须要联网",Toast.LENGTH_LONG).show();
	}
	
}
