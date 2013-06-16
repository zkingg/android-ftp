package com.ftp.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ftp.core.Session;

/**
 * Adapteur ListView <=> HashMap de session
 * 
 */
public class SessionsAdapteur extends BaseAdapter {
	private HashMap<String,Session> sessions;
	private Context context;
	
	public SessionsAdapteur(Context context, HashMap<String,Session> sessions){
		this.sessions = sessions;
		this.context = context;
	}

	@Override
	public int getCount() {
		return sessions.size();
	}

	@Override
	public Object getItem(int index) {
		return getItemFromHashMap(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		 
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_list_sessions, null);
            holder = new ViewHolder();
            holder.login = (TextView) convertView.findViewById(R.id.login_row_session);
            holder.ip = (TextView) convertView.findViewById(R.id.ip_row_session);
            
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Session session = getItemFromHashMap(position);
        holder.login.setText(session.getLogin());
        holder.ip.setText(session.getIp());
        
        return convertView;
	}
	
	private class ViewHolder{
		public TextView login;
		public TextView ip;
	}
	
	/**
	 * Recupere l'Item en fonction de la position souhaité
	 * @param position
	 * @return objet session demandé
	 */
	public Session getItemFromHashMap(int position){
		Iterator<Entry<String, Session>> iterator = sessions.entrySet().iterator();
		int i=0;
		while(iterator.hasNext()){
			Session session = (Session) iterator.next().getValue();
			if(position == i)
				return session;
			
			i++;
		}
		return null;
	}

}
