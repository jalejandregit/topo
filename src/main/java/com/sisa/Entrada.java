package com.sisa;

public class Entrada {
	private String link;
    private String title;
    private String content;
    private String img;
    private String thumb;

    // Constructor
    Entrada(String link, String title ,String content, String img, String thumb) {
        this.link=link;
        this.title = title;
        this.content = content;
        this.img = img;
        this.thumb = thumb;
    }

    // Print entrada data
    void printEntrada() {
        System.out.println("Name: " + link);
    }
}