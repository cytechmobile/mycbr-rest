package no.ntnu.mycbr.rest.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import no.ntnu.mycbr.rest.Query;
import no.ntnu.mycbr.rest.service.RetrievalService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static no.ntnu.mycbr.rest.utils.RESTCBRUtils.getFullResult;

import static no.ntnu.mycbr.rest.common.ApiResponseAnnotations.*;
import static no.ntnu.mycbr.rest.common.Constants.*;

@RestController
public class RetrievalController {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private RetrievalService retrievalService;

    @ApiOperation(value = "getSimilarInstances", nickname = "getSimilarInstances")
    @RequestMapping(method = RequestMethod.POST, path=DEFAULT_PATH+"retrieval", produces=APPLICATION_JSON)
    @ApiResponsesForQuery
    public Query getSimilarInstances(
	    @RequestParam(value=CASEBASE_STR, defaultValue=DEFAULT_CASEBASE) String casebase,
	    @RequestParam(value=CONCEPT_NAME_STR, defaultValue=DEFAULT_CONCEPT) String concept,
	    @RequestParam(value=AMALGAMATION_FUNCTION_STR, defaultValue=DEFAULT_AMALGAMATION_FUNCTION) String amalFunc,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k,
	    @RequestBody(required = true) HashMap<String, Object> queryContent) {
	return new Query(casebase, concept, amalFunc, queryContent, k);
    }

    @ApiOperation(value = "getSimilarInstancesByID", nickname = "getSimilarInstancesByID")
    @RequestMapping(method = RequestMethod.GET, path=DEFAULT_PATH+"retrievalByID", produces=APPLICATION_JSON)
    @ApiResponsesForQuery
    public Query getSimilarCasesByID(
	    @PathVariable(value="conceptID") String conceptID,
	    @PathVariable(value="casebaseID") String casebaseID,
	    @RequestParam(value="caseID") String caseID,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k) {
	return new Query(casebaseID, conceptID, null, caseID, k);
    }

