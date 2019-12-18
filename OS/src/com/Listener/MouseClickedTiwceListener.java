package com.Listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public  class MouseClickedTiwceListener extends MouseAdapter {
    private static  boolean flag = false;		//双击事件已执行时置为真
    private static int clickNum = 1;		//指示鼠标点击次数，默认为单击
    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        final MouseEvent me = e;
        MouseClickedTiwceListener.flag= false;
        if (MouseClickedTiwceListener.clickNum==2) {
            //鼠标点击次数为2调用双击事件
            this.mouseClickedTwice(me);
            //调用完毕clickNum置为1
            MouseClickedTiwceListener.clickNum=1;
            MouseClickedTiwceListener.flag=true;
            return;
        }
        //新建定时器，双击检测间隔为500ms
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            //指示定时器执行次数
            int num = 0;
            @Override
            public void run() {
                // 双击事件已经执行，取消定时器任务
                if(MouseClickedTiwceListener.flag) {
                    num=0;
                    MouseClickedTiwceListener.clickNum=1;
                    this.cancel();
                    return;
                }
                //定时器再次执行，调用单击事件，然后取消定时器任务
                if (num==1) {
                    mouseClickedOnce(me);
                    MouseClickedTiwceListener.flag=true;
                    MouseClickedTiwceListener.clickNum=1;
                    num=0;
                    this.cancel();
                    return;
                }
                clickNum++;
                num++;
            }
        },new Date(), 500);
    }
    protected void mouseClickedOnce(MouseEvent me) {
        // 单击事件
        System.out.println("1");
    }
    private void mouseClickedTwice(MouseEvent me) {
        // 双击事件
        System.out.println("2");
    }

}

