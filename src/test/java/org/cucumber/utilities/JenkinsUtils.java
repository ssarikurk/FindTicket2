package org.cucumber.utilities;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class JenkinsUtils {
    public static String triggerJenkinsJob( String jobName) {
        String username = ConfigurationReader.get("jenkinsUsername");
        String token = ConfigurationReader.get("jenkinsToken");
        String jenkinsUrl = ConfigurationReader.get("jenkinsUrl");
        String message = "";
        HttpURLConnection connection = null;
        try {
            String jobUrl = jenkinsUrl + "/job/" + jobName + "/build";
            URL url = new URL(jobUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set authentication
            String authStr = username + ":" + token;
            String authEncoded = Base64.getEncoder().encodeToString(authStr.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + authEncoded);

            // Sending request
            OutputStream os = connection.getOutputStream();
            os.write(0);
            os.flush();
            os.close();

            message = connection.getResponseMessage();
            System.out.println("message = " + message);
//            System.out.println("connection.getErrorStream() = " + connection.getErrorStream());
//            System.out.println("connection.getContent() = " + connection.getContent());
            int responseCode = connection.getResponseCode();
            if (responseCode == 201 || responseCode == 200) {
                System.out.println("Jenkins job triggered successfully.");
            } else {
                System.out.println("Failed to trigger Jenkins job. Response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return message;
    }


}





