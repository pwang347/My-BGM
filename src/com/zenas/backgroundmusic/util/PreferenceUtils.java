package com.zenas.backgroundmusic.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class PreferenceUtils {
	Context mContext;
	SharedPreferences preferences;
	String DEFAULT_APP_IMAGEDATA_DIRECTORY;
	File mFolder = null;
	public static String lastImagePath = "";

	public PreferenceUtils(Context appContext) {
		mContext = appContext;
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public Bitmap getImage(String path) {
		Bitmap theGottenBitmap = null;
		try {
			theGottenBitmap = BitmapFactory.decodeFile(path);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return theGottenBitmap;
	}

	/**
	 * Returns the String path of the last image that was saved with this Object
	 * <p>
	 * 
	 */
	public String getSavedImagePath() {
		return lastImagePath;
	}

	/**
	 * Returns the String path of the last image that was saved with this
	 * tnydbobj
	 * <p>
	 * 
	 * @param String
	 *            the theFolder - the folder path dir you want to save it to e.g
	 *            "DropBox/WorkImages"
	 * @param String
	 *            the theImageName - the name you want to assign to the image file e.g
	 *            "MeAtlunch.png"
	 *            
	 */
	public String putImagePNG(String theFolder, String theImageName,
			Bitmap theBitmap) {
		this.DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder;
		String mFullPath = setupFolderPath(theImageName);
		saveBitmapPNG(mFullPath, theBitmap);
		lastImagePath = mFullPath;
		return mFullPath;
	}

	private String setupFolderPath(String imageName) {
		File sdcard_path = Environment.getExternalStorageDirectory();
		mFolder = new File(sdcard_path, DEFAULT_APP_IMAGEDATA_DIRECTORY);
		String savePath = mFolder.getPath() + '/' + imageName;
		return savePath;
	}

	private boolean saveBitmapPNG(String strFileName, Bitmap bitmap) {
		if (strFileName == null || bitmap == null)
			return false;
		boolean bSuccess1 = false;
		boolean bSuccess2;
		boolean bSuccess3;
		File saveFile = new File(strFileName);

		if (saveFile.exists()) {
			if (!saveFile.delete())
				return false;
		}

		try {
			bSuccess1 = saveFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(saveFile);
			bSuccess2 = bitmap.compress(CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
			bSuccess2 = false;
		}
		try {
			if (out != null) {
				out.flush();
				out.close();
				bSuccess3 = true;
			} else
				bSuccess3 = false;

		} catch (IOException e) {
			e.printStackTrace();
			bSuccess3 = false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return (bSuccess1 && bSuccess2 && bSuccess3);
	}

	public int getInt(String key) {
		return preferences.getInt(key, -1);
	}
	
	public long getLong(String key) {
		return preferences.getLong(key, 0l);
	}

	public String getString(String key) {
		return preferences.getString(key, "");
	}
	
	public double getDouble(String key) {
		String number = getString(key);
		try {
		 double value = Double.parseDouble(number);
		 return value;
		}
		catch(NumberFormatException e)
		{
		  return 0;
		}
	}

	public void putInt(String key, int value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void putLong(String key, long value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public void putDouble(String key, double value) {
		putString(key, String.valueOf(value));
	}

	public void putString(String key, String value) {

		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void putList(String key, List<String> marray) {

		SharedPreferences.Editor editor = preferences.edit();
		String[] mystringlist = marray.toArray(new String[marray.size()]);
		// the comma like character used below is not a comma it is the SINGLE
		// LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
		// seprating the items in the list
		editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
		editor.commit();
	}

	public List<String> getList(String key) {
		String[] mylist = TextUtils
				.split(preferences.getString(key, ""), "‚‗‚");
		List<String> gottenlist = new ArrayList<String>(
				Arrays.asList(mylist));
		return gottenlist;
	}
	
	public List<String> getListLooper(String key) {
		if(preferences.getString(key, "‗").equals("‗")){
			return null;
		}
		String[] mylist = TextUtils
				.split(preferences.getString(key, ""), "‚‗‚");
		List<String> gottenlist = new ArrayList<String>(
				Arrays.asList(mylist));
		return gottenlist;
	}
	
	public void putListOfList(String key, List<List<String>> marray) {
		for(int i = 0; i<marray.size(); i++){
			putList(key+i, marray.get(i));
		} 
		eraseOldValues(key, marray.size());
	}
	
	public void eraseOldValues(String key, int start){
		int counter = start;
		SharedPreferences.Editor editor = preferences.edit();
		while(getListLooper(key+counter)!=null){
			editor.remove(key+counter);
			editor.commit();
			counter++;
		}
	}
	
	public List<List<String>> getListOfList(String key) {
		List<List<String>> gottenlist = new ArrayList<List<String>>();
		int counter = 0;
		while(getListLooper(key+counter)!=null){
			gottenlist.add(getList(key+counter));
			counter++;
		}
	
		return gottenlist;
	}

	public void putListInt(String key, List<Integer> marray) {
		SharedPreferences.Editor editor = preferences.edit();
		Integer[] mystringlist = marray.toArray(new Integer[marray.size()]);
		editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
		editor.commit();
	}
	
	public List<Integer> getListInt(String key) {
		String[] mylist = TextUtils
				.split(preferences.getString(key, ""), "‚‗‚");
		List<String> gottenlist = new ArrayList<String>(
				Arrays.asList(mylist));
		List<Integer> gottenlist2 = new ArrayList<Integer>();
		for (int i = 0; i < gottenlist.size(); i++) {
			gottenlist2.add(Integer.parseInt(gottenlist.get(i)));
		}

		return gottenlist2;
	}
	
	public void putListBoolean(String key,List<Boolean> marray){
		List<String> origList = new ArrayList<String>();
		for(Boolean b : marray){
			if(b==true){
				origList.add("true");
			}else{
				origList.add("false");
			}
		}
		putList(key, origList);
	}
	
	public List<Boolean> getListBoolean(String key) {
		List<String> origList = getList(key);
		List<Boolean> mBools = new ArrayList<Boolean>();
		for(String b : origList){
			if(b.equals("true")){
				mBools.add(true);
			}else{ 
				mBools.add(false);
			} 
		}
		return mBools;
	}
	
	public void putBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key) {
		return preferences.getBoolean(key, false);
	}
	
	public void putFloat(String key, float value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public float getFloat(String key) {
		return preferences.getFloat(key, -1f);
	}
	
	public void remove(String key) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public Boolean deleteImage(String path){
		File tobedeletedImage = new File(path);
		Boolean isDeleted = tobedeletedImage.delete();
		return isDeleted;
	}
	
	public void clear() {
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	
	public Map<String, ?> getAll() {
		return preferences.getAll();
	}
	
	public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
		preferences.registerOnSharedPreferenceChangeListener(listener);
	}
	
	public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
		preferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

}