package top.yang.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Bucket implements Serializable {

    /**
     * The name of this COS bucket
     */
    private String name = null;

    /**
     * The details on the owner of this bucket
     */
    private Owner owner = null;

    /**
     * The date this bucket was created
     */
    private LocalDateTime creationDate = null;

    /**
     * The location of the bucket
     */
    private String location = null;

    /**
     * The type of the bucket
     */
    private String bucketType = null;

    /**
     * The type set by internal
     */
    private String type = null;

    /**
     * Constructs a bucket without any name specified.
     *
     * @see Bucket#Bucket(String)
     */
    public Bucket() {
    }

    /**
     * Creates a bucket with a name. All buckets in Qcloud COS share a single namespace; ensure the bucket is given a unique name.
     *
     * @param name The name for the bucket.
     * @see Bucket#Bucket()
     */
    public Bucket(String name) {
        this.name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Bucket [name=" + getName() + ", creationDate=" + getCreationDate()
                + ", location=" + getLocation() + ", owner=" + getOwner() + "]";
    }

    /**
     * Gets the bucket's owner. Returns <code>null</code> if the bucket's owner is unknown.
     *
     * @return The bucket's owner, or <code>null</code> if it is unknown.
     * @see Bucket#setOwner(Owner)
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * For internal use only. Sets the bucket's owner in Qcloud COS. This should only be used internally by the COS Java client methods that retrieve information directly from
     * Qcloud COS.
     *
     * @param owner The bucket's owner.
     * @see Bucket#getOwner()
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * Gets the bucket's creation date. Returns <code>null</code> if the creation date is not known.
     *
     * @return The bucket's creation date, or <code>null</code> if not known.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * For internal use only. Sets the bucket's creation date in COS. This should only be used internally by COS Java client methods that retrieve information directly from Qcloud
     * COS.
     *
     * @param creationDate The bucket's creation date.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the name of the bucket.
     *
     * @return The name of this bucket.
     * @see Bucket#setName(String)
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the bucket. All buckets in Qcloud COS share a single namespace; ensure the bucket is given a unique name.
     *
     * @param name The name for the bucket.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBucketType(String bucketType) {
        this.bucketType = bucketType;
    }

    public String getBucketType() {
        return this.bucketType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
