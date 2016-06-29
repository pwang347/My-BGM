package com.zenas.backgroundmusic.util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.zenas.backgroundmusic.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter
{
    private Context context;
    private List<String> testList;
    private List<Integer> selectedIndice = new ArrayList<Integer>();
    private int selectedColor = Color.BLACK;
    private int selectedTextColor = Color.WHITE;
    private int defaultTextColor = Color.LTGRAY;
    private boolean multiChoice;
    private boolean hideNumbers = false;
    private String currentPath;
    private LruCache<String, Bitmap> mMemoryCache;
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private List<BitmapWorkerTask> workingTasks = new ArrayList<BitmapWorkerTask>(); 
    
    public FileAdapter(Context ctx, List<String> testList, String currentPath, boolean multiChoice, LruCache<String, Bitmap> mMemoryCache)
    {	
    	this.mMemoryCache = mMemoryCache;
        this.context = ctx;
        this.testList = testList;
        this.multiChoice = multiChoice;
        this.currentPath = currentPath;
        for(int i = 0; i<testList.size(); i++){
        	bitmaps.add(null);
        	String file = currentPath+testList.get(i);
        	if(isImage(file)){
        	loadBitmap(new File(file), i);
        	}
        }
        
    }

    public void setSelectedIndex(int ind)
    {
    	if(!multiChoice){
    		selectedIndice.clear();
    	}
        selectedIndice.add(ind);
        notifyDataSetChanged();
    }
    
    public void removeIndex(int ind)
    {
    	int index = selectedIndice.indexOf(ind);
    	if(selectedIndice.contains(ind)){
    	selectedIndice.remove(index);
    	notifyDataSetChanged();
    	}
    }

    @Override
    public int getCount()
    {
        return testList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return testList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View vi;
        if(convertView == null)
        {
            vi = LayoutInflater.from(context).inflate(R.layout.file_item, null);
        }
        else
        {
            vi = convertView;
        }
        
        String file = currentPath + (String)this.getItem(position);
        TextView fileText = (TextView) vi.findViewById(R.id.fileText);
        fileText.setText((String)this.getItem(position));
        ImageView fileThumb = (ImageView) vi.findViewById(R.id.fileThumb);
        
        if(isImage(file)&&bitmaps.get(position)!=null){
        	fileThumb.setImageBitmap(bitmaps.get(position));
        	fileThumb.setVisibility(View.VISIBLE);
        } else {
        	fileThumb.setVisibility(View.GONE);
        }

        return vi;
    }
    
    public boolean isImage(String file){
    	List<String> images = new ArrayList<String>();
		images.add(".png");
		images.add(".jpg");
		images.add(".bmp");
		images.add(".jpeg");
		for(int i = 0; i<images.size(); i++){
			if(file.endsWith(images.get(i))){
				images.clear();
				return true;
			}
		}
		images.clear();
		return false;
    }
    
    public void updateAll(){
        notifyDataSetChanged();
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
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
		if(key==null){
			return null;
		}
	    return mMemoryCache.get(key);
	}
	
	public void loadBitmap(File file, int position) {
		    final String imageKey = file.getAbsolutePath();
		    Bitmap bitmap = getBitmapFromMemCache(imageKey);
		    if (bitmap != null) {
		    	bitmaps.set(position, bitmap);	    
		    } else {
	        BitmapWorkerTask task = new BitmapWorkerTask(position);
	        task.execute(file);
	        workingTasks.add(task);
		    }
	}
	
	public void stopAllTasks(){
		for(int i = 0; i<workingTasks.size(); i++){
			workingTasks.get(i).cancel(true);
		}
		workingTasks.clear();
	}
	
	
	class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
		private int position;
		public BitmapWorkerTask(int position){
			this.position = position;
		}
	    @Override
	    protected Bitmap doInBackground(File... params) {
	    	try{
	        final Bitmap bitmap = decodeSampledBitmap(params[0], 50, 50);
	        bitmaps.set(position, bitmap);
	        addBitmapToMemoryCache(params[0].getAbsolutePath(), bitmap);
	        return bitmap;
	    	} catch (Exception e){
	    	 return null;	
	    	}
	    }
	    @Override
        protected void onPostExecute(Bitmap bitmap) {
	    	updateAll();
        }
	}
}
