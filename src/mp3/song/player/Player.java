package mp3.song.player;


import java.io.File;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Player extends Activity implements OnClickListener {

	static MediaPlayer mp;
	ArrayList<File> mySongs;
	
	Uri u;
	int position;
	String title;
	TextView tv;
	
	SeekBar sb;
	Button btPv, btFB, btPlay, btFF, btNxt;
	Thread updateSeekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		
		btPv = (Button) findViewById(R.id.btPv);
		btFB = (Button) findViewById(R.id.btFB);
		btPlay = (Button) findViewById(R.id.btPlay);
		btFF = (Button) findViewById(R.id.btFF);
		btNxt = (Button) findViewById(R.id.btNxt); 
		sb = (SeekBar) findViewById(R.id.seekBar);
		tv = (TextView) findViewById(R.id.textView);
		
		btPv.setOnClickListener(this);
		btFB.setOnClickListener(this);
		btPlay.setOnClickListener(this);
		btFF.setOnClickListener(this);
		btNxt.setOnClickListener(this);
		
		updateSeekBar = new Thread() {
			public void run() {
				int totalDuration = mp.getDuration();
				int currentPosition = 0;
				
				while(currentPosition < totalDuration) {
					try {
						sleep(500);
						currentPosition = mp.getCurrentPosition();
						sb.setProgress(currentPosition);
					} catch (InterruptedException e) {
						Log.e("error", e.getMessage());
					}
				}
			}
		};
		
		if(mp != null) {
			mp.stop();
			mp.release();
		}
		
		Intent i = getIntent();
		Bundle b = i.getExtras();
		
		mySongs = (ArrayList<File>) b.getSerializable("songlist");
		position = b.getInt("pos", 0);
		
		u = Uri.parse(mySongs.get(position).toString());
		mp = MediaPlayer.create(getApplicationContext(), u);
		mp.start();
		sb.setMax(mp.getDuration());
		
		title = mySongs.get(position).getName().toString().replace(".mp3", "");
		tv.setText(title);
		
		updateSeekBar.start();
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mp.seekTo(seekBar.getProgress());
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		switch(id) {
		
		case R.id.btPlay:
			if(mp.isPlaying()) {
				Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
				btPlay.setText("=>");
				mp.pause();
			}
			else {
				Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
				btPlay.setText("||");
				mp.start();
			}
			break;
			
		case R.id.btFF:
			Toast.makeText(getApplicationContext(), "Fast Forward", Toast.LENGTH_SHORT).show();
			mp.seekTo(mp.getCurrentPosition()+5000);
			break;
			
		case R.id.btFB:
			Toast.makeText(getApplicationContext(), "Fast Backward", Toast.LENGTH_SHORT).show();
			mp.seekTo(mp.getCurrentPosition()-5000);
			break;
			
		case R.id.btNxt:
			Toast.makeText(getApplicationContext(), "Next", Toast.LENGTH_SHORT).show();
			mp.stop();
			mp.release();
			
			position = (position+1)%mySongs.size();
			title = mySongs.get(position).getName().toString().replace(".mp3", "");
			tv.setText(title);
			u = Uri.parse(mySongs.get(position).toString());
			mp = MediaPlayer.create(getApplicationContext(), u);
			mp.start();
			sb.setMax(mp.getDuration());
			break;
			
		case R.id.btPv:
			Toast.makeText(getApplicationContext(), "Previous", Toast.LENGTH_SHORT).show();
			mp.stop();
			mp.release();
			
			position = (position-1 < 0)? mySongs.size()-1: position-1;
			title = mySongs.get(position).getName().toString().replace(".mp3", "");
			tv.setText(title);
			u = Uri.parse(mySongs.get(position).toString());
			mp = MediaPlayer.create(getApplicationContext(), u);
			mp.start();
			sb.setMax(mp.getDuration());
			break;
		}
	}
}
