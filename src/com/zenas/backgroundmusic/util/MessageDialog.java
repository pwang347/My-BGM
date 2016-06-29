package com.zenas.backgroundmusic.util;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.zenas.backgroundmusic.util.ListenerList.FireHandler;

public class MessageDialog {
	Context context;
	AlertDialog dialog;
	List<String> messages;
	private String title;
	private boolean yesno;
	
	public interface CompletionListener {
        void completed();
    }
	private ListenerList<CompletionListener> completionListenerList = new ListenerList<CompletionListener>();
	public interface YesListener {
        void yes();
    }
	private ListenerList<YesListener> yesListenerList = new ListenerList<YesListener>();
	public interface NoListener {
        void no();
    }
	private ListenerList<NoListener> noListenerList = new ListenerList<NoListener>();
	
	
	public MessageDialog(Context context, String title, String message, boolean yesno){
		List<String> messages = new ArrayList<String>();
		messages.add(message);
		this.context = context;
		this.messages = messages;
		this.title = title;
		this.yesno = yesno;
		createMessageDialog(0);
	}
	
	public MessageDialog(Context context, String title, List<String> messages, boolean yesno){
		this.context = context;
		this.messages = messages;
		this.title = title;
		this.yesno = yesno;
		createMessageDialog(0);
	}
	
	public void createMessageDialog(final int index){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(messages.get(index));
		if(!yesno){
		if(index>0){
		alertDialogBuilder
		.setNegativeButton("Prev",
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface d,int id) {
		    	d.dismiss();
		    	createMessageDialog(index-1);
		    	show();
		    }
		  });
		}
		if(index<messages.size()-1){
		alertDialogBuilder
		.setPositiveButton("Next",
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface d,int id) {
		    	d.dismiss();
		    	createMessageDialog(index+1);
		    	show();
		    }
		  });
	}
	 else {
		 alertDialogBuilder
			.setPositiveButton("Okay",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface d,int id) {
			    	fireCompletedEvent();
			    	d.dismiss();
			    }
			  });
	 }
		} else {
			alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int which) {
	            		fireYesEvent();
	            		dialog.dismiss();
	            	}
	            });
	        
			alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		fireNoEvent();
	                dialog.dismiss();
	        	}
	        });
		}
		dialog = alertDialogBuilder.create();
		dialog.setCanceledOnTouchOutside(false);
	}
	
	public void show(){
		dialog.show();
	}
	
	 public void addCompletionListener(CompletionListener listener) {
	        completionListenerList.add(listener);
	    }

	    public void removeFileListener(CompletionListener listener) {
	        completionListenerList.remove(listener);
	    }
	    
	    public void addYesListener(YesListener listener) {
	        yesListenerList.add(listener);
	    }

	    public void removeYesListener(YesListener listener) {
	        yesListenerList.remove(listener);
	    }
	    
	    public void addNoListener(NoListener listener) {
	        noListenerList.add(listener);
	    }

	    public void removeNoListener(NoListener listener) {
	        noListenerList.remove(listener);
	    }
	
	private void fireCompletedEvent() {
		completionListenerList.fireEvent(new FireHandler<CompletionListener>() {
            public void fireEvent(CompletionListener listener) {
                listener.completed();
            }
        });
    }
	
	private void fireYesEvent() {
		yesListenerList.fireEvent(new FireHandler<YesListener>() {
            public void fireEvent(YesListener listener) {
                listener.yes();
            }
        });
    }
	
	private void fireNoEvent() {
		noListenerList.fireEvent(new FireHandler<NoListener>() {
            public void fireEvent(NoListener listener) {
                listener.no();
            }
        });
    }
}
