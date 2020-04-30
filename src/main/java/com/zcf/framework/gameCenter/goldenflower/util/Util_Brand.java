package com.zcf.framework.gameCenter.goldenflower.util;
/***
 * 牌型计算
 * @author Administrator
 *
 */
public class Util_Brand {
	//用户手里的牌
	private int[] brand;
	//用户手里牌的总值
	private int count=0;
	//用户手里牌的花色0-3
	private int[] brands_type=new int[]{-1,-1,-1};
	//同牌型得牌值
	private int brandtype;
	//花色数量
	private int brandcool;
	
	public Util_Brand(int[] brand){
		this.brand = new int[brand.length];
		//整理出用户手里牌的真实牌值
		for(int i=0;i<brand.length;i++){
			if(brand[i]!=-1){
				this.brand[i]=brand[i]-13*(brand[i]/13)+1;
				this.count+=this.brand[i];
				//花色
				this.brands_type[i]=brand[i]/13;
			}
		}
		//冒泡
		for(int i=0;i<this.brand.length;i++){
			for(int j=i;j<this.brand.length;j++){
				if((j+1)<this.brand.length){
					if(this.brand[i]>this.brand[j+1]){
						int maxbrand = this.brand[i];
						this.brand[i]=this.brand[j+1];
						this.brand[j+1]=maxbrand;
					}
				}
			}
		}
	}
	/**
	 * 检测牌型是否相同
	 * @return
	 */
	private int GofenFlower(){
		int identical=0;//相同牌值
		//检测花色及豹子
		for(int i=0;i<this.brand.length;i++){
			if((i+1)<this.brand.length){
				//检测相同牌值
				if(this.brand[i]==this.brand[i+1]){
					//同牌型得数量
					identical++;
					//同牌型得牌值
					brandtype=this.brand[i];
				}
			}
		}
		return identical;
	}
	/**
	 * 检测豹子牌型（扎金花）
	 * @param
	 * @return 相同牌的数值
	 */
	public int GodenFlower_A(){
		int identical=GofenFlower();//相同牌值
		//豹子(3张牌值相同)
		if(identical==2){
			switch (count) {
			case 3://AAA
				return 990;
			case 39://KKK
				return 980;
			case 36://QQQ
				return 970;
			case 33://JJJ
				return 960;
			case 30://101010
				return 950;
			case 27://999
				return 940;
			case 24://888
				return 930;
			case 21://777
				return 925;
			case 18://666
				return 920;
			case 15://555
				return 915;
			case 12://444
				return 910;
			case 9://333
				return 905;
			case 6://222
				return 900;
			}
		}
		//不成豹子
		return -1;
	}
	/****
	 * 检测花色
	 * @return -1不同花 799=同花
	 */
	public int GodenFlower_B(){
		//检测花色及豹子
		for(int i=0;i<this.brand.length;i++){
			if((i+1)<this.brand.length){
				//检测花色
				if(brands_type[i]==brands_type[i+1])brandcool++;
			}
		}
		return brandcool==2?799:-1;
	}
	/***
	 * 检测顺子
	 * @return
	 */
	public int GodenFlower_C(){
		//为3则是顺子
		int shun=0;
		//顺子总值
		int shun_count=this.brand[0];
		//检测顺子(顺金)
		for(int i=0;i<this.brand.length;i++){
			if((i+1)<this.brand.length&&this.brand[i]!=-1){
				if((this.brand[i]+1)==this.brand[i+1]){
					shun++;
					shun_count+=this.brand[i+1];
				}
			}
		}
		//如果未检测花色则检测一次
		if(brandcool==0)GodenFlower_B();
		int number = brandcool==2?800:700;
		//同花顺和顺子
		if(shun==2){
			switch (shun_count) {
			case 26://QKA
				return number+90;
			case 6://123
				return number+80;
			case 36://JQK
				return number+70;
			case 33://10JQ
				return number+60;
			case 30://9 10 J
				return number+50;
			case 27://89 10
				return number+40;
			case 24://789
				return number+30;
			case 21://678
				return number+20;
			case 18://567
				return number+15;
			case 15://456
				return number+10;
			case 12://345
				return number+5;
			case 9://234
				return number;
			}
		}
		return -1;
	}
	/***
	 * 检测对子
	 * @return
	 */
	public int GodenFlower_D(){
		int identical=GofenFlower();//相同牌值
		if(identical==1){
			switch (brandtype) {
			case 1://AA
				return 690;
			case 13://KK
				return 680;
			case 12://QQ
				return 670;
			case 11://JJ
				return 660;
			case 10://10 10
				return 650;
			case 9://99
				return 640;
			case 8://88
				return 630;
			case 7://77
				return 625;
			case 6://66
				return 620;
			case 5://55
				return 615;
			case 4://44
				return 610;
			case 3://33
				return 605;
			case 2://22
				return 600;
			}
		}
		return -1;
	}
	/***
	 * 返回牌值或特殊牌型235
	 * @return
	 */
	public int GodenFlower_E(){
		//235牌型检测
		if(count==10){
			//如果任意2张牌花色相同就返回牌值特殊235牌型
			//if(brands_type[0]==brands_type[1]&&brands_type[1]==brands_type[2]&&brands_type[0]==brands_type[2])return 999;
		}
		//返回牌值
		return count;
	}
}
