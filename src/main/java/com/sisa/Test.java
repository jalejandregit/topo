package com.sisa;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/*
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
 */
/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Test() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		String linkCoordenades = request.getParameter("coordenades_x");
		String[] parts = linkCoordenades.split(",");
		String lat = parts[0]; // 123
		String lon = parts[1]; // 654321
		System.out.println("Coordenades:" + lat + " -- " + lon);
		String location="";
		try {
			location = OSMGeocoding.getAddess(Double.valueOf(lat), Double.valueOf(lon));
			JSONObject jsonObject = new JSONObject(location);
			 String name = jsonObject.optString("display_name");
			System.err.println("display_name=" + name); 
			 
			/*
			System.out.println("state=" + state);
			System.out.println( obj.getJSONObject("address").getString("road"));
			System.out.println( obj.getJSONObject("address").getString("village"));
			System.out.println( obj.getJSONObject("address").getString("county"));
			System.out.println( obj.getJSONObject("address").getString("state_district"));

			System.out.println( obj.getJSONObject("address").getString("state"));

			System.out.println( obj.getJSONObject("address").getString("postcode"));
			System.out.println( obj.getJSONObject("address").getString("country"));
			System.out.println( obj.getJSONObject("address").getString("country_code"));
			*/
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


		System.out.println("Location=" + location);
		String orientacio = request.getParameter("orientacion");
		System.out.println("Orientacion=" + orientacio);


	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
