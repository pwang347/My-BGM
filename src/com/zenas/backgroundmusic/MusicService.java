package com.zenas.backgroundmusic;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import com.zenas.backgroundmusic.activities.PlaylistActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

public class MusicService extends Service 
	{
	private String song;
	private int time;
	final MusicService ms = this;
	private static int classID = 579;
	private List<List<String>> playlists;
	private int currentPlaylist;
	private int currentSong;
	public static String START_PLAY = "START_PLAY";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try{
		playlists = ((BackgroundMusic) ms.getApplication()).getPlaylists();
		currentPlaylist = ((BackgroundMusic) ms.getApplication()).getCurPlaylist();
		currentSong = ((BackgroundMusic) ms.getApplication()).getCurSong();
		
		((BackgroundMusic) ms.getApplication()).getMp().setOnCompletionListener(new OnCompletionListener(){

			@Override
			public void onCompletion(MediaPlayer arg0) {
				if(playlists.get(currentPlaylist).size()>0){
				((BackgroundMusic) ms.getApplication()).setSongCompleted(true);

	        		if(!((BackgroundMusic) ms.getApplication()).getShuffle()){
	        			playNextSong();
	        		} else {
	        			playRandomSong();
	        		}
	        		((BackgroundMusic) ms.getApplication()).setSongCompleted(false);
	        	}
				}
			
		});
		if (intent.getBooleanExtra("START_PLAY", false)) {
			if(intent.getStringExtra("SONG").length()>0){
				song = intent.getStringExtra("SONG");
				time = intent.getIntExtra("TIME", 0);
				play();		
			}
		}
		} catch (Exception e){
			
		}
	
		return Service.START_STICKY;	
	}

	private void play(){
			stop();
			
			Intent intent = new Intent(this, PlaylistActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
							Intent.FLAG_ACTIVITY_SINGLE_TOP
							);

			PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
			
			@SuppressWarnings("deprecation")
			Notification notification = new Notification.Builder(getApplicationContext())
	         	.setContentTitle("My BGM")
	         	.setContentText("Now Playing: \"" + getNameFromPath(song) +"\"")
	         	.setSmallIcon(R.drawable.ic_launcher)
	         	.setContentIntent(pi)
	         	.getNotification();
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			
			try{
				((BackgroundMusic) ms.getApplication()).getMp().reset();
				((BackgroundMusic) ms.getApplication()).getMp().setDataSource(song);
				((BackgroundMusic) ms.getApplication()).getMp().prepare();
				((BackgroundMusic) ms.getApplication()).getMp().start();
				if(time>0){
					((BackgroundMusic) ms.getApplication()).getMp().seekTo(time);
					time = 0;
				}
			} catch (IOException e){
			}
			startForeground(classID, notification);
	}

	@Override
	public void onDestroy() {
		stop();
	}	
	
	private void stop() {
			stopForeground(true);
		}
	
	private String getFileFromPath(String path){
		String name = path;
		
		while(name.contains("/")){
			name = name.substring(name.indexOf("/")+1);
		}
		
		return name; 
	}
	
	public void playRandomSong(){
		if(playlists.get(currentPlaylist).size()>0){
		if(playlists.get(currentPlaylist).size()>0){
		int random = randInt(0,playlists.get(currentPlaylist).size()-1);
		while(random==currentSong){
			random = randInt(0,playlists.get(currentPlaylist).size()-1);
		}
		currentSong = random;
		((BackgroundMusic) ms.getApplication()).setCurSong(currentSong);
		song = playlists.get(currentPlaylist).get(currentSong);
		play();
		}
		}
	}
	
	public void playNextSong(){
		if(playlists.get(currentPlaylist).size()>0){
		if(currentSong+1>=playlists.get(currentPlaylist).size()){
			currentSong = -1;
		}
		currentSong++;
		song = playlists.get(currentPlaylist).get(currentSong);
		((BackgroundMusic) ms.getApplication()).setCurSong(currentSong);
		play();
		}
	}
	
	public void playPrevSong(){
		if(currentSong-1<0){
			currentSong = playlists.get(currentPlaylist).size();
		}
		currentSong--;
		song = playlists.get(currentPlaylist).get(currentSong);
		((BackgroundMusic) ms.getApplication()).setCurSong(currentSong);
		play();
	}
	
	private String getNameFromFile(String file){
		int end = file.indexOf(".mp3");
		return file.substring(0, end);
	}
	
	private String getNameFromPath(String path){
		return getNameFromFile(getFileFromPath(path));
	}
	
	public static int randInt(int min, int max) {

	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}

	
	
	
}
