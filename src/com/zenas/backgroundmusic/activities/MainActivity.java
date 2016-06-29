package com.zenas.backgroundmusic.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zenas.backgroundmusic.MusicService;
import com.zenas.backgroundmusic.R;
import com.zenas.backgroundmusic.BackgroundMusic;
import com.zenas.backgroundmusic.util.ColorPickerDialog;
import com.zenas.backgroundmusic.util.FileDialog;
import com.zenas.backgroundmusic.util.HelpDialog;
import com.zenas.backgroundmusic.util.ListAdapter;
import com.zenas.backgroundmusic.util.MessageDialog;
import com.zenas.backgroundmusic.util.MessageDialog.CompletionListener;
import com.zenas.backgroundmusic.util.MessageDialog.NoListener;
import com.zenas.backgroundmusic.util.MessageDialog.YesListener;
import com.zenas.backgroundmusic.util.PlaylistAdapter;
import com.zenas.backgroundmusic.util.PreferenceUtils;
import com.zenas.backgroundmusic.util.ColorPickerDialog.OnColorSelectedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private ListView list;
	private Button moveUpBtn;
	private Button moveDownBtn;
	private Button doneBtn;
	private static final String SD_PATH = new String(Environment.getExternalStorageDirectory().getPath());
	final Context context = this;
	final MainActivity ma = this;
	private List<List<String>> playlists = new ArrayList<List<String>>();
	private List<List<String>> playlists_songNames = new ArrayList<List<String>>();
	private List<String> playlistNames = new ArrayList<String>();
	private List<Integer> playlistColors = new ArrayList<Integer>();
	private List<String> directories = new ArrayList<String>();
	
	private int currentPlaylist = 0;
	//do not confuse with current playing list
	private int selectedPlaylist = 0;
	private int currentSong = 0;
	private int tempColor = Color.DKGRAY;
	private boolean shuffle = false;
	private boolean autoplay = false;
	private float volume = 1;
	private int song_position = 0;
	private PreferenceUtils tinydb;
	private ImageView wallpaperView;
	private String wallpaper_path;
	private Bitmap myBitmap;
	private boolean wallpaper_notStretched;
	private boolean hideHelp;
	private Button helpBtn;
	private boolean stopOnExit;
	private boolean exiting = true;
	
	
	public static final String MyPREFERENCES = "MyPrefs" ;
	
	@Override
	public void onBackPressed() {
		((BackgroundMusic) ma.getApplication()).setAnimation(false);
		super.onBackPressed();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(((BackgroundMusic) ma.getApplication()).getAnimation()){
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		}
		setContentView(R.layout.main_screen);
		list = (ListView) findViewById(R.id.folder_list);
		tinydb = new PreferenceUtils(context);
		playlists_getPref();
		playlistsSongNames_getPref();
		playlistNames_getPref();
		playlistColors_getPref();
		volume_getPref();
		shuffle_getPref();
		autoplay_getPref();
		directories_getPref();
		hideHelp_getPref();
		stopOnExit_getPref();
		wallpaperPath_getPref();
		wallpaperNotStretched_getPref();
		playlists = ((BackgroundMusic) ma.getApplication()).getPlaylists();
		playlists_songNames = ((BackgroundMusic) ma.getApplication()).getPlaylistsSongNames();
		playlistNames = ((BackgroundMusic) ma.getApplication()).getPlaylistNames();
		playlistColors = ((BackgroundMusic) ma.getApplication()).getPlaylistColors();
		currentPlaylist = ((BackgroundMusic) ma.getApplication()).getCurPlaylist();
		currentSong = ((BackgroundMusic) ma.getApplication()).getCurSong();
		song_position = ((BackgroundMusic) ma.getApplication()).getPositionInSong();
		volume = ((BackgroundMusic) ma.getApplication()).getVolume();
		shuffle = ((BackgroundMusic) ma.getApplication()).getShuffle();
		autoplay = ((BackgroundMusic) ma.getApplication()).getAutoplay();
		directories = ((BackgroundMusic) ma.getApplication()).getDirectories();
		hideHelp = ((BackgroundMusic) ma.getApplication()).getHideHelp();
		stopOnExit = ((BackgroundMusic) ma.getApplication()).getStopOnExit();
		wallpaper_path = ((BackgroundMusic) ma.getApplication()).getWallpaperPath();
		wallpaperView = (ImageView) findViewById(R.id.wallpaperView);
		updateWallpaper();
		
		list.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int playlist_position, long arg3) {
				
				selectedPlaylist = playlist_position;

				LayoutInflater li = LayoutInflater.from(context);
				View playlistOptionsView = li.inflate(R.layout.playlist_options, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
 
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(playlistOptionsView);
				alertDialogBuilder
				.setNegativeButton("Cancel",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });

			final AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.show(); 
			
				Button editBtn = (Button) playlistOptionsView
						.findViewById(R.id.editBtn);
				editBtn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						alertDialog.dismiss();
						editPlaylistDialog(playlist_position);
						}
					
				});
				
				moveUpBtn = (Button) findViewById(R.id.upBtn);
				moveUpBtn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						movePlaylistUp(selectedPlaylist);
						updatePlaylist(list);
						//displayList(list);
					}
					
				});
				
				moveDownBtn = (Button) findViewById(R.id.downBtn);
				moveDownBtn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						movePlaylistDown(selectedPlaylist);
						updatePlaylist(list);
						//displayList(list);
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
						playlistNames_putPref();
						playlistColors_putPref();
					}
					
				});
				
				moveUpBtn.setVisibility(View.GONE);
				moveDownBtn.setVisibility(View.GONE);
				doneBtn.setVisibility(View.GONE);
				
				Button moveBtn = (Button) playlistOptionsView
						.findViewById(R.id.moveBtn);
				if(playlists.size()<=2||selectedPlaylist==0){
					moveBtn.setEnabled(false);
				} else {
				
				moveBtn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
					
						moveUpBtn.setVisibility(View.VISIBLE);
						moveDownBtn.setVisibility(View.VISIBLE);
						doneBtn.setVisibility(View.VISIBLE);
						alertDialog.dismiss();
					}
					
				});
				}

				
				Button removeBtn = (Button) playlistOptionsView
						.findViewById(R.id.removeBtn);
				if(playlist_position == 0){
					removeBtn.setEnabled(false);
				} else {
					
				removeBtn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						playlists.remove(playlist_position);
						playlists_songNames.remove(playlist_position);
						playlistNames.remove(playlist_position);
						playlistColors.remove(playlist_position);
						playlists_putPref();
						playlistsSongNames_putPref();
						playlistNames_putPref();
						playlistColors_putPref();
						if(((BackgroundMusic) ma.getApplication()).getMp().isPlaying()
								&&currentPlaylist==playlist_position){
							currentPlaylist = 0;
							currentSong = 0;
							song_position = 0;
							((BackgroundMusic) ma.getApplication()).getMp().stop();
						}
						updatePlaylist(list);
						//displayList(list);
						alertDialog.dismiss();
					}
					
				});
				}
			
				return true;
			}
			
		});
		list.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try{
				((BackgroundMusic) ma.getApplication()).setPlaylists(playlists);
				((BackgroundMusic) ma.getApplication()).setPlaylistsSongNames(playlists_songNames);
				((BackgroundMusic) ma.getApplication()).setPlaylistNames(playlistNames);
				((BackgroundMusic) ma.getApplication()).setPlaylistColors(playlistColors);
				currentSong = ((BackgroundMusic) ma.getApplication()).getCurSong();
				boolean foundSong = false;
				boolean shouldAutoplay;
				String song = "";
				if(playlists.get(position).size()>0&&playlists.get(currentPlaylist).size()>0){
				song = playlists.get(currentPlaylist).get(currentSong);
				if(((BackgroundMusic) ma.getApplication()).getMp().isPlaying()){
				foundSong = playlists.get(position).contains(song);
					}
				}
				if(!foundSong||!((BackgroundMusic) ma.getApplication()).getMp().isPlaying()){
					if(((BackgroundMusic) ma.getApplication()).getMp().isPlaying()){
						((BackgroundMusic) ma.getApplication()).getMp().stop();
					}
				((BackgroundMusic) ma.getApplication()).setCurSong(0);
				((BackgroundMusic) ma.getApplication()).setPositionInSong(0);
				((BackgroundMusic) ma.getApplication()).setPlayStarted(false);
				shouldAutoplay = true;
				} else {
						song_position = ((BackgroundMusic) ma.getApplication()).getMp().getCurrentPosition();
					((BackgroundMusic) ma.getApplication()).setPositionInSong(song_position);
					if(!song.equals(""))
					((BackgroundMusic) ma.getApplication()).setCurSong(playlists.get(position).indexOf(song));
					((BackgroundMusic) ma.getApplication()).setPlayStarted(false);
					shouldAutoplay = false;
				}
				((BackgroundMusic) ma.getApplication()).setCurPlaylist(position);
				if(playlists.get(position).size()<1){
					if(((BackgroundMusic) ma.getApplication()).getMp().isPlaying()){
						((BackgroundMusic) ma.getApplication()).getMp().stop();
						shouldAutoplay = false;
							}
				}
				Intent myIntent = new Intent(getBaseContext(), PlaylistActivity.class);
				myIntent.putExtra("SHOULD_AUTOPLAY", shouldAutoplay);
				context.startActivity(myIntent);
				exiting = false;
				finish();
				} catch (Exception e){
				}
			}
		});
		

		init();
		settleDifferences();
		updatePlaylist(list);

		 Button settingsButton = (Button) findViewById(R.id.settings_button);
			settingsButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					Intent myIntent = new Intent(getBaseContext(), SettingsActivity.class);
					context.startActivity(myIntent);
				}
				
			});
		Button newB = (Button) findViewById(R.id.newBtn);
		newB.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				createPlaylistDialog();
			}
			
		});
		
		helpBtn = (Button) findViewById(R.id.mainHelpBtn);
		if(hideHelp){
			helpBtn.setVisibility(View.GONE);
		}
		helpBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HelpDialog hd = new HelpDialog(context);
				//List<String> h1 = new ArrayList<String>();
				hd.setTitle("Help: Main screen");
				hd.addHelpItem("What is a playlist?", "A playlist is a folder that contains a set of songs. Each playlist is represented by a large colored button on the home page, and pressing on it will open the playlist screen.");
				hd.addHelpItem("What is \"All songs\"?", "This is the root playlist that shows all of your songs. It cannot be moved, deleted, or have its song list modified (other than reordering). However, it is possible to change both the name and the background color of the playlist.");
				hd.addHelpItem("Creating a playlist", "You can create a playlist by pressing the \"New Playlist\" button in the bottom left.");
				hd.addHelpItem("Modifying a playlist", "Shortly after pressing and holding down on a playlist, a menu will show up that will allow you to edit, move or delete the selected playlist.");
				hd.addHelpItem("Playlist settings", "For each playlist, you can decide the displayed name and background color. Excluding the root playlist, you can also set the songs the playlist contains by copying from an existing playlist or by changing it manually.");
				hd.addHelpItem("Other settings", "You can access more application settings by pressing the gear icon in the bottom right.");
				hd.updateList();
				hd.show();
			}
			
		});
		
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    if(myBitmap!=null){
	    myBitmap.recycle();
	    myBitmap=null;
	    }
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		directories_getPref();
		directories = ((BackgroundMusic) ma.getApplication()).getDirectories();
		//wallpaperPath_getPref();
		wallpaper_path = ((BackgroundMusic) ma.getApplication()).getWallpaperPath();
		//wallpaperNotStretched_getPref();
		wallpaper_notStretched = ((BackgroundMusic) ma.getApplication()).getWallpaperNotStretched();
		updateWallpaper();
		//hideHelp_getPref();
		hideHelp = ((BackgroundMusic) ma.getApplication()).getHideHelp();
		if(hideHelp){
			helpBtn.setVisibility(View.GONE);
		} else {
			helpBtn.setVisibility(View.VISIBLE);
		}
		stopOnExit = ((BackgroundMusic) ma.getApplication()).getStopOnExit();
		
		settleDifferences();
	}
	
	private void init(){
		if(playlists.size()<1){
			createPlaylist("All songs", 0, tempColor, new ArrayList<String>());
			directories = new ArrayList<String>();
			File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
			musicDirectory.mkdir();
			directories.add(musicDirectory.getAbsolutePath()+"/");
			directories_putPref();
       	    ((BackgroundMusic) ma.getApplication()).setDirectories(directories);
   			fillPlaylist(directories.get(0), 0);
        		if(playlists.get(0).size()>0){
           		MessageDialog md = new MessageDialog(context, "Notice", "Added " + playlists.get(0).size() + " songs to the root folder!", false);
           		md.show();
        		} else {
           			MessageDialog md = new MessageDialog(context, "Notice", "Couldn't find any songs in your default music folder.", false);
        			md.addCompletionListener(new CompletionListener(){

						@Override
						public void completed() {
							// TODO Auto-generated method stub
		        			MessageDialog md2 = new MessageDialog(context, "Notice", "Would you like to select other folders now? (You can always do this later in the settings page)", true);
		        			md2.addYesListener(new YesListener(){

								@Override
								public void yes() {
									// TODO Auto-generated method stub
									List<String> messages = new ArrayList<String>();
									messages.add("In the following screen, you can select a folder by clicking on it. Selected folders have checkmarks beside them.");			
				        			messages.add("To navigate inside folders, press \"Navigation mode\" at the bottom.");			
				        			messages.add("Remember that you can always add or remove folders in the settings page later!");		
				        			MessageDialog md3 = new MessageDialog(context, "Getting started", messages, false);
				        			md3.show();
				        			md3.addCompletionListener(new CompletionListener(){

				        				@Override
				        				public void completed() {
				        					// TODO Auto-generated method stub
				        					showDirectoryDialog();
				        					//settleDifferences();
				        				}
				        				
				        			});
								}
		        				
		        			});
		        			md2.addNoListener(new NoListener(){

								@Override
								public void no() {
									// TODO Auto-generated method stub
									MessageDialog md4 = new MessageDialog(context, "Notice", "If you're ever stuck just press the \"Help\" button and there will be a brief guide on how to use each function.", false);
									md4.show();
								}
		        				
		        			});
		        			md2.show();
						}
        				
        			});
           			md.show();
        			
           		}
			}
		}
	
	private void settleDifferences(){
		List<String> temp = lookForMissingFiles(((BackgroundMusic) ma.getApplication()).getDirectories());
		
		if(temp.size()>0){			
			differencesDialog(true, temp);
			currentSong = 0;
		}
		
		List<String> temp2 = lookForNewFiles(((BackgroundMusic) ma.getApplication()).getDirectories());
		if(temp2.size()>0){	
			differencesDialog(false, temp2);
			}		
	}
	
	private void createPlaylistDialog(){
		final List<String> songs = new ArrayList<String>();
		//songs.add("");
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
			    	if(!userInput.getText().toString().equals("")&&userInput.getText()!=null){
			    			if(!(userInput.getText().toString().length()>30)){
			    			createPlaylist(userInput.getText().toString(), playlists.size(), tempColor, songs);
			    			} else {
			    				createPlaylistDialog();
				    			showError("Make sure the name isn't over 30 characters long.");
			    			}
			    		} else {
			    			createPlaylistDialog();
			    			showError("You forgot to enter a name for the playlist.");
			    		}
			    	}
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		
		final Button colorB = (Button) promptsView.findViewById(R.id.colorButton);
		
		//colorB.setBackgroundColor(tempColor);
		colorB.getBackground().setColorFilter(tempColor, PorterDuff.Mode.MULTIPLY);
		//colorB.setBackgroundResource(R.drawable.ic_launcher);
		colorB.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				ColorPickerDialog colorPickerDialog = new ColorPickerDialog(context, tempColor, new OnColorSelectedListener() {

			        @Override
			        public void onColorSelected(int color) {
			            // do action
						tempColor = color;
						colorB.getBackground().setColorFilter(tempColor, PorterDuff.Mode.MULTIPLY);
			        }

			    });
			    colorPickerDialog.show();
			}
			
		});
		
		//CREATEEEEE
		
		Button copyPlaylistBtn = (Button) promptsView.findViewById(R.id.copyBtn);
		//if(selectedPlaylist==0){
			//copyPlaylistBtn.setVisibility(View.GONE);
		//} else {
			copyPlaylistBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					LayoutInflater li = LayoutInflater.from(context);
					View playlistView = li.inflate(R.layout.select_playlist, null);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							context);

					// set prompts.xml to alertdialog builder
					alertDialogBuilder.setView(playlistView)
						.setNegativeButton("Cancel",
							  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });
					final AlertDialog alertDialog = alertDialogBuilder.create();
					
					// show it
					alertDialog.show();
					
					//final List<String> playlistsMinusCurrent = new ArrayList<String>(playlistNames);
					//playlistsMinusCurrent.remove(selectedPlaylist);
					
					final ListView lv = (ListView) playlistView.findViewById(R.id.playlist_list);
					PlaylistAdapter listOfPlaylists = new PlaylistAdapter(context, playlistNames, playlistColors);
					lv.setAdapter(listOfPlaylists);
					
					lv.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {

							songs.clear();
							for(int i = 0; i<playlists.get(position).size(); i++){
								songs.add(playlists.get(position).get(i));
							}
							
							

							
							alertDialog.dismiss();
							
						}
						
					});

				}
				
			});
		
		
		Button selectSongBtn = (Button) promptsView.findViewById(R.id.selectSongBtn);
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
 
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(selectSongView);
				alertDialogBuilder
				.setNegativeButton("Cancel",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
					//alertDialog.dismiss();
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
				    dialog.cancel();
					dialog.dismiss();
				    }
				  });

				
				alertDialogBuilder
				.setNeutralButton("Select all", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
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
	
	private void createPlaylist(String name, final int index, int color, List<String> songlist){
		List<String> songs = new ArrayList<String>();
		List<String> songNames = new ArrayList<String>();
		playlistNames.add(name);
		playlistColors.add(color);
		playlists.add(songs);
		//showError(playlists.get(playlists.size()-1).size()+"");
		playlists_songNames.add(songNames);
		playlists_putPref();
		for(int i = 0; i<songlist.size(); i++){
			//showError(songlist.get(i));
			addSongToList(playlists.size()-1, songlist.get(i), false);
		}
		
		playlists_putPref();
		playlistsSongNames_putPref();
		playlistNames_putPref();
		playlistColors_putPref();
		updatePlaylist(list);
		//displayList(list);
	}
	
	private void editPlaylistDialog(final int index){
		final List<String> songs = new ArrayList<String>(playlists.get(index));
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);
		userInput.setText(playlistNames.get(index));
		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
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
			    	if(selectedPlaylist!=0){
			    	playlists.get(selectedPlaylist).clear();
			    	playlists_songNames.get(selectedPlaylist).clear();
			    	//playlists_putPref();
			    	for(int i = 0; i<songs.size(); i++){
			    		addSongToList(selectedPlaylist, songs.get(i), false);
			    	}
		    		playlists_putPref();
		    		playlistsSongNames_putPref();
			    	}
			    	//playlists_putPref();
			    	updatePlaylist(list);
			    	//displayList(list);
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		tempColor = playlistColors.get(index);
		final Button colorB = (Button) promptsView.findViewById(R.id.colorButton);
		colorB.getBackground().setColorFilter(tempColor, PorterDuff.Mode.MULTIPLY);
		//colorB.setBackgroundColor(playlistColors.get(index));
		colorB.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				ColorPickerDialog colorPickerDialog = new ColorPickerDialog(context, tempColor, new OnColorSelectedListener() {

			        @Override
			        public void onColorSelected(int color) {
			            // do action
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
		if(selectedPlaylist==0){
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

					// set prompts.xml to alertdialog builder
					alertDialogBuilder.setView(playlistView)
						.setNegativeButton("Cancel",
							  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });
					final AlertDialog alertDialog = alertDialogBuilder.create();
					
					// show it
					alertDialog.show();
					
					final List<String> playlistsMinusCurrent = new ArrayList<String>(playlistNames);
					playlistsMinusCurrent.remove(selectedPlaylist);
					final List<Integer> playlistColorsMinusCurrent = new ArrayList<Integer>(playlistColors);
					playlistColorsMinusCurrent.remove(selectedPlaylist);
					
					final ListView lv = (ListView) playlistView.findViewById(R.id.playlist_list);
					final PlaylistAdapter listOfPlaylists = new PlaylistAdapter(context, playlistsMinusCurrent, playlistColorsMinusCurrent);
					lv.setAdapter(listOfPlaylists);
					
					lv.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							// TODO Auto-generated method stub
							if(position>=selectedPlaylist){
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
		if(selectedPlaylist==0){
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
	 
					// set prompts.xml to alertdialog builder
					alertDialogBuilder.setView(selectSongView);
					alertDialogBuilder
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						//alertDialog.dismiss();
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
					    dialog.cancel();
						dialog.dismiss();
					    }
					  });

					
					alertDialogBuilder
					.setNeutralButton("Select all", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
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
	
	private void showError(String message){
		LayoutInflater li = LayoutInflater.from(context);
		View errorView = li.inflate(R.layout.error_generic, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(errorView);

		TextView msg = (TextView) errorView
				.findViewById(R.id.errorMsg);
		msg.setText(message);
		// set dialog message
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
	
	private void swapPlaylistIndexes(int index1, int index2){
		if(index1!=index2){
		List<String> temp1 = playlists.get(index2);
		List<String> temp2 = playlists_songNames.get(index2);
		String temp3 = playlistNames.get(index2);
		int temp4 = playlistColors.get(index2);
		playlists.set(index2, playlists.get(index1));
		playlists_songNames.set(index2, playlists_songNames.get(index1));
		playlistNames.set(index2, playlistNames.get(index1));
		playlistColors.set(index2, playlistColors.get(index1));
		
		playlists.set(index1, temp1);
		playlists_songNames.set(index1, temp2);
		playlistNames.set(index1, temp3);
		playlistColors.set(index1, temp4);
		
		updatePlaylist(list);
		}
		//listScrollY = list.getScrollY();
		//ArrayAdapter<String> songAdapter = new ArrayAdapter<String>(context, R.layout.song_item, playlists_songNames.get(currentPlaylist));
		//list.setAdapter(songAdapter);
		//list.set
	}
	
	private void updatePlaylist(ListView lView){
		if (lView.getAdapter() == null) {
			PlaylistAdapter playlistAdapter = new PlaylistAdapter(context, playlistNames, playlistColors);
		    lView.setAdapter(playlistAdapter);
		} else {
			    ((PlaylistAdapter) lView.getAdapter()).notifyDataSetChanged();
		} 
	}
	
	private void movePlaylistDown(int index){
		if(index+1>=playlists.size()){
		swapPlaylistIndexes(index,1);	
		selectedPlaylist=1;
		} else {
		swapPlaylistIndexes(index, index+1);
		selectedPlaylist++;
		}
	}
	
	private void movePlaylistUp(int index){
		if(index-1<=0){
		swapPlaylistIndexes(index,playlists.size()-1);	
		selectedPlaylist=playlists.size()-1;
		} else {
		swapPlaylistIndexes(index, index-1);
		selectedPlaylist--;
		}
	}

	
	private void fillPlaylist(String path, int playlistID){
		File home = new File(path);
		if(home.listFiles( new Mp3Filter()).length>0){
			for(File file : home.listFiles( new Mp3Filter())){
				Log.w(getString(R.string.app_name), file.getName());
				String s = file.getAbsolutePath();
				playlists.get(playlistID).add(s);
				playlists_songNames.get(playlistID).add(getNameFromPath(s));
				
			}
		}
		playlists_putPref();
		playlistsSongNames_putPref();
	}
	
	private void playSong(int position){
		try{
			((BackgroundMusic) ma.getApplication()).getMp().reset();
			((BackgroundMusic) ma.getApplication()).getMp().setDataSource(playlists.get(currentPlaylist).get(position));
			((BackgroundMusic) ma.getApplication()).getMp().prepare();
			((BackgroundMusic) ma.getApplication()).getMp().start();
			currentSong = position;
			((BackgroundMusic) ma.getApplication()).setCurSong(currentSong);
		} catch (IOException e){
			Log.v(getString(R.string.app_name), e.getMessage());
		}
	}
	
	
	public static int randInt(int min, int max) {
		
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public void playRandomSong(){
		if(playlists.get(currentPlaylist).size()>0){
		int random = randInt(0,playlists.get(currentPlaylist).size()-1);
		while(random==currentSong){
			random = randInt(0,playlists.get(currentPlaylist).size()-1);
		}
		currentSong = random;
		playSong(currentSong);
		}
	}
	
	public void playNextSong(){
		if(playlists.get(currentPlaylist).size()>0){
		playSong(nextSong());
		}
	}
	
	public int nextSong(){
		if(playlists.get(currentPlaylist).size()>0){
			if(currentSong+1>=playlists.get(currentPlaylist).size()){
				currentSong = -1;
			}
			//play the song at current index + 1
			return(currentSong+1);
		} else {
			return 0;
		}
	}
	
	public void playPrevSong(){
		if(currentSong-1<0){
			currentSong = 1;
		}
		//play the song at current index + 1
		playSong(currentSong-1);
	}
	
	public void setVolume(float percent){
		((BackgroundMusic) ma.getApplication()).getMp().setVolume(percent, percent);
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
	}
	
	private void fatalErrorCheck(){
		if(playlists.size()!=playlists_songNames.size()){
			playlists.clear();
			playlists_songNames.clear();
		} else {
		for(int i = 0; i<playlists.size(); i++){
			if(playlists.get(i).size()!=playlists_songNames.size()){
				playlists.clear();
				playlists_songNames.clear();
				break;
			}
		}
		}
	}
	
	private String getFileFromPath(String path){
		String name = path;
		
		while(name.contains("/")){
			name = name.substring(name.indexOf("/")+1);
		}
		
		return name; 
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
	
	private void playlists_getPref(){
		List<List<String>> pl = tinydb.getListOfList("playlists");
		if(pl.size()>0){
		((BackgroundMusic) ma.getApplication()).setPlaylists(pl);
		playlists = pl;
		}
	}
	
	private void playlists_putPref(){
		tinydb.putListOfList("playlists", playlists);
	}
	
	private void playlistsSongNames_getPref(){
		List<List<String>> pl = tinydb.getListOfList("playlists_songNames");
		if(pl.size()>0)
		((BackgroundMusic) ma.getApplication()).setPlaylistsSongNames(pl);
	}
	
	private void playlistsSongNames_putPref(){
		tinydb.putListOfList("playlists_songNames", playlists_songNames);
	}
	
	private void playlistNames_getPref(){
		List<String> pl = tinydb.getList("playlistNames");
		if(pl.size()>0)
		((BackgroundMusic) ma.getApplication()).setPlaylistNames(pl);
	}
	
	private void playlistNames_putPref(){
		tinydb.putList("playlistNames", playlistNames);
	}
	
	private void playlistColors_getPref(){
		List<Integer> pl = tinydb.getListInt("playlistColors");
		if(pl.size()>0)
		((BackgroundMusic) ma.getApplication()).setPlaylistColors(pl);
	}
	
	private void playlistColors_putPref(){
		tinydb.putListInt("playlistColors", playlistColors);
	}
	
	private void volume_getPref(){
		float v = tinydb.getFloat("volume");
		if(v>=0)
		((BackgroundMusic) ma.getApplication()).setVolume(v);
	}
	
	private void shuffle_getPref(){
		boolean b = tinydb.getBoolean("shuffle");
		((BackgroundMusic) ma.getApplication()).setShuffle(b);
	}
	
	private void autoplay_getPref(){
		boolean b = tinydb.getBoolean("autoplay");
		((BackgroundMusic) ma.getApplication()).setAutoplay(b);
	}
	
	private void directories_getPref(){
		List<String> pl = tinydb.getList("directories");
		if(pl.size()>0)
		((BackgroundMusic) ma.getApplication()).setDirectories(pl);
	}
	
	private void directories_putPref(){
		tinydb.putList("directories", directories);
	}
	
	private void hideHelp_getPref(){
		Boolean b = tinydb.getBoolean("hideHelp");
		((BackgroundMusic) ma.getApplication()).setHideHelp(b);
	}
	
	private void stopOnExit_getPref(){
		Boolean b = tinydb.getBoolean("stopOnExit");
		((BackgroundMusic) ma.getApplication()).setStopOnExit(b);
	}
	
	private void wallpaperPath_getPref(){
		String s = tinydb.getString("wallpaper_path");
		((BackgroundMusic) ma.getApplication()).setWallpaperPath(s);
	}
	
	private void wallpaperNotStretched_getPref(){
		Boolean b = tinydb.getBoolean("wallpaper_notStretched");
		((BackgroundMusic) ma.getApplication()).setWallpaperNotStretched(b);
	}
	
	public void showDirectoryDialog(){
		File mPath = new File(SD_PATH);
        FileDialog fileDialog = new FileDialog(this, mPath, "", directories, true, false);
        fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
          public void directorySelected(List<String> dir) {
        	  directories = new ArrayList<String>(dir);
        	  directories_putPref();
        	  ((BackgroundMusic) ma.getApplication()).setDirectories(directories);
        		for(int i = 0; i<directories.size(); i++){
    				fillPlaylist(directories.get(i), 0);
    			}
        		List<String> messages2 = new ArrayList<String>();
         		if(playlists.get(0).size()>0){
            		messages2.add("Added " + playlists.get(0).size() + " songs to the root folder!");
            		} else {
            			messages2.add("Couldn't find any songs in those locations. Download music files and place them there!");
            		}
        		messages2.add("If you're ever stuck just press the \"Help\" button and there will be a brief guide on how to use each function.");
        		MessageDialog md = new MessageDialog(context, "Notice", messages2, false);
        		md.show();
          }
        });
        //fileDialog.setSelectDirectoryOption(true);
        fileDialog.showDialog();
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
	
	private String getNameFromPath(String path){
		return getNameFromFile(getFileFromPath(path));
	}
	
	private void updateWallpaper(){
		if(wallpaper_notStretched){
			wallpaperView.setScaleType(ScaleType.CENTER_CROP);
		} else {
			wallpaperView.setScaleType(ScaleType.FIT_XY);
		}
		if(wallpaper_path!=null){
		File imgFile = new  File(wallpaper_path);
		if(imgFile.exists()){

		    myBitmap = decodeSampledBitmap(imgFile, 250, 400);
		    		//BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    wallpaperView.setImageBitmap(myBitmap);

		} else {
			wallpaperView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper));
		}
	} else {
		wallpaperView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper));
	}
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	
	public static Bitmap decodeSampledBitmap(File file,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(file.getAbsolutePath(), options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
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
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(exiting){
		if(stopOnExit){
			if(((BackgroundMusic) ma.getApplication()).getMp().isPlaying()){
				((BackgroundMusic) ma.getApplication()).getMp().stop();
				//((BackgroundMusic) ma.getApplication()).getMp().release();
			}
			if(((BackgroundMusic) ma.getApplication()).getServiceRunning()){
				 Intent intent = new Intent(getApplicationContext(), MusicService.class);
			    	stopService(intent);
			}
		}
		}
	}

}
