package com.zenas.backgroundmusic.activities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.zenas.backgroundmusic.MusicService;
import com.zenas.backgroundmusic.R;
import com.zenas.backgroundmusic.BackgroundMusic;
import com.zenas.backgroundmusic.util.ColorPickerDialog;
import com.zenas.backgroundmusic.util.ConcurrentSorter;
import com.zenas.backgroundmusic.util.HelpDialog;
import com.zenas.backgroundmusic.util.ListAdapter;
import com.zenas.backgroundmusic.util.MessageDialog;
import com.zenas.backgroundmusic.util.PlaylistAdapter;
import com.zenas.backgroundmusic.util.PreferenceUtils;
import com.zenas.backgroundmusic.util.ColorPickerDialog.OnColorSelectedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

class Mp3Filter implements FilenameFilter{
	@Override
	public boolean accept(File arg0, String name) {
		if(name.endsWith(".mp3")||name.endsWith(".wav")||name.endsWith(".mkv")||name.endsWith(".mid")){
			return true;
		} else {
			return false;
		}
	}
}

class FolderFilter implements FilenameFilter{
	@Override
	public boolean accept(File arg0, String name) {
		return (!name.contains("."));
	}
}

public class PlaylistActivity extends Activity {
	
	private List<List<String>> playlists = new ArrayList<List<String>>();
	private List<List<String>> playlists_songNames = new ArrayList<List<String>>();
	private List<String> playlistNames = new ArrayList<String>();
	private List<Integer> playlistColors = new ArrayList<Integer>();
	private int currentPlaylist;
	private int currentSong;
	final Context context = this;
	private TextView tv;
	private TextView songName;
	private TextView songPositionText;
	private TextView songMaxText;
	private ListView list;
	private SeekBar sb;
	private FrameLayout frame;
	private Button moveUpBtn;
	private Button moveDownBtn;
	private Button doneBtn;
	private int tempColor = Color.GRAY;
	private boolean shuffle = false;
	private boolean autoplay = false;
	private float volume = 1;
	private PlaylistActivity pa = this;
	private int song_position;
	private PreferenceUtils tinydb;
	private int selectedSong;
	private int selectedColor;
	private Button stopBtn;
	private Button prev;
	private Button next;
	private Button shuff;
	private Button back;
	private Button settingsButton;
	private boolean play = false;
	private Runnable playlistRunnable;
	private Handler playlistHandler;
	private ImageView iv;
	private ImageView coverart;
	private boolean locked = false;
	private Button lockBtn;
	private boolean shouldAutoplay;
	private boolean hideHelp;
	private Button helpBtn;
	private boolean stopOnExit;
	private boolean exiting = true;
	private Toast mToast;
	private List<Integer> playlistColorsMinusCurrent = new ArrayList<Integer>();
	private List<String> nameMinusCurrent = new ArrayList<String>();
	private List<String> directories;
	
