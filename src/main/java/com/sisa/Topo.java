package com.sisa;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;

/**
 * Servlet implementation class Topo
 * 
 */
@WebServlet("/Topo")
public class Topo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	static String url;
	private Set<String> pagesPingedInDB ;//= new HashSet<String>();
	private List<String> pagesToVisit;// = new LinkedList<String>();
	//List<String> links;
	TreeMap<Integer,String> linkPost;
	TreeMap<Integer,String> linkTitlePost;
	TreeMap<Integer,String> linkContent;
	TreeMap<Integer,String> linksImg;
	TreeMap<Integer,String> linksImgThumb;

	TreeMap<Integer,String> linkPost2;
	TreeMap<Integer,String> linkTitlePost2;
	TreeMap<Integer,String> linkContent2;
	TreeMap<Integer,String> linksImg2;
	TreeMap<Integer,String> linksImgThumb2;
	private String domain;
	private String OneUrl;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Topo() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		linkPost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linkTitlePost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linkContent = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		String URL;
		String domain = null;
		String domainName = null;
		linkPost2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linkTitlePost2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linkContent2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linksImg2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linksImgThumb2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();


		Parameters params = new Parameters(request);
		try {
			domain = params.getDomainName();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!params.getEntrada().isEmpty()) {
			this.url=params.getUrl();
		}else {
			/* incluimos la barra para todas las urls */
			this.url= params.getUrl().endsWith("/") ? params.getUrl() : params.getUrl() + "/";
		}
		this.url=params.getUrl();
		this.OneUrl = url;

		ScrapingOne scrap =new ScrapingOne(params);

		Set<String> setVisited = scrap.getPagesVisited();
		this.linkPost = 		scrap.getLinkPost();
		this.linkContent =		scrap.getLinkContent();
		this.linksImg =			scrap.getLinksImg();
		this.linksImgThumb = 	scrap.getLinksImgThumb();
		this.linkTitlePost =	scrap.getLinkTitlePost();
		//List<String> listToVisit =scrap.getPagesToVisit();

		Set<String> setProcesar =  new HashSet<String>();
		//Set setLink = linkPost.entrySet();
		Set setRecopiladas = linkPost.entrySet();
		Iterator iVisited= setRecopiladas.iterator();
		while(iVisited.hasNext()) {
			Map.Entry meLink =(Map.Entry) iVisited.next();

			String url=meLink.getValue().toString();
			//if(!existUrlPagesPingedInDB(url)) {
			setProcesar.add((String) meLink.getValue());
			System.out.println("recopilada: " + url);
			//}
		}

		/* mostrar les pagines que hem visitat*/
		String excludes="";
		String includes="";

		for(String str: params.getTagsExclude()) {
			excludes += str+ System.lineSeparator();
		}

		for(String str: params.getTagsInclude()) {
			includes += str+ System.lineSeparator();
		}
		PrintWriter out = response.getWriter();
		out.println ("<HTML>");			
		out.println ("<BODY>");	
		out.println ("<BR>");

		/** Se devuelve la tabla de consulta pedida */
		out.println ("<H1>Resultados </H1>");
		out.println ("<table border=\"1\"><thead>" 
				+ "	<tr>"
				+ "		<td width='25%'>Url inicial: </td><td width='75%'>" + params.getUrl() +   "</td>" 
				+ "	</tr> " 
				+ "	<tr> " 
				+ "		<td width='25%'>Selector entradas: </td><td width='75%'>" + includes + "</td>"
				+ "</tr>"
				+ "	<tr> " 
				+ "		<td width='25%'>Selector exclusiones: </td><td width='75%'>" + excludes + "</td>"
				+ "</tr>"
				+ "</thead><tbody>");
		out.println ("</tbody></table>");
		out.println ("<BR>");

		/** botón de nueva consulta */
		out.println ("	<form action='/topo/' method='get' target='_self'>");
		out.println ("		<input type='submit'	id='idTopo'  name=''  value='new scraping'>");
		out.println ("	</form>");		
		out.println ("<BR>");

		/** Grabar las páginas visitadas y se muestran en listado html */
		out.println ("<H3>PAGINAS VISITADAS</H3>");			

		for (String s : setVisited) {
			out.println("\n" + s + "<BR>");
		}
		out.println ("<BR>");


		//fileVistas.graba(setProcesar);	
		out.println ("<H3>PAGINAS a procesar: " + setProcesar.size() +  "</H3>");
		for (String s : setProcesar) {
			out.println("\n" + s + "<BR>");
		}
		out.println ("<BR>");



		/***********************************************************/
		/** Mostrar los pots recopilados, resumen a consola + html */
		System.out.println("linkPost= " + linkPost.size());
		System.out.println("linkTitlePost= " + linkTitlePost.size());
		System.out.println("linksImg= " + linksImg.size());
		System.out.println("linksImgThumb= " + linksImgThumb.size());

		out.println ("<H3>Pots recopilados</H3>");
		Set setLink = linkPost.entrySet();
		Iterator iLink = setLink.iterator();

		Set setTitlePost = linkTitlePost.entrySet();
		Iterator iLinkTitlePost = setTitlePost.iterator();


		Set setImg = linksImg.entrySet();
		Iterator iImg = setImg.iterator();

		Set setThumb = linksImgThumb.entrySet();
		Iterator iThumb = setThumb.iterator();
		int nous=0;

		Set setLinkx = linkPost.entrySet();
		Iterator ilinkx = setLinkx.iterator();
		while(ilinkx.hasNext()) {
			Map.Entry meLink =(Map.Entry) ilinkx.next();
			///////////   if(!existUrlPagesPingedInDB(meLink.getValue().toString())) {
			if(OneUrl.compareTo(meLink.getValue().toString())==0) {
				System.err.println("OneUrl: " + OneUrl + " link:"+ meLink.getValue().toString());
				//}
				//System.out.println("Topo.doGet() " + meLink.getValue().toString());
				nous++;
				linkPost2.put(nous, meLink.getValue().toString()); //linkPost.get(meImgx.getKey()));
				linkTitlePost2.put(nous, linkTitlePost.get(meLink.getKey()));
				linkContent2.put(nous, linkContent.get(meLink.getKey()));
				linksImg2.put(nous, linksImg.get(meLink.getKey()));
				linksImgThumb2.put(nous, linksImgThumb.get(meLink.getKey()));

			}
		}


		Set setLinkf = linkPost2.entrySet();
		Iterator iLinkf = setLinkf.iterator();

		Set setTitlePostf = linkTitlePost2.entrySet();
		Iterator iLinkTitlePostf= setTitlePostf.iterator();

		Set setContent = linkContent2.entrySet();
		Iterator iLinkContentf= setContent.iterator();

		Set setImgf = linksImg2.entrySet();
		Iterator iImgf = setImgf.iterator();

		Set setThumbf = linksImgThumb2.entrySet();
		Iterator iThumbf = setThumbf.iterator();

		String tabla2="<TABLE BORDER>\r\n"
				+ "	<TR>\r\n"
				+ "		<TD>A</TD> <TD>B</TD> <TD>C</TD> <TD>D</TD> <TD>E</TD>\r\n"
				+ "	</TR>\r\n"
				+ "	<TR>\r\n"
				+ "		<TH COLSPAN=5>Head1</TH>\r\n"
				+ "	</TR>\r\n"
				+ "</TABLE>";

		/***********************************************************/
		/** Fom de procesar */
		out.println ("	<form action='/topo/Process' method='post' target='_self'>");
		out.println(tabla2);
		out.println("2 - Total entrades: " +  linkPost2.size());

		out.println("<input type=\"hidden\" name=\"linkPost_size\"  value=" + linkPost2.size() + ">");
		out.println("<input type=\"hidden\" name=\"linksDomain\"  value=\"" + domain + "\">");

		out.println ("<table border=\"1\"><thead>" );
		/***************  Display elements  ************************/
		int sel=0;
		while(iLinkf.hasNext()) {
			sel++;
			/**/
			String tablaOrientacion="<br> \r\n"
					+ "		Orientacion:<select\r\n"
					+ "			name=\"orientacion\">\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"\"></option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"N\">N</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"NE\">NE</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"E\">E</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"SE\">SE</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"S\">S</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"SO\">SO</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"O\">O</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"NO\">NO</option>\r\n"
					+ "		</select> ";


			String selectLinkPost="<br>"
					+ "		Procesar:<select "
					+ "			name=\"Process_linkPost_"  + sel + "\">"
					+ "			<option value='false'>false</option>"
					+ "			<option value='true'>true</option>"
					+ "		</select> ";

			String coordenades="<br>"
					+ "Coordenades lat/long:<input type=\"text\" size=\"50\" name=\"coordenades_"+ sel + "\" aria-invalid=\"true\" aria-describedby=\"first-name-coordenadas\">" 
					+ "<span id=\"first-name-coordenadas\">Please enter a value coordenadas.</span>";

			Map.Entry meLinkf = (Map.Entry)iLinkf.next();
			Map.Entry meTitlef= (Map.Entry)iLinkTitlePostf.next();
			Map.Entry meImgf = (Map.Entry)iImgf.next();
			Map.Entry meThumbf= (Map.Entry)iThumbf.next();
			Map.Entry meContentf =(Map.Entry)iLinkContentf.next();

			/* campos ocultos para el request del Applet Process */
			out.println ("<input name=\"linkTitle_"  + sel + "\" type=\"hidden\" value=\"" + (String) meTitlef.getValue().toString() + "\">");
			out.println ("<input name=\"linkHref_"  + sel + "\" type=\"hidden\" value=" + meLinkf.getValue() + ">");
			out.println ("<input name=\"linkContent_"  + sel + "\" type=\"hidden\" value=\"" + (String) meContentf.getValue().toString() + "\">");
			out.println ("<input name=\"linkImg_"  + sel + "\" type=\"hidden\" value=" + meImgf.getValue() + ">");
			out.println ("<input name=\"linkThumb_"  + sel + "\" type=\"hidden\" value=" + meThumbf.getValue() + ">");

			// "		<TD>A</TD> <TD>B</TD> <TD>C</TD> <TD>D</TD> <TD>E</TD>\r\n"

			out.println("<tr>");
			//out.println ("<td width='100%'>" + "<h3 class='post-title'><a href=" + meLinkf.getValue() +">" + meTitlef + "</a></h3></td>" );
			out.println("<td>" + "<h3 class='post-title'><a href=" + meLinkf.getValue() +">" + meTitlef + "</a></h3></td>" );
			out.println("<td>" + selectLinkPost + "</td>");
			out.println("<td>" + tablaOrientacion + "</td>");
			out.println("<td>" + coordenades + "</td>");
			out.println("<td width='25%'>" + "<div class='separator' style='clear: both; text-align: center;'>" +
					"<a href=" + meImgf.getValue() + " imageanchor='1' style='margin-left: 1em; margin-right: 1em;'><img border='0' height='192' " +
					" src=" + meThumbf.getValue() + " width='320'>" + 
					"</a></div></td>") ;
			out.println("</tr>");
			out.println("<tr>");

			out.println("<td COLSPAN=5>" + meContentf + "</td>");



			out.println("</tr>");

			/*
			out.println ("<div class='separator' style='clear: both; text-align: center;'>" +
					"<a href=" + meImgf.getValue() + " imageanchor='1' style='margin-left: 1em; margin-right: 1em;'><img border='0' height='192' " +
					" src=" + meThumbf.getValue() + " width='320'>" + 
					"</a></div>");
			 */
		}
		out.println ("</table>");


		out.println ("		<input type='submit'	id='idTopo'  name=''  value='Procesar'>");
		out.println ("	</form>");		
		out.println ("<BR>");


		out.println ("</BODY>");
		out.println ("</HTML>");
		destroy () ;



	}





	protected void doGetOLD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		linkPost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linkTitlePost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linkContent = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		String URL;
		String domain = null;
		String domainName = null;
		linkPost2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linkTitlePost2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linkContent2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linksImg2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
		linksImgThumb2 = (TreeMap<Integer,String>) new TreeMap<Integer,String>();

		Parameters params = new Parameters(request);


		try {
			domain = params.getDomainName();
			SqlRemote sql = new SqlRemote();
			pagesPingedInDB = sql.pagesPingedInDB(domain );
			System.out.println("Topo --> pagesPingedInDB.size(): " + pagesPingedInDB.size());

		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		TopoCrawler scrap = new TopoCrawler(params);
		this.linkPost = 		scrap.getLinkPost();
		this.linkPost = 		scrap.getLinkPost();
		this.linkContent =		scrap.getLinkContent();
		this.linksImg =			scrap.getLinksImg();
		this.linksImgThumb = 	scrap.getLinksImgThumb();
		this.linkTitlePost =	scrap.getLinkTitlePost();

		Set<String> setProcesar =  new HashSet<String>();
		Set setRecopiladas = linkPost.entrySet();
		Iterator iVisited= setRecopiladas.iterator();
		while(iVisited.hasNext()) {
			Map.Entry meLink =(Map.Entry) iVisited.next();

			String url=meLink.getValue().toString();
			if(!existUrlPagesPingedInDB(url)) {
				setProcesar.add((String) meLink.getValue());
			}
		}
		out.println ("<HTML>");			
		out.println ("<BODY>");	
		out.println ("<BR>");
		out.println ("<H1>Resultados </H1>");
		out.println ("<table border=\"1\"><thead>" 
				+ "	<tr>"
				+ "		<td width='25%'>Url inicial: </td><td width='75%'>" + params.getUrl() +   "</td>" 
				+ "	</tr> " 
				+ "</thead><tbody>");
		out.println ("</tbody></table>");
		out.println ("<BR>");
		System.out.println("linkPost= " + linkPost.size());
		System.out.println("linkTitlePost= " + linkTitlePost.size());
		System.out.println("linksImg= " + linksImg.size());
		System.out.println("linksImgThumb= " + linksImgThumb.size());

		Set setLink = linkPost.entrySet();
		Iterator iLink = setLink.iterator();

		Set setTitlePost = linkTitlePost.entrySet();
		Iterator iLinkTitlePost = setTitlePost.iterator();


		Set setImg = linksImg.entrySet();
		Iterator iImg = setImg.iterator();

		Set setThumb = linksImgThumb.entrySet();
		Iterator iThumb = setThumb.iterator();
		int nous=0;



		String guid = domain;

		Set setLinkx = linkPost.entrySet();
		Iterator ilinkx = setLinkx.iterator();
		while(ilinkx.hasNext()) {
			Map.Entry meLink =(Map.Entry) ilinkx.next();
			if(!existUrlPagesPingedInDB(meLink.getValue().toString())) {
				nous++;
				linkPost2.put(nous, meLink.getValue().toString()); //linkPost.get(meImgx.getKey()));
				linkTitlePost2.put(nous, linkTitlePost.get(meLink.getKey()));
				linkContent2.put(nous, linkContent.get(meLink.getKey()));
				linksImg2.put(nous, linksImg.get(meLink.getKey()));
				linksImgThumb2.put(nous, linksImgThumb.get(meLink.getKey()));
			}
		}


		Set setLinkf = linkPost2.entrySet();
		Iterator iLinkf = setLinkf.iterator();

		Set setTitlePostf = linkTitlePost2.entrySet();
		Iterator iLinkTitlePostf= setTitlePostf.iterator();

		Set setContent = linkContent2.entrySet();
		Iterator iLinkContentf= setContent.iterator();

		Set setImgf = linksImg2.entrySet();
		Iterator iImgf = setImgf.iterator();

		Set setThumbf = linksImgThumb2.entrySet();
		Iterator iThumbf = setThumbf.iterator();
		String tabla2="<TABLE BORDER>\r\n"
				+ "	<TR>\r\n"
				+ "		<TD>A</TD> <TD>B</TD> <TD>C</TD> <TD>D</TD> <TD>E</TD>\r\n"
				+ "	</TR>\r\n"
				+ "	<TR>\r\n"
				+ "		<TH COLSPAN=5>Head1</TH>\r\n"
				+ "	</TR>\r\n"
				+ "</TABLE>";
		out.println ("	<form action='/topo/Process' method='post' target='_self'>");
		out.println(tabla2);
		out.println("2 - Total entrades: " +  linkPost2.size());

		out.println("<input type=\"hidden\" name=\"linkPost_size\"  value=" + linkPost2.size() + ">");
		out.println("<input type=\"hidden\" name=\"linksDomain\"  value=\"" + domain + "\">");

		out.println ("<table border=\"1\"><thead>" );

		int sel=0;
		while(iLinkf.hasNext()) {
			sel++;

			String tablaOrientacion="<br> \r\n"
					+ "		Orientacion:<select\r\n"
					+ "			name=\"orientacion\">\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"\"></option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"N\">N</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"NE\">NE</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"E\">E</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"SE\">SE</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"S\">S</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"SO\">SO</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"O\">O</option>\r\n"
					+ "			<option name=\"orientacion_" + sel + " value=\"NO\">NO</option>\r\n"
					+ "		</select> ";


			String selectLinkPost="<br>"
					+ "		Procesar:<select "
					+ "			name=\"Process_linkPost_"  + sel + "\">"
					+ "			<option value='false'>false</option>"
					+ "			<option value='true'>true</option>"
					+ "		</select> ";

			String coordenades="<br>"
					+ "Coordenades lat/long:<input type=\"text\" size=\"50\" name=\"coordenades_"+ sel + "\" aria-invalid=\"true\" aria-describedby=\"first-name-coordenadas\">" 
					+ "<span id=\"first-name-coordenadas\">Please enter a value coordenadas.</span>";

			Map.Entry meLinkf = (Map.Entry)iLinkf.next();
			Map.Entry meTitlef= (Map.Entry)iLinkTitlePostf.next();
			Map.Entry meImgf = (Map.Entry)iImgf.next();
			Map.Entry meThumbf= (Map.Entry)iThumbf.next();
			Map.Entry meContentf =(Map.Entry)iLinkContentf.next();
			out.println ("<input name=\"linkTitle_"  + sel + "\" type=\"hidden\" value=\"" + (String) meTitlef.getValue().toString() + "\">");
			out.println ("<input name=\"linkHref_"  + sel + "\" type=\"hidden\" value=" + meLinkf.getValue() + ">");
			out.println ("<input name=\"linkContent_"  + sel + "\" type=\"hidden\" value=\"" + (String) meContentf.getValue().toString() + "\">");
			out.println ("<input name=\"linkImg_"  + sel + "\" type=\"hidden\" value=" + meImgf.getValue() + ">");
			out.println ("<input name=\"linkThumb_"  + sel + "\" type=\"hidden\" value=" + meThumbf.getValue() + ">");
			out.println("<tr>");
			out.println ("<td>" + "<h3 class='post-title'><a href=" + meLinkf.getValue() +">" + meTitlef + "</a></h3></td>" );

			out.println("</tr>");
			out.println("<tr>");
			out.println("<td width='5%'>" + selectLinkPost + "</td>");
			out.println("<td width='10%'>" + tablaOrientacion + "</td>");
			out.println("<td width='10%'>" + coordenades + "</td>");
			out.println ("<td width='60%'>" + meContentf + "</td><td width='25%'>" + "<div class='separator' style='clear: both; text-align: center;'>" +
					"<a href=" + meImgf.getValue() + " imageanchor='1' style='margin-left: 1em; margin-right: 1em;'><img border='0' height='192' " +
					" src=" + meThumbf.getValue() + " width='320'>" + 
					"</a></div></td>") ;

			out.println("</tr>");

		}
		out.println ("</table>");
		out.println ("		<input type='submit'	id='idTopo'  name=''  value='Procesar'>");
		out.println ("	</form>");		
		out.println ("<BR>");
		out.println ("</BODY>");
		out.println ("</HTML>");
		destroy () ;

		/*








		 */
	}



	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		System.out.println("do Post --> linkPost.size()=" + linkPost.size());
	}


	@Override 
	public  void  destroy () {
		/*
		System.out.println( " dentro del método de destrucción " );
		//links = null;
		linkPost = null;
		linkTitlePost = null;
		linksImg = null;
		linksImgThumb = null;

		linkPost2 = null;
		linkTitlePost2 = null;
		linksImg2 = null;
		linksImgThumb2 = null;
		pagesVisited = null;
		pagesToVisit = null;
		 */
	}



	public boolean isValidLink(String link) {
		boolean validLink = false;
		try {
			Response response = Jsoup.connect(link)
					.userAgent("Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2")
					.timeout(0).ignoreHttpErrors(true)
					.execute();

			if(response.statusCode() == 200) {
				validLink = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return validLink ;
	}

	boolean existUrlPagesPingedInDB(String url) {
		boolean existe=false;
		//for( Iterator it2 = pagesVisited.iterator(); it2.hasNext(); ) { 
		for( Iterator it2 = pagesPingedInDB.iterator(); it2.hasNext(); ) { 
			String x = it2.next().toString();
			if(Objects.equals(x, url)) {
				existe= true;
				break;
			}
		}
		//if(existe) {
		System.out.println(existe + " ->" + url);
		//}else {

		//}
		return existe; 
	}




	public Set<String> getPagesPingedInDB() {
		return pagesPingedInDB;
	}

	public List<String> getPagesToVisit() {
		return pagesToVisit;
	}

}
