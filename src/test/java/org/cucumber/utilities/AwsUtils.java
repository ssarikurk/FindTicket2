package org.cucumber.utilities;


import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.*;

public class AwsUtils {

    public static void executeCommandOnInstance(AWSSimpleSystemsManagement ssmClient, String instanceId, String filePath) {
        // Replace with your command to execute the .bat file
        String command = "cmd.exe /c start /wait "+filePath;

        // Create a request to execute the command
        SendCommandRequest sendCommandRequest = new SendCommandRequest()
                .withInstanceIds(instanceId)
                .withDocumentName("AWS-RunPowerShellScript")
                .withParameters(
                        new java.util.HashMap<String, java.util.List<String>>() {{
                            put("commands", java.util.Arrays.asList(command));
                        }}
                );

        SendCommandResult sendCommandResult = ssmClient.sendCommand(sendCommandRequest);
        String commandId = sendCommandResult.getCommand().getCommandId();

        // Monitor the command execution
        GetCommandInvocationRequest getCommandInvocationRequest = new GetCommandInvocationRequest()
                .withCommandId(commandId)
                .withInstanceId(instanceId);


        int maxRetries = 50;  // Set the maximum number of retries
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                GetCommandInvocationResult getCommandInvocationResult = ssmClient.getCommandInvocation(getCommandInvocationRequest);

                if (!getCommandInvocationResult.getStatus().equals("InProgress")) {
                    System.out.println("Command output: " + getCommandInvocationResult.getStandardOutputContent());
                    System.out.println("Command error: " + getCommandInvocationResult.getStandardErrorContent());
                    System.out.println("Command status: " + getCommandInvocationResult.getStatus());
                    BrowserUtils.waitFor(60);
                    break;
                }

                // Sleep for a while before retrying
                Thread.sleep(1000);
                retryCount++;
            } catch (InvocationDoesNotExistException e) {
                System.out.println("Invocation does not exist yet, retrying...");
                try {
                    Thread.sleep(2000); // Wait before retrying
                    retryCount++;
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if (retryCount >= maxRetries) {
                BrowserUtils.waitFor(30);
                System.out.println("Reached maximum retry limit. Command status could not be confirmed.");
            }
        }
    }
}