    @ApiOperation(value = "getSimilarInstancesByIDs", nickname = "getSimilarInstancesByIDs")
    @RequestMapping(method = RequestMethod.GET, path=DEFAULT_PATH+"retrievalByIDs", produces=APPLICATION_JSON)
    @ApiResponsesForQuery
    public HashMap<String, HashMap<String,Double>> getSimilarCasesByIDs(
	    @PathVariable(value="conceptID") String conceptID,
	    @PathVariable(value="casebaseID") String casebaseIDs,
	    @RequestParam(value="caseIDs") String caseIDsJson,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k) {
	ArrayList<String> caseIDs = new ArrayList<>();
	JSONParser parser = new JSONParser();
	JSONArray inpcases = null;
	try {
	    inpcases = (JSONArray) parser.parse(caseIDsJson);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	Iterator<String>  it = inpcases.iterator();
	while(it.hasNext())
	    caseIDs.add(it.next());
	return Query.retrieve(casebaseIDs, conceptID, null, caseIDs, k);
    }

    @ApiOperation(value = "getSimilarInstancesByIDWithinIDs", nickname = "getSimilarInstancesByIDWithinIDs")
    @RequestMapping(method = RequestMethod.GET, path=DEFAULT_PATH+"retrievalByIDInIDs", produces=APPLICATION_JSON)
    @ApiResponsesForQuery
    public HashMap<String, HashMap<String,Double>> getSimilarInstancesByIDWithinIDs(
	    @PathVariable(value="conceptID") String conceptID,
	    @PathVariable(value="casebaseID") String casebaseID,
	    @RequestParam(value="caseID", defaultValue = "queryCaseID1") String caseID,
	    @RequestParam(value="filterCaseIDs", defaultValue = "[caseID1, caseID2, caseID3]") String filterCaseIDs,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k) {
	JSONParser parser = new JSONParser();
	ArrayList<String> caseIDs = new ArrayList<>();
	caseIDs.add(caseID);
	ArrayList<String> queryBaseIDs = new ArrayList<>();
	JSONArray queryBase = null;
	try {
	    queryBase = (JSONArray) parser.parse(filterCaseIDs);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	Iterator<String>  it = queryBase.iterator();
	it = queryBase.iterator();
	while(it.hasNext())
	    queryBaseIDs.add(it.next());
	return Query.retrieve(casebaseID, conceptID, null, caseIDs, queryBaseIDs, k);
    }

    @ApiOperation(value = "getSimilarInstancesByIDsWithinIDs", nickname = "getSimilarInstancesByIDWithinIDs")
    @RequestMapping(method = RequestMethod.GET, path=DEFAULT_PATH+"retrievalByIDsInIDs", produces=APPLICATION_JSON)
    @ApiResponsesForQuery
    public HashMap<String, HashMap<String,Double>> getSimilarInstancesByIDsWithinIDs(
	    @PathVariable(value="conceptID") String conceptID,
	    @PathVariable(value="casebaseID") String casebaseID,
	    @RequestParam(value="caseIDs", defaultValue = "[queryCase1, queryCase2]") String caseIDs,
	    @RequestParam(value="filterCaseIDs", defaultValue = "[caseID1, caseID2, caseID3]") String filterCaseIDs,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k) {
	ArrayList<String> caseIDList = new ArrayList<>();
	JSONParser parser = new JSONParser();
	JSONArray inpcases = null;
	try {
	    inpcases = (JSONArray) parser.parse(caseIDs);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	Iterator<String>  it = inpcases.iterator();
	while(it.hasNext())
	    caseIDList.add(it.next());
	ArrayList<String> queryBaseIDs = new ArrayList<>();
	JSONArray queryBase = null;
	try {
	    queryBase = (JSONArray) parser.parse(filterCaseIDs);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	it = queryBase.iterator();
	while(it.hasNext())
	    queryBaseIDs.add(it.next());
	return Query.retrieve(casebaseID, conceptID, null, caseIDList, queryBaseIDs, k);
    }

    @ApiOperation(value = "getSimilarInstancesByAttribute", nickname = "getSimilarInstances")
    @RequestMapping(method = RequestMethod.GET, path=DEFAULT_PATH+"retrievalByAttribute", produces=APPLICATION_JSON)
    @ApiResponsesForQuery
    public Query getSimilarInstancesByAttribute(
	    @RequestParam(value=CASEBASE_STR, defaultValue=DEFAULT_CASEBASE) String casebase,
	    @RequestParam(value=CONCEPT_NAME_STR, defaultValue=DEFAULT_CONCEPT) String concept,
	    @RequestParam(value=AMALGAMATION_FUNCTION_STR, defaultValue=DEFAULT_AMALGAMATION_FUNCTION) String amalFunc,
	    @RequestParam(value="Symbol attribute name", defaultValue="Manufacturer") String attribute,
	    @RequestParam(value="value", defaultValue="vw") String value,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k) {
	return new Query(casebase, concept, amalFunc, attribute, value, k);
    }

    @ApiOperation(value = "getSimilarInstancesWithContent", nickname = "getSimilarInstancesWithContent")
    @RequestMapping(method = RequestMethod.POST, path=DEFAULT_PATH+"retrievalWithContent", produces=APPLICATION_JSON)
    @ApiResponsesDefault
    public @ResponseBody List<LinkedHashMap<String, String>> getSimilarInstancesWithContent(
	    @RequestParam(value=CASEBASE_STR, defaultValue=DEFAULT_CASEBASE) String casebase,
	    @RequestParam(value=CONCEPT_NAME_STR, defaultValue=DEFAULT_CONCEPT) String concept,
	    @RequestParam(value=AMALGAMATION_FUNCTION_STR, defaultValue=DEFAULT_AMALGAMATION_FUNCTION) String amalFunc,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k,
	    @RequestBody(required = true)  HashMap<String, Object> queryContent) {

	Query query = new Query(casebase, concept, amalFunc, queryContent, k);
	List<LinkedHashMap<String, String>> cases = getFullResult(query, concept);
	return cases;
    }

    @ApiOperation(value = "getSimilarInstancesByIDWithContent", nickname = "getSimilarInstancesByIDWithContent")
    @RequestMapping(method = RequestMethod.GET, path=DEFAULT_PATH+"retrievalByIDWithContent", produces=APPLICATION_JSON)
    @ApiResponsesDefault
    public @ResponseBody List<LinkedHashMap<String, String>> getSimilarInstancesByIDWithContent(
	    @PathVariable(value="conceptID") String conceptID,
	    @PathVariable(value="casebaseID") String casebaseID,
	    @RequestParam(value=AMALGAMATION_FUNCTION_STR, defaultValue=DEFAULT_AMALGAMATION_FUNCTION) String amalFunc,
	    @RequestParam(value="caseID", defaultValue="144_vw") String caseID,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k) {

	Query query = new Query(casebaseID, conceptID, amalFunc, caseID, k);
	List<LinkedHashMap<String, String>> cases = getFullResult(query, conceptID);
	return cases;
    }

    @ApiOperation(value = "getSimilarInstancesByAttributeWithContent", nickname = "getSimilarInstancesByAttributeWithContent")
    @RequestMapping(method = RequestMethod.GET, path=DEFAULT_PATH+"retrievalWithContent", produces=APPLICATION_JSON)
    @ApiResponsesDefault
    public @ResponseBody List<LinkedHashMap<String, String>> getSimilarInstancesByAttributeWithContent(
	    @PathVariable(value="conceptID") String conceptID,
	    @PathVariable(value="casebaseID") String casebaseID,
	    @RequestParam(value=AMALGAMATION_FUNCTION_STR, defaultValue=DEFAULT_AMALGAMATION_FUNCTION) String amalFunc,
	    @RequestParam(value="Symbol attribute name", defaultValue="Manufacturer") String attribute,
	    @RequestParam(value="value", defaultValue="vw") String value,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES,defaultValue = DEFAULT_NO_OF_CASES) int k) {

	Query query = new Query(casebaseID, conceptID, amalFunc, attribute, value, k);
	List<LinkedHashMap<String, String>> cases = getFullResult(query, conceptID);
	return cases;
    }

    /**
     * Retrieval of similarity values for all the cases of the case base, where every case is queried to the case base.
     * @param casebase: The name of the case base used in the myCBR project
     * @param concept: The name of the concept used in the myCBR project
     * @param amalFunc: Amalgamation function or Global Similarity Function that is used in global similarity computation
     * @param k: Number of retrieved cases desired by the user. Default value is -1, which means return all the cases.
     * @return A matrix of similarity values, where the rows and columns are case IDs. The data structure is map of maps. 
     */
    @ApiOperation(value = GET_CASE_BASE_SELF_SIMILARITY, nickname = GET_CASE_BASE_SELF_SIMILARITY)
    @RequestMapping(method = RequestMethod.GET, path=SLASH_GET_CASE_BASE_SELF_SIMILARITY, produces=APPLICATION_JSON)
    @ApiResponsesDefault
    public LinkedHashMap<String, LinkedHashMap<String, Double>> getCaseBaseSelfSimilarity(
	    @RequestParam(value=CASEBASE_STR, defaultValue=DEFAULT_CASEBASE) String casebase,
	    @RequestParam(value=CONCEPT_NAME_STR, defaultValue=DEFAULT_CONCEPT) String concept,
	    @RequestParam(value=AMALGAMATION_FUNCTION_STR, defaultValue=DEFAULT_AMALGAMATION_FUNCTION) String amalFunc,
	    @RequestParam(required = false, value=NO_OF_RETURNED_CASES, defaultValue = DEFAULT_NO_OF_CASES) int k) {

	return retrievalService.getCaseBaseSelfSimilarity(casebase, concept, amalFunc, k);
	//return new RetrievalService(casebase, concept, amalFunc, k).getSelfSimilarityMatrix();
    }
}
