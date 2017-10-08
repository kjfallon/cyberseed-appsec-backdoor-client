package edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@XStreamAlias("Notes")
public class Notes {

    @XStreamImplicit(itemFieldName = "Note")
    private List noteList = new ArrayList();

}
