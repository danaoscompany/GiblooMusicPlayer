package com.dn.gibloo;

import java.io.Serializable;

public class Genre implements Serializable {
    public String name = "";
    public int songCount = 0;
    public long id = 0L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
