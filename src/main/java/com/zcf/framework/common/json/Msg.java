package com.zcf.framework.common.json;
/*
 *@Author:ZhaoQi
 *@methodName:
 *@Params:
 *@Description:
 *@Return:
 *@Date:2019/6/11
 */
public class Msg {
    int code;//状态吗
    String msg;//提示信息
    Object data;//数据
    String type;//消息类型

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Msg(int code, String msg, Object data, String type) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.type = type;
    }
}
