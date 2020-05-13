package edu.lawrence.daycare.rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import edu.lawrence.daycare.data.*;
import java.sql.Date;

@RestController
@CrossOrigin(origins="*")
public class ProviderController {
private ProviderDAO providerDAO;

    public ProviderController(ProviderDAO dao) {
        this.providerDAO = dao;
    }

    @GetMapping("/provider")
    public Provider providerById(@RequestParam("id") int id) {
        return providerDAO.findById(id);
    }
    
    @GetMapping("/providers/by_location")
    public List<Provider> providersByLocation(@RequestParam String address,@RequestParam String city) {
        address = address.replace(" ", "%20");
        city = city.replace(" ","%20");

        String uri = "https://geoservices.tamu.edu/Services/Geocode/WebService/GeocoderWebServiceHttpNonParsed_V04_01.aspx?"
                + "apiKey=<key>&version=4.01&streetAddress="
                + address + "&city=" + city + "&state=WI";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        List<Provider> result = new ArrayList<Provider>();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            String body = response.body();
            String parts[] = body.split(",");
            double lat = Double.parseDouble(parts[3]);
            double lgt = Double.parseDouble(parts[4]);
            result = providerDAO.findByLocation(lat,lgt);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @GetMapping("/providers/by_child")
    public List<Provider> getProviders(@RequestParam(value="age") int age, @RequestParam(value="start") Date start, @RequestParam(value="end") Date end) {
        return providerDAO.getProvidersForAgeAndStartDate(age, start, end);
    }
}
