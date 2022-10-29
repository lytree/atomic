package top.lytree.oss.model;

import java.util.Date;

public class Bucket {

    /** The name of this COS bucket */
    private String name = null;

    /** The details on the owner of this bucket */
    private Owner owner = null;

    /** The date this bucket was created */
    private Date creationDate = null;

    /** The location of the bucket */
    private String location = null;

//    /** The type of the bucket */
//    private String bucketType = null;

//    /** The type set by internal */
//    private String type = null;

    private String extranetEndpoint = null;
//
//    private String intranetEndpoint = null;

    private String region = null;

//    private String hnsStatus = null;
//
//    private String resourceGroupId = null;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    //
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getExtranetEndpoint() {
        return extranetEndpoint;
    }

    public void setExtranetEndpoint(String extranetEndpoint) {
        this.extranetEndpoint = extranetEndpoint;
    }
}
