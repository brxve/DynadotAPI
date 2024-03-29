package club.fadex.dynadotapi;

import org.junit.jupiter.api.Test;

import java.util.List;

public class DynadotAPITest {

    @Test
    public void test() {
        DynadotAPI api = new DynadotAPI("apiKey");
        String domain = "example.com";
        String subdomain = "hello";
        String address = "127.0.0.1";

        // get a list of subdomain records using the domain
        List<DynadotAPI.SubdomainRecord> records = api.getSubdomains(domain);

        // add a new subdomain record
        DynadotAPI.SubdomainRecord record = new DynadotAPI.SubdomainRecord("A", address, subdomain, null);
        records.add(record);

        // this "address" is used for updating the main record, not the subdomain record. so you might want to change that.
        api.setDNS(domain, address, records);
    }

}
