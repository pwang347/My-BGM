package com.zenas.backgroundmusic.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.zenas.backgroundmusic.R;
import com.zenas.backgroundmusic.util.ListenerList.FireHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileDialog {
    private static final String PARENT_DIR = "..";
    private String[] fileList;
    private List<String> file_list = new ArrayList<String>();
    private File currentPath;
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public interface DirectorySelectedListener {
        void directorySelected(List<String> dir);
    }
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<FileDialog.DirectorySelectedListener>();
    private final Activity activity;
    private boolean selectDirectoryOption = false;
    private boolean selectMode = true;
    private List<String> fileEndsWith = new ArrayList<String>();    
    private List<String> directories = new ArrayList<String>();
    private boolean canCancel;
    private int offset = 0;
    private Dialog dialog;
    private LruCache<String, Bitmap> mMemoryCache;
    
    /**
     * @param activity 
     * @param initialPath
     */
    public FileDialog(Activity activity, File path, List<String> fileEndsWith, List<String> directories, boolean selectDir, boolean canCancel) {
    	if(directories!=null){
    	for(int i = 0; i<directories.size(); i++){
    		this.directories.add(directories.get(i));
    	}
    	}
        this.activity = activity;
        this.canCancel = canCancel;
        selectDirectoryOption = selectDir;
        if(!selectDirectoryOption){
        	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            
            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
     
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
     
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in bytes rather than number
                    // of items.
                    return bitmap.getByteCount();
                }
     
            };
        }
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        for(int i = 0; i<fileEndsWith.size(); i++){
        	this.fileEndsWith.add(fileEndsWith.get(i));
        }
        loadFileList(path);
    }
    
    public FileDialog(Activity activity, File path, String fileEndsWith, List<String> directories, boolean selectDir, boolean canCancel) {
    	if(directories!=null){
    	for(int i = 0; i<directories.size(); i++){
    		this.directories.add(directories.get(i));
    		}
    	}
        this.activity = activity;
        this.canCancel = canCancel;
        selectDirectoryOption = selectDir;
        if(!selectDirectoryOption){
        	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            
            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
     
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
     
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in bytes rather than number
                    // of items.
                    return bitmap.getByteCount();
                }
     
            };
        }
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        setFileEndsWith(fileEndsWith);
        loadFileList(path);
    }
    
    public boolean[] getCheckedItems(){
    	boolean[] checkedItems = new boolean[fileList.length];
    	if(fileList.length>1){
    	for(int i = 1; i<checkedItems.length; i++){
    		if(directories.contains(currentPath+"/"+fileList[i]+"/")){
    			checkedItems[i] = true;
    		} else {
    			checkedItems[i] = false;
    		}
    	}
    	} else {
    	}
    	return checkedItems;
    }

    /**
     * @return file dialog
     */
    public Dialog createFileDialog() {
        dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) {
        	if(selectMode){
            builder.setNeutralButton("Navigate mode", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	
                		selectMode = false;
                    	ListView lv = ((AlertDialog) dialog).getListView();
                    	offset = lv.getFirstVisiblePosition();
                	loadFileList(currentPath);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                }
            });
        	} else {
        		builder.setNeutralButton("Select mode", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    		
                    		selectMode = true;
                    		ListView lv = ((AlertDialog) dialog).getListView();
                        	offset = lv.getFirstVisiblePosition();
                    	loadFileList(currentPath);
                        dialog.cancel();
                        dialog.dismiss();
                        showDialog();
                    }
                });
        	}
            builder.setPositiveButton("Done", new OnClickListener() {
            	public void onClick(DialogInterface dialog, int which) {
            		fireDirectorySelectedEvent(directories);
            		dialog.cancel();
            		dialog.dismiss();
            	}
            });
        }
        if(canCancel){
        builder.setNegativeButton("Cancel", new OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
        		dialog.cancel();
                dialog.dismiss();
        	}
        });
        }
        if(selectDirectoryOption){
        	if(selectMode){
        	builder.setMultiChoiceItems(fileList, getCheckedItems(), new DialogInterface.OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which,
						boolean isChecked) {
					String fileChosen = fileList[which];
                    File chosenFile = getChosenFile(fileChosen);
						if(isChecked){
	                    	directories.add(chosenFile.getAbsolutePath()+"/");
						} else {
							directories.remove(chosenFile.getAbsolutePath()+"/");
						}
						}
				
			}); 
        	} else {
        		builder.setItems(fileList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String fileChosen = fileList[which];
                        File chosenFile = getChosenFile(fileChosen);
                        if (chosenFile.isDirectory()) {
                            loadFileList(chosenFile);
                            dialog.cancel();
                            dialog.dismiss();
                            showDialog();
                        } else fireFileSelectedEvent(chosenFile);
                    }
                });
        	}

        } else {
        	LayoutInflater li = LayoutInflater.from(activity);
    		View fileView = li.inflate(R.layout.select_file, null);
    		ListView lv = (ListView) fileView.findViewById(R.id.file_list);
    		final FileAdapter fa = new FileAdapter(activity, file_list, currentPath.getAbsolutePath()+"/", false, mMemoryCache);
    		lv.setAdapter(fa);
    		lv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					
					fa.stopAllTasks();
					String fileChosen = file_list.get(position);
					 File chosenFile = getChosenFile(fileChosen);
	                    dialog.cancel();
	                    dialog.dismiss();
		                if (chosenFile.isDirectory()) {
		                    loadFileList(chosenFile);
		                    showDialog();
		                } else fireFileSelectedEvent(chosenFile);
				}
    			
    		});
    		builder.setView(fileView);
    	
        }
        dialog = builder.show();
        if(selectDirectoryOption){
        ListView lv = ((AlertDialog) dialog).getListView();
    	lv.setItemsCanFocus(true);
    	lv.setSelection(offset);
        }
        return dialog;
    }


    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    public void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    /**
     * Show file dialog
     */
    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new FireHandler<FileDialog.FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final List<String> dir) {
        dirListenerList.fireEvent(new FireHandler<FileDialog.DirectorySelectedListener>() {
            public void fireEvent(DirectorySelectedListener listener) {
                listener.directorySelected(dir);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) return false;
                    if (selectDirectoryOption) return sel.isDirectory();
                    else {
                    	for(int i = 0; i<fileEndsWith.size(); i++){
                    	if(filename.toLowerCase(Locale.ENGLISH).endsWith(fileEndsWith.get(i))){
                    			return true;
                    		}
                    	}
                    	if(sel.isDirectory()){
                    		return true;
                    	} else {
                    		return false;
                    	}
         
                    }
                }
            };
            String[] fileList1 = path.list(filter);
            for (String file : fileList1) {
                r.add(file);
            }
        }
        fileList = (String[]) r.toArray(new String[]{});
        file_list.clear();
        for(int i = 0; i<fileList.length; i++){
        	file_list.add(fileList[i]);
        }
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    public void setFileEndsWith(String extension) {
    	if(extension!=null)
        fileEndsWith.add(extension.toLowerCase(Locale.ENGLISH));
    }
 }

class ListenerList<L> {
private List<L> listenerList = new ArrayList<L>();

public interface FireHandler<L> {
    void fireEvent(L listener);
}

public void add(L listener) {
    listenerList.add(listener);
}

public void fireEvent(FireHandler<L> fireHandler) {
    List<L> copy = new ArrayList<L>(listenerList);
    for (L l : copy) {
        fireHandler.fireEvent(l);
    }
}

public void remove(L listener) {
    listenerList.remove(listener);
}

public List<L> getListenerList() {
    return listenerList;
}
}
