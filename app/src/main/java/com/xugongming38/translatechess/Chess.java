package com.xugongming38.translatechess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

/**
 * Created by dell on 2017/6/19.
 */

public class Chess {

    private boolean isSelected=false;

    //  用来标志是否为Me玩家
    private  int ME=1;

    //未初始化状态，用以标志该棋子的身份 1-8 代表鼠。。象
    private  int range=-1;


    public Chess(int ME, int range) {
        this.ME = ME;
        this.range = range;
    }

    //外部获取两个值，用于View绘制时选择图片
    public int getME() {
        return ME;
    }

    public int getRange() {
        return range;
    }

    public boolean GetSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    //用于比较两个棋的大小返回是否a>b,即a是否能吃b  同级也可以吃
    public static boolean CanEat(Chess a,Chess b){

        if(a.getRange()>=b.getRange()){
            if(a.getRange()==8&&b.getRange()==1)
                return false;
            return true;
        }else{
            if(a.getRange()==1&&b.getRange()==8)
                return true;
            return false;
        }
    }

    //可能预留判断是否可以过黄河森林的函数。。
}
