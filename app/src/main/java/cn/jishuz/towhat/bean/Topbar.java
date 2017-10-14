package cn.jishuz.towhat.bean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.jishuz.towhat.About;
import cn.jishuz.towhat.R;

//这段代码网上搬过来的 主要目的是不重复造轮子 程序员应该要学会
@SuppressLint("NewApi")
public class Topbar extends LinearLayout{

	private TextView titleView;
	private ImageButton imageB;


	public Topbar(Context context) {
		this(context, null);
	}

	public void setText(String str) {
		titleView.setText(str);
	}

	public Topbar(Context context, AttributeSet attrs) {
		this(context, attrs, R.style.AppTheme);
	}

	public Topbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(final Context context) {
		View layout = LayoutInflater.from(context).inflate(R.layout.top1, this, true);

		titleView = (TextView) layout.findViewById(R.id.top_tv);
		imageB = (ImageButton) findViewById(R.id.top_about);
/*
		if (null != titleStr) {
			titleView.setText(titleStr);
			titleView.setTextSize(strSize);
			titleView.setTextColor(strColor);
			titleView.setTypeface(Typeface.DEFAULT);
		}*/

		imageB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context,About.class);
				context.startActivity(i);
//				Toast.makeText(getContext(), "hello", Toast.LENGTH_LONG).show();
			}
		});
	}
}
