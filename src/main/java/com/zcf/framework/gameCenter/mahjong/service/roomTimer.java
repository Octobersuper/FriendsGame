package com.zcf.framework.gameCenter.mahjong.service;


import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:房间内的倒计时
 * Date:2019/2/22
 * Time:13:34
 */
public class roomTimer extends Thread{

    private RoomBean myRoom;

    private Integer timer;

    private Mahjong_Socket ws;

    public roomTimer(RoomBean myRoom, Integer timer, Mahjong_Socket ws) {
        this.myRoom = myRoom;
        this.timer = timer;
        this.ws = ws;
    }

    @Override
    public void run() {
        while (timer>0){
            if ( Thread.currentThread().isInterrupted() ) {
                System.out.println("线程终止");
                break;
            }
            if(ws.roomBean==null){
                System.out.println("线程中止，房间不存在");
                break;
            }
            try {
                Thread.sleep(1000);
                System.out.println(timer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timer--;
            if(timer == 0){
            	if(myRoom.getExit_game()==1){
            		System.out.println(myRoom.getExit_game());
            		ws.Exit_All();
            	}
            }
        }
    }
}
