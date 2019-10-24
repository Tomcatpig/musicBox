package com.example.app11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MusicList extends AppCompatActivity {
private List<String> musicNames = new ArrayList<>();
private ListView music_list ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        if (NavUtils.getParentActivityName(MusicList.this)!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
        Intent intent = getIntent();
        musicNames = intent.getStringArrayListExtra("musicNames");
    }
  /*
  初始化控件方法
   */
  private void  init(){
        music_list =findViewById(R.id.music_list);
        music_list.setAdapter(new MusicListAdapter());//设置适配器
        music_list.setOnItemClickListener(new OnItemClickListenerImpl());//设置点击监听事件
  }

  private  class OnItemClickListenerImpl implements AdapterView.OnItemClickListener{

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          Intent intent =getIntent();
          Bundle bundle = new Bundle();
          bundle.putInt("songNum",position);
          intent.putExtras(bundle);
          setResult(0x01,intent);
          Toast.makeText(MusicList.this,musicNames.get(position),Toast.LENGTH_SHORT).show();
          finish();
      }
  }

  /*
  music_list适配器
   */
  private class MusicListAdapter extends BaseAdapter {


      @Override
      public int getCount() {//返回歌曲总数
          return musicNames.size();
      }

      @Override
      public Object getItem(int position) {//返回position处的歌曲名
          return musicNames.get(position);
      }

      @Override
      public long getItemId(int position) {
          return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
       View view = View.inflate(MusicList.this,R.layout.music_list_item,null);
          TextView  music_name = view.findViewById(R.id.music_list_name);
          ImageView music_image  = view.findViewById(R.id.music_list_image);
          music_name.setText(musicNames.get(position));music_image.setImageResource(R.drawable.music_photo);
          return view;
      }
  }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (keyCode == event.KEYCODE_BACK){
          finish();
          return false;
      }

        return super.onKeyDown(keyCode, event);
    }

}
