package com.zcf.framework.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zcf.framework.common.json.Body;
import com.zcf.framework.gameCenter.mahjong.bean.Order;
import com.zcf.framework.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.LayuiJson;

import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhaoQi
 * @since 2019-07-06
 */
@Service
public class OrderServiceImpl{
	
	@Autowired
    OrderMapper om;

	public Body getOrder(Order o) {
		// TODO Auto-generated method stub
		return null;
	}

	public LayuiJson selectAll(int pageNum, int pageSize) {
        LayuiJson lj = new LayuiJson();
        Integer count = om.selectCount(null);
        EntityWrapper<Order> ew = new EntityWrapper<Order>();
        ew.orderBy("create_time", false);
        List<Order> list = om.selectPage(new Page<Order>(pageNum, pageSize), ew);
        if (list != null) {
            lj.setCode(0);
            lj.setCount(count);
            lj.setData(list);
            lj.setMsg("成功");
            return lj;
        }
        lj.setCode(1);
        lj.setCount(count);
        lj.setData(null);
        lj.setMsg("暂无数据");
        return lj;
    }

	public Body delete(Long id) {
		// TODO Auto-generated method stub
		Integer integer = om.deleteById(id);
		if(integer!=0){
			return Body.BODY_200;
		}
		return Body.BODY_451;
	}

}
