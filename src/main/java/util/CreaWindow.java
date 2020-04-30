package util;/*
package com.zcf.mahjong.util;

import com.zcf.mahjong.bean.RoomBean;
import com.zcf.mahjong.mahjong.Public_State;
import com.zcf.mahjong.util.Mahjong_Util;
import com.zcf.mahjong.util.SelectServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class CreaWindow {
	public static void main(String[] args) {
		new CreaWindow().CreateWindow();
	}

	// 创建提示窗口
	private JFrame page_frame = null;
	private JTable page_table = null;
	private SelectServer page_model = null;
	private JScrollPane page_s_pan = null;
	private JScrollPane page_s_pan3 = null;
	JTextArea textArea2 = new JTextArea();
	JTextArea textArea = new JTextArea();
	JTextField textField1 = new JTextField();
	JTextField textField2 = new JTextField();
	private int num = 0;

	*/
/**
	 * 更新在线人数
	 * 
	 *//*

	public void updatethread_num(int number) {
		num += number;
		page_frame.setTitle("当前用户在线数量:" + num);
	}

	public void CreateWindow() {
		JPanel jPannel = new JPanel();
		JLabel label1 = new JLabel("房间号：");
		textField1.setColumns(6);
		JLabel label2 = new JLabel("牌值：");
		textField2.setColumns(5);
		// 添加列表
		page_frame = new JFrame("当前用户在线数量:0");
		page_model = new SelectServer(20);
		page_table = new JTable(page_model);
		page_s_pan = new JScrollPane(page_table);
		page_frame.getContentPane().add(page_s_pan, BorderLayout.CENTER);
		// 添加查询当前牌局列表的按钮
		JButton button = new JButton("查看当前房间信息");
		JButton button2 = new JButton("清空房间信息");
		JButton button3 = new JButton("调牌");
		JButton button4 = new JButton("清屏");
		jPannel.add(label1);
		jPannel.add(textField1);
		jPannel.add(label2);
		jPannel.add(textField2);
		jPannel.add(button3);
		jPannel.add(button);
		jPannel.add(button2);
		jPannel.add(button4);
		page_frame.getContentPane().add(jPannel, BorderLayout.NORTH);
		// 添加运行提示窗口
		textArea.setText("浩以麻将服务已启动,等待客户端连接!                                                \n");
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(Color.BLACK);// 黑色背景
		page_s_pan3 = new JScrollPane(textArea);
		page_frame.getContentPane().add(page_s_pan3, BorderLayout.WEST);
		page_frame.setSize(1300, 500);
		page_frame.setLocation(500, 500);
		page_frame.setVisible(true);
		// 关闭窗口就结束进程
		page_frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});
		String[] values = new String[] { "yiwan", "erwan", "sanwan", "siwan", "wuwan", "liuwan", "qiwan", "bawan",
				"jiuwan", "yibing", "erbing", "sanbing", "sibing", "wubing", "liubing", "qibing", "babing", "jiubing",
				"yitiao", "ertiao", "santiao", "sitiao", "wutiao", "liutiao", "qitiao", "batiao", "jiutiao", "dongfeng",
				"nanfeng", "xifeng", "beifeng", "hongzhong", "facai", "baipi" };
		// 调牌值
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String onwer = textField1.getText();
				String key = textField2.getText();
				for (int i = 0; i < values.length; i++) {
					if (key.equals(values[i])) {
						key = i + "";
						textField2.setText("");
						break;
					}
					if (i + 1 == values.length) {
						textArea.setText(textArea.getText() + "输入错误                                               \n");
					}
				}
				if (onwer != null && !"".equals(onwer)) {
					RoomBean pkbean = Public_State.PKMap.get(onwer);
					for (int i = 0; i < pkbean.getBrands().size(); i++) {
						int index = Mahjong_Util.mahjong_Util.getBrand_Value(pkbean.getBrands().get(i));
						if (index == Integer.parseInt(key)) {
							pkbean.setOnkey(i);
							textArea.setText(textArea.getText() + Mahjong_Util.mahjong_Util.getBrand_str(index)
									+ "已调                                                \n");
							break;
						}
						if (i + 1 == pkbean.getBrands().size()) {
							textArea.setText(
									textArea.getText() + "没有此牌                                               \n");
						}
					}
				} else {
					textArea.setText(textArea.getText() + "请输入房间号                                              \n");
				}
			}
		});
		// 查询当前房间信息
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				page_model.removeRow();
				page_table.updateUI(); // 刷新
				Map<String, RoomBean> pkmap = Public_State.PKMap;
				for (String key : pkmap.keySet()) {
					if (key == null) {
						continue;
					}
					Map<String, Object> pageMap = new HashMap<String, Object>();
					RoomBean pkbean = pkmap.get(key);
					pageMap.put("roomNo", key);
					String[] names = new String[] { "A", "B", "C", "D" };
					for (int i = 0; i < pkbean.getGame_userlist().size(); i++) {
						pageMap.put("user" + names[i], pkbean.getGame_userlist().get(i).getNickname());
					}
					int length = 8 - pkbean.getGame_userlist().size();
					for (int i = 8 - (length); i < 4; i++) {
						pageMap.put("user" + names[i], "未加入");
					}
					String nickname = pkbean.getBanker() == 0 ? "未选庄"
							: pkbean.getUserBean(pkbean.getBanker()).getNickname();
					pageMap.put("banker", nickname);
					pageMap.put("gamenumber", pkbean.getGame_number());
					pageMap.put("maxnumber", pkbean.getMax_number());
					// pageMap.put("draw_s",
					// Mahjong_Util.mahjong_Util.getBrand_str(pkbean.getDraw_s()));
					pageMap.put("max_person", pkbean.getMax_person() + "人房");
					String state = "";
					if (pkbean.getState() == 0)
						state = "等待加入";
					if (pkbean.getState() == 1)
						state = "准备阶段";
					if (pkbean.getState() == 2)
						state = "已开始";
					if (pkbean.getState() == 3)
						state = "正在结算";
					if (pkbean.getState() == 4)
						state = "已结算";
					pageMap.put("state", state);
					String room_type = "";
					*/
/*
					 * if (pkbean.getRoom_type() == 1) room_type = "初级"; if
					 * (pkbean.getRoom_type() == 2) room_type = "中级"; if
					 * (pkbean.getRoom_type() == 3) room_type = "高级";
					 * pageMap.put("game_type", pkbean.getRoom_type() == 0 ?
					 * "房卡模式" : room_type + "金币模式");
					 *//*

					page_model.addRow(pageMap);
					page_table.updateUI(); // 刷新
				}
			}
		});
		// 清空房间
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] keys = new String[Public_State.PKMap.size()];
				int i = 0;
				for (String key : Public_State.PKMap.keySet()) {
					keys[i] = key;
					i++;
				}
				for (int j = 0; j < keys.length; j++) {
					Public_State.PKMap.remove(keys[j]);
				}
			}
		});
		// 清屏
		button4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});
	}
}
*/
