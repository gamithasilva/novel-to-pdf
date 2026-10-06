package com.ak.novel2pdf.controller;



import com.ak.novel2pdf.dto.NovelRequest;
import com.ak.novel2pdf.service.NovelService;
import com.ak.novel2pdf.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/novel")
@CrossOrigin
public class NovelController {

    private final NovelService novelService;

    private final PdfService pdfService;


    public NovelController(
            NovelService novelService,
            PdfService pdfService
    ) {

        this.novelService = novelService;
        this.pdfService = pdfService;
    }


    @GetMapping("/chapter")
    public ResponseEntity<?> getChapter(
            @RequestParam String url
    ) {

        try {

            NovelService.ChapterData chapter =
                    novelService.getChapter(url);

            return ResponseEntity.ok(chapter);

        } catch (IOException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Could not access webpage."
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            e.getMessage()
                    );
        }
    }


    @PostMapping("/pdf/multiple")
    public ResponseEntity<?> generateMultipleChapterPdf(
            @RequestBody NovelRequest request
    ) {

        try {

            if (request == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Request cannot be null."
                        );
            }


            if (request.getNovelTitle() == null ||
                    request.getNovelTitle().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Novel title is required."
                        );
            }


            if (request.getChapterUrls() == null ||
                    request.getChapterUrls().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "At least one chapter URL is required."
                        );
            }


            System.out.println(
                    "================================="
            );

            System.out.println(
                    "MULTI CHAPTER PDF REQUEST"
            );

            System.out.println(
                    "Novel: " +
                            request.getNovelTitle()
            );

            System.out.println(
                    "Chapter count: " +
                            request.getChapterUrls().size()
            );


            List<NovelService.ChapterData> chapters =
                    new ArrayList<>();


            for (String url :
                    request.getChapterUrls()) {

                System.out.println(
                        "Fetching: " + url
                );

                NovelService.ChapterData chapter =
                        novelService.getChapter(url);

                chapters.add(chapter);

                System.out.println(
                        "Fetched: " +
                                chapter.title()
                );
            }


            System.out.println(
                    "Total chapters fetched: " +
                            chapters.size()
            );


            byte[] pdf =
                    pdfService.generateMultiChapterPdf(
                            request.getNovelTitle(),
                            chapters
                    );


            String fileName =
                    request.getNovelTitle()
                            .replaceAll(
                                    "[^a-zA-Z0-9-_ ]",
                                    ""
                            )
                            .replaceAll(
                                    "\\s+",
                                    "_"
                            )
                            + ".pdf";


            System.out.println(
                    "PDF generated: " +
                            pdf.length +
                            " bytes"
            );


            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    fileName +
                                    "\""
                    )
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .body(pdf);


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "PDF generation failed: " +
                                    e.getMessage()
                    );
        }
    }
}