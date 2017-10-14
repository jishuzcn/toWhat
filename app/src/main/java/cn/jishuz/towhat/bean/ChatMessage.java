package cn.jishuz.towhat.bean;
import java.util.Date;

public class ChatMessage {
    private String name;
    private String msg;
    private Type type;
    private Date date;

    private String imageUrl;

    public enum Type
    {
        INCOMING, OUTCOMING
    }

    public ChatMessage()
    {
    }



    public ChatMessage(String msg, Type type, Date date,String imageUrl)
    {
        super();
        this.msg = msg;
        this.type = type;
        this.date = date;
        this.imageUrl = imageUrl;
    }



    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public Date getDate()
    {
        return date;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
