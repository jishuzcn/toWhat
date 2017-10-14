package cn.jishuz.towhat.bean;

import java.text.SimpleDateFormat;
import java.util.List;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jishuz.towhat.R;


public class ChatMessageAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<ChatMessage> mDatas;
    private Context context;

    public ChatMessageAdapter(Context context, List<ChatMessage> mDatas)
    {
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        ChatMessage chatMessage = mDatas.get(position);
        if (chatMessage.getType() == ChatMessage.Type.INCOMING)
        {
            return 0;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ChatMessage chatMessage = mDatas.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            // 通过ItemType设置不同的布局
            if (getItemViewType(position) == 0)
            {
                convertView = mInflater.inflate(R.layout.item_from_msg, parent,
                        false);
                viewHolder = new ViewHolder();
                viewHolder.mDate = (TextView) convertView
                        .findViewById(R.id.id_form_msg_date);
                viewHolder.mMsg = (TextView) convertView
                        .findViewById(R.id.id_from_msg_info);
                viewHolder.mImg = (ImageView) convertView
                        .findViewById(R.id.id_from_msg_pic);

            } else
            {
                convertView = mInflater.inflate(R.layout.item_to_msg, parent,
                        false);
                viewHolder = new ViewHolder();
                viewHolder.mDate = (TextView) convertView
                        .findViewById(R.id.id_to_msg_date);
                viewHolder.mMsg = (TextView) convertView
                        .findViewById(R.id.id_to_msg_info);
                viewHolder.mImg = (ImageView) convertView
                        .findViewById(R.id.id_to_msg_pic);
            }
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 设置数据
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        viewHolder.mDate.setText(df.format(chatMessage.getDate()));
        viewHolder.mMsg.setText(chatMessage.getMsg());

        if(chatMessage.getImageUrl() != null){
            // 这里调用了图片加载工具类的setImage方法将图片直接显示到控件上
            viewHolder.mMsg.setText(chatMessage.getMsg()+chatMessage.getImageUrl());
            viewHolder.mMsg.setAutoLinkMask(Linkify.WEB_URLS);
            viewHolder.mMsg.setMovementMethod(LinkMovementMethod.getInstance());

            /*GetImageByUrl getImageByUrl = new GetImageByUrl();
            getImageByUrl.setImage(viewHolder.mImg, chatMessage.getImageUrl());*/
        }
        return convertView;
    }

    private final class ViewHolder
    {
        TextView mDate;
        TextView mMsg;
        ImageView mImg;
    }
}
