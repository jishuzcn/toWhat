package cn.jishuz.towhat;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.jishuz.towhat.bean.Topbar;

@SuppressLint("NewApi")
public class Frame2 extends Fragment {

	private WebView webView;
	private Topbar top;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frame2, container, false);
		webView = (WebView) view.findViewById(R.id.web_view);
		top = (Topbar) view.findViewById(R.id.frame2_tv);
		top.setText("篆体");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url); // ????????????????????????
				return true;
			}
			
			public void onPageFinished(WebView view, String url) {
	            view.loadUrl("javascript:function setTop(){document.querySelector('body > div.bottom').style.display=\"none\";}setTop();"); 
			      /*view.loadUrl("javascript:function setTop(){document.querySelector('.t2a').style.display=\"none\";"
			      		+ "document.querySelector('body > img').style.display=\"none\";"
			      		+ "document.querySelector('body > div.bottom').style.display=\"none\";}setTop();");  */
			}
		});
		
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int newProgress) {
	            view.loadUrl("javascript:function setTop(){document.querySelector('body > div.bottom').style.display=\"none\";}setTop();"); 
				/*view.loadUrl("javascript:function setTop(){document.querySelector('.t2a').style.display=\"none\";"
				      		+ "document.querySelector('body > img').style.display=\"none\";"
				      		+ "document.querySelector('body > div.bottom').style.display=\"none\";}setTop();");  */
	            super.onProgressChanged(view, newProgress);
	        }
		});
		webView.loadUrl("http://www.jurons.com/");
		return view;
	}
}
