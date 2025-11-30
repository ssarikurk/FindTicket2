package org.cucumber.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileUtils {

    public static void copyFile(String sourceFilePath, String destinationDirectoryPath, String fileName) {

        /* sample file paths below
        sourceFilePath = "C:\\Users\\Administrator\\cucumber\\fullstack\\ret-fullstack\\assets\\crosswalks\\semantic_payer_crosswalk_eligibility.csv";
        destinationDirectoryPath= "C:\\Users\\Administrator\\IdeaProjects\\ret-QA-automation-scripts\\src\\test\\resources\\Downloads\\crosswalk";
        fileName = "semantic_payer_crosswalk_eligibility.csv";
        */

        File sourceFile = new File(sourceFilePath);
        File destinationDirectory = new File(destinationDirectoryPath);

        // Check if the source file exists
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.out.println("Source file does not exist or is not a file.");
            return;
        }

        // Check if the destination directory exists
        if (!destinationDirectory.exists() || !destinationDirectory.isDirectory()) {
            System.out.println("Destination directory does not exist or is not a directory.");
            return;
        }

        // Create a Path object for the destination file
        Path destinationFilePath = destinationDirectory.toPath().resolve(fileName);

        try {
            // Check if the file exists in the destination directory
            if (Files.exists(destinationFilePath)) {
                // Delete the existing file
                Files.delete(destinationFilePath);
            }

            // Copy the file to the destination directory
            Files.copy(sourceFile.toPath(), destinationFilePath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File copied successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFileEndsWith(String endsWithStr, String directoryToDelete){
        File[] files = new File(directoryToDelete).listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if ((file.isFile() && fileName.endsWith(endsWithStr))) {
                file.delete();
                System.out.println(fileName + " deleted from " +directoryToDelete);
            }
        }
    }

    public static String readLogContent(String filePath) {
        String content = "";
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
            content = contentBuilder.toString();
//            System.out.println("File content: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static int countOccurrences(String content, String occurrent) {
        int count = 0;
        int index = content.indexOf(occurrent);
        while (index != -1) {
            count++;
            // Move to the next occurrence
            index = content.indexOf(occurrent, index + 1);
        }
        return count;
    }

    public static String getLineData(String content, String keyword) {
        String lineStr = "";
        int index = content.indexOf(keyword);
        if (index != -1) {
            int lastIndex = content.indexOf("~",index+3);
            lineStr = content.substring(index, lastIndex+1);
        }
        return lineStr;
    }

    public static String getKeyword(String keyword) {

        String userName = System.getProperty("user.name");
        String systemType = System.getProperty("os.name").toLowerCase();
//        System.out.println("systemType = " + systemType);
//        System.out.println("System.getProperty(\"user.home\") = " + System.getProperty("user.home"));
        String newKeyword = keyword;
        if(systemType.contains("linux")&&userName.equals("jenkins")){
            newKeyword = keyword.replace("\r\n","\n");
        }
//        System.out.println("newKeyword = " + newKeyword);
        return newKeyword;
    }

    public static String getX12SegmentData(String content, String segment, int segmentNum) {
//        String lineDataStr = getLineData(content,"~\r\n"+segment+"*");
        String lineDataStr = getLineData(content,segment+"*");
//        System.out.println("lineDataStr = " + lineDataStr);
        String[] array = lineDataStr.replace("~", "").split("\\*");
        List<String> lineDataStrList  = Arrays.asList(array);
        String segmentData = lineDataStrList.get(segmentNum);
//        System.out.println("segmentData = " + segmentData);
        return segmentData;
    }


    public static String getSegmentData(String segmentContent, int segmentNum) {
        String[] array = segmentContent.replace("~", "").split("\\*");
        List<String> lineDataStrList  = Arrays.asList(array);
        String segmentData = lineDataStrList.get(segmentNum);
//        System.out.println("segmentData = " + segmentData);
        return segmentData;
    }

    public static List<String> lineToList (String content, String segment) {
//        String lineDataStr = getLineData(content,"~\r\n"+segment+"*");
        String lineDataStr = getLineData(content,segment+"*");
        System.out.println("lineDataStr = " + lineDataStr);
        String[] array = lineDataStr.replace("~", "").split("\\*");
        List<String> lineDataStrList  = Arrays.asList(array);
        return lineDataStrList;
    }

    public static List<String> collectLineData2(String content, String segment) {
        List<String> lineDataList = new ArrayList<>();

//        String keywordFull = "~\r\n"+segment+"*";
        String keywordFull = segment+"*";
//        System.out.println("content = " + content);
        int index = content.indexOf(keywordFull);
        while (index != -1) {
            // Move to the next occurrence
//            System.out.println("index = " + index);
            int lastIndex = content.indexOf("~",index+3);
//            System.out.println("lastIndex = " + lastIndex);
            String st = content.substring(index, lastIndex+1);
//            System.out.println("st = " + st);
//            System.out.println("st.length() = " + st.length());
            lineDataList.add(st);
            content = content.replace(st,"");
            index = content.indexOf(keywordFull, index + 1);
        }
        return lineDataList;
    }

    public static List<String> collectLineData(String content, String keyword) {
        List<String> lineDataList = new ArrayList<>();
        String userName = System.getProperty("user.name");
        System.out.println("userName = " + userName);
//        System.out.println("content = " + content);
        int index = content.indexOf(keyword);
        while (index != -1) {
            // Move to the next occurrence
//            System.out.println("index = " + index);

            int lastIndex = content.indexOf("~",index+3);

            String st = content.substring(index+3, lastIndex+1);
            if (userName.equals("jenkins")){
                st = content.substring(index+2, lastIndex+1);
            }
//            System.out.println("lastIndex = " + lastIndex);
//            System.out.println("st = " + st);
//            System.out.println("st.length() = " + st.length());
            lineDataList.add(st);
            content = content.replace(st,"");
            index = content.indexOf(keyword, index + 1);
        }
        return lineDataList;
    }
    public static List<String> collectClpDataList(String content, String keyword) {
        List<String> lineDataList = new ArrayList<>();

//        System.out.println("content = " + content);
        int index = content.indexOf(keyword);
        while (index != -1) {
            // Move to the next occurrence
//            System.out.println("index = " + index);
            int lastIndex = content.indexOf("REF*1L",index+3);
//            System.out.println("lastIndex = " + lastIndex);
            String st = content.substring(index, lastIndex);
//            System.out.println("st = " + st);
//            System.out.println("st.length() = " + st.length());
            lineDataList.add(st);
            content = content.replace(st,"");
            index = content.indexOf(keyword, index + 1);
        }
        return lineDataList;
    }

    public static List<String> getStSeSegmentList (String content, String x12Type) {
        List<String> lineDataList = new ArrayList<>();

//        String keywordFull = "~\r\n*";
        String keywordFull = "ST*"+x12Type;
//        System.out.println("content = " + content);
        int index = content.indexOf(keywordFull);
        while (index != -1) {
            // Move to the next occurrence
//            System.out.println("index = " + index);
            int beginIndex = content.indexOf(keywordFull);
            int seIndex = content.indexOf(FileUtils.getKeyword("SE*"), beginIndex);
            int lastIndex = content.indexOf("~", seIndex + 1);

//            int lastIndex = content.indexOf("~",index+3);
//            System.out.println("lastIndex = " + lastIndex);
            String st = content.substring(index, lastIndex+1);
//            System.out.println("st = " + st);
//            System.out.println("st.length() = " + st.length());
            lineDataList.add(st);
            content = content.replace(st,"");
            index = content.indexOf(keywordFull, index + 1);
        }
        return lineDataList;
    }

    public static List<String> get277StSeSegmentList(String content, String x12Type) {
        List<String> lineDataList = new ArrayList<>();

        String keywordFull = "~ST*" + x12Type;
        int index = content.indexOf(keywordFull);

        while (index != -1) {
            // Find the beginning of the segment
            int beginIndex = content.indexOf(keywordFull, index);
            if (beginIndex == -1) break;

            // Find the ending segment "~SE*" and then "~" that follows
            int seIndex = content.indexOf(FileUtils.getKeyword("~SE*"), beginIndex);
            if (seIndex == -1) break; // Ensure we find "~SE*" in the content

            int lastIndex = content.indexOf("~", seIndex + 1);
            if (lastIndex == -1) lastIndex = content.length(); // If no "~", we assume the end of the content

            // Extract the segment data
            String st = content.substring(beginIndex, lastIndex+1);
            lineDataList.add(st);

            // Move index forward to continue searching for the next occurrence
            index = lastIndex;
        }
        return lineDataList;
    }


    public static List<String> getRecurringDataList(String content, String startKey, int segmentNum){
        List<String> checkNumList = new ArrayList<>();
        List<String> trnSegmentList = getRecurringSegmentList(content, startKey);
//        System.out.println("trnSegmentList.size() = " + trnSegmentList.size());
//        int countTrnSegment = 0;
        for (String segmentTrn : trnSegmentList) {
//                countTrnSegment++;
            String checkNum = getX12SegmentData(segmentTrn, startKey, segmentNum);

            checkNumList.add(checkNum);
        }

        return checkNumList;
    }

    public static List<String> getRecurringSegmentList(String content, String startKey) {
        content = content.replace("\r\n","").replace("\n","");
        List<String> lineDataList = new ArrayList<>();
        String keywordFull = "~"+startKey + "*";
//        System.out.println("keywordFull = " + keywordFull);
        int index = content.indexOf(keywordFull);
//        System.out.println("index = " + index);

        if (index == -1) {
            return lineDataList; // No occurrences found
        }
//        System.out.println("content = " + content);

        while (index != -1) {
            int beginIndex = content.indexOf(keywordFull, index);
//            System.out.println("beginIndex = " + beginIndex);
            if (beginIndex == -1) break;

            // Find the next occurrence
            int nextIndex = content.indexOf(keywordFull, beginIndex + keywordFull.length());

            int lastIndex;
            if (nextIndex != -1) {
                lastIndex = nextIndex;
            } else {
                // If no next occurrence, find end based on "SE*" or "~"
                int seIndex = content.indexOf(FileUtils.getKeyword("~SE*"), beginIndex);
                lastIndex = (seIndex != -1) ? content.indexOf("~", seIndex + 1) : content.length();
            }

            // Ensure lastIndex is valid
            if (lastIndex == -1) {
                lastIndex = content.length();
            }

            String segmentData = content.substring(beginIndex, lastIndex);
//            System.out.println("segmentData = " + segmentData);
            lineDataList.add(segmentData);

            // Move index forward instead of modifying `content`
            index = lastIndex;
        }

        return lineDataList;
    }





}





