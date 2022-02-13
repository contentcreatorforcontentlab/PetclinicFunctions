package org.petclinic.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.petclinic.data.Visit;import org.petclinic.functions.VisitByVetFunction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;import java.util.Date;import java.util.Properties;import java.util.logging.Logger;

public class Reports
{
    private static final Logger log;

    static
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(VisitByVetFunction.class.getName());

    }

    public static String getVisitsByVetReport(Connection connection, int vet_id,
                                                Date reportStart, Date reportEnd)
    {
        String query = "SELECT"
                       + " vets.first_name vet_first_name, vets.last_name  vet_last_name,"
                       + " pets.name pet_name,"
                       + " visits.visit_date, visits.description,"
                       + " owners.first_name owner_First_name, owners.last_name owner_last_name"
                       + " FROM pets"
                       + " JOIN owners"
                       + " ON pets.owner_id = owners.id"
                       + " left JOIN visits"
                       + " ON visits.pet_id = pets.id"
                       + " JOIN vets"
                       + " ON visits.vet_id = vets.id"
                       + " WHERE visits.vet_id = " + vet_id;

        if(reportEnd != null && reportEnd != null)
        {
            query += " AND visit_date <= '" + reportEnd + "'"
                    + " AND visit_date >= '" + reportStart + "'";
        }

        String jsonResults = "";
        try
        {
            PreparedStatement readStatement = connection.prepareStatement(query);
            ResultSet resultSet = readStatement.executeQuery();

            ArrayList<Visit> visitList = new ArrayList<Visit>();
            while(resultSet.next())
            {
                Visit visit = new Visit();

                visit.setVetFirstName(resultSet.getString("vet_first_name"));
                visit.setVetLastName(resultSet.getString("vet_last_name"));
                visit.setPetName(resultSet.getString("pet_name"));
                visit.setVisitDate(resultSet.getDate("visit_date"));
                visit.setDescription(resultSet.getString("description"));
                visit.setOwnerFirstName(resultSet.getString("owner_First_name"));
                visit.setOwnerLastName(resultSet.getString("owner_last_name"));

                visitList.add(visit);
            }

            if (visitList.size() == 0) {
                log.info("No visits were found.");
                return jsonResults;
            }

            log.info("Vet: " + visitList.get(0).getVetFirstName()
                                    + " " + visitList.get(0).getVetLastName()
                                    + " has " + visitList.size() + " visits.");
            log.info("First pet is: " + visitList.get(0).getPetName());

            // return JSON from to the client
            // Generate document
            ObjectMapper mapper = new ObjectMapper();
            jsonResults = mapper.writeValueAsString(visitList);
        }
        catch(Exception e)
        {
            System.out.println("Exception ----------------------");
            e.printStackTrace();
        }

        return jsonResults;
    }
}
