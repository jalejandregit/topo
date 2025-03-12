package com.sisa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class UrlsVistas {
	static String file;
	static String nameFile;
	static File filew;
	static Set<String> pagesVisited = new HashSet<String>();

	public UrlsVistas(String file) {
		this.file=file;
		this.nameFile="C:\\eclipse_workspace\\jee-2022-06\\topo\\logs\\" + file + ".log";
		this.filew = new File(nameFile);
	}

	public static void graba(Set<String> setVisited) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		Iterator<String> urlsIterator = setVisited.iterator();
		try {
			// String data = "Hola stackoverflow.com...";

			// Si el archivo no existe, se crea!
			if (!filew.exists()) {
				filew.createNewFile();
			}
			// flag true, indica adjuntar información al archivo.
			fw = new FileWriter(filew.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			while(urlsIterator.hasNext()) {
				String href = urlsIterator.next();
				//System.out.println("href=" + href + " -> https://" + file + "/");
				if(!Objects.equals(href, "https://" + file + "/") ){
					bw.write(href);
					bw.newLine();
				}

			}

			//bw.write(data);
			System.out.println("información agregada!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//Cierra instancias de FileWriter y BufferedWriter
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	public static void graba2(Set<String> setVisited){
        FileWriter fichero = null;
        PrintWriter pw = null;
        Iterator<String> urlsIterator = setVisited.iterator();
        try {
        	System.out.println("file=" + file);
            fichero = new FileWriter("C:\\eclipse_workspace\\jee-2022-06\\topo\\logs\\" + file + ".log");
            pw = new PrintWriter(fichero);

            //for (int i = 0; i < 10; i++)
                //pw.println("Linea " + i);
            while(urlsIterator.hasNext()) {
            	String href = urlsIterator.next();
            	 if(!Objects.equals(href, "https://" + file + "/") ){
            		 pw.println(href);
            	 }

            	}

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
    }
	 */


	public static Set<String> getPagesVisited() {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			archivo = new File (nameFile);
			fr = new FileReader (archivo);
			br = new BufferedReader(fr);

			// Lectura del fichero
			String linea;
			while((linea=br.readLine())!=null) {
				//System.out.println(linea);
				pagesVisited.add(linea);
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta 
			// una excepcion.
			try{                    
				if( null != fr ){   
					fr.close();     
				}                  
			}catch (Exception e2){ 
				e2.printStackTrace();
			}
		}
		return pagesVisited;
	}

}
