package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@XStreamAlias("DBFile")
public class DBFile {

    // Here is something more complicated. If we have list of elements that are
    // not wrapped in a element representing a list (like we have in our XML:
    // multiple <ban> elements not wrapped inside <bans> collection,
    // we have to declare that we want to treat these elements as an implicit list
    // so they can be converted to List of objects.
    @XStreamImplicit(itemFieldName = "SystemAdministratorUserProfile")
    private List sysAdminUserProfiles = new ArrayList();
}

