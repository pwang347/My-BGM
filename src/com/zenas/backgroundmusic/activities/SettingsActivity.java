package com.zenas.backgroundmusic.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zenas.backgroundmusic.MusicService;
import com.zenas.backgroundmusic.R;
import com.zenas.backgroundmusic.BackgroundMusic;
import com.zenas.backgroundmusic.util.FileDialog;
import com.zenas.backgroundmusic.util.HelpDialog;
import com.zenas.backgroundmusic.util.MessageDialog;
import com.zenas.backgroundmusic.util.MessageDialog.CompletionListener;
import com.zenas.backgroundmusic.util.PreferenceUtils;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressWarnings("deprecation")
public class SettingsActivity extends TabActivity {
	private boolean initial_shuffle;
	private boolean new_shuffle;
	private boolean initial_autoplay;
	private boolean new_autoplay;
	private float initial_volume;
	private float new_volume;
	private Activity sa = this;
	private Context context = this;
	private PreferenceUtils tinydb;
	private List<String> directories = new ArrayList<String>();
	private static final String SD_PATH = new String(Environment.getExternalStorageDirectory().getPath());
	private String initial_wallpaper_path;
	private String new_wallpaper_path;
	private boolean initial_wallpaper_notStretched;
	private boolean new_wallpaper_notStretched;
	private boolean initial_hideHelp;
	private boolean new_hideHelp;
	private boolean initial_stopOnExit;
	private boolean new_stopOnExit;
	private TabHost tabHost;
	private Bitmap myBitmap;
	private ListView lv;
	private Button helpBtn;
	
