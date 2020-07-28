package c4s.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import c4s.nodes.DecisionNodeDefinition;
import c4s.nodes.TaskDefinition;
import c4s.nodes.WorkflowInstance;
import c4s.repositories.DecisionNodeDefinitionRepository;
import c4s.repositories.TaskDefinitionRepository;
import c4s.repositories.WorkflowInstanceRepository;

@Controller
public class ResourceController {
	
	private static Logger log = LogManager.getLogger("ResourceController");
	private Gson gson = new Gson();
	
	private AMQPPublisher amqp;
	@Autowired
	WorkflowInstanceRepository wfiRepo;
	@Autowired
	TaskDefinitionRepository tdRepo;
		
	public ResourceController() throws IOException {
		String resourceName = "application.properties";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
		    props.load(resourceStream);
		}
		amqp = new AMQPPublisher(props);
	}
	
	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}
	
	@ResponseBody
	@RequestMapping(value = "/checks")
    public ResponseEntity<RestResponse> sendTrigger(
    		@RequestParam(value="constrId", defaultValue="") String constrId,
    		@RequestParam(value="constrType", defaultValue="") String constrType,
    		@RequestParam(value="wfiId", defaultValue="") String wfiId,
    		@RequestParam(value="wftId", defaultValue="") String wftId) {

		CheckMessage cm = new CheckMessage(constrId, constrType, wfiId, wftId);
		RestResponse r = new RestResponse();
		r.setPayload(cm.toString());

		if (cm.constrId.equals("")) {
			r.setMsg("Incomplete CheckMessage, constraint ID was undefined, replaced with '*'");
			log.warn(r.getMsg());
			cm.constrId = "*";
		}
		if (cm.constrType.equals("")) {
			r.setMsg("Incomplete CheckMessage, constraint type was undefined, replaced with '*' to check all");
			log.warn(r.getMsg());
			cm.constrType = "*";
		}
		if (cm.wftId.equals("")) {
			r.setMsg("Incomplete CheckMessage, WorkflowTask ID is undefined");
			log.warn(r.getMsg());
		}
		
		if (cm.wfiId.equals("")) {
			r.setMsg("Invalid CheckMessage, WorkflowInstance ID is undefined, message wasn't enqueued");
			log.warn(r.getMsg());
			return new ResponseEntity<>(r, HttpStatus.BAD_REQUEST);
		} else {
			String json = gson.toJson(cm);
			ResponseMessage response = amqp.sendToAMQP(json, "check");

			if (response != null) {
				r.setMsg(response.statusMsg);
				if (response.errorCode < 400)
					r.setMsg(r.getMsg()+"\nPlease reload page after a while to see changes!");
				return new ResponseEntity<>(r, HttpStatus.valueOf(response.errorCode));
			}
			else {
				r.setMsg("Message couldn't be sent, CheckMessage wasn't enqueued. \n Please try again later!");
				return new ResponseEntity<>(r, HttpStatus.BAD_REQUEST);
			}
		}
    }
	
	@ResponseBody
	@RequestMapping(value = "/add")
    public ResponseEntity<RestResponse> sendAddMessage(
    		@RequestParam(value="filterId", defaultValue="") String filterID,
    		@RequestParam(value="featureId", defaultValue="") String featureID) {
		
		AddMessage am = new AddMessage(filterID, featureID);
		RestResponse r = new RestResponse();
		r.setPayload(am.toString());
		
		if (!am.filterID.matches("[0-9]+") && !am.filterID.matches("")) {
			r.setMsg("Invalid filter ID, only numbers are allowed, message wasn't enqueued");
			log.warn(r.getMsg());
			return new ResponseEntity<>(r, HttpStatus.BAD_REQUEST);
		} 
		else if (!am.featureID.matches("[0-9]+") && !am.featureID.matches("")) {
			r.setMsg("Invalid feature ID, only numbers are allowed, message wasn't enqueued");
			log.warn(r.getMsg());
			return new ResponseEntity<>(r, HttpStatus.BAD_REQUEST);
		} else {
			String json = gson.toJson(am);
			ResponseMessage response = amqp.sendToAMQP(json, "add");
			if (response != null) {
				r.setMsg(response.statusMsg);
				if (response.errorCode < 400)
					r.setMsg(r.getMsg()+"\nPlease reload page after a while to see changes!");
				return new ResponseEntity<>(r, HttpStatus.valueOf(response.errorCode));
			}
			else {
				r.setMsg("Message couldn't be sent, Filter/feature wasn't enqueued. \n Please try again later!");
				return new ResponseEntity<>(r, HttpStatus.BAD_REQUEST);
			}
		}
    }
	
	@ResponseBody
	@RequestMapping(value = "/delete")
    public ResponseEntity<RestResponse> sendDeleteMessage(@RequestParam(value="wfiIds", defaultValue="") String wfiIds) {

		DeleteMessage dm = new DeleteMessage(Arrays.asList(wfiIds.split(",")));
		RestResponse r = new RestResponse();
		r.setPayload(dm.toString());
	
		String json = gson.toJson(dm);
		ResponseMessage response = amqp.sendToAMQP(json, "delete");
		if (response != null) {
			r.setMsg(response.statusMsg);
			if (response.errorCode < 400)
				r.setMsg(r.getMsg()+"\nPlease reload page after a while to see changes!");
			return new ResponseEntity<>(r, HttpStatus.valueOf(response.errorCode));
		}
		else {
			r.setMsg("Message couldn't be sent, Delete request wasn't enqueued. \n Please try again later!");
			return new ResponseEntity<>(r, HttpStatus.BAD_REQUEST);
		}
		
    }
	
	@GetMapping(value = "/wfi/WF")
    public @ResponseBody List<WorkflowInstance> getDocData(@RequestParam(value="wfiId", defaultValue="") String wfiId) {
		List<WorkflowInstance> wfis = new ArrayList<>();
		String[] params = wfiId.split(",");
		
		for (String s : params) {
			Optional<WorkflowInstance> optDoc = wfiRepo.findById(s, 10);
	        if (optDoc.isPresent())
	        	wfis.add(optDoc.get());
		}
		
        if (wfis.size() > 0)
        	return wfis;
        else
        	return null;
    }
	
	@GetMapping(value = "/wfi")
    public @ResponseBody List<WorkflowInstance> getWfi() {
        return wfiRepo.findAll(10);
    }
	
	@GetMapping(value = "/workflow")
    public @ResponseBody List<TaskDefinition> getWorkflow() {
        return tdRepo.findAll(1);
    }
	
	@GetMapping(value = "/wfis")
    public @ResponseBody List<WorkflowInstance> getWfis() {
        return wfiRepo.findAll(0);
    }
	
}
