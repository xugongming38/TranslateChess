package com.xugongming38.translatechess;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by dell on 2017/6/19.
 */

public class GameView extends View {


    private  int ME=1;
    private  int YOU=0;

    private int MoveSide=ME;//我先下蓝色方

    // 列的数量
    private static final int NUM_COLUMNS    =   4;
    // 行的数量
    private static final int NUM_ROWS       =   4;

    private int mColumnSize;

    private int mRowSize;
    private float width;
    private float height;
    private  Chess selectedChess=null;

    Chess [][] board= new Chess[4][4];
    int   [][]  map = new int[4][4];//用于检测翻牌动作

    // 棋盘背景颜色
    private int mBgColor = Color.parseColor("#F7F7F7");

    private DisplayMetrics mMetrics;
    private Paint mPaint;


    //由棋子的两个量确定图片id
    public  static int [][] picIds = {
            {
                    R.drawable.y8,R.drawable.y7,R.drawable.y6,R.drawable.y5,R.drawable.y4,R.drawable.y3,R.drawable.y2,R.drawable.y1
            },

            {
                    R.drawable.m8,R.drawable.m7,R.drawable.m6,R.drawable.m5,R.drawable.m4,R.drawable.m3,R.drawable.m2,R.drawable.m1
            }
    };

    private  Bitmap[][] bitmaps = new Bitmap [2][8];
    private  Bitmap select=null;
    private  Bitmap trans=null;

    Context context=null;



