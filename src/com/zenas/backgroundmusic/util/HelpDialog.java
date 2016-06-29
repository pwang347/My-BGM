package com.zenas.backgroundmusic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import com.zenas.backgroundmusic.util.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.zenas.backgroundmusic.R;

public class HelpDialog {
	Context context;
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
    AlertDialog dialog;
    TextView title;
	
	public HelpDialog(Context context){
		this.context = context;
		LayoutInflater li = LayoutInflater.from(context);
		View helpView = li.inflate(R.layout.help_screen, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(helpView);
		alertDialogBuilder
		.setNegativeButton("Close",
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface d,int id) {
			d.dismiss();
		    }
		  });
		dialog = alertDialogBuilder.create();
		expListView = (ExpandableListView) helpView.findViewById(R.id.expandableListView1);
		 expListView.setOnChildClickListener(new OnChildClickListener() {
			 
	            @Override
	            public boolean onChildClick(ExpandableListView parent, View v,
	                    int groupPosition, int childPosition, long id) {
	            	parent.collapseGroup(groupPosition);
	                return false;
	            }
	        });
		title = (TextView) helpView.findViewById(R.id.currentPage);
	}
	
	public void setTitle(String text){
		title.setText(text);
	}
	
	public void show(){
		dialog.show();
	}
	
	public void addHelpItem(String header, List<String> items){
		listDataHeader.add(header);
		listDataChild.put(header, items);
	}
	
	public void addHelpItem(String header, String item){
		listDataHeader.add(header);
		List<String> i = new ArrayList<String>();
		i.add(item);
		listDataChild.put(header, i);
	}
	
	public void updateList(){
        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
	}
}
	
	
	
