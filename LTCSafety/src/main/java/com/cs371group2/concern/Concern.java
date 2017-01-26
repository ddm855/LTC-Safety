package com.cs371group2.concern;

import com.cs371group2.client.OwnerToken;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The concern class is used to model and store all information relating to a concern within the
 * data store. This includes the submitted concern data and information for administrators to modify
 * as they tend to concerns.
 */
@Entity
public final class Concern {

    /**
     * Logger definition for this class.
     */
    private static final Logger LOGGER = Logger.getLogger( Concern.class.getName() );

    /**
     * Used to uniquely identify concerns within the database. This value will be automatically
     * created by the data store.
     */
    @Id
    private Long id;

    /**
     * The status of the concern within the system detailing action that has been taken. The status
     * changes as administrators acknowledge, respond to, and resolve concerns.
     */
    @Index
    private ConcernStatus status = ConcernStatus.PENDING;

    /**
     * Used to identify whether a concern is being actively tracked within the system. Once a
     * concern has been resolved or retracted it can be archived. Duplicate concerns may also be
     * archived by the administrators.
     */
    @Index
    private boolean isArchived = false;

    /**
     * The exact date and time the concern was submitted.
     */
    @Index
    private final Date submissionDate = new Date();

    /**
     * The user submitted data relating to the concern such as nature, location, and reporter.
     */
    private ConcernData data;

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public ConcernStatus getStatus() {
        return status;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public ConcernData getData() {
        return data;
    }

    /**
     * Create a new concern
     *
     * @param data The data for the concern that was submitted from the Android or iOS client.
     * @precond data != null data is valid based on its validate method
     */
    public Concern(ConcernData data) {
        if(data == null){ LOGGER.log(Level.WARNING, "Concern tried to be created with no data."); }
        assert data != null;

        if(!data.validate().isValid()){ LOGGER.log(Level.WARNING, "Concern tried to be created with invalid data."); }
        assert data.validate().isValid();

        this.data = data;
        LOGGER.log(Level.FINER, "Concern created: \n" + this.toString());
    }

    /**
     * Generates an owner token containing the concern id for this concern giving the holder
     * authorization to retract, access, or update the concern.
     *
     * @return The owner token for this concern giving the holder authorized access to it.
     * @precond The id of the concern must be populated. This means that the concern must be stored
     * in the datastore using the ConcernDao prior to generating the owner token.
     */
    public OwnerToken generateOwnerToken() {
        if(id == null){ LOGGER.log(Level.WARNING, "Concern token tried to be created when concern id is null."); }
        assert id != null;
        LOGGER.log(Level.FINER, "Owner Token being created: ID# " + this.id);
        return new OwnerToken(id);
    }

    /**
     * Updates the concern entity to reflect that the concern has been retracted.
     *
     * @postcond The status has been changed to RETRACTED and isArchived is now true.
     */
    public void retract() {
        status = ConcernStatus.RETRACTED;
        isArchived = true;
        LOGGER.log(Level.FINER, "Concern Retracted: ID# " + this.id);
    }

    @Override
    public String toString(){
        return "Concern:\nID# " + this.id + this.getData().toString();
    }
}
