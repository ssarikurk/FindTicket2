package org.cucumber.utilities;

import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class AggregatePipeline {


    /*This aggregate lists records person to carrier with
    person
    patplan
    inssub
    insplan
    carrier order.*/
    public static List<Document> personToCarrier(String practice, String person){

        List<Document> pipeline = Arrays.asList(new Document("$match",
                        new Document("person_id", person)
                                .append("practice_id", practice)),
                new Document("$lookup",
                        new Document("from", "person_plan_1")
                                .append("localField", "person_id")
                                .append("foreignField", "person_id")
                                .append("as", "patPlan")),
                new Document("$unwind",
                        new Document("path", "$patPlan")),
                new Document("$match",
                        new Document("patPlan.practice_id", practice)),
                new Document("$lookup",
                        new Document("from", "insurance_subscriber_1")
                                .append("localField", "patPlan.insurance_subscriber_id")
                                .append("foreignField", "insurance_subscriber_id")
                                .append("as", "insSub")),
                new Document("$unwind",
                        new Document("path", "$insSub")),
                new Document("$match",
                        new Document("insSub.practice_id", practice)),
                new Document("$lookup",
                        new Document("from", "insurance_plan_1")
                                .append("localField", "insSub.insurance_plan_id")
                                .append("foreignField", "insurance_plan_id")
                                .append("as", "insPlan")),
                new Document("$unwind",
                        new Document("path", "$insPlan")),
                new Document("$match",
                        new Document("insPlan.practice_id", practice)),
                new Document("$lookup",
                        new Document("from", "carrier_1")
                                .append("localField", "insPlan.carrier_id")
                                .append("foreignField", "carrier_id")
                                .append("as", "carrier")),
                new Document("$unwind",
                        new Document("path", "$carrier")),
                new Document("$match",
                        new Document("carrier.practice_id", practice)));

        return pipeline;
    }


    public static List<Document> payorTopersonList(String payorID){

        List<Document> pipeline = Arrays.asList(new Document("$match",
                        new Document("payer_id", payorID)),
                new Document("$lookup",
                        new Document("from", "insurance_plan_1")
                                .append("localField", "carrier_id")
                                .append("foreignField", "carrier_id")
                                .append("as", "insplan")),
                new Document("$unwind",
                        new Document("path", "$insplan")
                                .append("preserveNullAndEmptyArrays", false)),
                new Document("$lookup",
                        new Document("from", "insurance_subscriber_1")
                                .append("localField", "insplan.insurance_plan_id")
                                .append("foreignField", "insurance_plan_id")
                                .append("as", "inssub")),
                new Document("$unwind",
                        new Document("path", "$inssub")
                                .append("preserveNullAndEmptyArrays", false)),
                new Document("$lookup",
                        new Document("from", "person_plan_1")
                                .append("localField", "inssub.insurance_subscriber_id")
                                .append("foreignField", "insurance_subscriber_id")
                                .append("as", "patplan")),
                new Document("$unwind",
                        new Document("path", "$patplan")
                                .append("preserveNullAndEmptyArrays", false)),
                new Document("$lookup",
                        new Document("from", "person_1")
                                .append("localField", "patplan.person_id")
                                .append("foreignField", "person_id")
                                .append("as", "person")),
                new Document("$unwind",
                        new Document("path", "$person")
                                .append("preserveNullAndEmptyArrays", false)),
                new Document("$lookup",
                        new Document("from", "clinic_1")
                                .append("localField", "person.clinic_id")
                                .append("foreignField", "clinic_id")
                                .append("as", "clinic")),
                new Document("$unwind",
                        new Document("path", "$clinic")
                                .append("preserveNullAndEmptyArrays", false)));
        return pipeline;
    }

}
