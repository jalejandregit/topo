package com.sisa;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.StringUtils;

//import org.apache.tomcat.util.buf.StringUtils;

public class Parameters {
	static String url;
	String tagsinclusiones;
	String tagsexclusiones;
	String[] tagsExclude;
	String[] tagsInclude;
	String entrada;
	String paginas;
	String coordenadas;
	int ipaginas;
	//private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");
	 //String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
	
	
	public Parameters(HttpServletRequest request) {
		// TODO Auto-generated constructor stub
		this.url = request.getParameter("url");
		this.tagsinclusiones = request.getParameter("selectorentrada");
		this.tagsexclusiones = request.getParameter("tagsexclusiones");
		this.paginas = request.getParameter("paginas");
		setIpaginas();
		this.entrada = request.getParameter("entrada");
		this.coordenadas = request.getParameter("coordenadas");
		
		/*
		String rawString = "Entwickeln Sie mit Vergnügen"; 
		byte[] bytes = StringUtils.getBytesUtf8(rawString);;
		String utf8EncodedString = StringUtils.newStringUtf8(bytes);
		System.out.printf(utf8EncodedString, rawString);
		*/
	}
	/**
	 * @param url
	 * @return
	 * @throws URISyntaxException
	 */
	public  String getDomainName() throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		domain = domain.endsWith("/") ? domain.replaceFirst(".$","") : domain ;
		return domain.startsWith("www.") ? domain.substring(4) : domain ;
	}

	public String getUrl() {
		return url;
	}

	public String getSelectEntrades() {
		return tagsinclusiones;
	}
	
	public String[] getTagsExclude() {
		String[] tagsExclude = tagsexclusiones.split(",");
		return tagsExclude;
	}
	
	public String[] getTagsInclude()  {
		String[] tagsInclude = tagsinclusiones.split(",");
		return tagsInclude;
	}
	
	
	public boolean getExclude() {
		return this.tagsexclusiones!=""?true:false;
	}
	public boolean getInclude() {
		return this.tagsinclusiones!=""?true:false;
	}

	public int getIpaginas() {
		return ipaginas;
	}

	public void setIpaginas() {
		this.ipaginas = Integer.parseInt(this.paginas);
	}

	public String getEntrada() {
		return entrada;
	}
	public String getCoordenadas() {
		return coordenadas;
	}

}
