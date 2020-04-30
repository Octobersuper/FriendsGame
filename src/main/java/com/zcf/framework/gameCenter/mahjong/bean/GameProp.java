package com.zcf.framework.gameCenter.mahjong.bean;

/**
 * 转盘抽奖概率
 * @author Administrator
 *
 */
public class GameProp {

	private int proid;
	private int prop;// 概率
	private int awardline;// 价值
	private String awardname;// 奖品名称
	private int type;// 奖励类型

	public GameProp() {
	}

	public GameProp(int proid, int prop, int awardline, String awardname, int type) {
		super();
		this.proid = proid;
		this.prop = prop;
		this.awardline = awardline;
		this.awardname = awardname;
		this.type = type;
	}

	public int getProid() {
		return proid;
	}

	public void setProid(int proid) {
		this.proid = proid;
	}

	public int getProp() {
		return prop;
	}

	public void setProp(int prop) {
		this.prop = prop;
	}

	public int getAwardline() {
		return awardline;
	}

	public void setAwardline(int awardline) {
		this.awardline = awardline;
	}

	public String getAwardname() {
		return awardname;
	}

	public void setAwardname(String awardname) {
		this.awardname = awardname;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "GameProp [proid=" + proid + ", prop=" + prop + ", awardline=" + awardline + ", awardname=" + awardname
				+ ", type=" + type + "]";
	}

}
