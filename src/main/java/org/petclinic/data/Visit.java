package org.petclinic.data;

import org.petclinic.functions.VisitByVetFunction;import java.sql.*;import java.util.ArrayList;import java.util.logging.Logger;

public class Visit
{
    private static final Logger log;

    static
     {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(VisitByVetFunction.class.getName());
    }

    private String vetFirstName;
    private String vetLastName;
    private String petName;
    private Date visit_date;
    private String description;
    private String ownerFirstName;
    private String ownerLastName;

    public Visit()
    {
    }

    public String getVetFirstName() { return vetFirstName; }
    public void setVetFirstName(String vetFirstName) { this.vetFirstName = vetFirstName; }

    public String getVetLastName() { return vetLastName; }
    public void setVetLastName(String vetLastName) { this.vetLastName = vetLastName; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public Date getVisitDate() { return visit_date; }
    public void setVisitDate(Date visit_date) { this.visit_date = visit_date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOwnerFirstName() { return ownerFirstName; }
    public void setOwnerFirstName(String OwnerFirstName) { this.ownerFirstName = OwnerFirstName; }

    public String getOwnerLastName() { return ownerLastName; }
    public void setOwnerLastName(String OwnerLastName) { this.ownerLastName = OwnerLastName; }
}
