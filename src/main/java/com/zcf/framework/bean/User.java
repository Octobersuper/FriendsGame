package com.zcf.framework.bean;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.activerecord.Model;

/**
 * <p>
 * 
 * </p>
 *
 * @author zq123
 * @since 2019-06-11
 */
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String wxname;
    private String wximg;
    private String openid;
    private Date createtime;
    private Integer money;
    private Integer issign;
    private Integer signdays;
    private Date signtime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWxname() {
        return wxname;
    }

    public void setWxname(String wxname) {
        this.wxname = wxname;
    }

    public String getWximg() {
        return wximg;
    }

    public void setWximg(String wximg) {
        this.wximg = wximg;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getIssign() {
        return issign;
    }

    public void setIssign(Integer issign) {
        this.issign = issign;
    }

    public Integer getSigndays() {
        return signdays;
    }

    public void setSigndays(Integer signdays) {
        this.signdays = signdays;
    }

    public Date getSigntime() {
        return signtime;
    }

    public void setSigntime(Date signtime) {
        this.signtime = signtime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "User{" +
        ", id=" + id +
        ", wxname=" + wxname +
        ", wximg=" + wximg +
        ", openid=" + openid +
        ", createtime=" + createtime +
        ", money=" + money +
        ", issign=" + issign +
        ", signdays=" + signdays +
        ", signtime=" + signtime +
        "}";
    }
}