	@Override
	protected void onResume(){
		super.onResume();
		playlistHandler.post(playlistRunnable);
		shuffle = ((BackgroundMusic) pa.getApplication()).getShuffle();
		autoplay = ((BackgroundMusic) pa.getApplication()).getAutoplay();
		hideHelp = ((BackgroundMusic) pa.getApplication()).getHideHelp();
		directories_getPref();
		directories = ((BackgroundMusic) pa.getApplication()).getDirectories();
		if(hideHelp){
			helpBtn.setVisibility(View.GONE);
		} else {
			helpBtn.setVisibility(View.VISIBLE);
		}
		stopOnExit = ((BackgroundMusic) pa.getApplication()).getStopOnExit();
		settleDifferences();
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((BackgroundMusic) pa.getApplication()).setAnimation(true);
		Intent intent = getIntent();
		shouldAutoplay = intent.getBooleanExtra("SHOULD_AUTOPLAY", false);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		playlists = ((BackgroundMusic) pa.getApplication()).getPlaylists();
		playlists_songNames = ((BackgroundMusic) pa.getApplication()).getPlaylistsSongNames();
		playlistNames = ((BackgroundMusic) pa.getApplication()).getPlaylistNames();
		playlistColors = ((BackgroundMusic) pa.getApplication()).getPlaylistColors();
		currentPlaylist = ((BackgroundMusic) pa.getApplication()).getCurPlaylist();
		currentSong = ((BackgroundMusic) pa.getApplication()).getCurSong();
		song_position = ((BackgroundMusic) pa.getApplication()).getPositionInSong();
		play = ((BackgroundMusic) pa.getApplication()).getPlay();
		autoplay = ((BackgroundMusic) pa.getApplication()).getAutoplay();
		hideHelp = ((BackgroundMusic) pa.getApplication()).getHideHelp();
		stopOnExit = ((BackgroundMusic) pa.getApplication()).getStopOnExit();
		directories = ((BackgroundMusic) pa.getApplication()).getDirectories();
		tinydb = new PreferenceUtils(context);
		setContentView(R.layout.playlist_screen);
		frame = (FrameLayout) findViewById(R.id.frame);
		list = (ListView) findViewById(R.id.list);
		settingsButton = (Button) findViewById(R.id.settings_button);
		settingsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(!locked){
				LayoutInflater li = LayoutInflater.from(context);
				View settingsOptionsView = li.inflate(R.layout.settings_options, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
 
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(settingsOptionsView);
				alertDialogBuilder
				.setNegativeButton("Cancel",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });

			// create alert dialog
			final AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			Button editPlaylistBtn = (Button) settingsOptionsView
					.findViewById(R.id.editPlaylistBtn);
			editPlaylistBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					editPlaylistDialog(currentPlaylist);
					alertDialog.dismiss();
				}
				
			});
			
			Button sortAlphaBtn = (Button) settingsOptionsView
					.findViewById(R.id.sortAlphaBtn);
			sortAlphaBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					sortSongsAlphabetically(currentPlaylist);
					alertDialog.dismiss();
				}
				
			});
				
			Button otherSettingsBtn = (Button) settingsOptionsView
					.findViewById(R.id.otherSettingsBtn);
			otherSettingsBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent myIntent = new Intent(getBaseContext(), SettingsActivity.class);
					context.startActivity(myIntent);
					alertDialog.dismiss();
				}
				
			});
			}
				moveUpBtn.setVisibility(View.GONE);
				moveDownBtn.setVisibility(View.GONE);
				doneBtn.setVisibility(View.GONE);
				playlists_putPref();
				playlistsSongNames_putPref();
			}
			
			
		});
		moveUpBtn = (Button) findViewById(R.id.upBtn);
		moveUpBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				moveSongUp(selectedSong);
			}
			
		});
		
		moveDownBtn = (Button) findViewById(R.id.downBtn);
		moveDownBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				moveSongDown(selectedSong);
			}
			
		});
		
		doneBtn = (Button) findViewById(R.id.doneBtn);
		doneBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				moveUpBtn.setVisibility(View.GONE);
				moveDownBtn.setVisibility(View.GONE);
				doneBtn.setVisibility(View.GONE);
				playlists_putPref();
				playlistsSongNames_putPref();
			}
			
		});
		prev= (Button) findViewById(R.id.prevBtn);
		prev.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(!locked)
				playPrevSong();
			}
		});
		
		next = (Button) findViewById(R.id.nextBtn);
		next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(!locked){
				playNextSong();
				}}
		});
		
		shuff = (Button) findViewById(R.id.shuffleBtn);
		shuff.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(!locked)
				playRandomSong();
			}
		});
		
		back = (Button) findViewById(R.id.backBtn);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!locked){
				((BackgroundMusic) pa.getApplication()).setPlaylists(playlists);
				((BackgroundMusic) pa.getApplication()).setPlaylistsSongNames(playlists_songNames);
				((BackgroundMusic) pa.getApplication()).setPlaylistNames(playlistNames);
				((BackgroundMusic) pa.getApplication()).setPlaylistColors(playlistColors);
				((BackgroundMusic) pa.getApplication()).setCurSong(currentSong);
				((BackgroundMusic) pa.getApplication()).setCurPlaylist(currentPlaylist);
				((BackgroundMusic) pa.getApplication()).setPositionInSong(((BackgroundMusic) pa.getApplication()).getMp().getCurrentPosition());
				((BackgroundMusic) pa.getApplication()).setPlay(play);
				Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
				context.startActivity(myIntent);
				exiting = false;
				finish();
			}
			}
			
		});
		
		helpBtn = (Button) findViewById(R.id.playHelpBtn);
		if(hideHelp){
			helpBtn.setVisibility(View.GONE);
		} 
		helpBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HelpDialog hd = new HelpDialog(context);
				//List<String> h1 = new ArrayList<String>();
				hd.setTitle("Help: Playlist screen");
				List<String> controlOptions = new ArrayList<String>();
				controlOptions.add("Previous: Swap to the song index before the currently selected song, and then play it.");
				controlOptions.add("Play/Pause: Start playing a selected song, or pause it.");
				controlOptions.add("Next: Swap to the song index after the currently selected song, and then play it.");
				controlOptions.add("Shuffle: Swap to a random song and play it. \n(For playlist shuffle, see the settings page)");
				hd.addHelpItem("Control options (in sequence)", controlOptions);
				hd.addHelpItem("Returning to the main screen", "Press the arrow on the top left to return to the main screen displaying playlists.");
				hd.addHelpItem("Locking the screen", "Pressing the lock icon in the top right will lock the screen, making any subsequent clicks on other buttons to be ignored. Pressing the lock icon again will de-activate the lock and allows button press again. Note that the lock icon will glow it is enabled.");
				hd.addHelpItem("Playing a song", "You can play a song by clicking on its name inside of the song list, or by pressing any of the navigation buttons on the bottom of the screen. When a song is playing, a service will appear in the notifications showing the current song and will open the playlist screen when pressed.");
				hd.addHelpItem("Modifying a song", "Shortly after pressing and holding down on a song, a menu will show up that will allow you to add the selected song to another playlist, move, or remove it from the current playlist.");
				hd.addHelpItem("Settings", "You can access settings for the current playlist as well as other application settings by pressing the gear icon in the top right.");
				hd.updateList();
				hd.show();
			}
			
		});
		
		list.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int song_position, long arg3) {
				if(!locked){
					playlistColorsMinusCurrent = new ArrayList<Integer>(playlistColors);
					playlistColorsMinusCurrent.remove(currentPlaylist);
					nameMinusCurrent = new ArrayList<String>(playlistNames);
					nameMinusCurrent.remove(currentPlaylist);
				selectedSong = song_position;
				LayoutInflater li = LayoutInflater.from(context);
				View songOptionsView = li.inflate(R.layout.song_options, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
 
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(songOptionsView);
				alertDialogBuilder
				.setNegativeButton("Cancel",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });

			// create alert dialog
			final AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show(); 
				Button addToPlayList = (Button) songOptionsView
						.findViewById(R.id.addPlay);
				addToPlayList.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						LayoutInflater li = LayoutInflater.from(context);
						View selectPlaylistView = li.inflate(R.layout.select_playlist, null);
		 
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								context);
		 
						// set prompts.xml to alertdialog builder
						alertDialogBuilder.setView(selectPlaylistView);
						alertDialogBuilder
						.setNegativeButton("Cancel",
						  new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
							//alertDialog.dismiss();
						    }
						  });

					// create alert dialog
					final AlertDialog alertDialog2 = alertDialogBuilder.create();

					// show it
					alertDialog2.show(); 
						final ListView lv = (ListView) selectPlaylistView
								.findViewById(R.id.playlist_list);
						if(nameMinusCurrent.size()>0){
						final PlaylistAdapter listOfPlaylists = new PlaylistAdapter(context, nameMinusCurrent, playlistColorsMinusCurrent);
						lv.setAdapter(listOfPlaylists);
						lv.setOnItemClickListener(new OnItemClickListener(){

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int playlist_position, long arg3) {
								if(playlist_position>=currentPlaylist){
									playlist_position++;
								}
								
								if(!playlists.get(playlist_position).contains(playlists.get(currentPlaylist).get(selectedSong))){
									addSongToList(playlist_position, playlists.get(currentPlaylist).get(selectedSong), true);
									MessageDialog md = new MessageDialog(context, "Notice", "\""+playlists_songNames.get(currentPlaylist).get(selectedSong) + "\" was added to the "
											+ playlistNames.get(playlist_position) + " playlist!", false);
									md.show();
								}
								
		
								alertDialog2.dismiss();
								alertDialog.dismiss();
								
								
							}
							
						});		
						
						/*
						final Handler handler2=new Handler();
						 handler2.post(new Runnable(){

						        @Override
						        public void run() {
						        	//checks for end of song
						    		updatePlaylist(lv);
									for(int i = 0; i<playlistColors.size(); i++){
										Button b = (Button) getViewByPosition(i, lv).findViewById(R.id.playlist_button);
										b.getBackground().setColorFilter(playlistColors.get(i), PorterDuff.Mode.MULTIPLY);
										}
						        	handler2.postDelayed(this,500); // set time here to re-update
						            
						           
						        }

						    });*/
						}

						
						
					}
					
				});
				moveUpBtn.setVisibility(View.GONE);
				moveDownBtn.setVisibility(View.GONE);
				doneBtn.setVisibility(View.GONE);
				playlists_putPref();
				playlistsSongNames_putPref();
				Button moveSong = (Button) songOptionsView
						.findViewById(R.id.moveBtn);
				moveSong.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
						
						moveUpBtn.setVisibility(View.VISIBLE);
						moveDownBtn.setVisibility(View.VISIBLE);
						doneBtn.setVisibility(View.VISIBLE);
						alertDialog.dismiss();
					}
					
				});
				
				
				Button removeFromPlayList = (Button) songOptionsView
						.findViewById(R.id.removePlay);
				if(currentPlaylist == 0){
					removeFromPlayList.setEnabled(false);
				} else {
				removeFromPlayList.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						int firstViewIndex = list.getFirstVisiblePosition();
						playlists.get(currentPlaylist).remove(song_position);
						playlists_songNames.get(currentPlaylist).remove(song_position);
						if(currentSong==song_position){
							((BackgroundMusic) pa.getApplication()).getMp().stop();
							if(playlists.get(currentPlaylist).size()>0){
								loadSong(currentPlaylist, 0);
							} else {
							songName.setText("");
							}
							sb.setProgress(0);
						}
						if(song_position<currentSong){
							currentSong--;
							((BackgroundMusic) pa.getApplication()).setCurSong(currentSong);
						}
						selectPlaylist(currentPlaylist);
						setSelector(currentSong);
						alertDialog.dismiss();
						playlists_putPref();
						playlistsSongNames_putPref();
						list.setSelection(firstViewIndex);
					}
					
				});
				}
				}
				return true;
			}
			
		});
		 
		iv = (ImageView) findViewById(R.id.textureImg);
		iv.getLayoutParams().height = list.getHeight() + 5;
		
		coverart = (ImageView) findViewById(R.id.coverArt);
		
		list.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//ListView list_parent = (ListView) view.getParent();
				//list_parent.setSelection(position);
				if(!locked){
					ListAdapter la = (ListAdapter)list.getAdapter();
					 la.setSelectedIndex(position);
				play = true;

				playSong(position);
				//view.setSelected(true);
				}
				
			}
		});
		
		
		stopBtn = (Button) findViewById(R.id.stopBtn);
		stopBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(!locked){
				if(((BackgroundMusic) pa.getApplication()).getMp().isPlaying()){
							((BackgroundMusic) pa.getApplication()).getMp().stop();
							 Intent intent2 = new Intent(getApplicationContext(),
									 
						    			MusicService.class);
						    				
						    	stopService(intent2);
						    	((BackgroundMusic) pa.getApplication()).setServiceRunning(false);
							
				}	else {
							playSong(currentSong, sb.getProgress());
						}
				}
			}});
		
		sb = (SeekBar) findViewById(R.id.seekBar1);
		sb.setProgress(0);
		
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				// TODO Auto-generated method stub
				if(arg2&&((BackgroundMusic) pa.getApplication()).getMp().isPlaying()){
					((BackgroundMusic) pa.getApplication()).getMp().seekTo(progress);
				} else {
					song_position = progress;
				}
				songPositionText.setText(intToTime(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		lockBtn = (Button) findViewById(R.id.lockBtn);
		lockBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast mToast = Toast.makeText( context  , "" , Toast.LENGTH_SHORT );
				try{
				mToast.cancel();
				} catch (Exception e){
					
				}
				mToast = Toast.makeText( context  , "" , Toast.LENGTH_SHORT );
				if(!locked){
				locked = true;
				lockBtn.setBackgroundResource(R.drawable.lock_icon_glow);
				lockBtn.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
				mToast.setText("Controls locked");
				}
				else{
					locked = false;
					lockBtn.setBackgroundResource(R.drawable.lock_icon);
					lockBtn.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
					mToast.setText("Controls unlocked");
				}
				mToast.show();
			}
			
		});
		
		tv = (TextView) findViewById(R.id.textView1);
		songName = (TextView) findViewById(R.id.songName);
		songPositionText = (TextView) findViewById(R.id.songCurrent);
		songMaxText = (TextView) findViewById(R.id.songMax);
		
		selectPlaylist(currentPlaylist);
		//showError(autoplay + "");
		if(playlists_songNames.get(currentPlaylist).size()>0){
		if(((BackgroundMusic) pa.getApplication()).getMp().isPlaying()){
			//stopBtn.setBackgroundResource(R.drawable.pause_button);
			//stopBtn.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
			ListAdapter la = (ListAdapter) list.getAdapter();
			la.setSelectedIndex(currentSong);
			showCoverart(currentPlaylist, currentSong);
			songPositionText.setText(intToTime(((BackgroundMusic) pa.getApplication()).getMp().getCurrentPosition()));
			songMaxText.setText(intToTime(((BackgroundMusic) pa.getApplication()).getMp().getDuration()));
			songName.setText(playlists_songNames.get(currentPlaylist).get(currentSong));
		}
	} else {
		 Intent intent2 = new Intent(getApplicationContext(),
				 
	    			MusicService.class);
	    				
	    	stopService(intent2);
	    	((BackgroundMusic) pa.getApplication()).setServiceRunning(false);
	}
		
		
		if(playlists.get(currentPlaylist).size()>0){
		if(((BackgroundMusic) pa.getApplication()).getMp().isPlaying()){
			sb.setMax(((BackgroundMusic) pa.getApplication()).getMp().getDuration());
			sb.setProgress(song_position);
			songName.setText(playlists_songNames.get(currentPlaylist).get(currentSong));
			showCoverart(currentPlaylist, currentSong);
			songPositionText.setText(intToTime(((BackgroundMusic) pa.getApplication()).getMp().getCurrentPosition()));
			songMaxText.setText(intToTime(((BackgroundMusic) pa.getApplication()).getMp().getDuration()));
		} else {
			loadSong(currentPlaylist, 0);
		}
		}
		playlistHandler=new Handler();
		playlistRunnable = new Runnable(){

			@Override
	        public void run() {
				iv.getLayoutParams().height = list.getHeight() + 5;
				if(!locked){
	  		       	sb.setEnabled(true);
	        		} else {
	        			sb.setEnabled(false);
	        		}
				if(currentSong!=((BackgroundMusic) pa.getApplication()).getCurSong()){
        			currentSong = ((BackgroundMusic) pa.getApplication()).getCurSong();
        			updateSongText(currentPlaylist, currentSong);
        		}
				if(currentPlaylist!=((BackgroundMusic) pa.getApplication()).getCurPlaylist()){
        			currentPlaylist = ((BackgroundMusic) pa.getApplication()).getCurPlaylist();
        			shouldAutoplay = true;
        			selectPlaylist(currentPlaylist);
        		}
	        	if(((BackgroundMusic) pa.getApplication()).getMp().isPlaying()){
					stopBtn.setBackgroundResource(R.drawable.pause_button);
					stopBtn.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
	        		volume = ((BackgroundMusic) pa.getApplication()).getVolume();
	        		setVolume(volume);
	        	
			        sb.setProgress(((BackgroundMusic) pa.getApplication()).getMp().getCurrentPosition());
	        	} else {
					stopBtn.setBackgroundResource(R.drawable.play_button);
					stopBtn.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
	        	}

	        	playlistHandler.postDelayed(this,100);
	            
	           
			}
		};
		playlistHandler.post(playlistRunnable);

	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    playlistHandler.removeCallbacks(playlistRunnable);
	}

	
	public void playRandomSong(){
		if(playlists.get(currentPlaylist).size()>0){
		if(playlists.get(currentPlaylist).size()>0){
		int random = randInt(0,playlists.get(currentPlaylist).size()-1);
		while(random==currentSong){
			random = randInt(0,playlists.get(currentPlaylist).size()-1);
		}
		currentSong = random;
		playSong(currentSong);
		}
		}
	}
	
	public void playNextSong(){
		if(playlists.get(currentPlaylist).size()>0){
		if(currentSong+1>=playlists.get(currentPlaylist).size()){
			currentSong = -1;
		}
		//play the song at current index + 1
		playSong(currentSong+1);
		}
	}
	
	public void playPrevSong(){
		if(currentSong-1<0){
			currentSong = playlists.get(currentPlaylist).size();
		}
		//play the song at current index + 1
		playSong(currentSong-1);
		list.setSelection(currentSong-1);
	}
	
	public void setVolume(float percent){
		((BackgroundMusic) pa.getApplication()).getMp().setVolume(percent, percent);
	}
	
	public static int randInt(int min, int max) {

	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	private void editPlaylistDialog(final int index){
		final List<String> songs = new ArrayList<String>(playlists.get(index));
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);
		userInput.setText(playlistNames.get(index));
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	if(!userInput.getText().toString().equals("")&&userInput.getText()!=null){
		    			if(!(userInput.getText().toString().length()>30)){
					    	playlistNames.set(index, userInput.getText().toString());
					    	playlistNames_putPref();
		    			} else {
		    				editPlaylistDialog(index);
			    			showError("Make sure the name isn't over 30 characters long.");
		    			}
		    		} else {
		    			editPlaylistDialog(index);
		    			showError("You forgot to enter a name for the playlist.");
		    		}
		    	
			    	playlistColors.set(index, tempColor);
			    	playlistColors_putPref();
			    	boolean load = false;
			    	int newIndex = currentSong;
			    	if(index!=0){
			    		if(playlists.get(currentPlaylist).size()>0){
			    			if(songs.contains(playlists.get(currentPlaylist).get(currentSong))){
			    				newIndex = songs.indexOf(playlists.get(currentPlaylist).get(currentSong));
			    			} else {
			    				if(((BackgroundMusic) pa.getApplication()).getMp().isPlaying()){
			    					((BackgroundMusic) pa.getApplication()).getMp().stop();
			    			}
			    		if(songs.size()>0){
				    		load = true;
				    		newIndex = 0;
				    		} else {
				    			newIndex = -1;
				    		}
			    	}
			    }
			    	
			    	
			    	if(songs.size()>0){
			    		if(playlists.get(index).size()==0){
			    			load = true;
			    			newIndex = 0;
			    		}
			    	} else {
			    		 Intent intent = new Intent(getApplicationContext(),
			    				 
			    			MusicService.class);
			    				
			    	stopService(intent);
			    	((BackgroundMusic) pa.getApplication()).setServiceRunning(false);
			    	newIndex = -1;

			    	}
				    	playlists.get(index).clear();
				    	playlists_songNames.get(index).clear();
			    	for(int i = 0; i<songs.size(); i++){
			    		addSongToList(index, songs.get(i), false);
			    	}
		    		playlists_putPref();
		    		playlistsSongNames_putPref();
			    	}
			    	if(newIndex>-1){
		    		currentSong = newIndex;
		    		((BackgroundMusic) pa.getApplication()).setCurSong(currentSong);
		    		setSelector(currentSong);
			    	}
			    	if(playlists.get(currentPlaylist).size()>0){
			    		if(load){
			    			loadSong(currentPlaylist, 0);
			    		}
			    		} else {
			    			songName.setText("");
			    			songPositionText.setText(intToTime(0));
			    			songMaxText.setText(intToTime(0));
			    			sb.setProgress(0);
			    		}
					iv.getLayoutParams().height = list.getHeight() + 5;
			    	updateSongList(list);
		    		refreshColor(index);
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		tempColor = playlistColors.get(index);
		final Button colorB = (Button) promptsView.findViewById(R.id.colorButton);
		colorB.getBackground().setColorFilter(tempColor, PorterDuff.Mode.MULTIPLY);
		colorB.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				ColorPickerDialog colorPickerDialog = new ColorPickerDialog(context, tempColor, new OnColorSelectedListener() {

			        @Override
			        public void onColorSelected(int color) {
						tempColor = color;
						colorB.getBackground().setColorFilter(tempColor, PorterDuff.Mode.MULTIPLY);
			        }

			    });
			    colorPickerDialog.show();
			}
			
		});
		
		//EDITTTT
		TextView playlistText = (TextView) promptsView.findViewById(R.id.playlistSongsText);
		playlistText.setVisibility(View.GONE);
		Button copyPlaylistBtn = (Button) promptsView.findViewById(R.id.copyBtn);
		if(index==0){
			copyPlaylistBtn.setVisibility(View.GONE);
		} else {
			copyPlaylistBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					LayoutInflater li = LayoutInflater.from(context);
					View playlistView = li.inflate(R.layout.select_playlist, null);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							context);
					alertDialogBuilder.setView(playlistView)
						.setNegativeButton("Cancel",
							  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });
					final AlertDialog alertDialog = alertDialogBuilder.create();

					alertDialog.show();
					
					final List<String> playlistsMinusCurrent = new ArrayList<String>(playlistNames);
					playlistsMinusCurrent.remove(index);
					final List<Integer> playlistColorsMinusCurrent = new ArrayList<Integer>(playlistColors);
					playlistColorsMinusCurrent.remove(index);
					
					final ListView lv = (ListView) playlistView.findViewById(R.id.playlist_list);
					final PlaylistAdapter listOfPlaylists = new PlaylistAdapter(context, playlistsMinusCurrent, playlistColorsMinusCurrent);
					lv.setAdapter(listOfPlaylists);
					
					lv.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							// TODO Auto-generated method stub
							if(position>=index){
								position++;
							}
							songs.clear();
							for(int i = 0; i<playlists.get(position).size(); i++){
								songs.add(playlists.get(position).get(i));
							}
							alertDialog.dismiss();
							
						}
						
					});
					
					
							
					// set dialog message
				}
				
			});
		}
		
		Button selectSongBtn = (Button) promptsView.findViewById(R.id.selectSongBtn);
		if(index==0){
			selectSongBtn.setVisibility(View.GONE);
		} else {
		selectSongBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LayoutInflater li = LayoutInflater.from(context);
				View selectSongView = li.inflate(R.layout.select_song, null);
				
				final ListView lv = (ListView) selectSongView
						.findViewById(R.id.song_list);
				final ListAdapter listOfSongs = new ListAdapter(context, playlists_songNames.get(0), true);
				listOfSongs.hideNumbers();
				lv.setAdapter(listOfSongs);
				for(int i = 0; i<songs.size();i++){
					int index = playlists.get(0).indexOf(songs.get(i));
					if(index>-1){
					lv.setItemChecked(index, true);
					listOfSongs.setSelectedIndex(index);
					}
				}
				lv.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0,
							View arg1, int position, long arg3) {
						SparseBooleanArray selectedItems = lv.getCheckedItemPositions();
						if(selectedItems.get(position)){
						listOfSongs.setSelectedIndex(position);
						} else {
							listOfSongs.removeIndex(position);
						}
					}
					
				});
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setView(selectSongView);
				alertDialogBuilder
				.setNegativeButton("Cancel",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });
				alertDialogBuilder
				.setNeutralButton("Select all",
				  new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
					
				});
				alertDialogBuilder
				.setPositiveButton("Done",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
				    	songs.clear();
	
				    	SparseBooleanArray selectedItems = lv.getCheckedItemPositions();          
				    	//int id1 = lv.getCheckedItemPosition();        
				    
				    	for(int i = 0; i<lv.getAdapter().getCount(); i++){
				    		if(selectedItems.get(i)){
				    			songs.add(playlists.get(0).get(i));
				    		}
				    		//lv.indexOfChild(selectedItems.get(i));
				    		//songs.add(playlists.get(0).get());
				    	}
				    	//boolean load = false;
				    //dialog.cancel();
					dialog.dismiss();
				    }
				  });
				

			// create alert dialog
			final AlertDialog alertDialog2 = alertDialogBuilder.create();

			// show it
			alertDialog2.show(); 
			alertDialog2.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
		      {            
		          @Override
		          public void onClick(View v)
		          {
		        	  boolean selected = false;
						SparseBooleanArray selectedItems = lv.getCheckedItemPositions();
						for(int i = 0; i<lv.getAdapter().getCount(); i++){
							if(selectedItems.get(i)){
								selected = true;
								break;
							}
						}
						
						if(selected){
							for(int i = 0; i<lv.getAdapter().getCount(); i++){
								lv.setItemChecked(i, false);
								listOfSongs.removeIndex(i);
							}
						} else {
							for(int i = 0; i<lv.getAdapter().getCount(); i++){
								lv.setItemChecked(i, true);
								listOfSongs.setSelectedIndex(i);
							}
						}
						
		          }
		      });
				
			}
			
		});
		}
	}
	
	public View getViewByPosition(int position, ListView listView) {
	    final int firstListItemPosition = listView.getFirstVisiblePosition();
	    final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

	    if (position < firstListItemPosition || position > lastListItemPosition ) {
	        return listView.getAdapter().getView(position, null, listView);
	    } else {
	        final int childIndex = position - firstListItemPosition;
	        return listView.getChildAt(childIndex);
	    }
	}
	
	private void setSelector(int index){
		ListAdapter la = (ListAdapter)list.getAdapter();
		 la.setSelectedIndex(index);
	}

	private void playSong(int position){
		if(playlists.get(currentPlaylist).size()>0){
			loadSong(currentPlaylist, position);
			Intent intent = new Intent(getApplicationContext(),
   				 
	    			MusicService.class);
			if(((BackgroundMusic) pa.getApplication()).getServiceRunning()){
	
		    				
		    	stopService(intent);
			}
               intent.putExtra(MusicService.START_PLAY, true);
               intent.putExtra("SONG", playlists.get(currentPlaylist).get(position));
               startService(intent);
               ((BackgroundMusic) pa.getApplication()).setServiceRunning(true);
            song_position = 0;
			sb.setProgress(0);
			sb.setMax(((BackgroundMusic) pa.getApplication()).getMp().getDuration());
			currentSong = position;
			((BackgroundMusic) pa.getApplication()).setCurSong(currentSong);
    		sb.setEnabled(true);
		}
	}
	
	private void playSong(int position, int seekTo){
		if(playlists.get(currentPlaylist).size()>0){
			loadSong(currentPlaylist, position);
			//((BackgroundMusic) pa.getApplication()).getMp().start();
			Intent intent = new Intent(getApplicationContext(),
	   				 
	    			MusicService.class);
				if(((BackgroundMusic) pa.getApplication()).getServiceRunning()){			
		    	stopService(intent);
			}
               intent.putExtra(MusicService.START_PLAY, true);
               intent.putExtra("SONG", playlists.get(currentPlaylist).get(position));
               intent.putExtra("TIME", seekTo);
               startService(intent);
               ((BackgroundMusic) pa.getApplication()).setServiceRunning(true);
	        song_position = seekTo;
			sb.setProgress(seekTo);
			sb.setMax(((BackgroundMusic) pa.getApplication()).getMp().getDuration());
			currentSong = position;
			((BackgroundMusic) pa.getApplication()).setCurSong(currentSong);
    		sb.setEnabled(true);
		}
	}
	
	private void loadSong(int playlist, int song){
		try{
			((BackgroundMusic) pa.getApplication()).getMp().reset();
			((BackgroundMusic) pa.getApplication()).getMp().setDataSource(playlists.get(playlist).get(song));
			((BackgroundMusic) pa.getApplication()).getMp().prepare();
		} catch (IOException e){
		}
		sb.setProgress(0);
		updateSongText(playlist, song);
	}
	
	private void updateSongText(int playlist, int song){
		if(playlists.get(currentPlaylist).size()>0){
		songName.setText(playlists_songNames.get(playlist).get(song));
		songPositionText.setText(intToTime(0));
		songMaxText.setText(intToTime(((BackgroundMusic) pa.getApplication()).getMp().getDuration()));
		showCoverart(playlist, song);
		sb.setMax(((BackgroundMusic) pa.getApplication()).getMp().getDuration());
		setSelector(song);
		}
	}
	
	private void showCoverart(int playlist, int song){
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
               2f);
        layoutParams.gravity=Gravity.BOTTOM;
		try{
			android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
	        mmr.setDataSource(playlists.get(playlist).get(song));

	        byte [] data = mmr.getEmbeddedPicture();

	        if(data != null)
	        {
	            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	            coverart.setImageBitmap(bitmap);
	            coverart.setAdjustViewBounds(true);
	            coverart.setLayoutParams(layoutParams);
	     
	        }
	        else
	        {
	            coverart.setImageResource(R.drawable.fallback_cover);
	            coverart.setAdjustViewBounds(true);
	            coverart.setLayoutParams(layoutParams);
	        }
	        
		} catch(Exception e) {
				coverart.setImageResource(R.drawable.fallback_cover);
	            coverart.setAdjustViewBounds(true);
	            coverart.setLayoutParams(layoutParams);
		}
	}

	private void selectPlaylist(int index){
		if(((BackgroundMusic) pa.getApplication()).getMp().isPlaying()&&!playlists.get(index).contains(playlists.get(currentPlaylist).get(currentSong))){
			((BackgroundMusic) pa.getApplication()).getMp().stop();
			if(playlists.get(index).size()>0){
			loadSong(index, 0);
			}
			sb.setProgress(0);
			}
		ListAdapter songList = new ListAdapter(context, playlists_songNames.get(index), false);
		list.setAdapter(songList);
		refreshColor(index);
		tv.setText(playlistNames.get(index));
		currentPlaylist = index;
		if(playlists.get(currentPlaylist).size()>0&&autoplay&&shouldAutoplay){
			playSong(0);
		}
		shouldAutoplay = false;
	}
	
	private void refreshColor(int index){
		 ListAdapter la = (ListAdapter)list.getAdapter();
		frame.setBackgroundColor(playlistColors.get(index));
		if (Color.red(playlistColors.get(index)) + Color.green(playlistColors.get(index))
                + Color.blue(playlistColors.get(index)) < 384){
            selectedColor = Color.WHITE;
            la.setSelectorColor(Color.parseColor("#80000000"));
		}
        else{
        	selectedColor = Color.BLACK;
        	la.setSelectorColor(Color.parseColor("#80FFFFFF"));
        }
        la.setTextColor(selectedColor);
		tv.setTextColor(selectedColor);
		songName.setTextColor(selectedColor);
		songPositionText.setTextColor(selectedColor);
		songMaxText.setTextColor(selectedColor);
		helpBtn.setTextColor(selectedColor);
		shuff.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
		prev.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
		stopBtn.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
		next.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
		lockBtn.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
		settingsButton.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
		back.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
	}
	
	private void addSongToList(int listIndex, String song, boolean save){
		if(!playlists.get(listIndex).contains(song)){
		playlists.get(listIndex).add(song);
		playlists_songNames.get(listIndex).add(getNameFromPath(song));
		}
		if(save){
		playlists_putPref();
		playlistsSongNames_putPref();
		}
	}
	
	private void sortSongsAlphabetically(int playlistIndex){
		if(playlists.get(currentPlaylist).size()>0){
		 String cs = playlists.get(currentPlaylist).get(currentSong);
		 ConcurrentSorter.sort(playlists_songNames.get(playlistIndex), playlists_songNames.get(playlistIndex), playlists.get(playlistIndex));
		 playlists_putPref();
		 playlistsSongNames_putPref();
		 updateSongList(list);
		 int newIndex = playlists.get(currentPlaylist).indexOf(cs);
		 ((BackgroundMusic) pa.getApplication()).setCurSong(newIndex);
		 currentSong = newIndex;
		 setSelector(currentSong);
		}
		 try{
				mToast.cancel();
				} catch (Exception e){
					
		}
		 mToast = Toast.makeText(context, "Playlist sorted", Toast.LENGTH_SHORT);
		 mToast.show();
	}
	
	private String getNameFromPath(String path){
		return getNameFromFile(getFileFromPath(path));
	}
	
	private String getNameFromFile(String file){
		int end=0;
		if(file.endsWith(".mp3")){
			end = file.indexOf(".mp3");
		} else if(file.endsWith(".mkv")){
			end = file.indexOf(".mkv");
		} else if(file.endsWith(".wav")){
			end = file.indexOf(".wav");
		} else if(file.endsWith(".mid")){
			end = file.indexOf(".mid");
		}
		return file.substring(0, end);
	}
	
	private String getFileFromPath(String path){
		String name = path;
		
		while(name.contains("/")){
			name = name.substring(name.indexOf("/")+1);
		}
		
		return name; 
	}
	
	private void showError(String message){
		LayoutInflater li = LayoutInflater.from(context);
		View errorView = li.inflate(R.layout.error_generic, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		alertDialogBuilder.setView(errorView);

		TextView msg = (TextView) errorView
				.findViewById(R.id.errorMsg);
		msg.setText(message);
		alertDialogBuilder
		.setNegativeButton("Okay",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });
		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}
	
	private void swapSongIndexes(int index1, int index2){
		String temp = playlists.get(currentPlaylist).get(index2);
		String temp2 = playlists_songNames.get(currentPlaylist).get(index2);
		playlists.get(currentPlaylist).set(index2, playlists.get(currentPlaylist).get(index1));
		playlists_songNames.get(currentPlaylist).set(index2, playlists_songNames.get(currentPlaylist).get(index1));
		playlists.get(currentPlaylist).set(index1, temp);		
		playlists_songNames.get(currentPlaylist).set(index1, temp2);
		playlists_putPref();
		playlistsSongNames_putPref();
		updateSongList(list);
		iv.getLayoutParams().height = list.getHeight() + 5;
	}
	
	private void updateSongList(ListView lView){
		if (lView.getAdapter() == null) {
			ListAdapter songAdapter = new ListAdapter(context, playlists_songNames.get(currentPlaylist), false);
			lView.setAdapter(songAdapter);
		} else {
			    ((ListAdapter) lView.getAdapter()).notifyDataSetChanged();
		}
	}
	
	private void moveSongDown(int index){
		if(index == currentSong){
			if(index+1>=playlists.get(currentPlaylist).size()){
			((BackgroundMusic) pa.getApplication()).setCurSong(0);
			currentSong=0;
			} else {
			((BackgroundMusic) pa.getApplication()).setCurSong(currentSong+1);
			currentSong++;
			}
		} else if (index == currentSong-1){
			((BackgroundMusic) pa.getApplication()).setCurSong(currentSong-1);
			currentSong--;
		}
		if(index+1>=playlists.get(currentPlaylist).size()){
		swapSongIndexes(index,0);
		selectedSong=0;
		} else {
		swapSongIndexes(index, index+1);
		selectedSong++;
		}
		setSelector(currentSong);
	}
	
	private void moveSongUp(int index){
		if(index == currentSong){
			if(index-1<0){
			((BackgroundMusic) pa.getApplication()).setCurSong(playlists.get(currentPlaylist).size()-1);
			currentSong=playlists.get(currentPlaylist).size()-1;
			} else {
				((BackgroundMusic) pa.getApplication()).setCurSong(currentSong-1);
				currentSong--;
			}
			
		} else if (index == currentSong+1){
			((BackgroundMusic) pa.getApplication()).setCurSong(currentSong+1);
			currentSong++;
		}
		if(index-1<0){
		swapSongIndexes(index,playlists.get(currentPlaylist).size()-1);
		selectedSong=playlists.get(currentPlaylist).size()-1;
		} else {
		swapSongIndexes(index, index-1);
		selectedSong--;
		}
		setSelector(currentSong);
	}
	
	private void playlists_putPref(){
		tinydb.putListOfList("playlists", playlists);
	}
	
	private void playlistsSongNames_putPref(){
		tinydb.putListOfList("playlists_songNames", playlists_songNames);
	}
	
	private void playlistNames_putPref(){
		tinydb.putList("playlistNames", playlistNames);
	}
	
	private void playlistColors_putPref(){
		tinydb.putListInt("playlistColors", playlistColors);
	}
	
	private void directories_getPref(){
		List<String> pl = tinydb.getList("directories");
		if(pl.size()>0)
		((BackgroundMusic) pa.getApplication()).setDirectories(pl);
	}
	
	private String intToTime(long millis) {
	    StringBuffer buf = new StringBuffer();

	    int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
	    int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
	   
	    if(minutes<10){
	    	buf.append(minutes);
	    } else {
	    	buf.append(String.format("%02d", minutes));
	    }
	    buf
	        .append(":")
	        .append(String.format("%02d", seconds));

	    return buf.toString();
	}
	
	@Override
	public void onBackPressed() {    
		((BackgroundMusic) pa.getApplication()).setPlaylists(playlists);
		((BackgroundMusic) pa.getApplication()).setPlaylistsSongNames(playlists_songNames);
		((BackgroundMusic) pa.getApplication()).setPlaylistNames(playlistNames);
		((BackgroundMusic) pa.getApplication()).setPlaylistColors(playlistColors);
		((BackgroundMusic) pa.getApplication()).setCurSong(currentSong);
		((BackgroundMusic) pa.getApplication()).setCurPlaylist(currentPlaylist);
		((BackgroundMusic) pa.getApplication()).setPositionInSong(((BackgroundMusic) pa.getApplication()).getMp().getCurrentPosition());
		((BackgroundMusic) pa.getApplication()).setPlay(play);
		Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
		context.startActivity(myIntent);
		finish();
	}
	
	private void settleDifferences(){
		List<String> temp = lookForMissingFiles(((BackgroundMusic) pa.getApplication()).getDirectories());
		
		if(temp.size()>0){			
			differencesDialog(true, temp);
			currentSong = 0;
		}
		
		List<String> temp2 = lookForNewFiles(((BackgroundMusic) pa.getApplication()).getDirectories());
		if(temp2.size()>0){	
			differencesDialog(false, temp2);
			}		
	}
	
	public List<String> lookForMissingFiles(List<String> paths){
		List<String> differences = new ArrayList<String>();
		if(paths.size()>0){
		List<String> songlist =  new ArrayList<String>();
		for(int i = 0; i<paths.size(); i++){
		File home = new File(paths.get(i));
		if(home.listFiles( new Mp3Filter()).length>0){
			for(File file : home.listFiles( new Mp3Filter())){
				songlist.add(file.getAbsolutePath());
				
			}
			}
		}
		
		for(int i = 0; i<playlists.get(0).size(); i++){
				if(!songlist.contains(playlists.get(0).get(i))){
					differences.add(playlists.get(0).get(i));
				}
				
				
			}
		} else {
			differences.addAll(playlists.get(0));
		}
			return differences;
		}
	
	public List<String> lookForNewFiles(List<String> paths){
		List<String> songlist =  new ArrayList<String>();
		for(int i = 0; i<paths.size(); i++){
		File home = new File(paths.get(i));
		if(home.listFiles( new Mp3Filter()).length>0){
			for(File file : home.listFiles( new Mp3Filter())){
				songlist.add(file.getAbsolutePath());
			}
			}
		}
		List<String> differences = new ArrayList<String>();
		
		for(int i = 0; i<songlist.size(); i++){
			if(!playlists.get(0).contains(songlist.get(i))){
				differences.add(songlist.get(i));
			}
		}
		return differences;
	}
	
	
	public void differencesDialog(boolean missingNotNew, List<String> differences){
		LayoutInflater li = LayoutInflater.from(context);
		View path_alertView = li.inflate(R.layout.path_alert, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(path_alertView);

		// set dialog message
		alertDialogBuilder
			.setNegativeButton("Gotcha!",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	dialog.cancel();
			    }
			  });
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		TextView tv = (TextView) path_alertView.findViewById(R.id.dynText);
		if(missingNotNew){
		tv.setText("could not be found.");
		} else {
			tv.setText("have been added to the main folder!");
		}
		ListView lv = (ListView) path_alertView.findViewById(R.id.songList);
		ArrayAdapter<String> differenceAdapter = new ArrayAdapter<String>(context, R.layout.song_item, R.id.text1, differences);
		lv.setAdapter(differenceAdapter);
		if(missingNotNew){
		for(int i = 0; i<differences.size(); i++){
			for(int i2 = 0; i2<playlists.size(); i2++){
			if(playlists.get(i2).contains(differences.get(i))){
			playlists.get(i2).remove(differences.get(i));
			playlists_songNames.get(i2).remove(getNameFromPath(differences.get(i)));
				}
			}
			}
		} else {
			for(int i = 0; i<differences.size(); i++){
				playlists.get(0).add(differences.get(i));
				playlists_songNames.get(0).add(getNameFromPath(differences.get(i)));
			}
		}
		playlists_putPref();
		playlistsSongNames_putPref();
		updateSongList(list);
		if(playlists.get(currentPlaylist).size()<1){
			songName.setText("");
			sb.setEnabled(false);
			songMaxText.setText(intToTime(0));
			songPositionText.setText(intToTime(0));
		}
	}
	
}
