package com.zenas.backgroundmusic.util;

import java.util.List;
import com.zenas.backgroundmusic.R;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class PlaylistAdapter extends BaseAdapter
{
    private Context context;
    private List<String> testList;
    private List<Integer> colors;

    public PlaylistAdapter(Context ctx, List<String> testList, List<Integer> colors)
    {
        this.context = ctx;
        this.testList = testList;
        this.colors = colors;
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
    
    public int getColor(int position){
    	return colors.get(position);
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
            vi = LayoutInflater.from(context).inflate(R.layout.playlist_item, null);
        }
        else
        {
            vi = convertView;
        }
        
        String txt = (String) this.getItem(position);
        int color = (int) this.getColor(position);
        Button playlist = (Button) vi.findViewById(R.id.playlist_button);
        playlist.setText(txt);
        playlist.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
       

        return vi;
    }
    
}