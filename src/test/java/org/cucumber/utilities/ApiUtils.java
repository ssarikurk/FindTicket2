package org.cucumber.utilities;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class ApiUtils {

    static String finalToken = "";
    public static Object generateToken(String partner) {
        RestAssured.useRelaxedHTTPSValidation();
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("username", "test");
        postBody.put("password", "abc123");
//        System.out.println("postBody generatetoken = " + postBody);

        Response response = RestAssured.given()
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("https://petstore.swagger.io/oauth/authorize");

//       response.prettyPrint();
//       System.out.println("response.statusCode() = " + response.statusCode());
        Map<String,Object> jsonDataMap = response.body().as(Map.class);
//       System.out.println("jsonDataMap = " + jsonDataMap);

        finalToken = "Bearer " + jsonDataMap.get("token");
       System.out.println("finalToken = " + finalToken);
        return finalToken;
    }

    public static JsonPath getPetWithPetid (int petId){
        RestAssured.baseURI = "https://petstore.swagger.io";
        Response response = given()
                .relaxedHTTPSValidation()
                .header("Accept", "application/json") // 🔥 Buraya ekleriz
                .when()
                .get("/v2/pet/" + petId)
                .then()
                .assertThat().statusCode(200)
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static String getX12 (String partner, String practice, String claimNum){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .queryParam("practice_id",practice)
                .when().get("/api/clear/"+claimNum+"/get_x12");
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
        System.out.println(response.statusCode());
        String x12Str = jsonPath.getString("x12");
        return x12Str;
    }


    public static JsonPath getSweepPage (String partner){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("/api/accounts/sweep/list?limit=10&skip=0");
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }


    public static JsonPath getListOfSweep (String partner){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("/api/accounts/sweep/list");
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static JsonPath GetFullListOfAch(String partner, String accountId){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("/api/account/"+accountId+"/details/table?limit=10&skip=0&sortBy=request_timestamp&sortOrder=-1&startDate=&endDate=");
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static Response previewACH (String partner, String dounId){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("/api/accounts/sweep/xml/?account_id="+dounId+"&viceCode=NURG");
//                .when().get("/api/accounts/sweep/xml/?dfi_account_id=hsbc317000551&tin=844035200&npi=1811520976&achServiceCode=NURG");
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return response;
    }

    public static JsonPath enableNegativePatientResponsibility (String partner, String practiceId){
        RestAssured.baseURI = ConfigurationReader.get("url");
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("/api/practice/procedure_adj_type?practice_id="+practiceId);
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
        System.out.println("enableNegativePatientResponsibility api request Status: "+response.statusCode());
        return jsonPath;
    }

    public static JsonPath getPatientData (String partner, String practice, String patient){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("/api/patient/"+patient+"/"+practice+"/with_insurance");
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static Map<String, Object> getDefaultBillingProvider (String partner, String practice){
        Map<String, Object> map = null;
        try {
            RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
            Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                    .header("User-Agent", "test")
                    .when().get("/api/dilling_provider/"+practice+"?appoint_id=undefined");
            map = response.body().as(Map.class);
//            System.out.println("fromApi = " + response.prettyPrint());
//            System.out.println("map = " + map);
//        System.out.println(response.statusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    public static String getDefaultBillingProviderid (String partner, String practice){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("/api/dilling_provider/"+practice+"?appoint_id=undefined");
        JsonPath jsonPath = response.jsonPath();
        String providerID = jsonPath.get("provider_id").toString();
//        System.out.println("fromProvider = " + response.prettyPrint());
//        System.out.println(response.statusCode());
//        System.out.println("providerID = " + providerID);
        return providerID;
    }

    public static JsonPath getBillingProvideFromPractice (String partner, String practice){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("/api/practice?practice_id="+practice);
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
//        System.out.println("providerID = " + providerID);
        return jsonPath;
    }

    public static Map<String,Object> getEligRequirements (String partner, String practice, String dentalPayorId, String patient){
//        System.out.println("payor = " + payor);
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("api/eligibility/requirements?practice_id="+practice+"&patient_id="+patient+"&dental_payor_id="+dentalPayorId+"&type=primary");
        Map<String,Object> requirementBody = response.as(Map.class);

        System.out.println("requirementBody = " + requirementBody);
//        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return requirementBody;
    }

    public static JsonPath getEligRequirementsJson (String partner, String practice, String dentalPayorId, String patient){
//        System.out.println("payor = " + payor);
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("api/eligibility/requirements?practice_id="+practice+"&patient_id="+patient+"&dental_payor_id="+dentalPayorId+"&type=primary");
        Map<String,Object> requirementBody = response.as(Map.class);

        System.out.println("requirementBody = " + requirementBody);
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static Map<String,Object> getEligRequirementsforSecondaryInsurance (String partner, String practice, String dentalPayorId, String patient){
//        System.out.println("payor = " + payor);
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().get("api/eligibility/requirements?practice_id="+practice+"&patient_id="+patient+"&dental_payor_id="+dentalPayorId+"&type=secondary");
        Map<String,Object> requirementBody = response.as(Map.class);

        System.out.println("requirementBody = " + requirementBody);
//        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return requirementBody;
    }

    public static JsonPath getEligibilityResponse(String partner, String practice, String patient){
        Object token = generateToken(partner);
//        System.out.println("generateToken() = " + token);
        RestAssured.baseURI = ConfigurationReader.get("url");
        System.out.println("baseURI = " + baseURI);
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .header("Authorization",token)
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(prepareEligibilityPostbody(patient,partner,practice))
                .when().post("/api/eligibility_check"); // for url2 environment
               // .when().post("/eligibility_check");
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
        System.out.println("Eligibility response statusCode = " + response.statusCode());
        try {
            System.out.println("Eligibility response status = " + jsonPath.getString("cooked_response.plan_information.status"));
            System.out.println("Eligibility response id = " + jsonPath.getString("eligibility_response_id"));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Eligibility is NOT successfull ");
        }
        return jsonPath;
    }

    public static JsonPath manuallyTransferEra(String eraSplitFileAssignedId, String practiceId, String practiceName, String partner) {
        Object token = generateToken(partner);
        System.out.println("Token: " + token);

        RestAssured.baseURI = ConfigurationReader.get("url");
        System.out.println("baseURI = " + baseURI);

        // Construct the JSON payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("practice_id", practiceId);
        payload.put("practice_name", practiceName);
        payload.put("era_split_file_assigned_id", eraSplitFileAssignedId); // Assuming eraSplitFileAssignedId is relevant
        // Add more fields if needed

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .header("Authorization", token)
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(payload) // Send the payload
                .when().post("/api/era_file/transfer");

        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static JsonPath getAchOutList(String endToEndId, String partner) {
        Object token = generateToken(partner);
//        System.out.println("Token: " + token);

        RestAssured.baseURI = ConfigurationReader.get("url");
//        System.out.println("baseURI = " + baseURI);

        // Construct the JSON payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("end_to_end_id", endToEndId);
        // Add more fields if needed

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .header("Authorization", token)
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(payload) // Send the payload
                .when().post("api/account/virtual/ledgersByEndtoEndId");

        System.out.println("getAchOutList status code: "+response.statusCode());
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
        return jsonPath;
    }

    public static void reconcileLedger(String partner, String hsbcPayableSubmissionId) {
        Object token = generateToken(partner);
//        System.out.println("Token: " + token);

        RestAssured.baseURI = ConfigurationReader.get("url");
//        System.out.println("baseURI = " + baseURI);

        String currentUTCTimestamp = Instant.now()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        // Construct the JSON payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("hsbc_payables_submission_id", hsbcPayableSubmissionId);
        payload.put("reconcileDate", currentUTCTimestamp);
        payload.put("status", "reconciled");
        // Add more fields if needed

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .header("Authorization", token)
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(payload) // Send the payload
                .when().patch("api/hsbc_payables_submission_and_ledger");

        System.out.println("reconcileLedger API Request status code: "+response.statusCode());
    }


    public static Response getEligibilityResponse2(String partner, String practice, String patient){
        Object token = generateToken(partner);
//        System.out.println("generateToken() = " + token);
        RestAssured.baseURI = ConfigurationReader.get("url");
        System.out.println("baseURI = " + baseURI);
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .header("Authorization",token)
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(prepareEligibilityPostbody(patient,partner,practice))
                .when().post("/api/eligibility_check"); // for url2 environment
        // .when().post("/eligibility_check");
//        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
        System.out.println("Eligibility response statusCode = " + response.statusCode());
        try {
            System.out.println("Eligibility response status = " + response.jsonPath().getString("_doc.cooked_response.plan_information.status"));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Eligibility is NOT successfull ");
        }
        return response;
    }

    public static Map<String,Object> prepareEligibilityPostbody (String patient, String partner, String practice){
        Map<String,Object> postBody = new HashMap<>();
        try {
            MongoCollection<Document> patientColl = MongoDBUtils.connectMongodb("mongoURI",partner,"patient_1");
            MongoCollection<Document> patPlanColl = MongoDBUtils.connectMongodb("mongoURI",partner,"patient_plan_1");
            MongoCollection<Document> providerColl = MongoDBUtils.connectMongodb("mongoURI",partner,"provider_1");
//            MongoCollection<Document> clinicColl = MongoDBUtils.connectMongodb("mongoURI",partner,"clinic_1");
            MongoCollection<Document> practiceColl = MongoDBUtils.connectMongodb("mongoURI",partner,"practice_1");
            BasicDBObject queryPractice = BasicDBObject.parse("{patient_id:'" + patient + "', practice_id: '"+practice +"'}");
            Document patPlanDoc = patPlanColl.find(queryPractice).first();
            String patRelation = patPlanDoc.getString("relationship");
            System.out.println("patRelation = " + patRelation);

            AggregateIterable<Document> aggregateDoc = patientColl.aggregate(AggregatePipeline.personToCarrier(practice,patient));
            Document patientDoc = aggregateDoc.first();
//            System.out.println("patientDoc.get(\"patient_id\") = " + patientDoc.get("patient_id"));
            JsonPath patientJson = JsonPath.from(patientDoc.toJson());
//            patientJson.prettyPrint();
//            JsonPath patientJson2 = apiUtils.getPatientData(partner,practice,patient);
            JsonPath patientAllDataJson = ApiUtils.getPatientData(partner,practice,patient);
//            patientAllDataJson.prettyPrint();
            String dentalPayorId = patientAllDataJson.getString("insurance.primary.carrier.dental_payor_id");
            System.out.println("dentalPayorId = " + dentalPayorId);

            BasicDBObject queryPractice2 = BasicDBObject.parse("{practice_id:'"+practice+"'}");
            Document practiceDoc = practiceColl.find(queryPractice2).first();
            JsonPath praceticeJson = JsonPath.from(practiceDoc.toJson());
//            praceticeJson.prettyPrint();
            Map<String, Object> billingAddressJson = praceticeJson.getMap("billing_address");

            Map<String, Object> billingProviderData = ApiUtils.getDefaultBillingProvider(partner,practice);
//            System.out.println("billingProviderData = " + billingProviderData);
//            billingProviderData.prettyPrint();
//            System.out.println("----------------------");
            JsonPath practiceData = ApiUtils.getBillingProvideFromPractice(partner,practice);
//            practiceData.prettyPrint();
            String providerID = "";
            String providerNpi = "";
            String providerTin = "";
            boolean providerIsNotPerson = false;
            String fName = "";
            String lName = "";
            boolean usingTin = false;

            if (billingProviderData == null) {
                providerTin = practiceData.getString("tin_ein");
                List<String> npiList = practiceData.getList("npi", String.class);
                if (npiList != null && !npiList.isEmpty()) {
                    providerNpi = npiList.get(0);
                } else {
                    throw new Exception("NPI list is empty or not found.");
                }
//                System.out.println("entered if");
            } else {
//                System.out.println("entered else");
                providerID = billingProviderData.get("provider_id").toString();
                System.out.println("providerID = " + providerID);
                BasicDBObject queryProvider = BasicDBObject.parse("{provider_id:'" + providerID + "', practice_id:'" + practice + "'}");
                Document providerDoc = providerColl.find(queryProvider).first();
                if (providerDoc != null) {
                    providerIsNotPerson = providerDoc.getBoolean("is_not_person");
                    providerTin = providerDoc.getString("tin_ein");
                    providerNpi = providerDoc.getString("npi");
                    fName = providerDoc.getString("first_name");
                    lName = providerDoc.getString("last_name");
                    usingTin = providerDoc.getBoolean("using_tin");
                } else {
                    throw new Exception("Provider document not found.");
                }
            }



            Map<String,Object> eligReqMap = ApiUtils.getEligRequirements(partner, practice, dentalPayorId, patient);
            JsonPath eligReqMapJson = ConvertUtils.mapToJsonpath(eligReqMap);
//            eligReqMapJson.prettyPrint();
            String payorId = "";
            if (eligReqMap!=null){
                payorId = eligReqMapJson.get("eligibilityReqInfo.payor_id").toString();
//                providerNpi=eligReqMap.get("eligibility_npi").toString();
            }
            System.out.println("payorId = " + payorId);

            System.out.println("providerNpi = " + providerNpi);


            if (patRelation.equals("self")){
                postBody.put("practice_id", practice);
                postBody.put("requirements", eligReqMap);
                postBody.put("payor_id", payorId);
                postBody.put("payor_name", patientAllDataJson.getString("insurance.primary.carrierPayorLookup.payer_name"));
                postBody.put("provider_npi", providerNpi);
                postBody.put("provider_tax_id", providerTin);
                postBody.put("request_for", "subscriber");
                postBody.put("subscriber_last_name", patientAllDataJson.getString("insurance.primary.insurance_subscriber.last_name"));
                postBody.put("subscriber_first_name", patientAllDataJson.getString("insurance.primary.insurance_subscriber.first_name"));
                postBody.put("subscriber_member_id", patientJson.getString("insSub.subscriber_id"));
                String birthdate = DateUtils.getNewFormatedDate2(patientAllDataJson.get("insurance.primary.insurance_subscriber.birth_date"),"yyyyMMdd");
//                System.out.println("birthdate subscriber= " + birthdate);
                postBody.put("subscriber_birth_date", birthdate);
                postBody.put("subscriber_gender", patientJson.getString("gender"));
                postBody.put("patient_id", patient);
                postBody.put("insurance_subscriber_id", patientAllDataJson.getString("insurance.primary.insurance_subscriber.insurance_subscriber_id"));
                postBody.put("clinic_id", patientAllDataJson.getString("clinic_id"));
//                postBody.put("ordinal", patientAllDataJson.getString("insurance.primary.insurance_plan.ordinal"));
                postBody.put("ordinal", 1);
                postBody.put("patient_plan_id", patientAllDataJson.getString("insurance.primary.patient_plan.patient_plan_id"));
                postBody.put("carrier_id", patientAllDataJson.getString("insurance.primary.carrier.carrier_id"));
                postBody.put("carrier_name", patientAllDataJson.getString("insurance.primary.carrier.carrier_name"));
                postBody.put("insurance_group_number", patientAllDataJson.getString("insurance.primary.insurance_plan.group_num"));
                postBody.put("provider_id", providerID);
                postBody.put("provider_first_name", fName);
                postBody.put("provider_last_name", lName);
                postBody.put("provider_name", fName+lName);
                postBody.put("provider_is_not_person", providerIsNotPerson);
                postBody.put("provider_using_tin", usingTin);
                postBody.put("billing_address", billingAddressJson);
                postBody.put("dependent_gender", patientJson.getString("gender"));

            }else{
                BasicDBObject query = BasicDBObject.parse("{patient_id:'" + patientJson.getString("insSub.subscriber") + "', practice_id: '"+practice +"'}");
//            System.out.println("query = " + query);
                Document patientDoc2 = patientColl.find(query).first();
                JsonPath patient2Json = JsonPath.from(patientDoc2.toJson());

                postBody.put("practice_id", practice);
                postBody.put("requirements", eligReqMap);
                postBody.put("payor_id", payorId);
                postBody.put("payor_name", patientAllDataJson.getString("insurance.primary.carrierPayorLookup.payer_name"));
                postBody.put("provider_npi", providerNpi);
                postBody.put("provider_tax_id", providerTin);
                postBody.put("request_for", "dependent");
                postBody.put("subscriber_last_name", patientAllDataJson.getString("insurance.primary.insurance_subscriber.last_name"));
                postBody.put("subscriber_first_name", patientAllDataJson.getString("insurance.primary.insurance_subscriber.first_name"));
                postBody.put("subscriber_member_id", patientAllDataJson.getString("insurance.primary.insurance_subscriber.subscriber_id"));
                String birthdate = DateUtils.getNewFormatedDate2(patientAllDataJson.get("insurance.primary.insurance_subscriber.birth_date"),"yyyyMMdd");
//                System.out.println("birthdate subscriber= " + birthdate);
                postBody.put("subscriber_birth_date", birthdate);
                postBody.put("subscriber_gender", patientAllDataJson.getString("insurance.primary.insurance_subscriber.gender"));
                postBody.put("dependent_last_name", patientJson.get("last_name"));
                postBody.put("dependent_first_name", patientJson.get("first_name"));
                postBody.put("dependent_birth_date", DateUtils.getEpochToFormatted(patientDoc.getDate("birth_date").getTime(), "yyyyMMdd"));
                postBody.put("dependent_gender", patientJson.getString("gender"));
                postBody.put("patient_id", patient);
                postBody.put("insurance_subscriber_id", patientAllDataJson.getString("insurance.primary.insurance_subscriber.insurance_subscriber_id"));
                postBody.put("clinic_id", patientAllDataJson.getString("clinic_id"));
//                postBody.put("ordinal", patientAllDataJson.getString("insurance.primary.insurance_plan.ordinal"));
                postBody.put("ordinal", 1);
                postBody.put("patient_plan_id", patientAllDataJson.getString("insurance.primary.patient_plan.patient_plan_id"));
//                postBody.put("carrier_id", patientAllDataJson.getString("insurance.primary.carrier.carrier_id"));
                postBody.put("carrier_name", patientAllDataJson.getString("insurance.primary.carrier.carrier_name"));
                postBody.put("insurance_group_number", patientAllDataJson.getString("insurance.primary.insurance_plan.group_num"));
                postBody.put("provider_id", providerID);
                postBody.put("provider_first_name", fName);
                postBody.put("provider_last_name", lName);
                postBody.put("provider_name", fName+lName);
                postBody.put("provider_is_not_person", providerIsNotPerson);
                postBody.put("provider_using_tin", usingTin);
                postBody.put("billing_address", billingAddressJson);

            }

        System.out.println("postBody = " + postBody);


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
            System.out.println("Eligibility Post Body NOT created");
        }
        return postBody;

    }

    public static JsonPath reconcileLedger (String partner, Map<String,Object> postBody){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().patch("/api/ledger");
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static JsonPath reconcileLedger (String partner, String practice, String ledgerObjId){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();

        Map<String, Object> postBody = new HashMap<>();
        postBody.put("_id", ledgerObjId);
        postBody.put("status", "reconciled");
        postBody.put("reconcileDate", new Date());
        postBody.put("practice_id", practice);

        System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().patch("/api/ledger");
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static JsonPath retDisapproveERA (String partner, String era_split_file_assigned_id){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("type", "disapprove");


        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("/api/sweep/era/auto_post/"+era_split_file_assigned_id);
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static JsonPath retApproveERA (String partner, String era_split_file_assigned_id){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("type", "approve");


        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("/api/sweep/era/auto_post/"+era_split_file_assigned_id);
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static JsonPath providerApproveERA (String partner, String era_split_file_assigned_id){
//        era_split_file_assigned_id = "RdMsdQorPckTnag4";
        RestAssured.baseURI = ConfigurationReader.get("url");
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("type", "approve");
       System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("/api/era/auto_finalize/"+era_split_file_assigned_id);
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }


    public static JsonPath providerDisapproveERA (String partner, String era_split_file_assigned_id){
//        era_split_file_assigned_id = "RdMsdQorPckTnag4";
        RestAssured.baseURI = ConfigurationReader.get("url");
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("type", "disapprove");
//        System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("/api/era/process_open_dental/"+era_split_file_assigned_id);
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";


    public static JsonPath submitClaimsEraCycle  (String partner, String practice, List<String> claimIdListtoSubmit, String collectionName){
        RestAssured.baseURI = ConfigurationReader.get("url");
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("practice_id", practice);
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        JsonPath jsonPath = null;
//        System.out.println("postBody = " + postBody);

            for (String claim : claimIdListtoSubmit) {
                if (!claim.equals("null") && claim!=null){
                    System.out.println("--------------------------submission started for claim = " + claim + "--------------------------");
//                claim = "389";
                    Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                            .header("User-Agent", "test")
                            .contentType("application/json")
                            .body(postBody)
                            .when().post("/api/claim/"+claim+"/submit");
                    //        response.prettyPrint();

                    try {
                        jsonPath = response.jsonPath();
                        jsonPath.prettyPrint();
                        //        System.out.println(response.statusCode());
                        //        return jsonPath;
                        String claimIdentifier = jsonPath.get("claim_identifier").toString();
                        String filter = "{claimIdentifier:{$regex:'"+claimIdentifier+"'}}";
//                BasicDBObject query = BasicDBObject.parse(filter);
//                System.out.println("query = " + query);
                        MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", collectionName);
                        String update = "error";
                        boolean isSubmitSucceeded = jsonPath.get("success");
                        System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                        if (!isSubmitSucceeded){
                            update = "{$set:{claimSubmitSuccess:'Error: "+jsonPath.get("error")+"'}}";
                            System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_RED+"Submission FAILLED!! ---- claimIdentifier: "
                                    + claimIdentifier + " ---> submission error = "+jsonPath.get("error")+ANSI_RESET);
//                          jsonPath.prettyPrint();
//                          System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                        }else {
                            update = "{$set:{claimSubmitSuccess:'success'}}";
                            System.out.println(ANSI_GREEN+"Success ---- claimIdentifier: " + claimIdentifier + " submitted "+ANSI_RESET);
//                    System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                        }
                        MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            mongoClient.close();
            return jsonPath;
    }
    public static JsonPath submitClaimsEraCycle2  (String partner, String practice, List<String> claimIdListtoSubmit, String collectionName, String testCaseDB){
        RestAssured.baseURI = ConfigurationReader.get("url");
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("practice_id", practice);
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        JsonPath jsonPath = null;
//        System.out.println("postBody = " + postBody);

        for (String claim : claimIdListtoSubmit) {
            if (!claim.equals("null") && claim!=null){
                System.out.println("--------------------------submission started for claim = " + claim + "--------------------------");
//                claim = "389";
                Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                        .header("User-Agent", "test")
                        .contentType("application/json")
                        .body(postBody)
                        .when().post("/api/claim/"+claim+"/submit");
                //        response.prettyPrint();

                try {
                    jsonPath = response.jsonPath();
                    jsonPath.prettyPrint();
                    //        System.out.println(response.statusCode());
                    //        return jsonPath;
                    String claimIdentifier = jsonPath.get("claim_identifier").toString();
                    String filter = "{claimIdentifier:{$regex:'"+claimIdentifier+"'}}";
//                BasicDBObject query = BasicDBObject.parse(filter);
//                System.out.println("query = " + query);
                    MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, testCaseDB, collectionName);
                    String update = "error";
                    boolean isSubmitSucceeded = jsonPath.get("success");
                    System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                    if (!isSubmitSucceeded){
                        update = "{$set:{claimSubmitSuccess:'Error: "+jsonPath.get("error")+"'}}";
                        System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_RED+"Submission FAILLED!! ---- claimIdentifier: "
                                + claimIdentifier + " ---> submission error = "+jsonPath.get("error")+ANSI_RESET);
//                          jsonPath.prettyPrint();
//                          System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                    }else {
                        update = "{$set:{claimSubmitSuccess:'success'}}";
                        System.out.println(ANSI_GREEN+"Success ---- claimIdentifier: " + claimIdentifier + " submitted "+ANSI_RESET);
//                    System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                    }
                    MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mongoClient.close();
        return jsonPath;
    }

    public static void submitMultipleClaimsEraCycle (String partner, String practice, String claims, String collectionName){
        RestAssured.baseURI = ConfigurationReader.get("url");
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("practice_id", practice);
        postBody.put("claim_ids", claims);
//        System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("/api/claims-multiple/submit");
        List<Map<String, Object>> submissionResultList = new ArrayList<>();
//        response.prettyPrint();
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        try {
//            jsonPath = response.jsonPath();
            submissionResultList = response.body().as(List.class);
//            System.out.println("submissionResultList = " + submissionResultList);
            for (Map<String, Object> resultMap : submissionResultList) {
                boolean isSubmitSucceeded = (boolean) resultMap.get("success");

                String claimIdentifier = resultMap.get("claim_identifier").toString();
                String filter = "{claimIdentifier:{$regex:'"+claimIdentifier+"'}}";
//                BasicDBObject query = BasicDBObject.parse(filter);
//                System.out.println("query = " + query);
                MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", collectionName);
                String update = "error";
                if (!isSubmitSucceeded){
                    update = "{$set:{claimSubmitSuccess:'Error: "+resultMap.get("error")+"'}}";
                    System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_RED+"Submission FAILLED!! ---- claimIdentifier: "
                            + claimIdentifier + " ---> submission error = "+resultMap.get("error")+ANSI_RESET);
//                    System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                }else {
                    update = "{$set:{claimSubmitSuccess:'success'}}";
                    System.out.println(ANSI_GREEN+"Success ---- claimIdentifier: " + claimIdentifier + " submitted "+ANSI_RESET);
//                    System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                }
                MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            mongoClient.close();
        }
    }


    public static void submitMultipleClaims (String partner, String practice, String claims){
        RestAssured.baseURI = ConfigurationReader.get("url");
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("practice_id", practice);
        postBody.put("claim_ids", claims);
//        System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("/api/claims-multiple/submit");
        List<Map<String, Object>> submissionResultList = new ArrayList<>();
//        response.prettyPrint();
        try {
//            jsonPath = response.jsonPath();
            submissionResultList = response.body().as(List.class);
//            System.out.println("submissionResultList = " + submissionResultList);
            for (Map<String, Object> resultMap : submissionResultList) {
                boolean isSubmitSucceeded = (boolean) resultMap.get("success");
                if (!isSubmitSucceeded){
                    System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_RED+"Submission FAILLED!! ---- claimIdentifier: "
                            +resultMap.get("claim_identifier") + " ---> submission error = "+resultMap.get("error")+ANSI_RESET);
//                    System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                }else {
                    System.out.println(ANSI_GREEN+"Success ---- claimIdentifier: " +resultMap.get("claim_identifier") + " submitted "+ANSI_RESET);
//                    System.out.println("isSubmitSucceeded = " + isSubmitSucceeded);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JsonPath startFullSync (String partner, String integrationId){
        RestAssured.baseURI = ConfigurationReader.get("url");
        Map<String,Object> postBody = new HashMap<>();
        Map<String,Object> actionMap = new HashMap<>();
        actionMap.put("action","reset-tables");
        postBody.put("actions", actionMap);
//        System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("/api/integrations/"+integrationId+"?force=false");
        JsonPath jsonPath = null;
//        response.prettyPrint();
        try {
            jsonPath = response.jsonPath();
//            jsonPath.prettyPrint();
//        System.out.println(response.statusCode());
//        return jsonPath;
            if (jsonPath.getBoolean("success")){
                System.out.println("partner = "+partner+ " fullsync started");
            }else {
                System.out.println("fullsync ERROR!!!");
                System.out.println(jsonPath.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonPath;
    }

    public static JsonPath updateAllPlans (Map<String,Object> postBody, String partner){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("api/patient/insurance-settings/payer-mapping/update-all-plans");
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static JsonPath dropPartnerDatabase (Map<String,Object> postBody){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken("qa-vixwin"))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().delete("api/partners/delete");
        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
//        System.out.println(response.statusCode());
        return jsonPath;
    }


    public static JsonPath sendEra (String partner, String dfiAccount){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().patch("/api/accounts/sweep/"+dfiAccount+"/NURG");
        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
        System.out.println(response.statusCode());
        return jsonPath;
    }


    public static Response getSweepResponse (String partner, String dfiAccount){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        RestAssured.useRelaxedHTTPSValidation();
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .when().patch("/api/accounts/sweep/"+dfiAccount+"/NURG");

//        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
        System.out.println(response.statusCode());
        return response;
    }

    public static JsonPath validateTasks (Map<String,Object> postBody, String partner){
        RestAssured.baseURI = ConfigurationReader.get("url");
//        System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("api/appointment-tasks/validate-tasks");

        JsonPath jsonPath = response.jsonPath();
        response.prettyPrint();
        System.out.println(response.statusCode());
        return jsonPath;
    }

    public static JsonPath sendCrashLog (Map<String,Object> postBody, String partner){
        RestAssured.baseURI = ConfigurationReader.get("url");
        //System.out.println("postBody = " + postBody);

        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("api/integration/log");

        JsonPath jsonPath = response.jsonPath();
        //response.prettyPrint();
        System.out.println("Statuscode: "+response.statusCode());
        return jsonPath;
    }

    public static JsonPath requalifyERA (String eraSplitFileAssignedId, String partner){
        RestAssured.baseURI = ConfigurationReader.get("url");
        Map<String,Object> postBody = new HashMap<>();
//        System.out.println("postBody = " + postBody);
        Response response = given().relaxedHTTPSValidation().header("Authorization",generateToken(partner))
                .header("User-Agent", "test")
                .contentType("application/json")
                .body(postBody)
                .when().post("/api/era/"+eraSplitFileAssignedId+"/qualify");

        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
        System.out.println("Statuscode: "+response.statusCode());
        return jsonPath;
    }

    public static JsonPath getSweepList(String partner) {
        RestAssured.baseURI = ConfigurationReader.get("url");

        Response response = given()
                .relaxedHTTPSValidation()
                .header("Authorization", generateToken(partner))
                .header("User-Agent", "test")
                .when()
                .get("/api/accounts/sweep/list?limit=10&skip=0");

        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
        System.out.println("Response Status Code: " + response.statusCode());

        return jsonPath;
    }

    public static JsonPath getACH(String partner) {
        RestAssured.baseURI = ConfigurationReader.get("url");

        Response response = given()
                .relaxedHTTPSValidation()
                .header("Authorization", generateToken(partner))
                .header("User-Agent", "test")
                .when()
                .get("/api/accounts/xml/?account_id=000&tin=999&npi=888&achServiceCode=NURG");

        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
        System.out.println("Response Status Code: " + response.statusCode());

        return jsonPath;
    }

    public static Response getStatusCode(String partner) {
        RestAssured.baseURI = ConfigurationReader.get("url");

        Response response = given()
                .relaxedHTTPSValidation()
                .header("Authorization", generateToken(partner))
                .header("User-Agent", "test")
                .when()
                .get("/api/accounts/xml/?account_id=000&tin=999&npi=888&achServiceCode=NURG");
//        JsonPath jsonPath = response.jsonPath();
//        response.prettyPrint();
        System.out.println("Response Status Code: " + response.statusCode());

        return response;
    }

    public static String getAchOutField(JsonPath listAchOut, String endToEndId, String expectedField){
        String jsonResponse = listAchOut.prettify();
        // Convert the responseJson to a JSON String to parse as a JSONArray
        JSONArray rootArray = new JSONArray(jsonResponse);  // Assuming getListAchOut provides JSON as String

        // Access the nested array at index 1
        JSONArray dataArray = rootArray.getJSONArray(1);  // Adjust as needed

        JSONObject matchedItem = null;
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            JSONArray endToEndIds = item.optJSONArray("end_to_end_id");

            // Check if end_to_end_id matches the specified value
            if (endToEndIds != null && endToEndIds.length() > 0 && endToEndId.equals(endToEndIds.getString(0))) {
                matchedItem = item;
                break;
            }
        }
        return (String) matchedItem.get(expectedField);
    }
}


