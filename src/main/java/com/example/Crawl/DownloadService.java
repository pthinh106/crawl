package com.example.Crawl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class DownloadService {
    private final int BUFFER_SIZE = 4096;

    public String downloadFile(String storage,String fileURL, String saveDir)
            throws IOException {
        String fileName = "";
        fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                fileURL.length());
        URL url = new URL(storage + fileName);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

//            if (disposition != null) {
//                // extracts file name from header field
//                int index = disposition.indexOf("filename=");
//                if (index > 0) {
//                    fileName = disposition.substring(index + 10,
//                            disposition.length() - 1);
//                }
//            }
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
            System.out.println(saveFilePath);
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
        return fileName;
    }
    public String getFileName(String fileURL){
        return fileURL.substring(fileURL.lastIndexOf("/") + 1,
                fileURL.length());
    }
}
