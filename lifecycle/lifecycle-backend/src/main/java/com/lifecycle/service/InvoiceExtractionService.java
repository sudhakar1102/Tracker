package com.lifecycle.service;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.*;
import org.springframework.stereotype.Service;

/**
 * Parses invoice TEXT into suggested fields. Plug an OCR engine (e.g. Tesseract via Tess4J) in front of this
 * to turn scanned images into text; the user always confirms the result before saving.
 */
@Service
public class InvoiceExtractionService {
    private static final Pattern DATE = Pattern.compile("(\\d{1,2})[-/.](\\d{1,2})[-/.](\\d{4})");
    private static final Pattern PRICE = Pattern.compile("(?i)(?:total|amount|grand total)[^\\d]{0,20}(?:₹|rs\\.?|inr)?\\s*([\\d,]+(?:\\.\\d{1,2})?)");
    private static final Pattern PRICE_SYM = Pattern.compile("(?:₹|(?i:rs\\.?)|(?i:inr))\\s*([\\d,]+(?:\\.\\d{1,2})?)");
    private static final Pattern INVOICE = Pattern.compile("(?i)(?:invoice|bill)\\s*(?:no\\.?|number|#)?\\s*[:\\-]?\\s*([A-Za-z0-9\\-/]{3,})");
    private static final Pattern SERIAL = Pattern.compile("(?i)serial\\s*(?:no\\.?|number)?\\s*[:\\-]?\\s*([A-Za-z0-9\\-]{4,})");
    private static final Pattern MODEL = Pattern.compile("(?i)model\\s*(?:no\\.?)?\\s*[:\\-]?\\s*([A-Za-z0-9\\-]{2,})");

    public Map<String, Object> extract(String text) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (text == null || text.isBlank()) return out;
        Matcher d = DATE.matcher(text);
        if (d.find()) {
            try { out.put("purchaseDate", LocalDate.of(Integer.parseInt(d.group(3)), Integer.parseInt(d.group(2)), Integer.parseInt(d.group(1))).toString()); }
            catch (Exception ignored) { }
        }
        Matcher p = PRICE.matcher(text);
        if (p.find()) out.put("purchasePrice", p.group(1).replace(",", ""));
        else { Matcher s = PRICE_SYM.matcher(text); if (s.find()) out.put("purchasePrice", s.group(1).replace(",", "")); }
        put(out, "invoiceNumber", INVOICE, text); put(out, "serialNumber", SERIAL, text); put(out, "model", MODEL, text);
        text.lines().map(String::trim).filter(l -> !l.isBlank()).findFirst().ifPresent(l -> out.put("purchaseLocation", l));
        return out;
    }
    private void put(Map<String, Object> out, String key, Pattern pat, String text) {
        Matcher m = pat.matcher(text); if (m.find()) out.put(key, m.group(1));
    }
}
