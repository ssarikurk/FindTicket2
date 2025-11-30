package org.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import org.cucumber.utilities.ConfigurationReader;
import org.cucumber.utilities.SqlUtils;

import java.util.List;
import java.util.Map;

public class Mysql_Defs {
    @Given("Get data from {string} db")
    public void getDataFromDb(String mysqlDbName) {

        SqlUtils.createConnection(ConfigurationReader.get("MySQLIp"), mysqlDbName, "root", "retrace123");

        List<Map<String, Object>> patientData = SqlUtils.getQueryResultMap("select *  from patient p where PatNum = '1621';");
//        System.out.println("patientData = " + patientData);

        String patientName = patientData.get(0).get("FName").toString();
        System.out.println("patientName = " + patientName);


    }



}
