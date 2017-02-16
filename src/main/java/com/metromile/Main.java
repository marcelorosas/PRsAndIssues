package com.metromile;

import com.jayway.jsonpath.JsonPath;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {

            Client client = Client.create();

            WebResource webResource = client
                    .resource("yourUrl");

            ClientResponse response = webResource.accept("application/json")
                    .header("Authorization","I won't tell you")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            String output = response.getEntity(String.class);
            List<String> fromRefs = JsonPath.read(output, "$.values[*].fromRef.id");

            System.out.println("Number of PRs: " + fromRefs.size());

            for (String id: fromRefs) {
                System.out.println("fromRef: " + id);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
