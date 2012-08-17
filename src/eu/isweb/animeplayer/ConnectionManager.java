package eu.isweb.animeplayer;

import java.util.ArrayList;

import android.content.Context;

public class ConnectionManager {
	private static ConnectionManager instance = null;
	private ArrayList<Integer> connections = new ArrayList<Integer>();
	protected ConnectionManager() {}
	
	public synchronized void add(Integer c) {
		connections.add(c);
	}
	
	public synchronized void remove(Integer c) {
		connections.remove(c);
	}
	
	public synchronized int count() {
		return connections.size();
	}
	
	public static ConnectionManager getInstance() {
		if(instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}
}
