package cn.jishuz.towhat;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jishuz.towhat.bean.ChatMessage;
import cn.jishuz.towhat.bean.ChatMessageAdapter;
import cn.jishuz.towhat.bean.HttpUtils;

@SuppressLint("NewApi")
public class Frame4 extends Fragment {
    private ListView mMsgs;
    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> mDatas;

    private EditText mInputMsg;
    private Button mSendMsg;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame4, container, false);

        mMsgs = (ListView) view.findViewById(R.id.id_listview_msgs);
        mInputMsg = (EditText) view.findViewById(R.id.id_input_msg);
        mSendMsg = (Button) view.findViewById(R.id.id_send_msg);

        initDatas();
        // 初始化事件
        initListener();
        return view;
    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            // 等待接收，子线程完成数据的返回
            ChatMessage fromMessge = (ChatMessage) msg.obj;
            mDatas.add(fromMessge);
            mAdapter.notifyDataSetChanged();
            mMsgs.setSelection(mDatas.size()-1);
        };

    };

    private void initListener()
    {
        mSendMsg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String toMsg = mInputMsg.getText().toString();
                if (TextUtils.isEmpty(toMsg))
                {
                    Toast.makeText(getActivity(), "发送消息不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatMessage toMessage = new ChatMessage();
                toMessage.setDate(new Date());
                toMessage.setMsg(toMsg);
                toMessage.setType(ChatMessage.Type.OUTCOMING);
                toMessage.setImageUrl("");
                mDatas.add(toMessage);
                mAdapter.notifyDataSetChanged();
                mMsgs.setSelection(mDatas.size()-1);

                mInputMsg.setText("");

                new Thread()
                {
                    public void run()
                    {
                        ChatMessage fromMessage = HttpUtils.sendMessage(toMsg);
                        Message m = Message.obtain();
                        m.obj = fromMessage;
                        mHandler.sendMessage(m);
                    };
                }.start();

            }
        });
    }

    private void initDatas()
    {
        mDatas = new ArrayList<ChatMessage>();
        mDatas.add(new ChatMessage("你好，小图为您服务\n" +
                "吉凶查询|聊天对话|问答百科\n" +
                "生活百科|知识库|星座运势\n" +
                "新闻资讯|成语接龙|故事大全\n" +
                "菜谱大全|快递查询|笑话大全\n" +
                "天气查询|图片搜索|列车查询\n" +
                "航班查询|数字计算|日期查询...", ChatMessage.Type.INCOMING, new Date(),""));
        mAdapter = new ChatMessageAdapter(getActivity(), mDatas);
        mMsgs.setAdapter(mAdapter);
//        mMsgs.setEnabled(false);
    }
}
