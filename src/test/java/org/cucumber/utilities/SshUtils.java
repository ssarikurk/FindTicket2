package org.cucumber.utilities;


import com.amazonaws.AmazonServiceException;
import com.jcraft.jsch.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SshUtils {

    public static void sshJenkinsDockerAWS() {

        String privateKeyPath = BrowserUtils.getPath(".ssh/jenk.pem");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession(ConfigurationReader.get("awsUser"), ConfigurationReader.get("stagingJenkinsIP"), 22);

            // not recommend, uses jsch.setKnownHosts
            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand(
                    "sudo reboot"
                    );
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void sshJenkinsDockerAWSDev() {

        String privateKeyPath = BrowserUtils.getPath(".ssh/jenkins3.pem");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession(ConfigurationReader.get("awsUser"), ConfigurationReader.get("devJenkinsIP"), 22);

            // not recommend, uses jsch.setKnownHosts
            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand(
                    "sudo reboot"
            );
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }


    public static List<String> sftpGetFileName837(String sftpPath) {
        System.out.println("sftpPath = " + sftpPath);
        List<String> fileNameList = new ArrayList<>();

        Session jschSession = getSftpSession();

        try {
            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command

            Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("/" + sftpPath + "");
//            System.out.println("filelist.size() = " + filelist.size());

            String fileNameStr = "";
            for (ChannelSftp.LsEntry entry : filelist) {

                if (entry.getFilename().contains(".837")){
//                    System.out.println(filelist.get(i).toString());
                    fileNameStr = entry.getFilename();
//                    fileNameStr = fileNameStr.substring(fileNameStr.indexOf(".837")-19);
//                    System.out.println("fileNameStr = " + fileNameStr);
                    fileNameList.add(fileNameStr);
                }else if (entry.getFilename().contains(".835")) {
//                    System.out.println(filelist.get(i).toString());
                    fileNameStr = entry.getFilename();
//                    fileNameStr = fileNameStr.substring(fileNameStr.indexOf(".835") - 19);
                    System.out.println("fileNameStr = " + fileNameStr);
//                    fileNameList.add(fileNameStr);
                }else if (entry.getFilename().contains(".820")) {
//                    System.out.println(filelist.get(i).toString());
                    fileNameStr = entry.getFilename();
//                    fileNameStr = fileNameStr.substring(fileNameStr.indexOf(".820") - 40);
                    System.out.println("fileNameStr = " + fileNameStr);
//                    fileNameList.add(fileNameStr);
                }


            }
//            System.out.println("fileNameList.size() = " + fileNameList.size());
//            System.out.println("fileNameList = " + fileNameList);
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
//        System.out.println("Done");
        return fileNameList;
    }


    public static List<String> sftpGetFileName835() {

//        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");

        List<String> fileNameList = new ArrayList<>();
        Session jschSession = null;

        try {
            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command

            Vector filelist = channelSftp.ls("/generic-sftp/era_outbound/");
//            System.out.println("filelist.size() = " + filelist.size());
            String fileNameStr = "";
            for(int i=0; i<filelist.size();i++){
                if (filelist.get(i).toString().contains(".835")){
//                    System.out.println(filelist.get(i).toString());
                    fileNameStr = filelist.get(i).toString();
                    fileNameStr = fileNameStr.substring(fileNameStr.indexOf(".835")-19);
//                    System.out.println("fileNameStr = " + fileNameStr);
                    fileNameList.add(fileNameStr);
                }
            }
//            System.out.println("fileNameList.size() = " + fileNameList.size());
//            System.out.println("fileNameList = " + fileNameList);
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
//        System.out.println("Done");
        return fileNameList;
    }


    public static List<String> sftpGet835FileNameList(String sftpPath) {

        System.out.println("sftpPath = " + sftpPath);
        List<String> fileNameList = new ArrayList<>();

        Session jschSession = getSftpSession();

        try {
            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command

            Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("/" + sftpPath + "");
//            System.out.println("filelist.size() = " + filelist.size());
            String fileNameStr = "";
            for (ChannelSftp.LsEntry entry : filelist) {
                if (entry.getFilename().endsWith(".835")){
                    fileNameStr = entry.getFilename();
                    fileNameList.add(fileNameStr);
                }
            }
//            System.out.println("fileNameList.size() = " + fileNameList.size());
//            System.out.println("fileNameList = " + fileNameList);
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
//        System.out.println("Done");
        return fileNameList;
    }

    public static Vector<ChannelSftp.LsEntry> sftpGetEntryList(String sftpPath) {

        System.out.println("sftpPath = " + sftpPath);
        List<String> fileNameList = new ArrayList<>();

        Session jschSession = getSftpSession();
        Vector<ChannelSftp.LsEntry> filelist = null;
        try {
            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command

            filelist = channelSftp.ls("/" + sftpPath + "");
//            System.out.println("filelist.size() = " + filelist.size());
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
//        System.out.println("Done");
        return filelist;
    }

    public static void sftpDelete820FilesInFolder(String folderPath, String userSftp) {
        Session jschSession = getSftpSession(userSftp);

        try {
            Channel sftp = jschSession.openChannel("sftp");
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            channelSftp.cd(folderPath);

            // List all files in the directory
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls("*");
            for (ChannelSftp.LsEntry entry : fileList) {
                if (!entry.getAttrs().isDir()) {
                    LocalDate date820LocalDate = Instant.ofEpochSecond(entry.getAttrs().getATime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    LocalDate threeDaysBeforeLocalDate = LocalDate.now().minusDays(3);

                    if (date820LocalDate.isBefore(threeDaysBeforeLocalDate)) {
                        String fileName = entry.getFilename();
                        String filePath = folderPath + "/" + fileName;
                        System.out.println("Deleting file: " + filePath);
                        channelSftp.rm(filePath);
                        System.out.println("------------------------------------------------------------");
                    }
                }
            }
            channelSftp.exit();
            System.out.println("All files deleted successfully in folder: " + folderPath);
        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void sftpDeleteXDayOldRecords(String folderPath, String userSftp, int daysOld) {
        Session jschSession = getSftpSession(userSftp);

        try {
            Channel sftp = jschSession.openChannel("sftp");
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            channelSftp.cd(folderPath);

            // List all files in the directory
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls("*");
            LocalDateTime xDaysBeforeDateTime = LocalDateTime.now().minusDays(daysOld);

            for (ChannelSftp.LsEntry entry : fileList) {
//                System.out.println("entry = " + entry);
                if (!entry.getAttrs().isDir()) {
                    LocalDateTime fileDateTime = Instant.ofEpochSecond(entry.getAttrs().getATime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

//                    System.out.println("xDaysBeforeDateTime = " + xDaysBeforeDateTime);
//                    System.out.println("fileDateTime = " + fileDateTime);

                    if (fileDateTime.isBefore(xDaysBeforeDateTime)) {
                        String fileName = entry.getFilename();
                        String filePath = folderPath + "/" + fileName;
                        System.out.println("Deleting file: " + filePath);
                        channelSftp.rm(filePath);
                        System.out.println("------------------------------------------------------------");
                    }
                }
            }
            channelSftp.exit();
            System.out.println("All "+daysOld+" daysOld files deleted successfully in folder: " + folderPath);
        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static List<String> sftpGet820FileNameList(String sftpPath) {

        System.out.println("sftpPath = " + sftpPath);
        List<String> fileNameList = new ArrayList<>();

        Session jschSession = getSftpSession();

        try {
            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command

            Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("/" + sftpPath + "");
//            System.out.println("filelist.size() = " + filelist.size());
            String fileNameStr = "";
            for (ChannelSftp.LsEntry entry : filelist) {
                if (entry.getFilename().endsWith(".835")){
                    fileNameStr = entry.getFilename();
                    fileNameList.add(fileNameStr);
                }
            }
//            System.out.println("fileNameList.size() = " + fileNameList.size());
//            System.out.println("fileNameList = " + fileNameList);
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
//        System.out.println("Done");
        return fileNameList;
    }

    public static List<String> sftpGetFileNameNacha() {

//        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");

        List<String> fileNameList = new ArrayList<>();
        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("generic-sftp", "18.158.61.36", 22);

            // not recommend, uses jsch.setKnownHosts
            //jschSession.setConfig("StrictHostKeyChecking", "no");


            jschSession.setPassword("QwA!39JNuIW8T");

            // 10 seconds timeout session
            jschSession.connect(20000);

            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command

            Vector filelist = channelSftp.ls("/generic-sftp/nacha_inbox/");
//            System.out.println("filelist.size() = " + filelist.size());

            String fileNameStr = "";
            for(int i=0; i<filelist.size();i++){
                if (filelist.get(i).toString().contains(".txt")){
                    System.out.println(filelist.get(i).toString());
                    fileNameStr = filelist.get(i).toString();
                    fileNameStr = fileNameStr.substring(fileNameStr.indexOf(".txt")-21);
//                    System.out.println("fileNameStr = " + fileNameStr);
                    fileNameList.add(fileNameStr);
                }


            }
//            System.out.println("fileNameList.size() = " + fileNameList.size());
//            System.out.println("fileNameList = " + fileNameList);
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
//        System.out.println("Done");
        return fileNameList;
    }

    public static void sftpDownloadFileWOPath(String fileName) {

//        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        String downloadPath = BrowserUtils.getDownloadPath();

        Session jschSession = null;

        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("generic-sftp", "18.158.61.36", 22);

            // not recommend, uses jsch.setKnownHosts
            //jschSession.setConfig("StrictHostKeyChecking", "no");
            jschSession.setPassword("QwA!39JNuIW8T");

            // 10 seconds timeout session
            jschSession.connect(20000);

            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command
            channelSftp.get("/generic-sftp/pymt_outbound/"+fileName,downloadPath);

            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }
    private static Session getServerSessionWithPemFile (String ip){
        Session jschSession = null;

        // Path to your .pem file
        String pemFilePath = BrowserUtils.getPath(".ssh/aws4_us-east-1.pem");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");

        try {
            JSch jsch = new JSch();

            // Set known hosts file
            jsch.setKnownHosts(knownHostPath);

            // Add the .pem file as identity
            jsch.addIdentity(pemFilePath);

            // Create a session with the given username, IP, and port
            jschSession = jsch.getSession("ubuntu", ip, 22);

            // Recommended: explicitly set host key checking to "no" if known_hosts is not configured
            jschSession.setConfig("StrictHostKeyChecking", "no");

            // Connect to the server with a timeout of 20 seconds
            jschSession.connect(20000);
            System.out.println("Connected successfully to: " + ip);
        } catch (JSchException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
        }

        return jschSession;
    }

    private static Session getServerSessionWithKey (String ip){
        Session jschSession = null;
        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", ip, 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key
//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);
            // 10 seconds timeout session
            jschSession.connect(20000);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return jschSession;
    }


    private static Session getSftpSession (){
        Session jschSession = null;
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("synthetic-chi", "xx.xxx.xx.111", 22);

            // not recommend, uses jsch.setKnownHosts
            //jschSession.setConfig("StrictHostKeyChecking", "no");
            jschSession.setPassword("ret123");

            // 10 seconds timeout session
            jschSession.connect(20000);
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return jschSession;
    }

    private static Session getSftpSession (String userName){
        Session jschSession = null;
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession(userName, "xx.xxx.xx.111", 22);

            // not recommend, uses jsch.setKnownHosts
            //jschSession.setConfig("StrictHostKeyChecking", "no");
            jschSession.setPassword("ret123");

            // 10 seconds timeout session
            jschSession.connect(20000);
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return jschSession;
    }


    private static Session getMongoStagingSession (){

        Session jschSession = null;
        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("suleyman", "xx.xxx.xx.111", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key
            jschSession.setPassword("MSvT30qRe7NB");
            jsch.addIdentity(privateKeyPath);
            // 10 seconds timeout session
            jschSession.connect(20000);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return jschSession;


    }

    public static void sftpDeleteFilesInFolder(String folderPath) {
        Session jschSession = getSftpSession();

        try {
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Change to the specified directory
            channelSftp.cd(folderPath);

            // List all files in the directory
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls("*");
            for (ChannelSftp.LsEntry entry : fileList) {
                if (!entry.getAttrs().isDir()) {
                    String fileName = entry.getFilename();
                    String filePath = folderPath + "/" + fileName;
                    System.out.println("Deleting file: " + filePath);
                    channelSftp.rm(filePath);
                }
            }

            channelSftp.exit();
            System.out.println("All files deleted successfully in folder: " + folderPath);
        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void sftpDeleteFilesInFolder(String folderPath, String userSftp) {
        Session jschSession = getSftpSession(userSftp);

        try {
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Change to the specified directory
            channelSftp.cd(folderPath);

            // List all files in the directory
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls("*");
            for (ChannelSftp.LsEntry entry : fileList) {
                if (!entry.getAttrs().isDir()) {
                    String fileName = entry.getFilename();
                    String filePath = folderPath + "/" + fileName;
                    System.out.println("Deleting file: " + filePath);
                    channelSftp.rm(filePath);
                }
            }

            channelSftp.exit();
            System.out.println("All files deleted successfully in folder: " + folderPath);
        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void sftpDeleteFile(String folderPath, String fileName) {

        Session jschSession = getSftpSession();

        try {
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Delete the specified file

            String filePath = folderPath + "/" + fileName;
            channelSftp.rm(filePath);
            System.out.println(filePath + " deleted from sftp");
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }


    public static void sftpDownloadFile(String fileName, String downlodPath) {

        Session jschSession = getSftpSession();

        try {
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command
            channelSftp.get("/hsbc-chi/pymt_outbound/"+fileName,downlodPath);
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static String readFile(String filePath, String fileName) {

        Session jschSession = getSftpSession();
        String fileContent = "";
        ChannelSftp channelSftp = null;

        try {
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(10000);

            channelSftp = (ChannelSftp) sftp;
            // Run a command
//            channelSftp.exit();

            // Use ByteArrayOutputStream to capture the file content
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            channelSftp.get(filePath+ "/" + fileName, outputStream);

            fileContent = outputStream.toString();
            System.out.println("File content read successfully.");

        } catch (JSchException e) {
            System.err.println("JSchException occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (SftpException e) {
            System.err.println("SftpException occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.exit();
            }
            if (jschSession != null && jschSession.isConnected()) {
                jschSession.disconnect();
            }
        }
        return fileContent;
    }


    public static void sftpDownloadFile(String filePath, String fileName, String downlodPath) {

        Session jschSession = getSftpSession();
        try {
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command
//            channelSftp.exit();

            Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("/" + filePath + "");
//            System.out.println("filelist.size() = " + filelist.size());
//            System.out.println("filelist.toString() = " + filelist.toString());

            if (filelist.toString().contains(fileName)){
                String fullpath = filePath+"/"+fileName;
//                System.out.println("fullpath = " + fullpath);
                channelSftp.get(fullpath,downlodPath);
                System.out.println(fullpath + " --> downloaded to Downloads folder");
            } else System.out.println("file NOT found!!!!");

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }



    public static void sftpDownloadFile2(String filePath, String fileName, String downlodPath) {
        Session jschSession = getSftpSession();
        try {
            Channel sftp = jschSession.openChannel("sftp");
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
//            channelSftp.exit();

            Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("/" + filePath + "");
//            System.out.println("filelist.size() = " + filelist.size());
//            System.out.println("filelist.toString() = " + filelist.toString());

            for (ChannelSftp.LsEntry fileFromSftp : filelist) {
                String fileNameFromSftp = fileFromSftp.getFilename();
                if (fileNameFromSftp.contains(fileName)){
                    System.out.println("fileNameFromSftp = " + fileNameFromSftp);
                    String fullpath = filePath+"/"+fileNameFromSftp;
//                System.out.println("fullpath = " + fullpath);
                    channelSftp.get(fullpath,downlodPath);
                    System.out.println(fullpath + " --> downloaded to Downloads folder");
                }
            }


        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void download820FromSftp(String filePath, String fileName, String downlodPath, String collectionName) {

        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        Session jschSession = getSftpSession();
        try {
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command
//            channelSftp.exit();

            Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("/" + filePath + "");
//            System.out.println("filelist.size() = " + filelist.size());
//            System.out.println("filelist.toString() = " + filelist.toString());


            MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", collectionName);

            String filter = "{fileName820:'"+fileName+"'}";
            String update = "error";
            try {
                if (filelist.toString().contains(fileName)){
                    String fullpath = filePath+"/"+fileName;
//                System.out.println("fullpath = " + fullpath);
                    channelSftp.get(fullpath,downlodPath);
                    System.out.println(fullpath + " --> downloaded to Downloads folder");

                    String updateMessage = fileName+" downloded succesfully";
                    update = "{$set:{download820Success:'"+updateMessage+"'}}";
                }else {

                    update = "{$set:{download820Success:'failed'}}";
                }
                MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
            }

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void download820FromSftp2(String filePath, String fileName, String downlodPath, String collectionName, String databaseName) {

        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        Session jschSession = getSftpSession();
        try {
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command
//            channelSftp.exit();

            Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("/" + filePath + "");
//            System.out.println("filelist.size() = " + filelist.size());
//            System.out.println("filelist.toString() = " + filelist.toString());


            MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, databaseName, collectionName);

            String filter = "{fileName820:'"+fileName+"'}";
            String update = "error";
            try {
                if (filelist.toString().contains(fileName)){
                    String fullpath = filePath+"/"+fileName;
//                System.out.println("fullpath = " + fullpath);
                    channelSftp.get(fullpath,downlodPath);
                    System.out.println(fullpath + " --> downloaded to Downloads folder");

                    String updateMessage = fileName+" downloded succesfully";
                    update = "{$set:{download820Success:'"+updateMessage+"'}}";
                }else {

                    update = "{$set:{download820Success:'failed'}}";
                }
                MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
            }

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }


    public static void sftpListFiles() {

//        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("generic-sftp", "18.158.61.36", 22);

            // not recommend, uses jsch.setKnownHosts
            //jschSession.setConfig("StrictHostKeyChecking", "no");


            jschSession.setPassword("QwA!39JNuIW8T");

            // 10 seconds timeout session
            jschSession.connect(20000);

            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // Run a command
            Vector filelist = channelSftp.ls("/generic-sftp/inbound/");
            System.out.println("filelist.size() = " + filelist.size());
            for(int i=0; i<filelist.size();i++){
                System.out.println(filelist.get(i).toString());
            }
            System.out.println("inbound sftp = " + channelSftp.ls("/generic-sftp/inbound/"));
//            channelSftp.rm("/generic-sftp/era_outbound/2023012718016127373.835");

            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
        System.out.println("Done");
    }


    public static void syntheticClearingHouseandCheckClearingHouseftp() {
//        String privateKeyPath = "C:\\Users\\Administrator\\.ssh\\id_rsa";
//        String privateKeyPath = "src/test/resources/ssh/id_rsa";
//        String privateKeyPath = "/Users/ss/.ssh/id_rsa";
//        String pubKeyPath = "C:\\Users\\Administrator\\suleyman.pub";



        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");


        Session jschSession = null;

        try {

        JSch jsch = new JSch();
        jsch.setKnownHosts(knownHostPath);
        jschSession = jsch.getSession("ubuntu", "xxx.xxx.xxx.xx", 22);


        // not recommend, uses jsch.setKnownHosts
        //jschSession.setConfig("StrictHostKeyChecking", "no");

        // authenticate using private key
        jsch.addIdentity(privateKeyPath);

        // 10 seconds timeout session
        jschSession.connect(20000);

        ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

        // Run a command
        channelExec.setCommand("cd workspace &&" +
                "cd current &&" +
                "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat P0 --sts 3 &&" +
                "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc");
//        Thread.sleep(1000);

        // display errors to System.err
        channelExec.setErrStream(System.err);

        InputStream in = channelExec.getInputStream();

        // 5 seconds timeout channel
        channelExec.connect(5000);


        // read the result from remote server
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                System.out.print(new String(tmp, 0, i));
            }
            if (channelExec.isClosed()) {
                if (in.available() > 0) continue;
                System.out.println("exit-status: "
                        + channelExec.getExitStatus());
                break;
            }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
        }

        channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
        if (jschSession != null) {
            jschSession.disconnect();
        }
    }

  }

    public static void syntheticClearingHouseandCheckClearingHouseftpP0() {
//        String privateKeyPath = "C:\\Users\\Administrator\\.ssh\\id_rsa";
//        String privateKeyPath = "src/test/resources/ssh/id_rsa";
//        String privateKeyPath = "/Users/ss/.ssh/id_rsa";
//        String pubKeyPath = "C:\\Users\\Administrator\\suleyman.pub";



        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "xxx.xxx.xxx.xx", 22);

            // not recommend, uses jsch.setKnownHosts
            //jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat P0 --sts 3 &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc");
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }
    public static void syntheticClearingHouseandCheckClearingHouseftpStaging(String cat) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "xx.xxx.xx.111", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat "+cat+" --sts 1 --interface-id hsbcdev --interchange-name synthetic --interchange-env dev &&" +
                "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-cls5");
//                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc"
//            "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat "+cat+" --sts 3 &&" +
//                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc");
            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }
    public static void runMongoRestore(String script) {
        Session jschSession = getMongoStagingSession();

        try {
            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
            // Run a command
//            System.out.println("script = " + script);
            channelExec.setCommand("cd dump &&" +
//                    "cd current &&" +
                    "ls -la"
//                    + script
            );
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void runScripts(String script, String ip) {

//        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
//        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
////        System.out.println("privateKeyPath = " + privateKeyPath);
////        System.out.println("knownHostPath = " + knownHostPath);

        Session jschSession = getServerSessionWithKey(ip);

        try {
            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
            // Run a command
//            System.out.println("script = " + script);
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    script);
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void uploadFileToServer(String ip, List<String> remoteFileNameList, String uploadPath, String testCaseName) {
        Session jschSession = getServerSessionWithKey(ip);

        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        String update = "error";
        testCaseName = testCaseName.replace("_"," ");
        String filter = "{testCaseName:'"+testCaseName+"'}";
        MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", "eraCycleTestCases");
        try {
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

            try {
                // Create directory if it doesn't exist
                sftpChannel.mkdir(uploadPath);
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_FAILURE) {
                    System.out.println("Directory "+uploadPath+" already exists");
                } else {
                    e.printStackTrace();
                }
            }

            String downloadPath = BrowserUtils.getDownloadPath();
            for (String remoteFileName : remoteFileNameList) {
                String localFilePath = downloadPath+File.separator+remoteFileName;
                System.out.println("localFilePath = " + localFilePath);
                sftpChannel.put(localFilePath, uploadPath + "/" + remoteFileName);
                System.out.println("File uploaded to " + uploadPath + "/" + remoteFileName);
                String updateMessage = remoteFileName+" uploaded to server succesfully";
                update = "{$set:{upload820ToS3Success:'"+updateMessage+"'}}";

                MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
            }

            sftpChannel.disconnect();

        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            System.out.println("------------------------- !!!File Upload ERROR!!! ------------------------");
            e.printStackTrace();

            update = "{$set:{upload820ToS3Success:'failed'}}";
            MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }




    public static void uploadFileToServer2(String ip, List<String> remoteFileNameList, String uploadPath, String testCaseName, String databaseName) {
        Session jschSession = getServerSessionWithKey(ip);

        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        String update = "error";
        testCaseName = testCaseName.replace("_"," ");
        String filter = "{testCaseName:'"+testCaseName+"'}";
        MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, databaseName, "eraCycleTestCases");
        try {
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

            try {
                // Create directory if it doesn't exist
                sftpChannel.mkdir(uploadPath);
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_FAILURE) {
                    System.out.println("Directory "+uploadPath+" already exists");
                } else {
                    e.printStackTrace();
                }
            }

            String downloadPath = BrowserUtils.getDownloadPath();
            for (String remoteFileName : remoteFileNameList) {
                String localFilePath = downloadPath+File.separator+remoteFileName;
                System.out.println("localFilePath = " + localFilePath);
                sftpChannel.put(localFilePath, uploadPath + "/" + remoteFileName);
                System.out.println("File uploaded to " + uploadPath + "/" + remoteFileName);
                String updateMessage = remoteFileName+" uploaded to server succesfully";
                update = "{$set:{upload820ToS3Success:'"+updateMessage+"'}}";

                MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
            }

            sftpChannel.disconnect();

        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            System.out.println("------------------------- !!!File Upload ERROR!!! ------------------------");
            e.printStackTrace();

            update = "{$set:{upload820ToS3Success:'failed'}}";
            MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void uploadFileToServerFromProjectPath(String ip, List<String> remoteFileNameList, String uploadPath, String filePath) {
        Session jschSession = getServerSessionWithKey(ip);

        try {
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

            try {
                // Create directory if it doesn't exist
                sftpChannel.mkdir(uploadPath);
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_FAILURE) {
                    System.out.println("Directory "+uploadPath+" already exists");
                } else {
                    e.printStackTrace();
                }
            }


            for (String remoteFileName : remoteFileNameList) {
                String pathFrom = filePath+File.separator+remoteFileName;
                System.out.println("FilePath = " + pathFrom);
                sftpChannel.put(pathFrom, uploadPath + "/" + remoteFileName);
                System.out.println("File uploaded to " + uploadPath + "/" + remoteFileName);
            }

            sftpChannel.disconnect();

        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void uploadFileToServerFromProjectPath2CvEnv(String fileName, String pathFrom, String ip) {
        Session jschSession = getServerSessionWithPemFile(ip);

        try {
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

                System.out.println("FilePath = " + pathFrom);
                sftpChannel.put(pathFrom,   "/tmp/" + fileName);
                System.out.println("File uploaded to " + "/tmp/" + fileName);


            sftpChannel.disconnect();

        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void uploadFileToServerFromProjectPath2(String fileName, String pathFrom, String ip) {
        Session jschSession = getServerSessionWithKey(ip);

        try {
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

            System.out.println("FilePath = " + pathFrom);
            sftpChannel.put(pathFrom,   "/tmp/" + fileName);
            System.out.println("File uploaded to " + "/tmp/" + fileName);


            sftpChannel.disconnect();

        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void uploadFileToServerFromProjectPath3(List<String> remoteFileNameList, String pathFrom, String ip) {
        Session jschSession = getServerSessionWithKey(ip);

        try {
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

            for (String remoteFileName : remoteFileNameList) {
                String pathFromFull = pathFrom+File.separator+remoteFileName;
                System.out.println("FilePath = " + pathFromFull);
                sftpChannel.put(pathFromFull,    "/tmp/" + remoteFileName);
                System.out.println("File uploaded to "+"/tmp/" +remoteFileName);
            }
            sftpChannel.disconnect();
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void deleteFileFromServer(String ip, String remoteFileName, String remoteFilePath) {

        Session jschSession = getServerSessionWithKey(ip);

        try {
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

            try {
                // Delete the file from the remote server
                sftpChannel.rm(remoteFilePath + "/" + remoteFileName);
                System.out.println("File " + remoteFileName + " deleted from " + remoteFilePath);
            } catch (SftpException e) {
                System.out.println("Failed to delete file " + remoteFileName + " from " + remoteFilePath);
                e.printStackTrace();
            }

            // Disconnect the SFTP channel
            sftpChannel.disconnect();

        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            // Ensure the session is disconnected
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public static void deleteFileFromSftp( String remoteFileName, String remoteFilePath) {

        Session jschSession = getSftpSession();

        try {
            // Open SFTP channel
            Channel sftp = jschSession.openChannel("sftp");
            // Connect to the SFTP server with a 10-second timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            // Delete the file from the remote server
            channelSftp.rm(remoteFilePath+ "/" + remoteFileName);
            System.out.println("File ---" + remoteFileName + "--- deleted from "+remoteFilePath);

            // Exit the SFTP channel
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace(); // Print any exceptions that occur
        } finally {
            if (jschSession != null) {
                jschSession.disconnect(); // Ensure the session is disconnected
            }
        }

    }


    public static void uploadFileToSftp2( String filePathFrom, String fileName, String filePathToUpload) {

        Session jschSession = getSftpSession();
        String fromFilePath = filePathFrom+File.separator+fileName;
        String toFilePath = filePathToUpload+File.separator+fileName;

        try {
            // Open SFTP channel
            Channel sftp = jschSession.openChannel("sftp");
            // Connect to the SFTP server with a 10-second timeout
            sftp.connect(10000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            channelSftp.put(fromFilePath,toFilePath);
            System.out.println("File ---" + fileName + "--- uploaded to "+filePathToUpload);

            // Exit the SFTP channel
            channelSftp.exit();

        } catch (SftpException | JSchException e) {
            e.printStackTrace(); // Print any exceptions that occur
        } finally {
            if (jschSession != null) {
                jschSession.disconnect(); // Ensure the session is disconnected
            }
        }


//
//
//        try {
//            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
//            sftpChannel.connect(10000);
//
//            try {
//                // Delete the file from the remote server
//                sftpChannel.rm(remoteFilePath + "/" + remoteFileName);
//                System.out.println("File " + remoteFileName + " deleted from " + remoteFilePath);
//            } catch (SftpException e) {
//                System.out.println("Failed to delete file " + remoteFileName + " from " + remoteFilePath);
//                e.printStackTrace();
//            }
//
//            // Disconnect the SFTP channel
//            sftpChannel.disconnect();
//
//        } catch (JSchException e) {
//            e.printStackTrace();
//        } finally {
//            // Ensure the session is disconnected
//            if (jschSession != null) {
//                jschSession.disconnect();
//            }
//        }
    }

    public static void uploadFileToSftp(String filePathFrom, String fileName, String filePathToUpload) {

        Session jschSession = getSftpSession();
        String fromFilePath = filePathFrom + File.separator + fileName;
        String toFilePath = filePathToUpload + "/" + fileName;

        try {
            System.out.println("Uploading from: " + fromFilePath + " to: " + toFilePath);
            if (!new File(fromFilePath).exists()) {
                System.out.println("Local file does not exist: " + fromFilePath);
                return;
            }

            // Open SFTP channel
            Channel sftp = jschSession.openChannel("sftp");
            sftp.connect(10000);
            System.out.println("SFTP channel connected successfully.");

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            try {
                // Upload the file
                channelSftp.put(fromFilePath, toFilePath);
                System.out.println("File ---" + fileName + "--- uploaded to " + filePathToUpload);
            } catch (SftpException e) {
                System.err.println("Failed to upload file: " + e.getMessage());
            }

            // Exit the SFTP channel
            channelSftp.exit();

        } catch (Exception e) {
            e.printStackTrace(); // Print any exceptions that occur
        } finally {
            if (jschSession != null) {
                jschSession.disconnect(); // Ensure the session is disconnected
            }
        }
    }

    public static void uploadFileToSftp(String filePathFrom, String fileName, String filePathToUpload, String sftpUsername) {

        Session jschSession = getSftpSession(sftpUsername);
        String fromFilePath = filePathFrom + File.separator + fileName;
        String toFilePath = filePathToUpload + "/" + fileName;

        try {
            System.out.println("Uploading from: " + fromFilePath + " to: " + toFilePath);
            if (!new File(fromFilePath).exists()) {
                System.out.println("Local file does not exist: " + fromFilePath);
                return;
            }

            // Open SFTP channel
            Channel sftp = jschSession.openChannel("sftp");
            sftp.connect(10000);
            System.out.println("SFTP channel connected successfully.");

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            try {
                // Upload the file
                channelSftp.put(fromFilePath, toFilePath);
                System.out.println("File ---" + fileName + "--- uploaded to " + filePathToUpload);
            } catch (SftpException e) {
                System.err.println("Failed to upload file: " + e.getMessage());
            }

            // Exit the SFTP channel
            channelSftp.exit();

        } catch (Exception e) {
            e.printStackTrace(); // Print any exceptions that occur
        } finally {
            if (jschSession != null) {
                jschSession.disconnect(); // Ensure the session is disconnected
            }
        }
    }



    public static void emptyDirectoryOnServer(String ip, String uploadPath) {
        Session jschSession = getServerSessionWithKey(ip);

        try {
            // Open an SFTP channel to interact with the remote server
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

            // Check if the directory contains files and delete them
            try {
                sftpChannel.cd(uploadPath);
                sftpChannel.ls(uploadPath).forEach(item -> {
                    if (item instanceof ChannelSftp.LsEntry) {
                        String fileName = ((ChannelSftp.LsEntry) item).getFilename();
                        if (!fileName.equals(".") && !fileName.equals("..")) {
                            try {
                                sftpChannel.rm(uploadPath + "/" + fileName);
                                System.out.println("Deleted from Server("+ip+") path: "+ uploadPath + "/" + fileName);
                            } catch (SftpException se) {
                                se.printStackTrace();
                            }
                        }
                    }
                });
            } catch (SftpException e) {
                e.printStackTrace();
            }

            // Close the SFTP channel after upload
            sftpChannel.disconnect();

        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }


    public static void emptyDirectoryOnServerEndWith(String ip, String uploadPath, String ensWithStr) {
        Session jschSession = getServerSessionWithKey(ip);

        try {
            // Open an SFTP channel to interact with the remote server
            ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
            sftpChannel.connect();

            // Check if the directory contains files and delete them
            try {
                sftpChannel.cd(uploadPath);
                sftpChannel.ls(uploadPath).forEach(item -> {
                    if (item instanceof ChannelSftp.LsEntry) {
                        String fileName = ((ChannelSftp.LsEntry) item).getFilename();
                        System.out.println("fileName = " + fileName);
                        if (!fileName.endsWith(ensWithStr)) {
                            try {
                                sftpChannel.rm(uploadPath + "/" + fileName);
                                System.out.println("Deleted: " + fileName);
                            } catch (SftpException se) {
                                se.printStackTrace();
                            }
                        }
                    }
                });
            } catch (SftpException e) {
                e.printStackTrace();
            }

            // Close the SFTP channel after upload
            sftpChannel.disconnect();

        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }


    public static void runScriptsLogToFile2(String script, String ip, String logName, String testcasesDB) {

        // Create the log file path
        String logFilePath = "src/test/resources/Logs/"+logName+".log";
        File logFile = new File(logFilePath);

        // Delete old log file if it exists
        if (logFile.exists()) {
            logFile.delete();
        }

        // Create a new log file and initialize the writer
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFilePath, true))) {

            Session jschSession = getServerSessionWithKey(ip);

            try {
                ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
                channelExec.setCommand("cd workspace && cd current && " + script);

                // Redirect errors to the log file
                ByteArrayOutputStream errStream = new ByteArrayOutputStream();
                channelExec.setErrStream(errStream);

                InputStream in = channelExec.getInputStream();

                // 5 seconds timeout for channel
                channelExec.connect(5000);


                // Read the result from the remote server
                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) break;
                        String output = new String(tmp, 0, i);
                        logWriter.write(output);
                        logWriter.flush();  // Write to file immediately
                    }
                    if (channelExec.isClosed()) {
                        if (in.available() > 0) continue;

                        int exitStatus = channelExec.getExitStatus();
                        System.out.println("Exit status (if==0 means Success): " + exitStatus);

                        logWriter.write("exit-status: " + exitStatus + "\n");
                        logWriter.flush();
                        break;
                    }
                    BrowserUtils.waitFor(1);
                }

                // Log error stream if there are errors
                if (errStream.size() > 0) {
                    logWriter.write("Error: " + errStream.toString() + "\n");
                }

                channelExec.disconnect();

            } catch (JSchException | IOException e) {
                logWriter.write("Exception: " + e.getMessage() + "\n");
                e.printStackTrace();
            } finally {
                if (jschSession != null) {
                    jschSession.disconnect();
                }
                MongoDBUtils.setSingletonSettings(false,logName,script,testcasesDB); //to release settings change for next script run.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int runScriptsLogToFile(String script, String ip, String logName, String singletonDb) {

        // Create the log file path
        System.out.println("logName = " + logName);
        String logFilePath = "src/test/resources/Logs/"+logName+".log";
        File logFile = new File(logFilePath);
        int exitStatus = 9999999;
        // Delete old log file if it exists
        if (logFile.exists()) {
            logFile.delete();
        }

        // Create a new log file and initialize the writer
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFilePath, true))) {

            Session jschSession = getServerSessionWithKey(ip);

            try {
                ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
                channelExec.setCommand("cd workspace && cd current && " + script);

                // Redirect errors to the log file
                ByteArrayOutputStream errStream = new ByteArrayOutputStream();
                channelExec.setErrStream(errStream);

                InputStream in = channelExec.getInputStream();

                // 5 seconds timeout for channel
                channelExec.connect(5000);


                // Read the result from the remote server
                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) break;
                        String output = new String(tmp, 0, i);
                        logWriter.write(output);
                        logWriter.flush();  // Write to file immediately
                    }
                    if (channelExec.isClosed()) {
                        if (in.available() > 0) continue;

                        exitStatus = channelExec.getExitStatus();
                        System.out.println("Exit status (if==0 means Success): " + exitStatus);

                        logWriter.write("exit-status: " + exitStatus + "\n");
                        logWriter.flush();
                        break;
                    }
                    BrowserUtils.waitFor(1);
                }

                // Log error stream if there are errors
                if (errStream.size() > 0) {
                    logWriter.write("Error: " + errStream.toString() + "\n");
                }

                channelExec.disconnect();

            } catch (JSchException | IOException e) {
                logWriter.write("Exception: " + e.getMessage() + "\n");
                e.printStackTrace();
            } finally {
                if (jschSession != null) {
                    jschSession.disconnect();
                }
                MongoDBUtils.setSingletonSettings(false,logName,script,singletonDb); //to release settings change for next script run.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return exitStatus;
    }

    public static int runScriptsLogToFileCvEnv(String script, String ip, String logName, String singletonDb) {

        // Create the log file path
        System.out.println("logName = " + logName);
        String logFilePath = "src/test/resources/Logs/"+logName+".log";
        File logFile = new File(logFilePath);
        int exitStatus = 9999999;
        // Delete old log file if it exists
        if (logFile.exists()) {
            logFile.delete();
        }

        // Create a new log file and initialize the writer
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFilePath, true))) {

            Session jschSession = getServerSessionWithPemFile(ip);

            try {
                ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
                channelExec.setCommand("cd workspace && cd current && " + script);

                // Redirect errors to the log file
                ByteArrayOutputStream errStream = new ByteArrayOutputStream();
                channelExec.setErrStream(errStream);

                InputStream in = channelExec.getInputStream();

                // 5 seconds timeout for channel
                channelExec.connect(5000);


                // Read the result from the remote server
                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) break;
                        String output = new String(tmp, 0, i);
                        logWriter.write(output);
                        logWriter.flush();  // Write to file immediately
                    }
                    if (channelExec.isClosed()) {
                        if (in.available() > 0) continue;

                        exitStatus = channelExec.getExitStatus();
                        System.out.println("Exit status (if==0 means Success): " + exitStatus);

                        logWriter.write("exit-status: " + exitStatus + "\n");
                        logWriter.flush();
                        break;
                    }
                    BrowserUtils.waitFor(1);
                }

                // Log error stream if there are errors
                if (errStream.size() > 0) {
                    logWriter.write("Error: " + errStream.toString() + "\n");
                }

                channelExec.disconnect();

            } catch (JSchException | IOException e) {
                logWriter.write("Exception: " + e.getMessage() + "\n");
                e.printStackTrace();
            } finally {
                if (jschSession != null) {
                    jschSession.disconnect();
                }
                MongoDBUtils.setSingletonSettings(false,logName,script,singletonDb); //to release settings change for next script run.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return exitStatus;
    }

    public static void runScriptsonCvEnv(String script, String ip, String appVersion) {
        Session jschSession = null;
        String privateKeyPath = BrowserUtils.getPath(".ssh/aws4_us-east-1.pem");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", ip, 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key
//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);
            // 10 seconds timeout session
            jschSession.connect(20000);
        } catch (JSchException e) {
            e.printStackTrace();
        }

        try {
            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
            // Run a command
//            System.out.println("script = " + script);
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
//                    "cd "+appVersion+" &&" +
                    script);
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }


    }
    public static void runScripts(String script, String ip, String appVersion) {


        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);

        Session jschSession = getServerSessionWithKey(ip);

        try {
            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
            // Run a command
//            System.out.println("script = " + script);
            channelExec.setCommand("cd workspace &&" +
//                    "cd current &&" +
                    "cd "+appVersion+" &&" +
                    script);
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void runDeletePartnerS3 (){
        runScripts("node ./background/delete-s3-buckets.js", "xxx.xx.xx.xx", "v1.0.0" );
    }

    public static void runNachaScript() {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/process-nacha-files.js --source SVB --env dev --partner-id qa-clc");
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }


    public static void executeScheduledTasks(String practice) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "node ./background/execute-scheduled-tasks --partner xxxxxxxx --practice "+practice );
            
                    channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }


    public static void syntheticClearingHouseandCheckClearingHouseftpR0() {
//        String privateKeyPath = "C:\\Users\\Administrator\\.ssh\\id_rsa";
//        String privateKeyPath = "src/test/resources/ssh/id_rsa";
//        String privateKeyPath = "/Users/ss/.ssh/id_rsa";
//        String pubKeyPath = "C:\\Users\\Administrator\\suleyman.pub";



        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
            //jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat R0 --sts 3 &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc");
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void syntheticClearingHouseandCheckClearingHouseftpF0() {
//        String privateKeyPath = "C:\\Users\\Administrator\\.ssh\\id_rsa";
//        String privateKeyPath = "src/test/resources/ssh/id_rsa";
//        String privateKeyPath = "/Users/ss/.ssh/id_rsa";
//        String pubKeyPath = "C:\\Users\\Administrator\\suleyman.pub";



        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "xx.xxx.xxx.x", 22);

            // not recommend, uses jsch.setKnownHosts
            //jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat F0 --sts 3 &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc");
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void runAutoClaimCreateScript() {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);

        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "xxx.xx.xx.xx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "node ./background/auto-create.js --partner-id xxxxxxxx --user-email suleyman@mail.ai");
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void runAutoEligibilityScript(String partner, String practice) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "node background/execute-auto-eligibility.js --partner "+partner+" --practice "+practice);
//                    "node background/execute-auto-eligibility.js --partner all-chi-api --practice "+practice);

            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void runAutoEligibilityScript(String practice) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node background/execute-auto-eligibility.js --partner xxxxxxxxx --practice "+practice);
//                    "node background/execute-auto-eligibility.js --partner all-chi-api --practice "+practice);

            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void runERAQualifyScript(String partner, String data) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            String command = "node background/qualify-era-split-file-assigned --partner-id "+partner+" --era-split-file-assigned-id "+data+" --ignore-status --ignore-claim-payment-id";
            System.out.println("command = " + command);
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    command);
//                    "node background/execute-auto-eligibility.js --partner all-chi-api --practice "+practice);

            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }


    public static void runAutoClaimSubmitScript() {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node background/auto-submit-claims.js --partner-id xxxxxxxxx --practice-id xxxxxxx --user-email suleyman+super@xxxxxx.ai");
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }
    public static void runTerminalCommand(String ip) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", ip, 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node background/auto-submit-claims.js --partner-id \"xxxxxxx\" --practice-id \"xxxxxxxx\" --user-email \"suleyman@mail.ai\"");
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void runAutoClaimCreateScript(String script) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);

        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    script);
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }
    public static void runAutoClaimSubmitScript(String script) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    script);
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void syntheticClearingHouseandCheckClearingHouseftpStagingwithtransactions(String cat) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "xx.xxx.xx.111", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat "+cat+" --sts 1 --interface-id hsbcdev --interchange-name synthetic --interchange-env dev &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-cls5 --transaction 999 &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-cls5 --transaction 277");
            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void syntheticClearingHouseScript(String cat) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat "+cat+" --sts 1 --interface-id hsbcdev --interchange-name synthetic --interchange-env dev");
//                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc"
//            "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat "+cat+" --sts 3 &&" +
//                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc");
            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }


    public static void syntheticClearingHouseandCheckClearingHouseftpStagingTrans277() {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-cls5 --transaction 277");
//                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc"
//            "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/synthetic-clearinghouse.js --cat "+cat+" --sts 3 &&" +
//                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-clc");
            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void syntheticClearingHouseandCheckClearingHouseftpStagingTrans999() {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-cls5 --transaction 999");

            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void syntheticClearingHouseandCheckClearingHouseftpStagingTrans835() {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-cls5 --transaction 835");

            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }
    public static void runScriptCrawler(String ip,String script) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
//        System.out.println("privateKeyPath = " + privateKeyPath);
//        System.out.println("knownHostPath = " + knownHostPath);

        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", ip, 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    script);
//        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }


    public static void processHelpTicket(String partner) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    "/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/process-help-tickets.js --partner-id all-chi-api");

            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }


    public static void checkClearingHouseForOne277(String fileName) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            String command3="/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/checkClearinghouseFtp.js --clearinghouse synthetic --env dev --partner-id qa-retest --file "+fileName;
            System.out.println("command3 = " + command3);
            // Run a command
            channelExec.setCommand("cd workspace &&" +
                    "cd current &&" +
                    command3);
//
            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void reissueWritebackRecords(String partnerid,String practiceid, String category) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            String command="/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node ./background/reissue-writeback-records.js --partner "+partnerid+" --practice "
                    +practiceid+" --category "+category;
            System.out.println("command = " + command);
            // Run a command
            channelExec.setCommand("cd workspace &&" + "cd current &&" + command);
//
            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }


    public static void revertERAChanges(String partnerid,String trn) {

        String privateKeyPath = BrowserUtils.getPath(".ssh/id_rsa");
        String knownHostPath = BrowserUtils.getPath(".ssh/known_hosts");
        System.out.println("privateKeyPath = " + privateKeyPath);
        System.out.println("knownHostPath = " + knownHostPath);


        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostPath);
            jschSession = jsch.getSession("ubuntu", "10.xxx.xx.xxx", 22);

            // not recommend, uses jsch.setKnownHosts
//            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using private key

//            jschSession.setPassword("ret@123");
            jsch.addIdentity(privateKeyPath);

            // 10 seconds timeout session
            jschSession.connect(20000);

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");

            String command="/home/ubuntu/.nvm/versions/node/v20.10.0/bin/node batch/revertERAChanges.js --partner "+partnerid+" --trn "+trn;

            System.out.println("command = " + command);
            //node batch/revertERAChanges.js --partner pi-all-dev-test-b --trn 151718462250202 1> revert.1.logs 2> revert.2.errors
            // Run a command
            channelExec.setCommand("cd workspace &&" + "cd current &&" + command);
//
            //        Thread.sleep(1000);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();

            // 5 seconds timeout channel
            channelExec.connect(5000);


            // read the result from remote server
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: "
                            + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channelExec.disconnect();

        } catch (JSchException | IOException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }




}