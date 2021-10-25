package isslocation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ISSWebServiceImpl implements ISSWebService {
  @Override
  public long fetchIssFlyOverData(double lat, double lon) {
    try {
      return parseData(getDataFromURL(lat, lon));
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public String getDataFromURL(double lat, double lon) throws Exception {
    String uri = "http://api.open-notify.org/iss-pass.json?lat=%.6f&lon=%.6f";
    String uriParam = String.format(uri, lat, lon);
    System.out.println(uriParam);

    StringBuilder sb = new StringBuilder();
    URL url = new URL(uriParam);
    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
    while(reader.ready()) {
      sb.append(reader.readLine());
    }
    return sb.toString();
  }
  public long parseData(String json) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    JsonNode node = mapper.readTree(json);
    String message = node.get("message").asText();
    if("failure".equalsIgnoreCase(message)) {
      String reason = node.get("reason").asText();
      throw new RuntimeException(reason);
    }
    String riseTime = node.get("response").get(0).get("risetime").asText();
    return Long.parseLong(riseTime);
  }
}