	@Override
	protected void onPause(){
		super.onPause();
		  if(myBitmap!=null){
			    myBitmap.recycle();
			    myBitmap=null;
			}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			directories = ((BackgroundMusic) sa.getApplication()).getDirectories();
			initial_shuffle = ((BackgroundMusic) sa.getApplication()).getShuffle();
			initial_autoplay = ((BackgroundMusic) sa.getApplication()).getAutoplay();
			initial_volume = ((BackgroundMusic) sa.getApplication()).getVolume();
			initial_wallpaper_path = ((BackgroundMusic) sa.getApplication()).getWallpaperPath();
			initial_wallpaper_notStretched = ((BackgroundMusic) sa.getApplication()).getWallpaperNotStretched();
			initial_hideHelp = ((BackgroundMusic) sa.getApplication()).getHideHelp();
			initial_stopOnExit = ((BackgroundMusic) sa.getApplication()).getStopOnExit();
			new_shuffle = initial_shuffle;
			new_autoplay = initial_autoplay;
			new_volume = initial_volume;
			new_wallpaper_path = initial_wallpaper_path;
			new_wallpaper_notStretched = initial_wallpaper_notStretched;
			new_hideHelp = initial_hideHelp;
			new_stopOnExit = initial_stopOnExit;
		setContentView(R.layout.settings_screen);
		tinydb = new PreferenceUtils(context);
		tabHost = getTabHost();
		tabHost.setCurrentTabByTag("First");

		TabSpec firstTab = tabHost.newTabSpec("general");  
		firstTab.setIndicator("General");
		firstTab.setContent(R.id.first_content);
		tabHost.addTab(firstTab);

		TabSpec secondTab = tabHost.newTabSpec("audio");
		secondTab.setIndicator("Audio");
		secondTab.setContent(R.id.second_content);
		tabHost.addTab(secondTab);

		TabSpec thirdTab = tabHost.newTabSpec("advanced");
		thirdTab.setIndicator("Advanced");
		thirdTab.setContent(R.id.third_content);
		tabHost.addTab(thirdTab);

		tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String tabID) {
				// TODO Auto-generated method stub
				if(tabID.equals("general")){
					ToggleButton tb = (ToggleButton) findViewById(R.id.shuffleBtn);
					tb.setChecked(new_shuffle);
					tb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean arg1) {
							new_shuffle = arg1;
						}
						
					});
					
					ToggleButton tb2 = (ToggleButton) findViewById(R.id.autoplayBtn);
					tb2.setChecked(new_autoplay);

					tb2.setOnCheckedChangeListener(new OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean arg1) {
							// TODO Auto-generated method stub
							new_autoplay = arg1;
							//showError(new_autoplay+"");
						}
						
					});
					
					Button selectFileBtn = (Button) findViewById(R.id.selectFileBtn);
					selectFileBtn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							List<String> images = new ArrayList<String>();
							images.add(".png");
							images.add(".jpg");
							images.add(".bmp");
							images.add(".jpeg");
							showFileDialog(images);
							images.clear();
						}
						
					});
					
					Button defaultBtn = (Button) findViewById(R.id.defaultBtn);
					defaultBtn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							new_wallpaper_path = "";
							tabHost.setCurrentTab(1);
							tabHost.setCurrentTab(0);
						}
						
					});
					
					final ImageView wallpaperPreview = (ImageView) findViewById(R.id.wallpaperPreview);
					if(new_wallpaper_notStretched){
						wallpaperPreview.setScaleType(ScaleType.CENTER_CROP);
					} else {
						wallpaperPreview.setScaleType(ScaleType.FIT_XY);
					}
					File imgFile = new  File(new_wallpaper_path);
					if(imgFile.exists()){

					    myBitmap = decodeSampledBitmap(imgFile, 100, 160);
					    wallpaperPreview.setImageBitmap(myBitmap);

					} else {
						wallpaperPreview.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper));
					}
					
					CheckBox cb = (CheckBox) findViewById(R.id.notStretch);
					cb.setChecked(new_wallpaper_notStretched);
					cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean notStretch) {
							// TODO Auto-generated method stub
							new_wallpaper_notStretched = notStretch;
							if(notStretch){
								wallpaperPreview.setScaleType(ScaleType.CENTER_CROP);
							} else {
								wallpaperPreview.setScaleType(ScaleType.FIT_XY);
							}
						}
						
					});
					

				} else if (tabID.equals("audio")){
					SeekBar sb = (SeekBar) findViewById(R.id.second_content).findViewById(R.id.seekBar2);
					sb.setMax(100);
					sb.setProgress(((int)(new_volume*100)));
					sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

						@Override
						public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
							float c = arg1;
							new_volume = c/arg0.getMax();
							((BackgroundMusic) sa.getApplication()).setVolume(new_volume);
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
					
					ToggleButton stopOnExitBtn = (ToggleButton) findViewById(R.id.stopOnExitBtn);
					stopOnExitBtn.setChecked(new_stopOnExit);
					stopOnExitBtn.setOnCheckedChangeListener(new OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							new_stopOnExit = isChecked;
						}
						
					});
					
				} else if (tabID.equals("advanced")){
					Button manage = (Button) findViewById(R.id.manageBtn);
					manage.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							showDirectoryDialog();
						}
						
					});
					
					lv = (ListView) findViewById(R.id.directory_list);
					updateDirList();
					
					ToggleButton hideHelp = (ToggleButton) findViewById(R.id.hideHelpBtn);
					hideHelp.setChecked(new_hideHelp);
					hideHelp.setOnCheckedChangeListener(new OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							new_hideHelp = isChecked;
							((BackgroundMusic) sa.getApplication()).setHideHelp(isChecked);
							if(isChecked){
							helpBtn.setVisibility(View.GONE);
							} else {
								helpBtn.setVisibility(View.VISIBLE);
							}
							// TODO Auto-generated method stub
							
						}
						
					});
					
				}
			}
			
		});
		
		tabHost.setCurrentTab(2);
		tabHost.setCurrentTab(1);
		tabHost.setCurrentTab(0);
		
		helpBtn = (Button) findViewById(R.id.settingsHelp);
		if(new_hideHelp){
			helpBtn.setVisibility(View.GONE);
		} 
		helpBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HelpDialog hd = new HelpDialog(context);
				if(tabHost.getCurrentTab()==0){
					hd.setTitle("Help: General settings screen");
					hd.addHelpItem("Shuffle mode", "Enabling this option will make a random song play from the playlist each time a song finishes. If disabled, the next song in the playlist will be played instead.");
					hd.addHelpItem("Autoplay on playlist click", "Enabling this option will make the first song of a playlist play as soon as a playlist is clicked from the main screen.");
					hd.addHelpItem("Setting the wallpaper image", "Pressing the \"Default\" button will set the wallpaper to the default My BGM wallpaper. To change this to a custom wallpaper image, press \"Select File\" and a window will show up to select your image file.");
					hd.addHelpItem("Adjusting the ratio of the wallpaper image", "By default, the image will stretch to fit your screen. However, if \"Maintain Ratio\" is checked the image will be resized and cropped to fit the screen size.");
				} else if(tabHost.getCurrentTab()==1){
					hd.setTitle("Help: Audio settings screen");
					hd.addHelpItem("Volume control", "You can use the slider to adjust the volume of the application. This does not affect the volume settings of your android phone.");
					hd.addHelpItem("Close music service on exiting app", "Enabling this option will close the service playing the song whenever the application is closed by the android back button on the main screen.");
				} else if(tabHost.getCurrentTab()==2){
					hd.setTitle("Help: Advanced settings screen");
					hd.addHelpItem("Song source folders", "These are the folders where My BGM will search for songs. Note that subfolders should be selected as well if they need to be included. The current list of source folders will be listed under the section heading.");
					List<String> addRemoveSourceFolders = new ArrayList<String>();
					addRemoveSourceFolders.add("Pressing the \"Manage Folders\" button will show a window that will allow you to select and deselect folders. \nYou can return to a root folder by pressing the \"..\" folder in navigation mode. By default navigation mode is enabled, but you can easily toggle to select mode by pressing the select mode button on the bottom.");
					addRemoveSourceFolders.add("Navigation mode: Clicking a folder will make you navigate inside of it and display its contents.");
					addRemoveSourceFolders.add("Selection mode: Folders have checkboxes next to them to indicate whether or not they are selected. Clicking a folder will select/deselect it.");
					hd.addHelpItem("Adding or removing source folders", addRemoveSourceFolders);
					hd.addHelpItem("Hide help button", "Enabling this option will hide the \"Help\" button on each page.");
				}
				hd.addHelpItem("Applying the changes", "Press the apply button in the bottom left to save the settings for the current and future sessions and return to the previous activity.");
				hd.addHelpItem("Canceling the changes", "Press the cancel button in the bottom left to revert to previous settings and return to the previous activity.");
				hd.updateList();
				hd.show();
			}
			
		});
		
		Button aboutBtn = (Button) findViewById(R.id.aboutBtn);
		aboutBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MessageDialog md = new MessageDialog(context, "About", "© 2014 Paul Wang\nAll Rights Reserved.\n\nPlease contact me at zenas.apps@gmail.com for any questions, bug reports or suggestions.", false);
				md.addCompletionListener(new CompletionListener(){

					@Override
					public void completed() {
						// TODO Auto-generated method stub
						Toast.makeText(context, "Don't forget to rate the app if you like it!", Toast.LENGTH_LONG).show();
					}
					
				});
				md.show();
			}
			
		});
		
		Button apply = (Button) findViewById(R.id.applyBtn);
		apply.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				((BackgroundMusic) sa.getApplication()).setShuffle(new_shuffle);
				((BackgroundMusic) sa.getApplication()).setAutoplay(new_autoplay);
				((BackgroundMusic) sa.getApplication()).setWallpaperPath(new_wallpaper_path);
				((BackgroundMusic) sa.getApplication()).setWallpaperNotStretched(new_wallpaper_notStretched);
				((BackgroundMusic) sa.getApplication()).setHideHelp(new_hideHelp);
				((BackgroundMusic) sa.getApplication()).setStopOnExit(new_stopOnExit);
				volume_putPref();
				shuffle_putPref();
				autoplay_putPref();
				wallpaperPath_putPref();
				wallpaperNotStretched_putPref();
				directories_putPref();
				hideHelp_putPref();
				stopOnExit_putPref();
				finish();
			}
			
		});
		
		Button cancel = (Button) findViewById(R.id.cancelBtn);
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				((BackgroundMusic) sa.getApplication()).setVolume(initial_volume);
				((BackgroundMusic) sa.getApplication()).setShuffle(initial_shuffle);
				((BackgroundMusic) sa.getApplication()).setAutoplay(initial_autoplay);
				((BackgroundMusic) sa.getApplication()).setWallpaperPath(initial_wallpaper_path);
				((BackgroundMusic) sa.getApplication()).setWallpaperNotStretched(initial_wallpaper_notStretched);
				((BackgroundMusic) sa.getApplication()).setHideHelp(initial_hideHelp);
				((BackgroundMusic) sa.getApplication()).setStopOnExit(initial_stopOnExit);
				finish();
			}
			
		});
	}
	
	private void updateDirList(){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.directory_item, directories);
		lv.setAdapter(adapter);
	}
	
	private void volume_putPref(){
		tinydb.putFloat("volume", new_volume);
	}
	
	private void shuffle_putPref(){
		tinydb.putBoolean("shuffle", new_shuffle);
	}
	
	private void autoplay_putPref(){
		tinydb.putBoolean("autoplay", new_autoplay);
	}
	
	private void directories_putPref(){
		tinydb.putList("directories", directories);
	}
	
	private void wallpaperPath_putPref(){
		tinydb.putString("wallpaper_path", new_wallpaper_path);
	}
	
	private void wallpaperNotStretched_putPref(){
		tinydb.putBoolean("wallpaper_notStretched", new_wallpaper_notStretched);
	}
	
	private void hideHelp_putPref(){
		tinydb.putBoolean("hideHelp", new_hideHelp);
	}
	
	private void stopOnExit_putPref(){
		tinydb.putBoolean("stopOnExit", new_stopOnExit);
	}
	
	public void showDirectoryDialog(){
		File mPath = new File(SD_PATH);
        FileDialog fileDialog = new FileDialog(this, mPath, "", directories, true, true);
        fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
          public void directorySelected(List<String> dir) {
        	  directories = new ArrayList<String>(dir);
        	  ((BackgroundMusic) sa.getApplication()).setDirectories(directories);
        	  if( ((BackgroundMusic) sa.getApplication()).getMp().isPlaying()){
        		  ((BackgroundMusic) sa.getApplication()).getMp().stop();
        	  }
        	  updateDirList();
          }
        });
        fileDialog.setSelectDirectoryOption(true);
        fileDialog.showDialog();
	}
	
	public void showFileDialog(List<String> types){
		File mPath = new File(SD_PATH);
        FileDialog fileDialog = new FileDialog(this, mPath, types, null, false, true);
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                new_wallpaper_path = file.toString();
                tabHost.setCurrentTab(1);
                tabHost.setCurrentTab(0);
           	}
        });
        fileDialog.showDialog();
	}
	
	public void showFileDialog(String types){
		File mPath = new File(SD_PATH);
        FileDialog fileDialog = new FileDialog(this, mPath, types, null, false, true);
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                new_wallpaper_path = file.toString();
                tabHost.setCurrentTab(1);
                tabHost.setCurrentTab(0);
           	}
        });
        fileDialog.showDialog();
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	
	public static Bitmap decodeSampledBitmap(File file,
	        int reqWidth, int reqHeight) {

	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(file.getAbsolutePath(), options);
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
	}
	
}
