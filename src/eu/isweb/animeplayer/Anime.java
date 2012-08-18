package eu.isweb.animeplayer;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Anime
	implements Serializable {
	public String name;
	public String URL;
	
	public Anime(String n, String u) {
		this.name = n;
		this.URL = u;
	}
	@Override
	public String toString() {
		return this.name;
	}
}
