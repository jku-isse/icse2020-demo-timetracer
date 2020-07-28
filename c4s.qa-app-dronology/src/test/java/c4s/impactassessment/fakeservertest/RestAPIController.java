package c4s.impactassessment.fakeservertest;

/*import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.google.gson.JsonParser;

@RestController
public class RestAPIController {

	private static int counter = 0;
	
@RequestMapping("rest/api/latest/issue/{issueKey}")
    public ResponseEntity<String> getIssue(Model model, @PathVariable("issueKey") String issueKey) throws URISyntaxException, IOException {
		String jsonPCVSG = Files.readAllLines(Paths.get(RestAPIController.class.getResource("PVCSG-2.json").getPath()),
			Charset.forName("UTF-8")).get(0);
		String jsonCVCSXO = Files.readAllLines(Paths.get(RestAPIController.class.getResource("CVCSXO-1301.json").getPath()),
			Charset.forName("UTF-8")).get(0);
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Content-Security-Policy", "frame-ancestors 'self'");
        httpHeaders.set("Content-Type", "application/json;charset=UTF-8");
        httpHeaders.set("Transfer-Encoding", "chunked");
        httpHeaders.set("Vary", "Accept-Encoding");
        httpHeaders.set("X-AREQUESTID", "526x35752578x6");
        httpHeaders.set("X-ASEN", "SEN-2062203");
        httpHeaders.set("X-ASESSIONID", "1u23sik");
        httpHeaders.set("X-AUSERNAME", "anonymous");
        httpHeaders.set("X-Content-Type-Options", "nosniff");
        httpHeaders.set("X-Frame-Options", "SAMEORIGIN");
        httpHeaders.set("X-XSS-Protection", "1; mode=block");
        
        if(issueKey.equals("PVCSG-2")) {
        	return new ResponseEntity<String>(jsonPCVSG, httpHeaders, HttpStatus.OK);    
        } else if(issueKey.equals("CVCSXO-1301")) {
        	return new ResponseEntity<String>(jsonCVCSXO, httpHeaders, HttpStatus.OK); 
        } else {
        	return new ResponseEntity<String>(jsonCVCSXO, httpHeaders, HttpStatus.OK); 
        }
        
	}


	@RequestMapping("rest/api/latest/search")
	public ResponseEntity<String> getSearch(Model model)
			throws URISyntaxException, IOException {
		String jsonPCVSG = Files.readAllLines(Paths.get(RestAPIController.class.getResource("PVCSG-2.json").getPath()),
				Charset.forName("UTF-8")).get(0);
		String jsonCVCSXO = Files.readAllLines(Paths.get(RestAPIController.class.getResource("CVCSXO-1301.json").getPath()),
				Charset.forName("UTF-8")).get(0);
		
		
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("Content-Security-Policy", "frame-ancestors 'self'");
		httpHeaders.set("Content-Type", "application/json;charset=UTF-8");
		httpHeaders.set("Transfer-Encoding", "chunked");
		httpHeaders.set("Vary", "Accept-Encoding");
		httpHeaders.set("X-AREQUESTID", "526x35752578x6");
		httpHeaders.set("X-ASEN", "SEN-2062203");
		httpHeaders.set("X-ASESSIONID", "1u23sik");
		httpHeaders.set("X-AUSERNAME", "anonymous");
		httpHeaders.set("X-Content-Type-Options", "nosniff");
		httpHeaders.set("X-Frame-Options", "SAMEORIGIN");
		httpHeaders.set("X-XSS-Protection", "1; mode=block");
		
		JsonParser parser = new JsonParser();
		
		String response0 = "{\"expand\":\"schema,names\",\"startAt\":0,\"maxResults\":50,\"total\":1,\"issues\":[" + jsonCVCSXO + "],\"names\":" + parser.parse(jsonCVCSXO).getAsJsonObject().getAsJsonObject("names").toString() + ",\"schema\":" + parser.parse(jsonCVCSXO).getAsJsonObject().getAsJsonObject("schema").toString() + "}";
		String response1 = "{\"expand\":\"schema,names\",\"startAt\":0,\"maxResults\":50,\"total\":2,\"issues\":[" + jsonCVCSXO + "," + jsonPCVSG + "],\"names\":" + parser.parse(jsonPCVSG).getAsJsonObject().getAsJsonObject("names").toString() + ",\"schema\":" + parser.parse(jsonPCVSG).getAsJsonObject().getAsJsonObject("schema").toString() + "}";
		
		switch(counter) {
			case 0:
				counter++;
				return new ResponseEntity<String>(response0, httpHeaders, HttpStatus.OK);
			case 1:
				counter++;
				return new ResponseEntity<String>(response1, httpHeaders, HttpStatus.OK);
			case 2:
				counter++;
				return new ResponseEntity<String>(response1, httpHeaders, HttpStatus.OK);
			case 3:
				counter++;
				return new ResponseEntity<String>(response1, httpHeaders, HttpStatus.OK);
		}
		
		return new ResponseEntity<String>("{\"error\":true}", httpHeaders, HttpStatus.OK); 
	}

}*/