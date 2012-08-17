package eu.isweb.animeplayer;

public class Video {
	public String name;
	public String URL;
	public String type;
	
	public Video(String n, String u, String t) {
		this.name = n;
		this.URL = u;
		this.type = t;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
