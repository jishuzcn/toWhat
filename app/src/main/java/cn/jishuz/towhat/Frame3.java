package cn.jishuz.towhat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import cn.jishuz.towhat.bean.Topbar;


@SuppressLint("NewApi")
public class Frame3 extends Fragment implements OnClickListener{
	private Topbar top;
	private EditText search_edit;
	private ImageButton search_btn;
	private TextView res;
	private String reply = "";
	private String input;

	private Button[] btn = new Button[60];

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frame3, container, false);
		top = (Topbar) view.findViewById(R.id.frame3_tv);
		search_edit = (EditText) view.findViewById(R.id.search_edit);
		search_btn = (ImageButton) view.findViewById(R.id.search_btn);
		res = (TextView) view.findViewById(R.id.res);
		res.setMovementMethod(ScrollingMovementMethod.getInstance());

		for(int i = 0;i<btn.length;i++){
			int id = getResources().getIdentifier("btn"+(i+1),"id", getActivity().getPackageName());
			btn[i] = (Button) view.findViewById(id);
			btn[i].setOnClickListener(this);
		}

		Bundle bundle = getArguments();//从activity传过来的Bundle
		if(bundle!=null){
			input = bundle.getString("str");
			if (isContainChinese(input)) {// 如果传入的是汉字
				new Thread(getResult1).start();
			} else {
				new Thread(getResult2).start();
			}
		}

		search_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				input = search_edit.getText().toString();
				if (input.equals("")) {
					Toast.makeText(getActivity(), "请不要输入空字符..", Toast.LENGTH_LONG).show();
				} else {
					if (isContainChinese(input)) {// 如果输入的是汉字
						new Thread(getResult1).start();
					} else {
						new Thread(getResult2).start();
					}
				}
			}
		});

		top.setText("字典");
		return view;
	}

	@Override
	public void onClick(View v) {
		// 这个for循环有点浪费系统资源,但是如果用switch的话代码就太过冗长
		for(int i = 0;i<btn.length;i++){
			int id = getResources().getIdentifier("btn"+(i+1),"id", getActivity().getPackageName());
			if(v.getId() == id){
				input = btn[i].getText().toString();
				new Thread(getResult3).start();
				break;
			}
		}
	}

	Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				res.setText(reply);
			} else if (msg.what == 0x124) {
				Toast.makeText(getActivity(), "查询错误", Toast.LENGTH_SHORT).show();
				res.setText("");
			}
		}
	};

	private Runnable getResult1 = new Runnable() {
		public void run() {
			try {
				String APPKEY = "7bc4d75806e838b2f1b957121bd9afec";
				Map<String, String> params = new HashMap<String, String>();// 请求参数
				params.put("key", APPKEY);// 应用APPKEY(应用详细页查询)
				params.put("word", input);
				params.put("dtype", "");// 返回数据的格式,xml或json，默认json
				String url_path = getUrlWithQueryString("http://v.juhe.cn/xhzd/query", params);
				// System.out.println(url_path);
				/*
				 * String url_path = "http://fanyi.youdao.com/openapi.do?keyfrom=" + clientID +
				 * "&key=" + clientSecret + "&type=data&doctype=json&version=1.1&q=" +
				 * URLEncoder.encode("hello", "utf8");
				 */
				URL getUrl = new URL(url_path);
				HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
				connection.setConnectTimeout(3000);
				connection.connect();
				BufferedReader replyReader = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"));// 约定输入流的编码
				reply = replyReader.readLine();
				JSONObject replyJson = new JSONObject(reply);
				String errorCode = replyJson.getString("error_code");
				if (errorCode.equals("0")) {

					JSONObject result = replyJson.has("result") ? replyJson.getJSONObject("result") : null;
					String zi = result.getString("zi");
					String wubi = result.getString("wubi");
					String pinyin = result.getString("pinyin");
					String bushou = result.getString("bushou");
					String bihua = result.getString("bihua");
					String jijie = result.getString("jijie");
					String xiangjie = result.getString("xiangjie");
					reply = "字:" + zi + "\n\n拼音:" + pinyin + "\n\n五笔:" + "\n\n部首:" + bushou + "\n\n笔画:" + bihua
							+ "\n\n简解:" + jijie + "\n\n详解:" + xiangjie;
					hander.sendEmptyMessage(0x123);
				} else {
					// Message errorMsg=new Message();
					// int what = Integer.parseInt(errorCode);
					// errorMsg.what=what;
					hander.sendEmptyMessage(0x124);
				}

			} catch (Exception e) {
				Log.e("errss", e.toString());
				hander.sendEmptyMessage(0x124);
			}
		}
	};

	private Runnable getResult2 = new Runnable() {
		public void run() {
			try {
				// 配置您申请的KEY
				String APPKEY = "7bc4d75806e838b2f1b957121bd9afec";
				Map<String, String> params = new HashMap<String, String>();// 请求参数
				params.put("key", APPKEY);// 应用APPKEY(应用详细页查询)
				params.put("word", input);// 填写需要查询的拼音
				params.put("dtype", "");// 返回数据的格式,xml或json，默认json
				params.put("page", "");// 页数，默认1
				params.put("pageszie", "");// 每页返回条数，默认10 最大50
				params.put("isjijie", "");// 是否显示简解，1显示 0不显示 默认1
				params.put("isxiangjie", "");// 是否显示详解，1显示 0不显示 默认1
				String url_path = getUrlWithQueryString("http://v.juhe.cn/xhzd/querypy", params);
				// System.out.println(url_path);
				/*
				 * String url_path = "http://fanyi.youdao.com/openapi.do?keyfrom=" + clientID +
				 * "&key=" + clientSecret + "&type=data&doctype=json&version=1.1&q=" +
				 * URLEncoder.encode("hello", "utf8");
				 */
				URL getUrl = new URL(url_path);
				HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
				connection.setConnectTimeout(3000);
				connection.connect();
				BufferedReader replyReader = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"));// 约定输入流的编码
				reply = replyReader.readLine();
				JSONObject replyJson = new JSONObject(reply);
				String errorCode = replyJson.getString("error_code");
				if (errorCode.equals("0")) {

					JSONObject result = replyJson.has("result") ? replyJson.getJSONObject("result") : null;
					String chinese = "";
					if (result != null) {
						JSONArray list = result.getJSONArray("list");
						String totalcount = result.getString("totalcount");
						chinese += "总数---"+totalcount+"\n";
						for (int i = 0; i < list.length(); i++) {
							JSONObject ob = list.getJSONObject(i);
							chinese += "\t【" + (i + 1) + "】" + "字:" + ob.getString("zi") + " 拼音:" + ob.getString("py")
									+ " 五笔:" + ob.getString("wubi") + " 部首:" + ob.getString("bushou") + " 笔画:"
									+ ob.getString("bihua") + "\n";
						}
					}
					reply = chinese;
					hander.sendEmptyMessage(0x123);
				} else {
					// Message errorMsg=new Message();
					//int what = Integer.parseInt(errorCode);
					// errorMsg.what=what;
					hander.sendEmptyMessage(0x124);
				}

			} catch (Exception e) {
				Log.e("errss", e.toString());
				hander.sendEmptyMessage(0x124);
			}
		}
	};

	private Runnable getResult3 = new Runnable() {
		public void run() {
			try {
				// 配置您申请的KEY
				String APPKEY = "7bc4d75806e838b2f1b957121bd9afec";
				Map<String, String> params = new HashMap<String, String>();// 请求参数
				params.put("key", APPKEY);// 应用APPKEY(应用详细页查询)
				params.put("word", input);//笔画
				params.put("dtype", "");// 返回数据的格式,xml或json，默认json
				params.put("page", "");// 页数，默认1
				params.put("pageszie", "");// 每页返回条数，默认10 最大50
				params.put("isjijie", "");// 是否显示简解，1显示 0不显示 默认1
				params.put("isxiangjie", "");// 是否显示详解，1显示 0不显示 默认1
				String url_path = getUrlWithQueryString("http://v.juhe.cn/xhzd/querybs", params);
				// System.out.println(url_path);
				/*
				 * String url_path = "http://fanyi.youdao.com/openapi.do?keyfrom=" + clientID +
				 * "&key=" + clientSecret + "&type=data&doctype=json&version=1.1&q=" +
				 * URLEncoder.encode("hello", "utf8");
				 */
				URL getUrl = new URL(url_path);
				HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
				connection.setConnectTimeout(3000);
				connection.connect();
				BufferedReader replyReader = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"));// 约定输入流的编码
				reply = replyReader.readLine();
				JSONObject replyJson = new JSONObject(reply);
				String errorCode = replyJson.getString("error_code");
				if (errorCode.equals("0")) {

					JSONObject result = replyJson.has("result") ? replyJson.getJSONObject("result") : null;
					String chinese = "";
					if (result != null) {
						JSONArray list = result.getJSONArray("list");
						String totalcount = result.getString("totalcount");
						chinese += "总数---"+totalcount+"\n";

						for (int i = 0; i < list.length(); i++) {
							JSONObject ob = list.getJSONObject(i);
							chinese += "\t【" + (i + 1) + "】" + "字:" + ob.getString("zi") + " 拼音:" + ob.getString("pinyin")
									+ " 五笔:" + ob.getString("wubi") + " 部首:" + ob.getString("bushou") + " 笔画:"
									+ ob.getString("bihua") + "\n";
						}
					}
					reply = chinese;
					hander.sendEmptyMessage(0x123);
				} else {
					// Message errorMsg=new Message();
					//int what = Integer.parseInt(errorCode);
					// errorMsg.what=what;
					hander.sendEmptyMessage(0x124);
				}

			} catch (Exception e) {
				Log.e("errss", e.toString());
				hander.sendEmptyMessage(0x124);
			}
		}
	};

	// 根据api地址和参数生成请求URL
	public static String getUrlWithQueryString(String url, Map<String, String> params) {
		if (params == null) {
			return url;
		}

		StringBuilder builder = new StringBuilder(url);
		if (url.contains("?")) {
			builder.append("&");
		} else {
			builder.append("?");
		}

		int i = 0;
		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null) { // 过滤空的key
				continue;
			}

			if (i != 0) {
				builder.append('&');
			}

			builder.append(key);
			builder.append('=');
			builder.append(encode(value));

			i++;
		}

		return builder.toString();
	}

	// 进行URL编码
	public static String encode(String input) {
		if (input == null) {
			return "";
		}
		try {
			return URLEncoder.encode(input, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return input;
	}

	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
}