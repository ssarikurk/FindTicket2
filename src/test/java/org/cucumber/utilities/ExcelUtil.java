package org.cucumber.utilities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.*;
import org.testng.Assert;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

public class ExcelUtil {
    private Sheet workSheet;
    private Workbook workBook;
    private String path;

    public ExcelUtil(String path, String sheetName) {
        this.path = path;
        try {
            // Open the Excel file
            FileInputStream ExcelFile = new FileInputStream(path);
            // Access the required test data sheet
            workBook = WorkbookFactory.create(ExcelFile);
            workSheet = workBook.getSheet(sheetName);
            // check if sheet is null or not. null means sheetname was wrong
            Assert.assertNotNull(workSheet, "Sheet: \""+sheetName+"\" does not exist\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getCellData(int rowNum, int colNum) {
        Cell cell;
        try {
            cell = workSheet.getRow(rowNum).getCell(colNum);
            String cellData = cell.toString();
            return cellData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String[][] getDataArray() {

        String[][] data = new String[rowCount()][columnCount()];

        for (int i = 0; i <rowCount(); i++) {
            for (int j = 0; j < columnCount(); j++) {
                String value = getCellData(i, j);
                data[i][j] = value;
            }
        }
        return data;

    }

    //this method will return data table as 2d array
    //so we need this format because of data provider.
    public String[][] getDataArrayWithoutFirstRow() {

        String[][] data = new String[rowCount()-1][columnCount()];

        for (int i = 1; i < rowCount(); i++) {
            for (int j = 0; j < columnCount(); j++) {
                String value = getCellData(i, j);
                data[i-1][j] = value;
            }
        }
        return data;

    }

    
    
    
    public List<Map<String, String>> getDataList() {
        // get all columns
        List<String> columns = getColumnsNames();
        // this will be returned
        List<Map<String, String>> data = new ArrayList<>();

        for (int i = 1; i < rowCount(); i++) {
            // get each row
            Row row = workSheet.getRow(i);
            // create map of the row using the column and value
            // column map key, cell value --> map bvalue
            Map<String, String> rowMap = new HashMap<String, String>();
            for (Cell cell : row) {
                int columnIndex = cell.getColumnIndex();
                rowMap.put(columns.get(columnIndex), cell.toString());
            }

            data.add(rowMap);
        }

        return data;
    }

    public List<String> getColumnsNames() {
        List<String> columns = new ArrayList<>();

        for (Cell cell : workSheet.getRow(0)) {
            columns.add(cell.toString());
        }
        return columns;
    }

    public void setCellData(String value, int rowNum, int colNum) {
        Cell cell;
        Row row;

        try {
            row = workSheet.getRow(rowNum);
            cell = row.getCell(colNum);

            if (cell == null) {
                cell = row.createCell(colNum);
                cell.setCellValue(value);
            } else {
                cell.setCellValue(value);
            }
            FileOutputStream fileOut = new FileOutputStream(path);
            workBook.write(fileOut);

            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCellData(String value, String columnName, int row) {
        int column = getColumnsNames().indexOf(columnName);
        setCellData(value, row, column);
    }

    public int columnCount() {
        return workSheet.getRow(0).getLastCellNum();
    }

    public int rowCount() {
        return workSheet.getLastRowNum()+1;
    }


    public static List<Map<String, String>> readCSV (String fileName) {

        Reader in = null;
        try {
            in = new FileReader(BrowserUtils.getDownloadPath()+"/"+fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Iterable<CSVRecord> records = null;
        try {
            records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map<String, String>> listOfMaps = new ArrayList<>();
        for (CSVRecord record : records) {
            listOfMaps.add(record.toMap());
        }

        return listOfMaps;
    }

    public static List<Map<String, String>> readCSVNoHeader(String fileName) throws IOException {

        Reader in = new FileReader(BrowserUtils.getDownloadPath() + "/" + fileName);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

        List<Map<String, String>> listOfMaps = new ArrayList<>();
        for (CSVRecord record : records) {
            Map<String, String> map = new LinkedHashMap<>();
            for (int i = 0; i < record.size(); i++) {
                map.put("Column_" + i, record.get(i));
            }
            listOfMaps.add(map);
        }

        return listOfMaps;
    }
    public static List<List<String>> readCSVtoList (String fileName) throws IOException {
        Reader in = new FileReader(BrowserUtils.getDownloadPath() + "/" + fileName);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

        List<List<String>> listOfLists = new ArrayList<>();
        for (CSVRecord record : records) {
            List<String> row = new ArrayList<>();
            for (String value : record) {
                row.add(value);
            }
            listOfLists.add(row);
        }

        return listOfLists;
    }

    public static List<Map<String, Object>> readCSVtoListofMapWithPath(String filePath) {
        List<Map<String, Object>> listOfMaps = new ArrayList<>();
        try {
            Reader in = new FileReader(filePath);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                Map<String, Object> row = new HashMap<>();
                for (String header : record.toMap().keySet()) {
                    row.put(header, record.get(header));
                }
                listOfMaps.add(row);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfMaps;
    }


    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
//    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

//    public static List<Map<String, Object>> readGoogleSheetToListOfMap(String spreadsheetId, String range, String filePath)
//            throws IOException, GeneralSecurityException {
//        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, filePath))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//
//        ValueRange response = service.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//
//        List<List<Object>> values = response.getValues();
//        List<Map<String, Object>> listOfMaps = new ArrayList<>();
//
//        if (values == null || values.isEmpty()) {
//            System.out.println("No data found.");
//        } else {
//            List<String> headers = new ArrayList<>();
//            for (int i = 0; i < values.size(); i++) {
//                List<Object> row = values.get(i);
//                if (i == 0) {
//                    for (Object header : row) {
//                        headers.add(header.toString());
//                    }
//                } else {
//                    Map<String, Object> rowData = new HashMap<>();
//                    for (int j = 0; j < row.size(); j++) {
//                        rowData.put(headers.get(j), row.get(j));
//                    }
//                    listOfMaps.add(rowData);
//                }
//            }
//        }
//        return listOfMaps;
//    }

    private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
        return new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
                requestInitializer.initialize(httpRequest);
                httpRequest.setConnectTimeout(60000); // 1 minute
                httpRequest.setReadTimeout(60000);    // 1 minute
            }
        };
    }

    private static <T> T executeWithRetry(Callable<T> callable) throws IOException {
        int maxRetries = 5;
        int backoff = 1000; // initial backoff in milliseconds
        for (int i = 0; i < maxRetries; i++) {
            try {
                return callable.call();
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    throw new IOException("Maximum retries reached", e);
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Retry interrupted", ie);
                }
                backoff *= 2; // exponential backoff
            }
        }
        throw new IOException("Failed after retries");
    }

    public static List<Map<String, Object>> readGoogleSheetToListOfMap(String spreadsheetId, String range, String filePath)
            throws IOException, GeneralSecurityException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(getCredentials(HTTP_TRANSPORT, filePath)))
                .setApplicationName(APPLICATION_NAME)
                .build();

        List<Map<String, Object>> listOfMaps = new ArrayList<>();

        try {
            ValueRange response = executeWithRetry(() -> service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute());

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                List<String> headers = new ArrayList<>();
                for (int i = 0; i < values.size(); i++) {
                    List<Object> row = values.get(i);
                    if (i == 0) {
                        for (Object header : row) {
                            headers.add(header.toString());
                        }
                    } else {
                        Map<String, Object> rowData = new HashMap<>();
                        for (int j = 0; j < row.size(); j++) {
                            rowData.put(headers.get(j), row.get(j));
                        }
                        listOfMaps.add(rowData);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error fetching Google Sheet data: " + e.getMessage());
        }
        return listOfMaps;
    }


    public static void exportDataToGoogleSheet(String spreadsheetId, String sheetName, List<Map<String, Object>> data, String filePath)
            throws IOException, GeneralSecurityException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, filePath))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Get the sheet ID by name (if it exists)
        Integer sheetId = getSheetIdByName(service, spreadsheetId, sheetName);

        // If the sheet exists, delete it
        if (sheetId != null) {
            BatchUpdateSpreadsheetRequest deleteRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(new Request()
                            .setDeleteSheet(new DeleteSheetRequest()
                                    .setSheetId(sheetId))));
            service.spreadsheets().batchUpdate(spreadsheetId, deleteRequest).execute();
        }

        // Define the desired column order
        List<String> orderedHeaders = Arrays.asList("testId", "testCaseName", "testStatus", "errorLogs", "isTestCaseHealthy", "claimIdentifier", "purpose" );

        // Prepare the data to be written
        List<List<Object>> sheetData = new ArrayList<>();
        sheetData.add(new ArrayList<>(orderedHeaders));

        for (Map<String, Object> row : data) {
            List<Object> rowData = new ArrayList<>();
            for (String key : orderedHeaders) {
                Object value = row.get(key);
                if (value instanceof List) {
                    rowData.add(((List<?>) value).isEmpty() ? "" : String.join(", ", (List<String>) value));
                } else {
                    rowData.add(value != null ? value : "");
                }
            }
            sheetData.add(rowData);
        }

        // Create the new sheet
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(new Request()
                        .setAddSheet(new AddSheetRequest()
                                .setProperties(new SheetProperties()
                                        .setTitle(sheetName)))));

        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

        // Write data to the new sheet
        ValueRange body = new ValueRange().setValues(sheetData);
        service.spreadsheets().values()
                .update(spreadsheetId, sheetName + "!A1", body)
                .setValueInputOption("RAW")
                .execute();

        // Apply conditional formatting
        int testStatusColumnIndex = orderedHeaders.indexOf("testStatus");
        if (testStatusColumnIndex != -1) {
            GridRange gridRange = new GridRange()
                    .setSheetId(getSheetIdByName(service, spreadsheetId, sheetName))
                    .setStartRowIndex(1)
                    .setEndRowIndex(sheetData.size())
                    .setStartColumnIndex(0)
                    .setEndColumnIndex(orderedHeaders.size());

            // Format for "PASSED" = Green
            ConditionalFormatRule passedRule = new ConditionalFormatRule()
                    .setRanges(Collections.singletonList(gridRange))
                    .setBooleanRule(new BooleanRule()
                            .setCondition(new BooleanCondition()
                                    .setType("TEXT_EQ")
                                    .setValues(Collections.singletonList(new ConditionValue().setUserEnteredValue("PASSED"))))
                            .setFormat(new CellFormat()
                                    .setBackgroundColor(new Color().setGreen(1f))));

            // Format for "FAILED" = Red
            ConditionalFormatRule failedRule = new ConditionalFormatRule()
                    .setRanges(Collections.singletonList(gridRange))
                    .setBooleanRule(new BooleanRule()
                            .setCondition(new BooleanCondition()
                                    .setType("TEXT_EQ")
                                    .setValues(Collections.singletonList(new ConditionValue().setUserEnteredValue("FAILED"))))
                            .setFormat(new CellFormat()
                                    .setBackgroundColor(new Color().setRed(1f))));

            List<Request> requests = Arrays.asList(
                    new Request().setAddConditionalFormatRule(new AddConditionalFormatRuleRequest()
                            .setRule(passedRule)
                            .setIndex(0)),
                    new Request().setAddConditionalFormatRule(new AddConditionalFormatRuleRequest()
                            .setRule(failedRule)
                            .setIndex(1))
            );

            BatchUpdateSpreadsheetRequest formattingRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, formattingRequest).execute();
        }
    }


