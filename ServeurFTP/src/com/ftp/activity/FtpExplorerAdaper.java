package com.ftp.activity;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FtpExplorerAdaper extends BaseAdapter {
	private ArrayList<File> list_file;
	private Context c;
	
	public ArrayList<File> getListFiles(){return this.list_file;}
	
	public FtpExplorerAdaper(Context c,ArrayList<File> list_file){
		this.c = c;
		this.list_file = list_file;
	}
	
	@Override
	public int getCount() {
		return list_file.size();
	}

	@Override
	public Object getItem(int i) {
		return list_file.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		 
        LayoutInflater mInflater = (LayoutInflater)
        c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_list_ftp_explorer, null);
            holder = new ViewHolder();
            holder.file_name = (TextView) convertView.findViewById(R.id.textView_ftp_explorer_file_name);
            holder.icon = (ImageView) convertView.findViewById(R.id.imageView_ftp_explorer_icon);
            
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        File file = list_file.get(position);
        if(file.getPath().equals("..")){
        	holder.file_name.setText("..");
        	holder.icon.setImageResource(R.drawable.back_directory);
        }else{        
	        holder.file_name.setText(file.getName());
	        if(file.isDirectory()){
	        	holder.icon.setImageResource(R.drawable.directory);
	        }else if(file.isFile()){
	        	holder.icon.setImageResource(R.drawable.file);
	        }else{
	        	holder.icon.setImageResource(R.drawable.search_file);
	        }
        }
        
        return convertView;
	}
	
	private class ViewHolder{
		public TextView file_name;
		public ImageView icon;
	}

}
