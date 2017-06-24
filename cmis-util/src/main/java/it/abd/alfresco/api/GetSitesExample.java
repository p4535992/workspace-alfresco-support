package it.abd.alfresco.api;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;

import it.abd.alfresco.api.model.SiteEntry;
import it.abd.alfresco.api.model.SiteList;

/**
 * Simple example that shows how to hit the Alfresco Public API with
 * the REST API to find the user's home network and to list up to
 * 10 sites visible to the user.
 *
 * @author jpotts
 */
public class GetSitesExample extends BaseOnPremExample {

    public static void main(String[] args) {
        GetSitesExample gse = new GetSitesExample();
        gse.doExample();
    }

    public void doExample() {
        try {
            // Find the user's home network
            String homeNetwork = getHomeNetwork();

            // List some of the sites the user can see
            GenericUrl sitesUrl = new GenericUrl(getAlfrescoAPIUrl() +
                                                 homeNetwork +
                                                 SITES_URL + "?maxItems=10");
            HttpRequest request = getRequestFactory().buildGetRequest(sitesUrl);
            SiteList siteList = request.execute().parseAs(SiteList.class);
            System.out.println("Up to 10 sites you can see are:");
            for (SiteEntry siteEntry : siteList.list.entries) {
                    System.out.println(siteEntry.entry.id);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("Done!");
    }
}
