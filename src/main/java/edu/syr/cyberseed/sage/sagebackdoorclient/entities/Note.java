package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("Note")
public class Note {

    @XStreamAlias("Date")
    private String date;

    @XStreamAlias("Text")
    private String text;

}
