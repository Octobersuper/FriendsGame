package com.zcf.framework.gameCenter.goldenflower.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.zcf.framework.gameCenter.goldenflower.bean.F_PKBean;
import com.zcf.framework.gameCenter.goldenflower.bean.F_PublicState;
import util.SelectServer;


public class CreaWindow {
	public static void main(String[] args) {
		new CreaWindow().CreateWindow();
	}
	 //创建提示窗口
    private JFrame page_frame = null;
    private JTable page_table = null;
    private SelectServer page_model = null;
    private JScrollPane page_s_pan = null;
    private JScrollPane page_s_pan3 = null;
    JTextArea textArea2 = new JTextArea();
    JTextArea textArea = new JTextArea();
    JTextField textField1=new JTextField();
    JTextField textField2=new JTextField();
    private int num=0;
    /**
     * 更新在线人数
     * @param num
     */
    public void updatethread_num(int number){
    	num+=number;
    	page_frame.setTitle("当前用户在线数量"+num);
    }
    public void CreateWindow(){
    	JPanel jPannel = new JPanel();
    	JLabel label1=new JLabel("房间号：");
  		textField1.setColumns(6);
    	JLabel label2=new JLabel("牌值：");
  		textField2.setColumns(5);
    	//添加列表
    	page_frame = new JFrame("当前用户在线数量:0");
    	page_model = new SelectServer(20);
    	page_table = new JTable(page_model);
        page_s_pan = new JScrollPane(page_table);
        page_frame.getContentPane().add(page_s_pan,BorderLayout.CENTER);
        //添加查询当前牌局列表的按钮
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
        //添加运行提示窗口
        textArea.setText("扎金花服务已启动,等待客户端连接!                                                \n");
    	textArea.setForeground(Color.WHITE);
    	textArea.setBackground(Color.BLACK);//黑色背景
    	page_s_pan3 = new JScrollPane(textArea);
    	page_frame.getContentPane().add(page_s_pan3, BorderLayout.WEST);
        page_frame.setSize(1000, 500);
        page_frame.setLocation(500, 500);
        page_frame.setVisible(true);
        //关闭窗口就结束进程
        page_frame.addWindowListener(new WindowAdapter() {  
    		public void windowClosing(WindowEvent e) {  
	    		super.windowClosing(e);
	    		 System.exit(0);
    		}
    	});
        //调牌值
        button3.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String onwer = textField1.getText();
        		String key = textField2.getText();
        		if(onwer!=null&&!"".equals(onwer)){
        			F_PKBean pkbean = F_PublicState.PKMap.get(onwer);
        			System.out.println(pkbean.getBrand());
        			if(pkbean.getBrands().indexOf(key)!=-1){
        				pkbean.setOnkey(Integer.parseInt(key));
        				System.out.println(textArea.getText()+key+"牌值已调\n");
        			}else{
        				System.out.println(textArea.getText()+"无此牌\n");
        			}
        		}
        	}
        });
        //查询当前房间信息
    	button.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 page_model.removeRow();
            	 page_table.updateUI(); // 刷新
            	 Map<String,F_PKBean> pkmap = F_PublicState.PKMap;
            	 for(String key:pkmap.keySet()){
            		 if(key==null){
            			 continue;
            		 }
	            	 Map<String,Object> pageMap = new HashMap<String, Object>();
	            	 F_PKBean pkbean = pkmap.get(key);
	            	 pageMap.put("roomNo",key);
	            	 String[] names = new String[]{"A","B","C","D","E","F"};
	            	 for(int i=0;i<pkbean.getGame_userList(0).size();i++){
	            		 pageMap.put("user"+names[i],pkbean.getGame_userList(0).get(i).getNickname());
	            	 }
	            	 int length = 8-pkbean.getGame_userList(0).size();
	            	 for(int i=8-(length);i<6;i++){
	            		 pageMap.put("user"+names[i], "未加入");
	            	 }
	            	 pageMap.put("number", pkbean.getGame_number());
	            	 page_model.addRow(pageMap);
	            	 page_table.updateUI(); // 刷新
            	 }
             }
         });
    	//清空房间
    	button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String[] keys = new String[F_PublicState.PKMap.size()];
            	int i=0;
	           	 for(String key :F_PublicState.PKMap.keySet()){
	           		keys[i]=key;
	           		i++;
	           	 }
	           	 for(int j=0;j<keys.length;j++){
	           		F_PublicState.PKMap.remove(keys[j]);
	           	 }
            }
        });
    	//清屏
    	button4.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 textArea.setText("");
             }
         });
     }
}
