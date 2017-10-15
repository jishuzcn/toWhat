package cn.jishuz.towhat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cn.jishuz.towhat.bean.Topbar;

@SuppressLint("NewApi")
public class Frame1 extends Fragment {

	private EditText edit;
	private Spinner spinner;
	private Button sub;
	private TextView result;
	private Topbar top;
	private static final String[] m = { "目标语言:英文", "目标语言:日文", "目标语言:韩文", "目标语言:法文", "目标语言:俄文", "目标语言:葡萄牙文", "目标语言:西班牙文","目标语言:中文"};
	private final Gson gson = new Gson();
	/*
	 * private String clientID="299ae3fafcfdce39"; private String
	 * clientSecret="4EXtcxiyxe09Uar6RyOpJukQHLBJQ02q";
	 */
	private String reply;
	private String quer; // 翻译的字
	private String to = "en"; // 翻译语言
	private String str;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frame1, container, false);
		edit = (EditText) view.findViewById(R.id.edit_test);
		result = (TextView) view.findViewById(R.id.text_result);
		spinner = (Spinner) view.findViewById(R.id.select);
		sub = (Button) view.findViewById(R.id.sub);
		top = (Topbar) view.findViewById(R.id.frame1_tv);
		top.setText("翻译");
		result.setMovementMethod(ScrollingMovementMethod.getInstance());

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, m);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				str = arg0.getItemAtPosition(position).toString();
			}
		});

		Bundle bundle = getArguments();//从activity传过来的Bundle
		if(bundle!=null){
			edit.setText(bundle.getString("str"));
			quer = bundle.getString("str");
			to = "en";
			new Thread(askForYouDao).start();
		}

		sub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				quer = edit.getText().toString();
				if (str.equals("目标语言:中文")) {
					to = "zh-CHS";
				}
				if (str.equals("目标语言:英文")) {
					to = "en";
				}
				if (str.equals("目标语言:日文")) {
					to = "ja";
				}
				if (str.equals("目标语言:韩文")) {
					to = "ko";
				}
				if (str.equals("目标语言:法文")) {
					to = "fr";
				}
				if (str.equals("目标语言:俄文")) {
					to = "ru";
				}
				if (str.equals("目标语言:葡萄牙文")) {
					to = "pt";
				}
				if (str.equals("目标语言:西班牙文")) {
					to = "es";
				}
				if (quer.equals("")) {
					Toast.makeText(getActivity(), "请输入文字...", Toast.LENGTH_SHORT).show();
				} else {
					new Thread(askForYouDao).start();
				}
			}
		});

		return view;
	}

	Handler errHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String notice = "";
			switch (msg.what) {
				case 101:
					notice = "缺少必填的参数，出现这个情况还可能是et的值和实际加密方式不对应";
					break;
				case 102:
					notice = "不支持的语言类型";
					break;
				case 103:
					notice = "翻译文本过长";
					break;
				case 104:
					notice = "不支持的API类型";
					break;
				case 105:
					notice = "不支持的签名类型";
					break;
				case 106:
					notice = "不支持的响应类型";
					break;
				case 107:
					notice = "不支持的传输加密类型";
					break;
				case 108:
					notice = "appKey无效";
					break;
				case 109:
					notice = "batchLog格式不正确";
					break;
				case 110:
					notice = "无相关服务的有效实例";
					break;
				case 111:
					notice = "开发者账号无效，可能是账号为欠费状态";
					break;
				case 201:
					notice = "解密失败，可能为DES,BASE64,URLDecode的错误";
					break;
				case 202:
					notice = "签名检验失败";
					break;
				case 203:
					notice = "访问IP地址不在可访问IP列表";
					break;
				case 302:
					notice = "翻译查询失败";
					break;
				case 303:
					notice = "服务端的其它异常";
					break;
				case 401:
					notice = "账户已经欠费停";
					break;
			}
			Toast.makeText(getActivity(), notice, Toast.LENGTH_SHORT).show();
		}
	};
	Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				result.setVisibility(View.VISIBLE);
				result.setText(reply);
			} else if (msg.what == 0x124) {
				Toast.makeText(getActivity(), "查询错误", Toast.LENGTH_SHORT).show();
				result.setText("");
			}
		}
	};
	private Runnable askForYouDao = new Runnable() {
		@Override
		public void run() {
			try {
				String appKey = "299ae3fafcfdce39";
				String salt = String.valueOf(System.currentTimeMillis());
				String from = "auto";
				String sign = md5(appKey + quer + salt + "4EXtcxiyxe09Uar6RyOpJukQHLBJQ02q");
				Map<String, String> params = new HashMap<String, String>();
				params.put("q", quer);
				params.put("from", from);
				params.put("to", to);
				params.put("sign", sign);
				params.put("salt", salt);
				params.put("appKey", appKey);
				String url_path = getUrlWithQueryString("https://openapi.youdao.com/api", params);
//				System.out.println(url_path);
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
				String errorCode = replyJson.getString("errorCode");
				if (errorCode.equals("0")) {

					JSONArray translation = replyJson.has("translation") ? replyJson.getJSONArray("translation") : null;
					String translationStr = "";
					if (translation != null) {
						translationStr = "\n翻译：\n";
						for (int i = 0; i < translation.length(); i++) {
							translationStr += "\t【" + (i + 1) + "】" + translation.getString(i) + "\n";
						}
					}

					if (to.equals("en") || to.equals("zh-CHS")) {
						String query = replyJson.getString("query");
						JSONObject basic = replyJson.has("basic") ? replyJson.getJSONObject("basic") : null;
						JSONArray web = replyJson.has("web") ? replyJson.getJSONArray("web") : null;
						String phonetic = null;
						String uk_phonetic = null;
						String us_phonetic = null;
						JSONArray explains = null;
						if (basic != null) {
							phonetic = basic.has("phonetic") ? basic.getString("phonetic") : null;
							uk_phonetic = basic.has("uk-phonetic") ? basic.getString("uk-phonetic") : null;
							us_phonetic = basic.has("us-phonetic") ? basic.getString("us-phonetic") : null;
							explains = basic.has("explains") ? basic.getJSONArray("explains") : null;
						}
						// if(web!=null){
						// JSONArray webs=web.getJSONObject()
						// }

						String phoneticStr = (phonetic != null ? "\n发音：" + phonetic : "")
								+ (uk_phonetic != null ? "\n英式发音：" + uk_phonetic : "")
								+ (us_phonetic != null ? "\n美式发音：" + us_phonetic : "");
						String explainStr = "";
						if (explains != null) {
							explainStr = "\n\n释义：\n";
							for (int i = 0; i < explains.length(); i++) {
								explainStr += "\t【" + (i + 1) + "】" + explains.getString(i) + "\n";
							}
						}
						reply = "原文：" + query + "\n" + translationStr + phoneticStr + explainStr;
					}else {
						reply = translationStr;
					}
					hander.sendEmptyMessage(0x123);
				} else {
					// Message errorMsg=new Message();
					int what = Integer.parseInt(errorCode);
					// errorMsg.what=what;
					Log.e("er", what + "");
					errHander.sendEmptyMessage(what);
				}
			} catch (Exception e) {
				Log.e("errss", e.toString());
				hander.sendEmptyMessage(0x124);
			}
		}
	};

	/*
	 * private void sendRequestWithHttpClient() { new Thread(new Runnable() {
	 *
	 * @Override public void run() { // TODO Auto-generated method stub try { String
	 * appKey = "299ae3fafcfdce39"; String query = "good"; String salt =
	 * String.valueOf(System.currentTimeMillis()); String from = "en"; String to =
	 * "zh_CHS"; String sign = md5(appKey + query + salt +
	 * "4EXtcxiyxe09Uar6RyOpJukQHLBJQ02q"); Map<String, String> params = new
	 * HashMap<String, String>(); params.put("q", query); params.put("from", from);
	 * params.put("to", to); params.put("sign", sign); params.put("salt", salt);
	 * params.put("appKey", appKey); String url =
	 * getUrlWithQueryString("https://openapi.youdao.com/api", params);
	 *
	 * HttpClient httpClient = new DefaultHttpClient(); HttpGet httpGet = new
	 * HttpGet(url);
	 *
	 * HttpResponse httpResponse = httpClient.execute(httpGet);
	 *
	 * if (httpResponse.getStatusLine().getStatusCode() == 200) { HttpEntity
	 * httpEntity = httpResponse.getEntity();
	 *
	 * String str = EntityUtils.toString(httpEntity, "utf-8"); //
	 * parseJSONWithGSON(str); // parseJSONWithJSONObject(str);
	 *
	 * JSONObject obj = JSONObject.fromObject(str); //获取Object中的UserName if
	 * (obj.has("UserName")) { System.out.println("UserName:" +
	 * obj.getString("UserName")); } //获取ArrayObject if (obj.has("Array")) {
	 * JSONArray transitListArray = obj.getJSONArray("Array"); for (int i = 0; i <
	 * transitListArray.size(); i++) { System.out.print("Array:" +
	 * transitListArray.getString(i) + " "); } } }
	 *
	 * } catch (Exception e) { // TODO: handle exception } } }).start(); }
	 *
	 * private void parseJSONWithJSONObject(String jsonData) { try { JSONArray
	 * jsonArray = new JSONArray(jsonData); for (int i = 0; i < jsonArray.length();
	 * i++) { JSONObject jsonObject = jsonArray.getJSONObject(i); String id =
	 * jsonObject.getString("ErrorCode"); String name =
	 * jsonObject.getString("Query"); String version =
	 * jsonObject.getString("Translation"); Log.d("MainActivity", "ErrorCode is " +
	 * id); Log.d("MainActivity", "Query is " + name); Log.d("MainActivity",
	 * "Translation is " + version); } } catch (Exception e) { e.printStackTrace();
	 * } }
	 *
	 * private void parseJSONWithGSON(String jsonData) { Gson gson = new Gson();
	 * List<Trans> appList = gson.fromJson(jsonData, new TypeToken<List<Trans>>() {
	 * }.getType()); for (Trans app : appList) { Log.d("MainActivity", "Query is " +
	 * app.getQuery()); Log.d("MainActivity", "Translation is " +
	 * app.getTranslation()); Log.d("MainActivity", "Basic is " + app.getBasic()); }
	 * }
	 *
	 *
	 * private void parseJsonWithGson(String str) { Gson gson = new Gson();
	 * List<Trans> t = gson.fromJson(str, new TypeToken<List<Trans>>() {
	 * }.getType()); for (Trans tran : t) { Log.d("Frame1", tran.getErrorCode());
	 * Log.d("Frame1", tran.getQuery()); Log.d("Frame1", tran.getTranslation());
	 * Log.d("Frame1", tran.getBasic()); Log.d("Frame1", tran.getWeb());
	 * System.out.println(tran.getErrorCode()); } }
	 */

	// 生成32位MD5摘要
	public static String md5(String string) {
		if (string == null) {
			return null;
		}
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		byte[] btInput = string.getBytes();
		try {
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (byte byte0 : md) {
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

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
}
