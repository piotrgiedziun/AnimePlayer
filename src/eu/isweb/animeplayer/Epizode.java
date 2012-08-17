package eu.isweb.animeplayer;

public class Epizode {
	public String name;
	public String URL;
	
	public Epizode(String n, String u) {
		this.name = n;
		this.URL = u;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
