package com.zcf.framework.controller;

import com.zcf.framework.common.json.Body;
import com.zcf.framework.gameCenter.mahjong.bean.Order;
import com.zcf.framework.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import util.LayuiJson;

@CrossOrigin
@Controller
@ResponseBody
@RequestMapping("/order/")
public class OrderContoller {

	@Autowired
	private OrderServiceImpl os;
	
	
	/**
	 * 查询订单
	 * @param o
	 * @return
	 */
	@PostMapping("getOrder")
	public Body getOrder(Order o){
		return os.getOrder(o);
	}
	
	@PostMapping("/selectAll")
    @ResponseBody
    public LayuiJson selectAll(@RequestParam(value = "pageNum") int pageNum,
							   @RequestParam(value = "pageSize") int pageSize){
        return os.selectAll(pageNum,pageSize);
    }
	
	@PostMapping("/delete")
	@ResponseBody
	public Body delete(Long id){
		return os.delete(id);
	}
	
	
}
