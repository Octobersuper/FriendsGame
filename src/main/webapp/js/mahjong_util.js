	
	function IS_Victory(userBean,index,draw){
		var state=0;
		var brands = order_brand(userBean.brands,draw);
		//剔除癞子
		var draw_count = getdraw(brands, draw);
		brands=Remove_index(brands, draw);
		console.log("癞子数量"+draw_count);
		//朴(111)
		var pave_count_s = ISPave_Count_s(brands, draw);
		console.log("朴111:"+pave_count_s);
		//Sysout_Brand(brands);
		//用户碰的牌
		pave_count_s+=userBean.bump_brands.length/3;
		//用户暗杠的牌
		pave_count_s+=userBean.bar_brands.length/4;
		//朴(123)
		var pave_count = ISPave_Count(brands, draw);
		console.log("朴123:"+pave_count);
		//Sysout_Brand(brands);
		//将
		var double_count = ISDouble_Count(brands, draw);
		console.log("将"+double_count);
		//Sysout_Brand(brands);
		//4朴1将胡法
		if((pave_count_s+pave_count)==4&&double_count==1){
			console.log("4朴1将");
			state=900;
		}
		//1癞子3朴1将
		if(draw_count==1&&(pave_count_s+pave_count)==3&&double_count==1){
			state = IS_DrawBrands(brands)?890:0;
		}
		//1癞子4朴缺将
		if(draw_count==1&&(pave_count_s+pave_count)==4&&double_count==0){
			state = 880;
		}
		if(state==0){
			userBean.Remove_Brands(index);
		}
		return state;
	}
	
	//排序
	function order_brand(brands,draw){
		var brands_order=brands;
		for(var index=0;index<brands_order.length;index++){
			for(var j=index;j<brands_order.length;j++){
				if((j+1)<brands_order.length){
					if(parseInt(brands_order[index])>parseInt(brands_order[j+1])){
						var brand = brands_order[index];
						brands_order[index]=brands_order[j+1];
						brands_order[j+1]=brand;
					}
				}
			}
		}
		//手里的牌进行混排序
		if(draw!=-1){
			//混放在最前面
			for(var index=0;index<brands_order.length;index++){
				if(brands_order[index]==draw){
					for(var j=index;j>=0;j--){
						if((j-1)>=0){
							var index = brands_order[j];
							brands_order[j] = brands_order[j-1];
							brands_order[j-1] = index;
						}
					}
				}
			}
		}
		return brands_order;
	}
	//检测癞子的数量
	function getdraw(brands,draw){
		var count =0;
		for(var i=0;i<brands.length;i++){
			if(brands[i]==draw){
				count++;
			}
		}
		return count;
	}
	//剔除指定牌
	function Remove_index(brands,index){
		for(var i=0;i<brands.length;i++){
			if(brands[i]==index){
				brands[i]=-1;
			}
		}
		return brands;
	}
	//检测朴（123）
	function ISPave_Count(brands,draw){
		var pave_count=0;
		for(var i=0;i<brands.length;i++){
			//不检测癞子
			if(brands[i]==draw)continue;
			if(brands[i]==-1)continue;
			if(IS_Brands_A(brands, brands[i])){
				brands[i]=-1;
				pave_count++;
			}
		}
		return pave_count;
	}
	//检测朴(123)
	function IS_Brands_A(brands,draw){
		var brand_index=-1;
		var brand_t = ISUser_Mahjong(index);
		for(var i=0;i<brands.length;i++){
			if(brands[i]==-1)continue;
			var brand = ISUser_Mahjong(brands[i]);
			//牌值大1且花色相同的牌
			if((brand_t[0]+1)==brand[0]&&brand_t[1]==brand[1]){
				brand_index=i;
			}
			//牌值大2且花色相同的牌
			if((brand_t[0]+2)==brand[0]&&brand_t[1]==brand[1]){
				if(brand_index!=-1){
					brands[brand_index]=-1;
					brands[i]=-1;
					return true;
				}
			}
		}
		
		return false;
	}
	//获取单张牌值和花色
	function ISUser_Mahjong(index){
		var indexs = [];
		var color = -1;
		//获取单幅
		var value = getBrand_Value(index);
		//小于27的是万筒条
		if(value<27){
			//获取牌值ֵ
			index = value-9*(value/9);
			//获取花色
			color = (value/9);
		}else{
			color = value;
		}
		indexs[0] = index;
		indexs[1] = color;
		return indexs;
	}
	//获取单张牌值
	function getBrand_Value(index){
		return index-34*(index/34);
	}
	//检测将
	function ISDouble_Count(brands,draw){
		var count =0;
		for(var i=0;i<brands.length;i++){
			//不检测癞子
			if(brands[i]==draw)continue;
			if(brands[i]==-1)continue;
			if((i+1)<brands.length&&brands[i]==brands[i+1]){
				brands[i]=-1;
				brands[i+1]=-1;
				count++;
			}
		}
		return count;
	}
	//检测朴（111）
	function ISPave_Count_s(brands,draw){
		var pave_count = 0;
		for(var i=0;i<brands.length;i++){
			//不检测癞子
			if(brands[i]==draw)continue;
			if(brands[i]==-1)continue;
			if(IS_Brands_B(brands, brands[i])){
				brands[i]=-1;
				pave_count++;
			}
		}
		return pave_count;
	}
	//检测不完整的朴
	function IS_DrawBrands(brands){
		var brand = [];
		var count=0;
		for(var i=0;i<brands.length;i++){
			if(brands[i]==-1)continue;
			if(count==2)return false;
			brand[count]=brands[i];
			count++;
		}
		var brand_value = ISUser_Mahjong(brand[0]);
		var brand_value2 = ISUser_Mahjong(brand[1]);
		//AB
		if((brand_value[0]+1)==brand_value2[0]&&brand_value[1]==brand_value2[1]){
			return true;
		}
		//AC
		if((brand_value[0]+2)==brand_value2[0]&&brand_value[1]==brand_value2[1]){
			return true;
		}
		return false;
	}