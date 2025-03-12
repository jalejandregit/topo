package com.sisa;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//https://jarroba.com/scraping-java-jsoup-ejemplos/


public class TopoCrawler2 {
	String[] Tipos = { "jpg", "JPG", "jpeg", "JPGE", "gif","GIF", "png", "PNG" };
	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();


	public TopoCrawler2(String url) {
		
		try {
			String domain = getDomainName(url);
			// Compruebo si me da un 200 al hacer la petición
			int status = getStatusConnectionCode("https://" + domain); 
			if (status == 200) {
				System.out.println("urlRequest=" + domain);
			}else {
				System.err.println(status + " --> error conexion:" + domain);
			}
			 Document htmlDocument =  Jsoup.connect(url)
						.userAgent(USER_AGENT)
						.timeout(1000*5) //it's in milliseconds, so this means 5 seconds. 
						.ignoreHttpErrors(true)
						.get();
			 
			 
				String postLink=getMetaTag(htmlDocument, "og:url");
				String blog_and_title 	= htmlDocument.title();
				String postTitle= getMetaTag(htmlDocument, "og:title");
				String postDescription = getMetaTag(htmlDocument, "og:description");
				String text = htmlDocument.text();
			 
				System.out.println("postLink=" + postLink);
				System.out.println("blog_and_title=" + blog_and_title);
				System.out.println("postTitle:" + postTitle);
				System.out.println("text=" + text);
			 
			//Elements entrada = htmlDocument.select("div.s-prose.js-post-body");
			//Elements entrada = htmlDocument.getElementsByClass("div.post-body");
				Elements entrada = htmlDocument.getElementsByAttributeValueContaining("class", "blog-posts hfeed");
			System.out.println("entrada:"  + entrada.text());
			System.err.println(entrada.size() + "-->" + entrada.text());
			for (Element elem : entrada) {
				 String titulo = elem.getElementsByClass("post-title").text();
	             String autor = elem.getElementsByClass("a.href").toString();
	             String fecha = elem.getElementsByClass("lightText").text();
	             System.out.println(elem.text());
			}
			
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * Con esta método compruebo el Status code de la respuesta que recibo al hacer la petición
	 * EJM:
	 * 		200 OK			300 Multiple Choices
	 * 		301 Moved Permanently	305 Use Proxy
	 * 		400 Bad Request		403 Forbidden
	 * 		404 Not Found		500 Internal Server Error
	 * 		502 Bad Gateway		503 Service Unavailable
	 * @param url
	 * @return Status Code
	 */
	public static int getStatusConnectionCode(String url) {

		Response response = null;
		//USER_AGENT
		try {
			response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
		}
		return response.statusCode();
	}

	public static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) +"/" : domain +"/";
	}

	public static int isTipo(String needle, String[] haystack){
		for (int i=0; i<haystack.length; i++){
			if (haystack[i] != null && haystack[i].equals(needle)
					|| needle == null && haystack[i] == null) return i;
		}

		return -1;
	}
	private String getMetaTag(Document document, String attr) {
		Elements elements = document.select("meta[name=" + attr + "]");
		for (Element element : elements) {
			final String s = element.attr("content");
			if (s != null) return s;
		}
		elements = document.select("meta[property=" + attr + "]");
		for (Element element : elements) {
			final String s = element.attr("content");
			if (s != null) return s;
		}
		return null;
	}


}
