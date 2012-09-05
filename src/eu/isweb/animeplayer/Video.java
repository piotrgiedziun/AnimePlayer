package eu.isweb.animeplayer;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Video 
	implements Serializable {
	
	public static String TYPE_MYSPACE = "myspace.com";
	public static String TYPE_SIBNET = "sibnet.ru";
	public static String TYPE_DAILYMOTION = "dailymotion.com";
	public static String TYPE_VK = "vk.com";
	public static String TYPE_ANIME_SHIDEN = "anime-shinden.info";
	
	public String name;
	public String URL;
	public String type;
	public int position;
	
	public Video(String n, String u, String t, int p) {
		this.name = n;
		this.URL = u;
		this.type = t;
		this.position = p;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
