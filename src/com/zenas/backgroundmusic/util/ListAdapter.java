package com.zenas.backgroundmusic.util;

import java.util.ArrayList;
import java.util.List;

import com.zenas.backgroundmusic.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter
{
    private Context context;
    private List<String> testList;
    private List<Integer> selectedIndice = new ArrayList<Integer>();
    private int selectedColor = Color.BLACK;
    private int selectedTextColor = Color.WHITE;
    private int defaultTextColor = Color.LTGRAY;
    private boolean multiChoice;
    private boolean hideNumbers = false;

    public ListAdapter(Context ctx, List<String> testList, boolean multiChoice)
    {
        this.context = ctx;
        this.testList = testList;
        this.multiChoice = multiChoice;
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
            vi = LayoutInflater.from(context).inflate(R.layout.song_item, null);
        }
        else
        {
            vi = convertView;
        }
        
        TextView songText = (TextView) vi.findViewById(R.id.text1);
        TextView numText = (TextView) vi.findViewById(R.id.numText);
        if(hideNumbers){
        	numText.setVisibility(View.GONE);
        }
        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.linearLayout1);
        
        if(selectedIndice.size()>0 && selectedIndice.contains(position))
        {
        	if(multiChoice){
        		songText.setTextColor(Color.BLACK);
        		numText.setTextColor(Color.BLACK);
         		layout.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        	} else {
            songText.setTextColor(selectedTextColor);
            numText.setTextColor(selectedTextColor);
            layout.setBackgroundColor(selectedColor);
        	}
        }
        else
        {
            songText.setTextColor(defaultTextColor);
            numText.setTextColor(defaultTextColor);
            layout.setBackgroundColor(Color.TRANSPARENT);
        }
        numText.setText((position+1)+"");
        songText.setText(testList.get(position));

        return vi;
    }
    
    public void setSelectorColor(int color){
    	selectedColor = color;
    }
    
    public void setTextColor(int color){
    	selectedTextColor = color;
    	if(color==Color.BLACK){
    		defaultTextColor = Color.DKGRAY;
    	} else {
    		defaultTextColor = Color.LTGRAY;
    	}
    }
    
    public void hideNumbers(){
    	hideNumbers = true;
    }

}