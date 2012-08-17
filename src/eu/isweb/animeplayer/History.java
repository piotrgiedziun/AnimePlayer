package eu.isweb.animeplayer;

public class History {
	public String url;
	public String name;
	public String epizode;
	public String lastURL;
	
	public History(String url, String name, String epizode, String lastURL) {
		this.url = url;
		this.name = name;
		this.epizode = epizode;
		this.lastURL = lastURL;
	}
	
	@Override
	public String toString() {
		return this.name + " : " + this.epizode;
	}
}
