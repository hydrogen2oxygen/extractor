package net.hydrogen2oxygen.extractor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hydrogen2oxygen.domain.Category;
import net.hydrogen2oxygen.domain.MateriaMedica;
import net.hydrogen2oxygen.extractor.IExtractor;
import net.hydrogen2oxygen.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ClarkeExtractor implements IExtractor {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String outputFolder;
    private Map<String, Category> categories = new HashMap<>();

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

        List<Category> categoryList = new ArrayList<>();
        categoryList.addAll(categories.values());

        categoryList.sort(new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o2.getCount().compareTo(o1.getCount());
            }
        });

        for (Category category : categoryList) {
            System.out.println(category);
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

            if (remedyName.length() == 0) {
                remedyName = doc.getElementsByTag("blockquote").get(0)
                        .getElementsByTag("p").get(1).getElementsByTag("font").get(0).text()
                        .replace(".","").trim();

                remedyName = StringUtil.cleanString(remedyName);
            }

            if (!StringUtil.isPureAscii(remedyName)) {
                throw new Exception("NOT ASCII!");
            }

            materiaMedica.setRemedyName(remedyName);

            String alternativeNames = doc.getElementsByTag("blockquote").get(0)
                    .getElementsByTag("b").get(1).text().trim();

            if ("Clinical.─".equals(alternativeNames)) {
                alternativeNames = "";
            }

            materiaMedica.setRemedyAlternativeNames(StringUtil.cleanString(alternativeNames));

            Elements symptomBlocks = doc.getElementsByTag("blockquote").get(0)
                    .getElementsByTag("p");

            String lastCategory = null;

            for (Element symptomBlock : symptomBlocks) {
                if ("justify".equals(symptomBlock.attr("align").toLowerCase())) {
                    if (symptomBlock.getElementsByTag("font").size() > 0) {
                        Element categoryElement = symptomBlock.getElementsByTag("font").get(0);

                        String category = symptomBlock.getElementsByTag("font").get(0).text()
                                .replace(".", "")
                                .replace("─","").trim();
                        category = StringUtil.removeNumbers(category);

                        if ("1, Mind and Head".equals(category)) {
                            category = "Mind";
                        }

                        if (categories.get(category) == null) {
                            categories.put(category, new Category(category, 1, targetUrl));
                        } else {
                            categories.put(category, new Category(category, categories.get(category).getCount() + 1, targetUrl));
                        }

                        String symptoms = StringUtil.cleanString(symptomBlock.text());
                        symptoms = StringUtil.removeNumbers(symptoms);

                        List<String> symptomList = getSymptomList(symptoms);
                        symptomList.remove(category + ".");

                        if (categoryElement.hasAttr("color") &&
                                "#ff0000".equals(categoryElement.attr("color").toLowerCase())) {

                            materiaMedica.getCategories().put(category, symptomList);
                            lastCategory = category;
                        } else {
                            materiaMedica.getCategories().get(lastCategory).addAll(symptomList);
                        }
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
