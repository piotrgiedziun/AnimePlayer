package eu.isweb.animeplayer;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class HTMLLinkExtrator {
 
	  private Pattern patternTag, patternLink;
	  private Matcher matcherTag, matcherLink;
 
	  private static final String HTML_A_TAG_PATTERN = 
                      "(?i)<h5>[^<]+<a([^>]+)>(.+?)</a>[^<]+</h5>";
 
	  private static final String HTML_A_HREF_TAG_PATTERN = 
                      "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
 
	  public HTMLLinkExtrator(){
		  patternTag = Pattern.compile(HTML_A_TAG_PATTERN, Pattern.MULTILINE);
		  patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
	  }
 
	  /**
	   * Validate html with regular expression
	   * @param html html content for validation
	   * @return Vector links and link text
	   */
	  public Vector<HtmlLink> grabHTMLLinks(final String html){
 
		  Vector<HtmlLink> result = new Vector<HtmlLink>();
 
		  matcherTag = patternTag.matcher(html);
 
		  while(matcherTag.find()){
 
			  String href = matcherTag.group(1); //href
			  String linkText = matcherTag.group(2); //link text
 
			  matcherLink = patternLink.matcher(href);
 
			  while(matcherLink.find()){
 
				  String link = matcherLink.group(1); //link
 
				  result.add(new HtmlLink(link.substring(1, link.length()-1), linkText));
 
			  }
 
		  }
 
		  return result;
 
	  }
 
	class HtmlLink {
 
		String link;
		String linkText;
 
		HtmlLink(String link, String linkText){
			this.link = link;
			this.linkText = linkText;
		}
 
		@Override
		public String toString() {
			return new StringBuffer("Link : ")
			.append(this.link)
			.append(" Link Text : ")
			.append(this.linkText).toString();
		}	    
	}
}