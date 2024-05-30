package org.example.hakmana;

import java.sql.*;

public class GetNoteController {
    private String id;
    private String title;
    private Date date;

    public GetNoteController(String id, String title, Date date) {
        this.id = id;
        this.title = title;
        this.date = date;

    }
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }


}


