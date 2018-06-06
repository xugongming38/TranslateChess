package com.xugongming38.translatechess;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏toolbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().setTitle("动物翻棋");//最上面一栏设置标题

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes, menu);
        return true;
    }//创建关于菜单

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id== R.id.about)
        {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            View localView = getLayoutInflater().inflate(R.layout.abouts, null);
            TextView localTextView1 = (TextView)localView.findViewById(R.id.title);
            Typeface localTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
            localTextView1.setTypeface(localTypeface);
            TextView localTextView2 = (TextView)localView.findViewById(R.id.content);
            SpannableString localSpannableString = new SpannableString("游戏规则：\n\n" +
                    "\n"+


                    "象>狮>虎>豹>狼>狗>猫>鼠\n" +
                    "特例：鼠能吃象！\n" +
                    "注意：先手为蓝色方！");
            Linkify.addLinks(localSpannableString,Linkify.ALL);
            localTextView2.setTypeface(localTypeface);
            localTextView2.setText(localSpannableString);
            localBuilder.setView(localView).setPositiveButton("确定！", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                {
                }
            });
            localBuilder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void reStart(View view){
        startActivity(new Intent(this,MainActivity.class));
    }
}
