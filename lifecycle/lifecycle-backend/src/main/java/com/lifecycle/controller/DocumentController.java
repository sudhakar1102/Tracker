package com.lifecycle.controller;

import com.lifecycle.dto.Dtos.ExtractReq;
import com.lifecycle.service.InvoiceExtractionService;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/documents")
public class DocumentController {
    private final InvoiceExtractionService extractor;
    public DocumentController(InvoiceExtractionService e) { extractor = e; }
    @PostMapping("/extract") public Map<String, Object> extract(@RequestBody ExtractReq r) { return extractor.extract(r.text()); }
}
