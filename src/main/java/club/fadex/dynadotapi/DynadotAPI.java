package club.fadex.dynadotapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DynadotAPI {

    String key;

    public DynadotAPI(String key) {
        this.key = key;
    }

    public void setDNS(String domain, String mainHost, List<SubdomainRecord> subdomains) {
        StringBuilder requestString = new StringBuilder("https://api.dynadot.com/api3.xml?" +
                "key=" + key +
                "&command=set_dns2" +
                "&domain=" + domain +
                // main record
                "&main_record_type0=a" +
                "&main_record0=" + mainHost);

        int i = 0;
        for(SubdomainRecord subdomain: subdomains) {
            requestString.append("&subdomain" + i + "=").append(subdomain.getSubdomain()).
                    append("&sub_record_type" + i + "=").append(subdomain.getType().toLowerCase(Locale.ENGLISH))
                    .append("&sub_record" + i + "=").append(subdomain.getValue());

            if(subdomain.getValue2() != null) {
                requestString.append("&sub_recordx" + i + "=").append(subdomain.getValue2());
            }
            i++;
        }

        String request2 = getRequest(requestString.toString());
        System.out.println(request2);
    }

    public List<SubdomainRecord> getSubdomains(String domain) {
        String request = getRequest("https://api.dynadot.com/api3.xml?key=" + key + "&command=get_dns&domain=" + domain);

        JSONObject xmlJSONObj = XML.toJSONObject(request);
        String jsonPrettyPrintString = xmlJSONObj.toString(4);

        List<SubdomainRecord> subdomains = new ArrayList<>();
        JsonElement element = new JsonParser().parse(jsonPrettyPrintString);
        for(JsonElement record: element.getAsJsonObject().get("GetDnsResponse")
                .getAsJsonObject().get("GetDnsContent")
                .getAsJsonObject().get("NameServerSettings")
                .getAsJsonObject().get("SubDomains").getAsJsonObject()
                .get("SubDomainRecord").getAsJsonArray()) {

            subdomains.add(new SubdomainRecord(
                    record.getAsJsonObject().get("RecordType").getAsString(),
                    record.getAsJsonObject().get("Value").getAsString(),
                    record.getAsJsonObject().get("Subhost").getAsString(),
                    record.getAsJsonObject().get("Value2") != null ? record.getAsJsonObject().get("Value2").getAsString() : null
            ));
        }

        return subdomains;
    }

    @Getter @Setter @AllArgsConstructor
    public static class SubdomainRecord {
        String type;
        String value;
        String subdomain;
        String value2;
    }

    public String getRequest(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return response.toString();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}