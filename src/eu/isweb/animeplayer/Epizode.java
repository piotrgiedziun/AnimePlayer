package eu.isweb.animeplayer;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Epizode 
	implements Serializable {
	public String name;
	public String URL;
	public boolean lastWatched = false;
	
	public Epizode(String n, String u) {
		this.name = n;
		this.URL = u;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public boolean isLastWatched() {
		return lastWatched;
	}
}
