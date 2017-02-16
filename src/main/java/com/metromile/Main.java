package com.metromile;

import com.jayway.jsonpath.JsonPath;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.cli.*;

import java.util.List;

public class Main {

    private static String path = "";

    public static void main(String[] args) {
        try {

            path = "https://stash.com/rest/api/latest/projects/MET/repos/";
            Options options = new Options();

            Option stateOption = Option.builder("state")
                    .required(false)
                    .desc("state of the PR (ALL, MERGED, OPEN")
                    .hasArg()
                    .build();

            Option startOption = Option.builder("start")
                    .required(false)
                    .desc("start for the batch to retrieve, 0 to n")
                    .hasArg()
                    .build();

            Option userOption = Option.builder("user")
                    .required(false)
                    .desc("user name, i.e marcelo.rosas")
                    .hasArg()
                    .build();

            Option repoOption = Option.builder("repo")
                    .required(true)
                    .desc("repository, i.e salesforce, sugar, mms")
                    .hasArg()
                    .build();

            options.addOption(stateOption);
            options.addOption(startOption);
            options.addOption(userOption);
            options.addOption(repoOption);

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            path += cmd.getOptionValue("repo");
            path += "/pull-requests?order=newest&limit=100";

            if (cmd.hasOption("state")) {
                path += "&state=" + cmd.getOptionValue("state");
            }

            if (cmd.hasOption("start")) {
                path += "&start=" + cmd.getOptionValue("start");
            }

            if (cmd.hasOption("user")) {
                path += "&role.1=AUTHOR&username.1=" + cmd.getOptionValue("user");
            }

            Client client = Client.create();

            WebResource webResource = client
                    .resource(path);

            ClientResponse response = webResource.accept("application/json")
                    .header("Authorization", "")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            String output = response.getEntity(String.class);
            List<String> fromRefs = JsonPath.read(output, "$.values[*].fromRef.id");

            System.out.println("");
            System.out.println("*************************************************");
            System.out.println("****** WELCOME TO PRs AND ISSUES ****************");
            System.out.println("Hello Lucia, this is the endpoint you're hitting:");
            System.out.println(path);
            System.out.println("Here's the total number of PRs: " + fromRefs.size());
            System.out.println("*************************************************");

            System.out.println("");
            System.out.println("And here they are:");

            for (String id : fromRefs) {
                System.out.println("fromRef: " + id);
            }

        } catch (MissingOptionException moe) {
            System.out.println("Dear friend, remember that the -repo parameter is required :)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
