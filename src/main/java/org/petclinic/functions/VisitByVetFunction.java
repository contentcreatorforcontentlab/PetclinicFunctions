package org.petclinic.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.petclinic.reports.Reports;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class VisitByVetFunction
{
    private static final Logger log;
    private static Properties properties = new Properties();

    static
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(VisitByVetFunction.class.getName());
        try
        {
            properties.load(VisitByVetFunction.class.getClassLoader().getResourceAsStream("application.properties"));
        }
        catch(Exception e)
        {
            log.severe("Could not load the application properties.");
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * This function listens at endpoint "/vets/{id}/visits?startDate=2022-01-01&endDate=2022-02-01".
     */
    @FunctionName("VisitsByVet")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "vets/{id}/visits") // name is optional and defaults to EMPTY
                HttpRequestMessage<Optional<String>> request,
                @BindingName("id") String vetId,
                final ExecutionContext context)
    {
        log.info("Java HTTP trigger processed a request.");

        // Get the vet ID
        if (vetId.isEmpty())
        {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("'id' for Wet is missing.")
                            .build();
        }

        int id = 0;
        try
        {
            id = Integer.parseInt(vetId);
            log.info("vet id = " + id);
        }
        catch(Exception e)
        {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("Could not parse vet id.")
                            .build();
        }

        // Get the query string parameters
        Map<String, String> parameters = request.getQueryParameters();
        String startDate = parameters.getOrDefault("startDate", "1999-01-01");
        String endDate = parameters.getOrDefault("endDate", "2999-12-31");

        // Parse the dates
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date reportStart = null;
        Date reportEnd = null;

        try
        {
            log.info("qstring: startDate = " + startDate);
            log.info("qstring: endDate = " + endDate);

            reportStart = formatter.parse(startDate);
            reportEnd = formatter.parse(endDate);
         }
        catch(Exception e)
        {
            log.warning("Could not parse the report dates. Ignoring all dates.");
            log.warning(e.getMessage());
            reportStart = null;
            reportEnd = null;
        }
        log.info("reportStart = " + reportStart);
        log.info("reportEnd = " + reportEnd);

        // Run the report
        Connection connection = null;

        String jsonResults = "";
        try
        {
            log.info("Connecting to the database");
            connection = DriverManager.getConnection(properties.getProperty("url"), properties);

            // Get the visits for this vet
            jsonResults = Reports.getVisitsByVetReport(connection, id, reportStart, reportEnd);

            log.info("Closing database connection");
            connection.close();
         }
        catch(Exception e)
        {
            log.severe("Exception while running report");
            log.severe(e.getMessage());
        }

        return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResults)
                        .build();
    }
}
