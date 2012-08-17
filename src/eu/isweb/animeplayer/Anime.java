package eu.isweb.animeplayer;

public class Anime {
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
