package com.example.music;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Music {
    //"/netease/cloudmusic/music"
    private static final String MUSIC_PATH = Environment.getExternalStorageDirectory() + "/netease/cloudmusic/music";
    private static MediaPlayer mediaPlayer;
    private static List<String> musicList;
    private  List<String> musicNameList;
    private static int songNum = 0;
    private static boolean firstPlay = true;
    private int currentPlayMode=1;


    public Music() {
        super();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayListener());
        musicList = new ArrayList<>();
        musicNameList = new ArrayList<>();
        try {

            File music_path = new File(MUSIC_PATH);//创建文件对象

            File[] fileList = music_path.listFiles();//创建文件数组

            //文件数组不为空
            if (null != fileList) {
                //遍历文件找出MP3文件
                for (File file : fileList) {
                    //以.MP3为结尾的文件
                    if (file.isFile() && file.getName().endsWith(".mp3")) {
                        //获取音乐文件的绝对路径
                        musicList.add(file.getAbsolutePath());
                        //打印文件路径
                        Log.i("TAg", file.getAbsolutePath());

                        //获取文件名
                        String musicName = file.getName().substring(0, file.getName().lastIndexOf("."));
                        //添加文件名
                        musicNameList.add(musicName);
                        //打印文件名
                        Log.i("文件名", musicName);


                    }
                }
            } else {

                Log.i("TAG", "文件未找到");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("warning? music", "文件读取异常！");
        }


    }

    public int getCurrentPlayMode() {
        return currentPlayMode;
    }

    /*
        改变播放模式
         */
    public void changePlayMode(){
    if (currentPlayMode==3){
        currentPlayMode=0;
    }
    currentPlayMode++;
    }
    /*
     随机播放
     */
    public void randomPosition(){
        int random = (int)(Math.random()*musicNameList.size());
        songNum = random;
    }
    /*
    播放第position歌
     */
    public void palyOnPosition(int position){

         songNum=position;
         play();
    }


    //初始化播放
    public void play() {
        try {
            mediaPlayer.reset();//重置多媒体
            String dataSource = musicList.get(songNum);//获取第一首歌的路径
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置媒体类型
            mediaPlayer.setDataSource(dataSource);//设置播放路径
            mediaPlayer.prepare();//准备播放歌曲
            mediaPlayer.start();//开始播放
        } catch (Exception e) {
            Log.i("TAG", e.getMessage());

        }


    }

    //继续播放
    public void goPlay() {
        //判断mediaplay的状态
        //mediaPlay不为空，没有播放，不是第一次播放，判断为继续播放
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && !firstPlay) {
            mediaPlayer.start();
        } else {
            //初始化播放
            firstPlay = false;//判断是否为第一次播放音乐
            play();//初始化播放
        }

    }

    //暂停播放
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            play();
        }
    }

    //上一曲
    public void previous() {


        if (currentPlayMode ==3){
            randomPosition();
            play();
        }else {
            //判断当前歌曲是否为第一首
            if (songNum == 0) {
                songNum = musicList.size()-1;
            } else {
                songNum = songNum - 1;
            }
            play();
        }
        Log.i("music-songNum", "上一曲-当前位置:" + songNum);
    }

    //下一曲
    public void next() {



        if (currentPlayMode ==3){
            randomPosition();
            play();
        }else {
            if (songNum == musicList.size() - 1) {
                songNum = 0;
            } else {
                songNum = songNum + 1;
            }
            play();
        }
        Log.i("music-songNum", "下一曲-当前位置:" + songNum);
    }

    /*
    播放指定位置
    progress为毫秒数
     */
    public void seekTo(Integer progress) {

        mediaPlayer.seekTo(progress);
        Log.i("music-songTime", "当前歌曲毫秒数:" + progress);
    }

    /*
    获取当前歌曲进度毫秒数
     */

    public int getCrruentProgress() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        } else if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /*
    获取当前歌曲是否在播放 ，播放返回true
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /*
     获取歌曲总数
     */
    public int getSongNumber() {
        return musicList.size();
    }


    /*
    返回当前歌曲游标
     */
    public int getSongNum() {
        return songNum;
    }

    /*
    返回当前歌曲名
     */
    public String getSongName(Integer songNum) {

        return musicNameList.get(songNum);
    }

    public List<String> getMusicNameList() {
        return musicNameList;
    }

    /*
            返回歌曲最大毫秒数
             */
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    /*
判断mediaPlay==null
为空返回true
 */
    public boolean isNull() {
        if (mediaPlayer == null) {

            return true;
        } else {

            return false;
        }
    }

    //格式化毫秒数
    public String getFormatMusicTime(Integer musicTime) {

        int sTime, mTime;
        String formatTime = "";
        sTime = musicTime / 1000 % 60;
        mTime = musicTime / 1000 / 60;

        if (mTime < 10) {
            formatTime = formatTime + "0" + mTime + ":";
        } else {
            formatTime = formatTime + mTime + ":";
        }
        if (sTime < 10) {
            formatTime = formatTime + "0" + sTime;
        } else {
            formatTime = formatTime + sTime;
        }
        //System.out.println(formatTime);

        return formatTime;

    }

    /*
    MediaPlay监听类
     */
    class MediaPlayListener implements MediaPlayer.OnCompletionListener {


        @Override
        public void onCompletion(MediaPlayer mp) {
            if (currentPlayMode== 1){
                //当前歌曲播放完后，调用next方法播放下一曲
                next();
            }else if (currentPlayMode == 2){
                //单曲循环
                play();
            }else if (currentPlayMode == 3){
                  //随机播放
                 randomPosition();
                 play();
            }

        }
    }
///////
}
