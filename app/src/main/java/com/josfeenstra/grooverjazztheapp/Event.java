package com.josfeenstra.grooverjazztheapp;

import java.util.ArrayList;

/**
 * Created by Jos on 12/14/2017.
 */

public class Event {


    public String title;
    public String description;
    public String venue;
    public String link;
    public ArrayList<String> interested;

    // init object
    public Event(String title, String description, String venue, String link) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.link = link;
    }

    public void addInterest(String interestedId) {
        this.interested.add(interestedId);
    }

    public void removeInterest(String interestedId) {
        this.interested.remove(interestedId);
    }

}