    public static void exportDataToGoogleSheet2(String spreadsheetId, String sheetName, List<Map<String, Object>> data, String filePath, List<String> orderedHeaders )
            throws IOException, GeneralSecurityException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, filePath))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Get the sheet ID by name (if it exists)
        Integer sheetId = getSheetIdByName(service, spreadsheetId, sheetName);

        // If the sheet exists, delete it
        if (sheetId != null) {
            BatchUpdateSpreadsheetRequest deleteRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(new Request()
                            .setDeleteSheet(new DeleteSheetRequest()
                                    .setSheetId(sheetId))));
            service.spreadsheets().batchUpdate(spreadsheetId, deleteRequest).execute();
        }

        // Prepare the data to be written
        List<List<Object>> sheetData = new ArrayList<>();
        sheetData.add(new ArrayList<>(orderedHeaders));

        for (Map<String, Object> row : data) {
            List<Object> rowData = new ArrayList<>();
            for (String key : orderedHeaders) {
                Object value = row.get(key);
                if (value instanceof List) {
                    rowData.add(((List<?>) value).isEmpty() ? "" : String.join(", ", (List<String>) value));
                } else {
                    rowData.add(value != null ? value : "");
                }
            }
            sheetData.add(rowData);
        }

        // Create the new sheet
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(new Request()
                        .setAddSheet(new AddSheetRequest()
                                .setProperties(new SheetProperties()
                                        .setTitle(sheetName)))));

        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

        // Write data to the new sheet
        ValueRange body = new ValueRange().setValues(sheetData);
        service.spreadsheets().values()
                .update(spreadsheetId, sheetName + "!A1", body)
                .setValueInputOption("RAW")
                .execute();

        // Apply conditional formatting
        int testStatusColumnIndex = orderedHeaders.indexOf("testStatus");
        if (testStatusColumnIndex != -1) {
            GridRange gridRange = new GridRange()
                    .setSheetId(getSheetIdByName(service, spreadsheetId, sheetName))
                    .setStartRowIndex(1)
                    .setEndRowIndex(sheetData.size())
                    .setStartColumnIndex(0)
                    .setEndColumnIndex(orderedHeaders.size());

            // Format for "PASSED" = Green
            ConditionalFormatRule passedRule = new ConditionalFormatRule()
                    .setRanges(Collections.singletonList(gridRange))
                    .setBooleanRule(new BooleanRule()
                            .setCondition(new BooleanCondition()
                                    .setType("TEXT_EQ")
                                    .setValues(Collections.singletonList(new ConditionValue().setUserEnteredValue("PASSED"))))
                            .setFormat(new CellFormat()
                                    .setBackgroundColor(new Color().setGreen(1f))));

            // Format for "FAILED" = Red
            ConditionalFormatRule failedRule = new ConditionalFormatRule()
                    .setRanges(Collections.singletonList(gridRange))
                    .setBooleanRule(new BooleanRule()
                            .setCondition(new BooleanCondition()
                                    .setType("TEXT_EQ")
                                    .setValues(Collections.singletonList(new ConditionValue().setUserEnteredValue("FAILED"))))
                            .setFormat(new CellFormat()
                                    .setBackgroundColor(new Color().setRed(1f))));

            List<Request> requests = Arrays.asList(
                    new Request().setAddConditionalFormatRule(new AddConditionalFormatRuleRequest()
                            .setRule(passedRule)
                            .setIndex(0)),
                    new Request().setAddConditionalFormatRule(new AddConditionalFormatRuleRequest()
                            .setRule(failedRule)
                            .setIndex(1))
            );

            BatchUpdateSpreadsheetRequest formattingRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, formattingRequest).execute();
        }
    }


    private static Integer getSheetIdByName(Sheets service, String spreadsheetId, String sheetName) throws IOException {
        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        for (com.google.api.services.sheets.v4.model.Sheet sheet : spreadsheet.getSheets()) {
            if (sheet.getProperties().getTitle().equals(sheetName)) {
                return sheet.getProperties().getSheetId();
            }
        }
        return null; // Sheet not found
    }

    private static Credential getCredentials(final HttpTransport HTTP_TRANSPORT, String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + filePath);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void exportDataToGoogleSheetSimple(String spreadsheetId, String sheetName, List<Map<String, Object>> data, String filePath)
            throws IOException, GeneralSecurityException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, filePath))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Get the sheet ID by name (if it exists)
        Integer sheetId = getSheetIdByName(service, spreadsheetId, sheetName);

        // If the sheet exists, delete it
        if (sheetId != null) {
            BatchUpdateSpreadsheetRequest deleteRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(new Request()
                            .setDeleteSheet(new DeleteSheetRequest()
                                    .setSheetId(sheetId))));
            service.spreadsheets().batchUpdate(spreadsheetId, deleteRequest).execute();
        }



        // Prepare the data to be written
        List<List<Object>> sheetData = new ArrayList<>();

        // Add headers from the first map's keyset if data is not empty
        if (!data.isEmpty()) {
            sheetData.add(new ArrayList<>(data.get(0).keySet()));
        }

        // Add data rows
        for (Map<String, Object> row : data) {
            List<Object> rowData = new ArrayList<>();
            for (String key : row.keySet()) {
                Object value = row.get(key);
                rowData.add(value != null ? value : "");
            }
            sheetData.add(rowData);
        }

        // Create the new sheet
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(new Request()
                        .setAddSheet(new AddSheetRequest()
                                .setProperties(new SheetProperties()
                                        .setTitle(sheetName)))));

        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

        // Write data to the new sheet
        ValueRange body = new ValueRange().setValues(sheetData);
        service.spreadsheets().values()
                .update(spreadsheetId, sheetName + "!A1", body)
                .setValueInputOption("RAW")
                .execute();
    }


    public static List<List<String>> readCSVtoListWithPath (String filePath) throws IOException {
        Reader in = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

        List<List<String>> listOfLists = new ArrayList<>();
        for (CSVRecord record : records) {
            List<String> row = new ArrayList<>();
            for (String value : record) {
                row.add(value);
            }
            listOfLists.add(row);
        }

        return listOfLists;
    }



    public void excelUtilRowBased(Map<String, List<String>> mapOf, List<String> columnName) {
        Cell cell;
        Row row;

        for (int i = 1; i < mapOf.size() + 1 ; i++) {
            List<String> keys = (List<String>) mapOf.keySet();
            String keyValue = keys.get(i);
            for (int j = 0; j < mapOf.get(keyValue).size(); j++) {

                try {
                    row = workSheet.getRow(i);
                    cell = row.getCell(j);

                    if (cell == null) {
                        cell = row.createCell(i);
                        cell.setCellValue(mapOf.get(keyValue).get(j));
                    } else {
                        cell.setCellValue(mapOf.get(keyValue).get(j));
                    }
                    FileOutputStream fileOut = new FileOutputStream(path);
                    workBook.write(fileOut);

                    fileOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }




    public static void deleteSheetsFromGoogleSheet(String spreadsheetId, List<String> sheetNames, String filePath) throws IOException, GeneralSecurityException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, filePath))
                .setApplicationName(APPLICATION_NAME)
                .build();


        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        List<com.google.api.services.sheets.v4.model.Sheet> sheets = spreadsheet.getSheets();



        for (String sheetName : sheetNames) {
            for (com.google.api.services.sheets.v4.model.Sheet sheet : sheets) {
                if (sheet.getProperties().getTitle().equals(sheetName)) {
                    Integer sheetId = sheet.getProperties().getSheetId();


                    BatchUpdateSpreadsheetRequest deleteRequest = new BatchUpdateSpreadsheetRequest()
                            .setRequests(Collections.singletonList(new Request()
                                    .setDeleteSheet(new DeleteSheetRequest()
                                            .setSheetId(sheetId))));


                    service.spreadsheets().batchUpdate(spreadsheetId, deleteRequest).execute();
                    System.out.println("Sheet deleted: " + sheetName);
                    break;  // Exit after deleting the sheet
                }
            }
        }
    }

    public static List<String> getSheetTabNames(String spreadsheetId, String filePath)
            throws IOException, GeneralSecurityException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, filePath))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Retrieve the spreadsheet metadata to get all sheets
        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        List<com.google.api.services.sheets.v4.model.Sheet> sheets = spreadsheet.getSheets();

        // Extract and return sheet names
        List<String> sheetTabNames = new ArrayList<>();
        if (sheets != null) {
            for (com.google.api.services.sheets.v4.model.Sheet sheet : sheets) {
                SheetProperties properties = sheet.getProperties();
                sheetTabNames.add(properties.getTitle());
            }
        }

        return sheetTabNames;
    }

    public static void deleteSheetsFromGoogleSheet2(String spreadsheetId, String filePath)
            throws IOException, GeneralSecurityException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, filePath))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Retrieve spreadsheet metadata to get all sheet names
        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        List<com.google.api.services.sheets.v4.model.Sheet> sheets = spreadsheet.getSheets();

        // Get the list of dates to preserve (last 3 days)
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        List<String> preserveDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 3; i++) {
            preserveDates.add(formatter.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, -1); // Move back one day
        }

        // Identify sheets to delete
        List<String> tabsToDelete = new ArrayList<>();


        for (com.google.api.services.sheets.v4.model.Sheet sheet : sheets) {
            String sheetName = sheet.getProperties().getTitle();
            boolean isPreserved = sheetName.contains("ControlGroup--") ||
                    (sheetName.length() > 6 && preserveDates.contains(sheetName.substring(sheetName.length() - 6)));

            if (!isPreserved) {
                tabsToDelete.add(sheetName);
            }
        }




        // Delete sheets if there are any to delete
        if (!tabsToDelete.isEmpty()) {
            for (String sheetName : tabsToDelete) {

                for (com.google.api.services.sheets.v4.model.Sheet sheet : sheets) {
                    if (sheet.getProperties().getTitle().equals(sheetName)) {
                        Integer sheetId = sheet.getProperties().getSheetId();
                        BatchUpdateSpreadsheetRequest deleteRequest = new BatchUpdateSpreadsheetRequest()
                                .setRequests(Collections.singletonList(new Request()
                                        .setDeleteSheet(new DeleteSheetRequest().setSheetId(sheetId))));

                        service.spreadsheets().batchUpdate(spreadsheetId, deleteRequest).execute();
                        System.out.println("Sheet deleted: " + sheetName);
                        break; // Exit after deleting the sheet
                    }
                }
            }
        } else {
            System.out.println("No tabs to delete. All tabs are within the last three days or marked as ControlGroup.");
        }
                }

    public static void deleteSheetsFromGoogleSheet3(String spreadsheetId, String filePath)
            throws IOException, GeneralSecurityException, InterruptedException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, filePath))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Retrieve spreadsheet metadata to get all sheet names
        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        List<com.google.api.services.sheets.v4.model.Sheet> sheets = spreadsheet.getSheets();

        // Get the list of dates to preserve (last 3 days)
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        List<String> preserveDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 3; i++) {
            preserveDates.add(formatter.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
      //  System.out.println("calendar = " + calendar);
        // Identify sheets to delete
        List<Request> requests = new ArrayList<>();
        for (com.google.api.services.sheets.v4.model.Sheet sheet : sheets) {
            String sheetName = sheet.getProperties().getTitle();
            boolean isPreserved = sheetName.contains("ControlGroup--") ||
                    (sheetName.length() > 6 && preserveDates.contains(sheetName.substring(sheetName.length() - 6)));



            if (!isPreserved) {
                Integer sheetId = sheet.getProperties().getSheetId();
                requests.add(new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(sheetId)));
            }
        }
//
//        for (com.google.api.services.sheets.v4.model.Sheet sheet : sheets) {
//            String sheetName = sheet.getProperties().getTitle();
//            String dateSuffix = sheetName.length() > 6 ? sheetName.substring(sheetName.length() - 6) : "";
//            boolean isPreserved = sheetName.contains("ControlGroup--") || preserveDates.contains(dateSuffix);
//            System.out.println("Sheet: " + sheetName + ", Date Suffix: " + dateSuffix
//                    + ", Preserved: " + isPreserved);
//        }



        // Check if there are sheets to delete
        if (!requests.isEmpty()) {
            int retries = 0;
            while (true) {
                try {
                    BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                    service.spreadsheets().batchUpdate(spreadsheetId, batchRequest).execute();
                    System.out.println("Sheets deleted successfully.");
                    break; // Exit the loop after successful deletion
                } catch (GoogleJsonResponseException e) {
                    if (e.getStatusCode() == 429) { // Rate limit exceeded
                        System.out.println("Rate limit exceeded, retrying...");
                        retries++;
                        if (retries < 5) { // Retry up to 5 times
                            Thread.sleep((long) Math.pow(2, retries) * 1000); // Exponential backoff
                        } else {
                            throw new IOException("Exceeded maximum retry attempts for deleting sheets", e);
                        }
                    } else {
                        throw e; // Re-throw if not a rate limit error
                    }
                }
            }
        } else {
            System.out.println("No tabs to delete. All tabs are within the last three days or marked as ControlGroup.");
        }
    }

    public static StringBuilder exportListofMapToHTMLTable(String s, List<Map<String, Object>> flights) {
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<html><body><table border='1'>");

        // Add table headers
        if (!flights.isEmpty()) {
            htmlTable.append("<tr>");
            for (String key : flights.get(0).keySet()) {
                htmlTable.append("<th>").append(key).append("</th>");
            }
            htmlTable.append("</tr>");
        }

        // Add table rows
        for (Map<String, Object> flight : flights) {
            htmlTable.append("<tr>");
            for (Object value : flight.values()) {
                htmlTable.append("<td>").append(value != null ? value.toString() : "").append("</td>");
            }
            htmlTable.append("</tr>");
        }

        htmlTable.append("</table></body></html>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BrowserUtils.getDownloadPath() + "/" + s))) {
            writer.write(htmlTable.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlTable;
    }








}
