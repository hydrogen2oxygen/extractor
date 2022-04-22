package net.hydrogen2oxygen.extractor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hydrogen2oxygen.domain.MateriaMedica;
import net.hydrogen2oxygen.extractor.IExtractor;
import net.hydrogen2oxygen.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClarkeExtractor implements IExtractor {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String outputFolder;

    @Override
    public void extract(String baseUrl, String outputFolder) {

        System.out.println("starting extraction");
        System.out.println("baseUrl = " + baseUrl);
        System.out.println("output folder = " + outputFolder);

        this.outputFolder = outputFolder;

        try {

            Document doc = Jsoup.connect(baseUrl).get();
            Elements links = doc.getElementsByTag("blockquote").get(0).getElementsByTag("p").get(1).getElementsByTag("a");

            for (Element link : links) {

                if (link.attributes().size() == 0) continue;

                String target = link.attributes().get("target");

                if ("_top".equals(target)) {
                    System.out.println(link.absUrl("href"));
                    extractTopLink(link.absUrl("href"));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractTopLink(String targetUrl) {

        try {

            Document doc = Jsoup.connect(targetUrl).get();
            Elements links = doc.getElementsByTag("p").get(3).getElementsByTag("a");

            for (Element link : links) {

                if (link.attributes().size() == 0) continue;

                String target = link.attributes().get("target");

                if ("_top".equals(target)) {
                    System.out.println(link.absUrl("href"));
                    extractPage(link.absUrl("href"));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractPage(String targetUrl) {

        try {
            MateriaMedica materiaMedica = new MateriaMedica();
            Document doc = Jsoup.parse(new URL(targetUrl).openStream(), "windows-1252", targetUrl);
            String remedyName = doc.getElementsByTag("blockquote").get(0)
                    .getElementsByTag("font").get(4).text()
                    .replace(".","").trim();

            remedyName = StringUtil.cleanString(remedyName);

            if (!StringUtil.isPureAscii(remedyName)) {
                throw new Exception("NOT ASCII!");
            }

            materiaMedica.setRemedyName(remedyName);

            String alternativeNames = doc.getElementsByTag("blockquote").get(0)
                    .getElementsByTag("b").get(1).text().trim();

            if ("Clinical.─".equals(alternativeNames)) {
                alternativeNames = "";
            }

            materiaMedica.setRemedyAlternativeNames(alternativeNames);

            Elements symptomBlocks = doc.getElementsByTag("blockquote").get(0)
                    .getElementsByTag("p");

            for (Element symptomBlock : symptomBlocks) {
                if ("justify".equals(symptomBlock.attr("align").toLowerCase())) {
                    if (symptomBlock.getElementsByTag("font").size() > 0) {

                        String category = symptomBlock.getElementsByTag("font").get(0).text()
                                .replace(".", "")
                                .replace("─","").trim();
                        category = StringUtil.removeNumbers(category);

                        String symptoms = StringUtil.cleanString(symptomBlock.text());
                        symptoms = StringUtil.removeNumbers(symptoms);

                        List<String> symptomList = getSymptomList(symptoms);
                        symptomList.remove(category + ".");

                        materiaMedica.getCategories().put(category, symptomList);
                    }
                }
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFolder + File.separator + materiaMedica.getRemedyName() + ".json"), materiaMedica);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List getSymptomList(String symptoms) {
        String parts [] = symptoms.split("─");

        List symptomList = new ArrayList<>();

        for (String part : parts) {
            symptomList.add(part);
        }
        return symptomList;
    }

}