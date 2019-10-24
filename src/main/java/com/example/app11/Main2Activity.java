package com.example.app11;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.music.Music;

import java.util.ArrayList;


public class Main2Activity extends AppCompatActivity {
    private static Music music = new Music();
    private TextView music_count;
    private ImageView music_photo;
    private ImageButton music_previous;
    private ImageButton music_next;
    private ImageButton music_play_or_pause, music_recycle;
    private SeekBar music_time;
    private Handler handler;
    private Thread musicProgressThread;
    private static final int UPDATE = 0x01;
    private boolean seekBarChange = true;
    private TextView music_current_time;
    private TextView music_duration_time;
    private long currentTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //隐藏actionbar
        /*
         if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
         */

        //获取读取存储的权限
        if (!hasPermission()) {
            requestPermission();

        }

        //初始化歌曲控件
        musicInit();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == UPDATE) {
                    music_time.setProgress(msg.arg1);
                    music_current_time.setText(music.getFormatMusicTime(msg.arg2));
                    music_duration_time.setText(music.getFormatMusicTime(music.getDuration()));
                }
            }
        };


        //创建子线程
        musicProgressThread = new Thread(new musicRun());
        //启动子线程
        musicProgressThread.start();
    }

    //更新线程内部类
    class musicRun implements Runnable {

        @Override
        public void run() {
            while (!musicProgressThread.currentThread().isInterrupted()) {

                if (!music.isNull() && music.isPlaying() && seekBarChange) {
                    int position = music.getCrruentProgress();//当前秒数
                    int mMax = music.getDuration();
                    int seekBar = position * 200 / mMax;
                    Message message = handler.obtainMessage();
                    message.arg1 = seekBar;
                    message.arg2 = position;
                    message.what = UPDATE;
                    //Log.i("MUSIC_TIME",position+":"+mMax+":"+seekBar);//测试打印时间
                    handler.sendMessage(message);
                    try {
                        musicProgressThread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("MUSIC_THREAD", "更新进度异常!");
                    }
                }
            }
          /*
            for (int i=0;i<100;i++){
                try {
                    handler.sendEmptyMessage(0x01);
                    musicProgressUpdate.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("MUSIC_TIME", "更新进度异常");
                }
                Log.i("run!!!!","progress");
            }
           */

        }
    }

    //初始化歌曲控件
    public void musicInit() {

        //初始化控件
        music_previous = findViewById(R.id.music_previous);
        music_next = findViewById(R.id.music_next);
        music_play_or_pause = findViewById(R.id.play_or_pause);
        music_count = findViewById(R.id.song_count);
        music_photo = findViewById(R.id.music_photo);
        music_time = findViewById(R.id.music_time);
        music_current_time = findViewById(R.id.music_current_time);
        music_duration_time = findViewById(R.id.music_duration_time);
        ImageButton music_list = findViewById(R.id.music_listActivity);
        music_recycle = findViewById(R.id.music_recycle);


        //给button添加监听
        music_play_or_pause.setOnClickListener(new MusicCtrl());
        music_next.setOnClickListener(new MusicCtrl());
        music_previous.setOnClickListener(new MusicCtrl());
        //music_count.setText("当前歌曲: " + music.getSongName(0) + "—" + "当前播放位置：" + music.getSongNum());
        music_time.setOnSeekBarChangeListener(new musicSeekBarCtrl());
        music_list.setOnClickListener(new MusicCtrl());
        music_recycle.setOnClickListener(new MusicCtrl());
    }

  /**
  按钮监听
   */
    class MusicCtrl implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_or_pause:
                    if (music.isPlaying()) {
                        //提示暂停
                        Toast.makeText(Main2Activity.this, "暂停", Toast.LENGTH_SHORT).show();
                        music.pause();//暂停播放
                        //清除动画
                        clearViewAnimation(music_photo);
                        //设置点击动画
                        setViewAnimation(music_play_or_pause, R.anim.img_btn_click);
                        //设置暂停播放图标
                        setViewImage(music_play_or_pause, R.drawable.zanting);

                    } else {


                        music.goPlay();//继续播放
                        //设置点击动画
                        setViewAnimation(music_play_or_pause, R.anim.img_btn_click);
                        //设置播放图标
                        setViewImage(music_play_or_pause, R.drawable.bofang);
                        //设置播放动画
                        setViewAnimation(music_photo, R.anim.img_xz);

                        //提示播放
                        Toast.makeText(Main2Activity.this, "播放", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.music_previous:

                    music.previous();
                    //设置播放图标，判断歌曲是否在播放
                    if (music.isPlaying()) {
                        setViewImage(music_play_or_pause, R.drawable.bofang);
                    }
                    //设置播放信息
                    music_count.setText("当前歌曲: " + music.getSongName(music.getSongNum()) + "—" + "当前播放位置：" + music.getSongNum());
                    //设置点击动画
                    setViewAnimation(music_previous, R.anim.img_btn_click);
                    break;
                case R.id.music_next:

                    music.next();
                    //设置播放图标,判断歌曲是否在播放
                    if (music.isPlaying()) {
                        setViewImage(music_play_or_pause, R.drawable.bofang);
                    }
                    //设置播放信息
                    music_count.setText("当前歌曲: " + music.getSongName(music.getSongNum()) + "—" + "当前播放位置：" + music.getSongNum());
                    //设置点击动画
                    setViewAnimation(music_next, R.anim.img_btn_click);

                    break;
                case R.id.music_listActivity:
                    Intent intent = new Intent();
                    intent.setClass(Main2Activity.this, MusicList.class);
                    intent.putStringArrayListExtra("musicNames", (ArrayList<String>) music.getMusicNameList());
                    startActivityForResult(intent, 0x01);
                    break;
                case R.id.music_recycle:
                    music.changePlayMode();
                    Log.i("playMode", "当前模式" + music.getCurrentPlayMode());
                    changePlayMode();
                    break;

            }
        }

        /**
         * @描述 设置播放模式图标
         * @返回值 void
         */
        private void changePlayMode() {

            if (music.getCurrentPlayMode() == 1) {
                showMessage("列表循环");
                setViewImage(music_recycle, R.drawable.liebiaoxunhuan);
            } else if (music.getCurrentPlayMode() == 2) {
                showMessage("单曲循环");
                setViewImage(music_recycle, R.drawable.danquxunhuan);
            } else if (music.getCurrentPlayMode() == 3) {
                showMessage("随机播放");
                setViewImage(music_recycle, R.drawable.suiji);
            }
        }
    }

    /**
     * @描述 弹出toast提示框
     * @参数 [message]
     * @返回值 void
     */
    private void showMessage(String message) {

        Toast.makeText(Main2Activity.this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 返回的Intent处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01 && resultCode == 0x01) {
            try {
                setViewImage(music_play_or_pause, R.drawable.bofang);
                Bundle bundle = data.getExtras();
                int position = bundle.getInt("songNum");
                music.palyOnPosition(position);
            } catch (Exception e) {
                Toast.makeText(Main2Activity.this, "播放异常！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }

    /*
         启动activity，flag=true关闭当前activity，flag=false不关闭当前界面

    public void openActivity( Class<?> activity, boolean flag) {
       Intent intent = new Intent(Main2Activity.this,activity);
       startActivity(intent);

       if (flag){
           finish();
       }
    }
*/


    /**
     * 返回键退出
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == event.KEYCODE_BACK) {
            exitSystem();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出
     */
    public void exitSystem() {
        //判断两次按键时间间隔小于3秒
        if (System.currentTimeMillis() - currentTime > 3000) {
            Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_LONG).show();
            currentTime = System.currentTimeMillis();
        } else {
            finish();
        }

    }

    /**
     * seekBar监听类
     */
    class musicSeekBarCtrl implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            seekBarChange = false;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = music_time.getProgress();
            int position = progress * music.getDuration() / 200;
            music.seekTo(position);
            seekBarChange = true;


        }
    }

    /**
     * *判断是否有读取存储的权限
     */
    private boolean hasPermission() {
        Log.i("TAG", "hasPermission:判断是否有权限");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        } else {

            return true;
        }

    }

    /**
     * 请求权限
     */

    private void requestPermission() {
        Log.i("requestPermission", "请求权限");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Log.i("TAG", "requestPermission:请求权限");
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    /**
     * 设置播放动画
     * setViewAnimation
     */
    public void setViewAnimation(ImageView imageView, Integer animResource) {
        Animation animation = AnimationUtils.loadAnimation(this, animResource);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        imageView.startAnimation(animation);

    }

    /**
     * 设置图标
     *
     * @param imageButton
     * @param imageResource
     */

    public void setViewImage(ImageButton imageButton, Integer imageResource) {
        imageButton.setBackgroundResource(imageResource);

    }

    /**
     * 清除播放动画
     * clearViewAnimation
     */
    public void clearViewAnimation(ImageView imageView) {
        imageView.clearAnimation();
    }
}
