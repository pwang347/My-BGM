package com.zenas.backgroundmusic;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.media.MediaPlayer;

public class BackgroundMusic extends Application{
		private MediaPlayer mp = new MediaPlayer();
	    private float globalVolume = 1;
	    private boolean shuffle = false;
	    private int currentPlaylist = 0;
	    private int currentSong = 0;
	    private boolean startPlaylist = false;
	    private int positionInSong = 0;
	    private boolean songCompleted = false;
		private List<List<String>> playlists = new ArrayList<List<String>>();
		private List<List<String>> playlists_songNames = new ArrayList<List<String>>();
		private List<String> playlistNames = new ArrayList<String>();
		private List<Integer> playlistColors = new ArrayList<Integer>();
		private List<String> directories = new ArrayList<String>();
		private String wallpaper_path = "";
		private boolean play = false;
		private boolean animation = false;
		private boolean autoplay = false;
		private boolean wallpaper_notStretched = false;
		private boolean hideHelp = false;
		private boolean serviceRunning = false;
		private boolean stopOnExit = true;
	
		public void setStopOnExit(boolean state){
			stopOnExit = state;
		}
		
		public boolean getStopOnExit(){
			return stopOnExit;
		}
		
		public void setServiceRunning(boolean state){
			serviceRunning = state;
		}
		
		public boolean getServiceRunning(){
			return serviceRunning;
		}
		
		public void setHideHelp(boolean state){
			hideHelp = state;
		}
		
		public boolean getHideHelp(){
			return hideHelp;
		}
		
		public void setWallpaperNotStretched(boolean state){
			wallpaper_notStretched = state;
		}
		
		public boolean getWallpaperNotStretched(){
			return wallpaper_notStretched;
		}
		
		public void setWallpaperPath(String path){
			wallpaper_path = path;
		}
		
		public String getWallpaperPath(){
			return wallpaper_path;
		}
		
		public void setAnimation(boolean state){
			animation = state;
		}
		
		public boolean getAnimation(){
			return animation;
		}
		
	    public float getVolume() {
	        return globalVolume;
	    }

	    public void setVolume(float volume) {
	        globalVolume = volume;
	        mp.setVolume(volume, volume);
	    }
	    
	    public boolean getShuffle(){
	    	return shuffle;
	    }
	    
	    public void setShuffle(boolean state){
	    	shuffle = state;
	    }
	    
	    public boolean getAutoplay(){
	    	return autoplay;
	    }
	    
	    public void setAutoplay(boolean state){
	    	autoplay = state;
	    }
	    
	    public int getCurPlaylist(){
	    	return currentPlaylist;
	    }
	    
	    public void setCurPlaylist(int value){
	    	currentPlaylist = value;
	    }
	    
	    public int getCurSong(){
	    	return currentSong;
	    }
	    
	    public void setCurSong(int value){
	    	currentSong = value;
	    }
	    
	    public List<List<String>> getPlaylists(){
	    	return playlists;
	    }
	    
	    public void setPlaylists(List<List<String>> playlists){
	    	this.playlists = playlists;	
	    }
	    
	    public List<List<String>> getPlaylistsSongNames(){
	    	return playlists_songNames;
	    }
	    
	    public void setPlaylistsSongNames(List<List<String>> playlists_songNames){
	    	this.playlists_songNames = playlists_songNames;	
	    }
	    
	    public List<String> getPlaylistNames(){
	    	return playlistNames;
	    }
	    
	    public void setPlaylistNames(List<String> playlistNames){
	    	this.playlistNames = playlistNames; 
	    }
	    
	    public List<Integer> getPlaylistColors(){
	    	return playlistColors;
	    }
	    
	    public void setPlaylistColors(List<Integer> playlistColors){
	    	this.playlistColors = playlistColors;
	    }
	    
	    public boolean getPlayStarted(){
	    	return startPlaylist;
	    }
	    
	    public void setPlayStarted(boolean state){
	    	startPlaylist = state;
	    }
	    
	    public void setPositionInSong(int value){
	    	positionInSong = value;
	    }
	    
	    public int getPositionInSong(){
	    	if(mp.isPlaying())
	    	return mp.getCurrentPosition();
	    	else
	    	return 0;
	    }
	    
	    public MediaPlayer getMp(){
	    	if(mp==null){
	    		mp = new MediaPlayer();
	    	}
    		return mp;
	    }
	    
	    public boolean songIsComplete(){
	    	return songCompleted;
	    }
	    
	    public void setSongCompleted(boolean state){
	    	songCompleted = state;
	    }
	    
	    public List<String> getDirectories(){
	    	return directories;
	    }
	    
	    public void setDirectories(List<String> directories){
	    	this.directories = directories;
	    }
	    
	    public boolean getPlay(){
	    	return play;
	    }
	    
	    public void setPlay(boolean play){
	    	this.play = play;
	    }

}
