package com.example.Crawl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(path = "/crawl")
@RequiredArgsConstructor
@ResponseBody
public class CrawlController {
    private final DownloadService downloadService;
    @GetMapping("/download-to-url")
    public ResponseEntity<InputStreamResource>download(@RequestParam("url") String urlDetails) throws IOException {
        String fileName = "";
        Document document = Jsoup.connect(urlDetails).get();
        for(Element a : document.select("a")){
            String url = a.attr("href");
            if(url.endsWith(".doc") || url.endsWith(".docx") ||url.endsWith(".xls") ||url.endsWith(".xlsx") ||
                    url.endsWith(".ppt") ||url.endsWith(".pptx") || url.endsWith(".pdf") || url.endsWith(".odt")){
                 fileName =  downloadService.downloadFile("https://file-examples.com/storage/fee3d1095964bab199aee29/2017/02/",url, "C:\\Users\\ADMIN\\Downloads");
                 break;
            }
            if(url.endsWith(".jpg") || url.endsWith(".png") ||url.endsWith(".gif") ||url.endsWith(".ico") ||
                    url.endsWith(".svg") ||url.endsWith(".webp")){
                fileName =  downloadService.downloadFile("https://file-examples.com/storage/fee3d1095964bab199aee29/2017/10/",url, "C:\\Users\\ADMIN\\Downloads");
                break;
            }
            if(url.endsWith(".mp4") || url.endsWith(".mov") ||url.endsWith(".ogg") ||url.endsWith(".avi") ||
                    url.endsWith(".webm") ){
                fileName =  downloadService.downloadFile("https://file-examples.com/storage/fee3d1095964bab199aee29/2018/04/",url, "C:\\Users\\ADMIN\\Downloads");
                break;
            }
        }
        HttpHeaders responseHeader = new HttpHeaders();
        try {
            File file = ResourceUtils.getFile("C:\\Users\\ADMIN\\Downloads\\" + fileName);
            byte[] data = FileUtils.readFileToByteArray(file);
            responseHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            responseHeader.set("Content-disposition", "attachment; filename=" + file.getName());
            responseHeader.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            File f= new File("C:\\Users\\ADMIN\\Downloads\\" + fileName);
            if(f.delete())
            {
                System.out.println(f.getName() + " deleted");
            }
            return new ResponseEntity<InputStreamResource>(inputStreamResource, responseHeader, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<InputStreamResource>(null, responseHeader, HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

    @GetMapping("/download-multiple-to-url")
    public void zipDownload (HttpServletRequest request, HttpServletResponse response,@RequestParam("url") String urlDetails) throws IOException {

        List<String> list = new ArrayList<>();
        String fileName = "";
        Document document = Jsoup.connect(urlDetails).get();
        for(Element a : document.select("a")){
            String url = a.attr("href");
            if(url.endsWith(".doc") || url.endsWith(".docx") ||url.endsWith(".xls") ||url.endsWith(".xlsx") ||
                    url.endsWith(".ppt") ||url.endsWith(".pptx") || url.endsWith(".pdf") || url.endsWith(".odt")){
                fileName =  downloadService.downloadFile("https://file-examples.com/storage/fee3d1095964bab199aee29/2017/02/",url, "C:\\Users\\ADMIN\\Downloads");
                list.add(fileName);
            }
            if(url.endsWith(".jpg") || url.endsWith(".png") ||url.endsWith(".gif") ||url.endsWith(".ico") ||
                    url.endsWith(".svg") ||url.endsWith(".webp")){
                fileName =  downloadService.downloadFile("https://file-examples.com/storage/fee3d1095964bab199aee29/2017/10/",url, "C:\\Users\\ADMIN\\Downloads");
                list.add(fileName);
            }
            if(url.endsWith(".mp4") || url.endsWith(".mov") ||url.endsWith(".ogg") ||url.endsWith(".avi") ||
                    url.endsWith(".webm") ){
                fileName =  downloadService.downloadFile("https://file-examples.com/storage/fee3d1095964bab199aee29/2018/04/",url, "C:\\Users\\ADMIN\\Downloads");
                list.add(fileName);
            }
        }

        List<Path> files = new ArrayList<>();
        for(String resource : list){
            files.add(Paths.get("C:\\Users\\ADMIN\\Downloads\\"+resource));
        }
        response.setContentType("application/zip"); // zip archive format
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename("download.zip", StandardCharsets.UTF_8)
                .build()
                .toString());


        // nén file và trả về người dùng
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())){
            for (Path file : files) {
                try (InputStream inputStream = Files.newInputStream(file)) {
                    zipOutputStream.putNextEntry(new ZipEntry(file.getFileName().toString()));
                    StreamUtils.copy(inputStream, zipOutputStream);
                    zipOutputStream.flush();
                }
            }
        }
        for(String resource : list){
            File f= new File("C:\\Users\\ADMIN\\Downloads\\" + resource);
            if(f.delete())
            {
                System.out.println(f.getName() + " deleted");
            }
        }
    }
    @PostMapping("/preview")
    public ResponseEntity<List<Map<String,String>>> preview(@RequestParam("url") String urlDetails) throws IOException {
        List<Map<String,String>> list = new ArrayList<>();

        String fileName = "";
        Document document = Jsoup.connect(urlDetails).get();
        for(Element a : document.select("a")){
            String url = a.attr("href");
            if(url.endsWith(".doc") || url.endsWith(".docx") ||url.endsWith(".xls") ||url.endsWith(".xlsx") ||
                    url.endsWith(".ppt") ||url.endsWith(".pptx") || url.endsWith(".pdf") || url.endsWith(".odt")){
                fileName =  downloadService.getFileName(url);
                Map<String,String> m = new HashMap<>();
                m.put("url","https://file-examples.com/storage/fee3d1095964bab199aee29/2017/02/"+fileName);
                m.put("fileName", fileName);
                list.add(m);
            }
        }
        return ResponseEntity.ok().body(list);

    }


}
