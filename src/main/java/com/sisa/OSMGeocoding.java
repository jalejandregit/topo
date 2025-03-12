package com.sisa;
import com.atlis.location.model.impl.Address;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import com.atlis.location.model.impl.MapPoint;
import com.atlis.location.nominatim.NominatimAPI;



public class OSMGeocoding {
	 static String endpointUrl = "http://nominatim.openstreetmap.org/";
	 BasicConfigurator config;
	 
	public static String getAddess(Double latitude,Double longitude) throws IOException, InterruptedException {
		//Double latitude = 40.7470;
		//Double longitude = -73.9860;
		//MapPoint mapPoint = new MapPoint().buildMapPoint(latitude, longitude);
		//Address address = NominatimAPI.with(endpointUrl).getAddressFromMapPoint(mapPoint);
		//System.out.println("Adress:" + address.getProvince()  + "-" +  address.getCountry() + "-" + address.getDisplayName());
		//return  address.getDisplayName();
		HttpRequest request = HttpRequest.newBuilder()
				//.uri(URI.create("https://forward-reverse-geocoding.p.rapidapi.com/v1/reverse?lat=41.8755616&lon=-87.6244212&accept-language=en&polygon_threshold=0.0"))
				.uri(URI.create("https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude + "&zoom=27&addressdetails=1"))
				.header("X-RapidAPI-Key", "SIGN-UP-FOR-KEY")
				.header("X-RapidAPI-Host", "forward-reverse-geocoding.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body() ;
	
	}
}
