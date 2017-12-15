package com.josfeenstra.grooverjazztheapp;

import java.util.ArrayList;

/**
 * Created by Jos on 12/14/2017.
 */



    /*
         JSON data possible to access:

                        "id": "125735",
                    "title": "Nascente",
                    "artist": {
                        "name": "Hermeto Pascoal",
                        "mbid": "c7a4c4a0-9544-44e3-87f2-5779d18b753f",
                        "imgurl": "http://userserve-ak.last.fm/serve/126/24551353.jpg"
                    },
                    "releasedate": "1988",
                    "genre": "jazz",
                    "arousal": "486304",
                    "valence": "364305",
                    "popularity": "35",
                    "originalid": [],
                    "favorite": "0"
     */

public class Track {

    public String id;
    public String title;
    public String artist;
    public String genre;

    // default constructor for firebase
    public Track() {}

    // init object
    public Track(String id, String title, String artist, String genre) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }
}