    public GameView(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    //初始化棋盘
    private void init() {

        // 获取手机屏幕参数
        mMetrics = getResources().getDisplayMetrics();
        // 创建画笔
        mPaint = new Paint();


        for(int i=0;i<NUM_COLUMNS;i++)
            for(int j=0;j<NUM_COLUMNS;j++){
                map[i][j]=0;
            }


        //用于分别生成两类棋子，并赋值,写的可能不容易读懂
        int t=0;
        int range=0;
        for(int i=0;i<NUM_COLUMNS;i++)
            for(int j=0;j<NUM_COLUMNS;j++){
                if(i>=(NUM_COLUMNS/2)){
                    t=1;
                }
                board[i][j]= new Chess(t,((range++)%8+1) );
            }

        for(int i=0;i<NUM_COLUMNS;i++){
            for(int j=0;j<NUM_COLUMNS;j++){
                System.out.print(board[i][j].getME()+""+board[i][j].getRange()+" ");
            }
            System.out.println("");
        }

        //初始化图片
        for(int i=0;i<2;i++)
            for(int j=0;j<8;j++)
                bitmaps[i][j]= BitmapFactory.decodeResource(getResources(),picIds[i][j]);

        select=BitmapFactory.decodeResource(getResources(),R.drawable.select);
        Matrix matrix = new Matrix();
        matrix.postScale(bitmaps[0][0].getHeight()*1.0f/select.getHeight(),bitmaps[0][0].getHeight()*1.0f/select.getHeight()); //长和宽放大缩小的比例
        select = Bitmap.createBitmap(select,0,0,select.getWidth(),select.getHeight(),matrix,true);
        trans  = BitmapFactory.decodeResource(getResources(),R.drawable.init);
        confuseBoard();
    }

    /**
     * 初始化列宽和高
     */
    private void initSize() {


        width=getWidth();
        height=getHeight();

        // 初始化每列的大小
        mColumnSize =  (int)width/ NUM_COLUMNS;
        // 初始化每行的大小
        mRowSize = mColumnSize;
        //System.out.println(mColumnSize+" "+mRowSize);
    }
    private void DrawBroad(Canvas canvas) {

        //黑色棋盘
        mPaint.setColor(Color.BLACK);

        mPaint.setStrokeWidth(6);

        for (int i = 0; i <= NUM_COLUMNS; i++){
            float startX = 0;
            float stopX = width;
            float startY = (float) (i* mColumnSize);
            float stopY = (float) (i* mColumnSize);
            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
            canvas.drawLine(startY, startX, stopY, stopX, mPaint);

        }


    }
    @Override
    protected void onDraw(Canvas canvas) {
        /*
        DrawBroad(canvas);
        DrawPiece(canvas);
        checkGameOver();
        */
        initSize();//必须在此调用，否则为0！！

        // 绘制棋盘
        DrawBroad(canvas);

        for(int j=0;j<4;j++)
            for(int i=0;i<4;i++){
                if(board[i][j]==null)
                    continue;

                Bitmap Tbitmap =bitmaps[board[i][j].getME()][board[i][j].getRange()-1];
                int startX=(int)(mColumnSize*i)+(mColumnSize-Tbitmap.getWidth())/2;
                int startY=(int)(mRowSize*j)+(mRowSize-Tbitmap.getHeight())/2;
                if(map[i][j]==0)
                    Tbitmap=trans;

                //System.out.println(startX+" "+startY);
                canvas.drawBitmap(Tbitmap, startX, startY, mPaint);
                if(board[i][j].GetSelected()){
                    canvas.drawBitmap(select, startX, startY, mPaint);
                    board[i][j].setSelected(false);
                }
            }

        checkGameOver();
    }

    //判断是否输赢
    private void checkGameOver() {
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                if(map[i][j]==0)
                    return;
        int sumOfMe=0,sumOfYou=0;

        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                if(board[i][j]!=null){
                    if(board[i][j].getME()==1)
                        sumOfMe++;
                    else
                        sumOfYou++;
                }

        if(sumOfMe==0)
        {
            showResult("红方胜");
        }
        if(sumOfYou==0)
        {
            showResult("蓝方胜");
        }


    }

    private void showResult(String s) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        View localView = LayoutInflater.from(context).inflate(R.layout.abouts, null);
        TextView localTextView1 = (TextView)localView.findViewById(R.id.title);
        Typeface localTypeface = Typeface.createFromAsset(context.getAssets(), "Roboto-Thin.ttf");
        localTextView1.setTypeface(localTypeface);
        TextView localTextView2 = (TextView)localView.findViewById(R.id.content);
        SpannableString localSpannableString = new SpannableString(s);
        Linkify.addLinks(localSpannableString,Linkify.ALL);
        localTextView2.setTypeface(localTypeface);
        localTextView2.setText(localSpannableString);
        localBuilder.setView(localView).setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
            }
        });
        localBuilder.show();
    }

    //打乱棋盘
    private  void  confuseBoard(){
        //随机打乱棋盘次数为10次
        int times=10;
        int ti1=-1,tj1=-1;
        int ti2=-1,tj2=-1;
        Chess tChess=null;
        Random random=new Random();
        for(int t=0;t<times;t++){

            ti1=random.nextInt(NUM_COLUMNS);
            tj1=random.nextInt(NUM_COLUMNS);
            ti2=random.nextInt(NUM_COLUMNS);
            tj2=random.nextInt(NUM_COLUMNS);

            tChess=board[ti1][tj1];
            board[ti1][tj1]=board[ti2][tj2];
            board[ti2][tj2]=tChess;


        }
    }


    private int downX = 0,downY = 0;
    private int SelectedR=-1,SelectedC=-1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventCode = event.getAction();
        switch(eventCode){
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:


                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if(Math.abs(upX - downX) < 10 && Math.abs(upY - downY) < 10){
                    //performClick();
                    onClick((upX + downX) / 2, (upY + downY) / 2);
                }
                break;
        }
        return true;
    }

    private void onClick(int x, int y) {
        int row = y / mRowSize;
        int column = x / mColumnSize;
        if (column >= NUM_COLUMNS || row >= NUM_ROWS) {
            if (selectedChess != null) {
                selectedChess.setSelected(false);
                SelectedR = -1;
                SelectedC = -1;
                selectedChess = null;
            }
            invalidate();
            return;
        }

        if (map[column][row] == 0) {
            map[column][row] = 1;
            changeMoveSide();
            invalidate();
            return;
        }

        //已经翻开
        if (map[column][row] == 1){
            if (board[column][row] != null) {
                if (board[column][row].getME() == MoveSide) {
                    //清空上一次选定项
                    if(selectedChess!=null) {
                        selectedChess.setSelected(false);
                        SelectedR = -1;
                        SelectedC = -1;
                    }

                    board[column][row].setSelected(true);
                    selectedChess = board[column][row];
                    SelectedR = row;
                    SelectedC = column;
                    invalidate();
                }else{//此次选中非己方棋子
                    if(selectedChess!=null){
                        if(JudgeEat(column,row,SelectedC,SelectedR)){
                            board[column][row]=board[SelectedC][SelectedR];
                            board[SelectedC][SelectedR]=null;
                            selectedChess.setSelected(false);

                            selectedChess=null;
                            SelectedR = -1;
                            SelectedC = -1;

                            changeMoveSide();
                            invalidate();
                        }
                    }
                }
            }else{
                if(selectedChess!=null){
                    if(JudgeBeside(column,row,SelectedC,SelectedR)){
                        board[column][row]=selectedChess;
                        board[SelectedC][SelectedR]=null;//!!!
                        SelectedR = -1;
                        SelectedC = -1;

                        changeMoveSide();
                        invalidate();
                    }
                }
            }
        }


    }

    private boolean JudgeEat(int currentC,int currentR,int selectC,int selectR) {
        if(JudgeBeside(currentC, currentR,selectC, selectR)){
            return Chess.CanEat(board[selectC][selectR],board[currentC][currentR]);

        }
        return false;
    }

    private boolean JudgeBeside(int currentC,int currentR,int selectC,int selectR) {
        if(currentC==selectC){
            if(currentR+1==selectR||currentR-1==selectR)
                return true;
            return false;
        }

        if(currentR==selectR){
            if(currentC+1==selectC||currentC-1==selectC)
                return true;
            return false;
        }

        return false;
    }


    public void changeMoveSide() {
        if(MoveSide==ME)
            MoveSide=YOU;
        else
            MoveSide=ME;
    }

}
