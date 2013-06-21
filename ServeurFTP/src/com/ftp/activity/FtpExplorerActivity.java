package com.ftp.activity;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ftp.core.DTPServer;
import com.ftp.core.Session;

public class FtpExplorerActivity extends Activity implements OnItemClickListener {
	private ArrayList<File> list_files;
	private ListView listview_files;
	private String path = "/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp_explorer);
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null && bundle.containsKey("path")){
			this.path = bundle.getString("path");
		}
		
		this.listview_files = (ListView) findViewById(R.id.listView_ftp_explorer);
		refreshListFile();
		registerForContextMenu(listview_files);
		listview_files.setOnCreateContextMenuListener(this);
		listview_files.setOnItemClickListener(this);
		((TextView)findViewById(R.id.textView_ftp_explorer_path)).setText(this.path);
		
	}
	
	@Override
	protected void onResume() {
		refreshListFile();
		super.onResume();
	}
	
	private void refreshListFile(){
		this.list_files = getListFile();
		listview_files.setAdapter(new FtpExplorerAdaper(this, list_files));
	}

	private ArrayList<File> getListFile() {
		ArrayList<File> list_file = new ArrayList<File>();
		list_file.add(new File(".."));
		String path_to_dir = getCacheDir()+"/"+DTPServer.RACINE_FTP+"/"+this.path;
		path_to_dir = path_to_dir.replaceAll("\\s", "\\ ");
		File root_repertory = new File(path_to_dir);
		Log.i("ftp-explorer", "scanning dir :"+root_repertory.getAbsolutePath());
		if(root_repertory.list() != null){
			for(File f : root_repertory.listFiles()){
				list_file.add(f);
			}			
		}
		
		return list_file;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		File selected_file = list_files.get(position);
		if(selected_file.getPath().equals("..")){//si retour arriere
			/*if(! path.equals("/")){
				String[] dirs = path.split("/");
				int length = dirs[dirs.length-1].length();
				
			}*/
			
			finish();//retour activité précedente
		}else if(selected_file.isDirectory()){
			Intent i = new Intent(this,FtpExplorerActivity.class);
			if(path.equals("/")){
				i.putExtra("path", path+selected_file.getName());
				//this.path = path+selected_file.getName();
			}else{
				i.putExtra("path", path+"/"+selected_file.getName());
				//this.path = path+"/"+selected_file.getName();
			}
			//refreshListFile();	
			startActivity(i);
		}else if(selected_file.isFile()){
			
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.gestion_user_context_menu,menu);		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		File dir = (File) listview_files.getAdapter().getItem(info.position);
		
		if(dir.getPath().equals("..")) 
			return true;
		
		switch(item.getItemId()){
		case R.id.menu_item_delete:
			Session s = new Session(this);
			if(DTPServer.removeDirectoryAndFile(dir)){
				Toast.makeText(this, "Suppression réussite", Toast.LENGTH_SHORT).show();
				refreshListFile();
			}else{
				Toast.makeText(this, "Echec suppression", Toast.LENGTH_SHORT).show();
			}
			
			break;
		}
		
		return true;
	}
	

}
