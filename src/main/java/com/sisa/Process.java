package com.sisa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class Process
 */
@WebServlet("/Process")
public class Process extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String domain = null;  
	private static String domainName = null;
	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
	private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

	private TreeMap<Integer,String> linkPost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkTitlePost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkContent =  (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkLat = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkLong = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	
	private TreeMap<Integer,String> linkAutor = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkAutorUrl = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkOrientacio = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkLocation = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private SqlRemote sql;
	//private TreeMap<Integer,String>[] listaPosts;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Process() {
		super();
		sql = new SqlRemote();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		domain = request.getParameter("linksDomain");
		domainName = domain.split("\\.")[0];
		int linkPost_size=0;
		linkPost_size = Integer.parseInt(request.getParameter("linkPost_size") );
		String test = "https://urtohijodelia.blogspot.com/2010/01/hurto-en-calamontse.html";

		for(int i=1; i<linkPost_size+1; i++){
			// System.out.println("Process_linkPost_" + i + ": -> "+ request.getParameter("Process_linkPost_" + i) );
			if(  Objects.equals(request.getParameter("Process_linkPost_" + i)  , "true")) {
				String linkTitle = request.getParameter("linkTitle_" + i);
				String linkSlug = convertToSlug(linkTitle);
				String linkHref = request.getParameter("linkHref_" + i);
				String linkContent = request.getParameter("linkContent_" + i);
				String linkImg = request.getParameter("linkImg_" + i);
				String linkThumb = request.getParameter("linkThumb_" + i);
				String linkProfileName = request.getParameter("linkProfileName_" + i);
				String linkProfileUrl = request.getParameter("linkProfileUrl_" + i);
					
				
				System.out.println( "Process_linkPost_" + i + ": -> "+ request.getParameter("Process_linkPost_" + i) );
				String linkCoordenades = "0,0";
				String location="";
				String location_names = null;
				if (request.getParameter("coordenades_"+ i)!="" ) {
					linkCoordenades = request.getParameter("coordenades_"+ i);
				}
				String[] parts = linkCoordenades.split(",");
				String lat = parts[0]; // 123
				String lon = parts[1]; // 654321
				//String autor = request.getParameter("linksDomain");
				String autor = linkProfileName;
				String orientacio = "S";
				if (request.getParameter("orientacion_"+ i)!="") {
					orientacio= request.getParameter("orientacion_"+ i);
				};
				
				if (request.getParameter("coordenades_"+ i)!="" ) {
				try {
					location = OSMGeocoding.getAddess(Double.valueOf(lat), Double.valueOf(lon));
					JSONObject jsonObject = new JSONObject(location);
					location_names = jsonObject.optString("display_name");
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				
				
				System.out.println( "-----> " + linkTitle );
				System.out.println( "-----> " + linkHref );
				System.out.println( "-----> " + linkContent );
				System.out.println( "-----> " + linkImg );
				System.out.println( "-----> " + linkThumb );
				System.out.println( "-----> "  + linkProfileName);
				System.out.println( "-----> "  + linkProfileUrl);
				System.out.println("Latitud=" + lat.trim());
				System.out.println("Longitud=" + lon.trim());
				this.linkPost.put(i, linkHref);
				this.linkTitlePost.put(i,linkTitle);
				this.linkContent.put(i, linkContent);
				this.linksImg.put(i,linkImg);
				this.linksImgThumb.put(i,linkThumb);
				this.linkLat.put(i, lat.trim());
				this.linkLong.put(i, lon.trim());
				this.linkAutor.put(i, linkProfileName);
				this.linkAutorUrl.put(i, linkProfileUrl);
				this.linkOrientacio.put(i, orientacio);
				this.linkLocation.put(i, location_names);
				
				
				//String query = "SELECT * FROM vertikal_wp2.wp_posts WHERE post_content_filtered ='" + domain + "' AND guid='" + linkHref + "' AND post_name='" + linkSlug + "'";
				//sql.select(query);
			}
		}
		if(linkPost_size>0) {
			//TreeMap<Integer,String>[] myStringArray = new TreeMap<Integer,String>[3];

			List<TreeMap<Integer,String>> listaPosts = new ArrayList<TreeMap<Integer,String>>();
			listaPosts.add(linkPost);
			listaPosts.add(linkTitlePost);
			listaPosts.add(linkContent);
			listaPosts.add(linksImg);
			listaPosts.add(linksImgThumb);
			listaPosts.add(linkLat);
			listaPosts.add(linkLong);			
			listaPosts.add(linkAutor);
			listaPosts.add(linkAutorUrl);
			listaPosts.add(linkOrientacio);
			listaPosts.add(linkLocation);
			
			/*
			Set<Map.Entry<Integer, String> > entryPost 			= listaPosts.get(0).entrySet();
			Set<Map.Entry<Integer, String> > entryTitlePost 	= listaPosts.get(1).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkContent	= listaPosts.get(2).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkImg 		= listaPosts.get(3).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkImgThumb = listaPosts.get(4).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkLatitud  = listaPosts.get(5).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkLongitud = listaPosts.get(6).entrySet();
			
			Set<Map.Entry<Integer, String> > entrylinkAutor		 = listaPosts.get(7).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkOrientacio = listaPosts.get(8).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkLocation	 = listaPosts.get(9).entrySet();
			 */
			
			//sql.select(query);
			sql.into(domain, listaPosts);
			
			/*
			int sizeLinkPost = listaPosts.get(0).size();
			Set<Map.Entry<Integer, String> > entryPost 			= listaPosts.get(0).entrySet();
			Set<Map.Entry<Integer, String> > entryTitlePost 	= listaPosts.get(1).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkContent	= listaPosts.get(2).entrySet();
			Set<Map.Entry<Integer, String> > entrylinksImg 		= listaPosts.get(3).entrySet();
			Set<Map.Entry<Integer, String> > entrylinksImgThumb = listaPosts.get(4).entrySet();
			// Convert entrySet to Array using toArray method
			Map.Entry<Integer, String>[] arrayPost 			= entryPost.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arrayTitlePost 	= entryTitlePost.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkContent 	= entrylinkContent.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinksImg 		= entrylinksImg.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinksImgThumb	= entrylinksImgThumb.toArray( new Map.Entry[entryPost.size()]);


			for (int i = 0; i < sizeLinkPost; i++)    {
				// Get Key using index and print
				System.out.println("\nKey at " + i + ": key -> "  	+ arrayPost[i].getKey() + " value ->" + arrayPost[i].getValue());
				System.out.println("Key at " + i + ": key -> "  	+ arrayTitlePost[i].getKey() + " value ->" + arrayTitlePost[i].getValue());
				System.out.println("Key at " + i + ": key -> "  	+ arraylinkContent[i].getKey() + " value ->" + arraylinkContent[i].getValue());
				System.out.println("nKey at " + i + ": key -> "  	+ arraylinksImg[i].getKey() + " value ->" + arraylinksImg[i].getValue());
				System.out.println("nKey at " + i + ": key -> "  	+ arraylinksImgThumb[i].getKey() + " value ->" + arraylinksImgThumb[i].getValue());
			}
			 */


			//for (int i = 0; i < listaPosts.size(); i++) {
				//System.out.println(listaPosts.size() + " -> " + listaPosts.get(i));
				//for (int post = 0; post < listaPosts[i].size(); post++) {

				//}
				//System.out.println("listaPosts.get(" + i + ").size()= " + listaPosts.get(i).size());
				
				/*
				for (int p = 0; p < listaPosts.get(i).size(); p++) {

					Set setLinkx = listaPosts.get(p).entrySet();
					Iterator ilinkx = setLinkx.iterator();
					while(ilinkx.hasNext()) {
						Map.Entry meLink =(Map.Entry) ilinkx.next();
						//System.out.println(meLink.getKey() + "----> " + meLink.getValue().toString());
					}
				}
*/
				/*
				Set setLinkx = listaPosts.get(i).entrySet();
				Iterator ilinkx = setLinkx.iterator();
				while(ilinkx.hasNext()) {
					Map.Entry meLink =(Map.Entry) ilinkx.next();
					System.out.println(meLink.getKey() + "----> " + meLink.getValue().toString());
				}
				 */
			//}
		}



		/*
         	out.println ("<input name=\"linkTitle_"  + sel + "\" type=\"hidden\" value=" + meTitlef.getValue() + "\">");
			out.println ("<input name=\"linkHref_"  + sel + "\" type=\"hidden\" value=" + meLinkf.getValue() + "\">");
			out.println ("<input name=\"linkContent_"  + sel + "\" type=\"hidden\" value=" + meContentf.getValue() + "\">");
			out.println ("<input name=\"linkImg_"  + sel + "\" type=\"hidden\" value=" + meImgf.getValue() + "\">");
			out.println ("<input name=\"linkThumb_"  + sel + "\" type=\"hidden\" value=" + meThumbf.getValue() + "\">");
		 */
		/*
		 Scanner s = null;
	        try {
	            s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        if (s.hasNext()) {
	        	response.getWriter().append(s.next() ) ;
	        }else {
	        	response.getWriter().append("Served at: ").append(request.getContextPath());
	        	//response.getWriter().append(request.getParameter("Orientacion_1"));
	        	response.getWriter().append(request.getParameter("linkPost_size") );
	        }
		 */
	}


	public void export_wp_post_Csv() {
		// Our example data






		/* wp_postmeta_domain
meta_id;post_id;meta_key;meta_value
;273643;link_post;http://parquesdeaventura.blogspot.com/2016/12/nuevo-parque-en-dubai.html
;273643;_link_post;field_630485e99c971
;273643;urlImagen;https://3.bp.blogspot.com/-Cyl3RSO1-Yo/WGUNB5zpWpI/AAAAAAAADWQ/s4ujkCOSVW84QbdLBEqsTKRPHX5s1iFVACLcB/s1600/Team%2BVertikalist.jpg
;273643;_urlImagen;field_630367a79eff5
;273643;_thumbnail_id;https://3.bp.blogspot.com/-Cyl3RSO1-Yo/WGUNB5zpWpI/AAAAAAAADWQ/s4ujkCOSVW84QbdLBEqsTKRPHX5s1iFVACLcB/s300/Team%2BVertikalist.jpg
;273643;__thumbnail_id;field_630367ca9eff6
		 */
		String fileName = "wp_posts_" +  domainName + ".csv";

		File file = new File(fileName);
		FileWriter writer = null;
		//writer = new FileWriter(file);
		// writer.write("test");

		FileWriter csvWriter = null;
		//csvWriter = new FileWriter(fileName);

		try {
			csvWriter = new FileWriter(file);
			csvWriter.append("ID");
			csvWriter.append(";");
			csvWriter.append("post_author");
			csvWriter.append(";");
			csvWriter.append("post_date");
			csvWriter.append(";");
			csvWriter.append("post_date_gmt");
			csvWriter.append(";");
			csvWriter.append("post_content"); //--> post_content
			csvWriter.append(";");
			csvWriter.append("post_title"); //--> post_title
			csvWriter.append(";");
			csvWriter.append("post_excerpt");
			csvWriter.append(";");
			csvWriter.append("post_status"); //--> post_status = draft
			csvWriter.append(";");
			csvWriter.append("comment_status"); //--> comment_status = closed
			csvWriter.append(";");
			csvWriter.append("ping_status"); //--> ping_status = closed 
			csvWriter.append(";");
			csvWriter.append("post_password");
			csvWriter.append(";");
			csvWriter.append("post_name"); //--> post_name --> un-palo-dificil-al-forat-del-vent
			csvWriter.append(";");
			csvWriter.append("to_ping");
			csvWriter.append(";");
			csvWriter.append("pinged");
			csvWriter.append(";");

			/* wp_posts _domain
			 * ID;post_author;post_date;post_date_gmt;post_content;post_title;post_excerpt;post_status;comment_status;ping_status;post_password;post_name;to_ping;pinged;
			 * post_modified;post_modified_gmt;post_content_filtered;post_parent;guid;menu_order;post_type;post_mime_type;comment_count
			 * ;1;;;;VERTIKALIST CONSTRUYE EL PRIMER PARQUE DE TOTEMS DE LOS EMIRATOS �RABES;;publish;closed;closed;;pat-eau;;;;;;0;;0;post;;0
			 * 
			 */
			csvWriter.append("post_modified");
			csvWriter.append(";");
			csvWriter.append("post_modified_gmt");
			csvWriter.append(";");
			csvWriter.append("post_content_filtered");
			csvWriter.append(";");
			csvWriter.append("post_parent");
			csvWriter.append(";");
			csvWriter.append("guid");
			csvWriter.append(";");
			csvWriter.append("menu_order");
			csvWriter.append(";");
			csvWriter.append("post_type");//--> post_type = post
			csvWriter.append(";");
			csvWriter.append("post_mime_type");
			csvWriter.append(";");
			csvWriter.append("comment_count");
			csvWriter.append("\n");


			Set setLinkx = linkPost.entrySet();
			Iterator ilinkx = setLinkx.iterator();
			while(ilinkx.hasNext()) {
				Map.Entry meLink =(Map.Entry) ilinkx.next();

				String Url 		= meLink.getValue().toString(); //linkPost.get(meImgx.getKey()));
				String Title	= linkTitlePost.get(meLink.getKey());
				String Content	= linkContent.get(meLink.getKey());
				String Img		= linksImg.get(meLink.getKey());
				String Thumb	= linksImgThumb.get(meLink.getKey());
				//               ;"1";"2022-08-23 12:52:07";"2022-08-23 12:52:07";"arranca yaaaaaaaaaaa...   ya queda menos... neng, aixó està fred! empieza el desmelene... es verdad, no es plano pero es .....;"la calma";"<strong><h3><a href=""https://urtohijodelia.blogspot.com/2010/01/la-calma.html"" target=""_blank"" rel=""noopener"">la calma</a></h3></strong><br/><a href=""https://4.bp.blogspot.com/_LezKAWX0txU/S0oi9PZnYNI/AAAAAAAABV0/qtgCg5PE59g/s1600-h/0002.jpg"" target=""_blank"" rel=""noopener""><img border=""0"" height=""192"" src=""https://4.bp.blogspot.com/_LezKAWX0txU/S0oi9PZnYNI/AAAAAAAABV0/qtgCg5PE59g/s300/0002.jpg"" width=""320""></a>";"publish";"open";"open";;"la-calma";;;"2022-08-23 15:15:51";"2022-08-23 15:15:51";;"0";"https://vertikalist.com/test/?p=273624";"0";"post";;"0"
				//csvWriter.append(";" + "1" ;"2022-08-23 12:52:07";"2022-08-23 12:52:07";"arranca yaaaaaaaaaaa...   ya queda menos... neng, aixó està fred! empieza el desmelene... es verdad, no es plano pero es .....;"la calma";"<strong><h3><a href=""https://urtohijodelia.blogspot.com/2010/01/la-calma.html"" target=""_blank"" rel=""noopener"">la calma</a></h3></strong><br/><a href=""https://4.bp.blogspot.com/_LezKAWX0txU/S0oi9PZnYNI/AAAAAAAABV0/qtgCg5PE59g/s1600-h/0002.jpg"" target=""_blank"" rel=""noopener""><img border=""0"" height=""192"" src=""https://4.bp.blogspot.com/_LezKAWX0txU/S0oi9PZnYNI/AAAAAAAABV0/qtgCg5PE59g/s300/0002.jpg"" width=""320""></a>";"publish";"open";"open";;"la-calma";;;"2022-08-23 15:15:51";"2022-08-23 15:15:51";;"0";"https://vertikalist.com/test/?p=273624";"0";"post";;"0");
				List<String> rowData = Arrays.asList("", "1", "", "",  Content , Title, "excerpt","draft", "closed","closed","",convertToSlug(Title),"","","","","","","","","post","","0");
				csvWriter.append(String.join(";", rowData));
				csvWriter.append("\n");
			}

			/*
			List<List<String>> rows = Arrays.asList(
					Arrays.asList("Jean", "author", "Java"),
					Arrays.asList("David", "editor", "Python"),
					Arrays.asList("Scott", "editor", "Node.js")
					);



			for (List<String> rowData : rows) {
				csvWriter.append(String.join(",", rowData));
				csvWriter.append("\n");
			}
			 */
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public void export_wp_postmeta_Csv() {
		String fileName = "wp_postmeta_" +  domainName + ".csv";
		File file = new File(fileName);
		FileWriter csvWriter = null;

		try {
			csvWriter = new FileWriter(file);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String convertToSlug(String input)  {
		String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		slug = EDGESDHASHES.matcher(slug).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}

}
