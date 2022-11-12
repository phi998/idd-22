package it.uniroma3.idd.hw4.api;

import it.uniroma3.idd.hw4.utils.Constants;
import it.uniroma3.idd.hw4.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExtractorApiImplTest {

    private static final String URLS_DIRECTORY = Utils.getResourceFullPath("./input/url/");
    private static final String XPATHS_DIRECTORY = Utils.getResourceFullPath("./input/xpath/");
    private static final String SEPARATOR = ":";
    private static final String SLASH = "/";
    private static final String TXT_EXTENSION = ".txt";

    @Test
    void getLabeledDataFromXpathAmazonLibri() {
        String datasetName = "amazon_books";
        exec(datasetName);
    }

    /** private methods */

    private void exec(String datasetName) {
        System.setProperty(Constants.DATASET_PROPERTY, datasetName);

        Map<String,List<String>> label2xpaths = readLabel2Xpath(datasetName);

        ExtractorApi extractorApi = new ExtractorApiImpl();
        for(String url : readUrls(datasetName)) {
            extractorApi.getLabeledDataFromXpath(url, label2xpaths);
        }
    }

    private Map<String,List<String>> readLabel2Xpath(String datasetName) {
        Map<String,List<String>> label2xpaths = new HashMap<>();
        String fullPath = XPATHS_DIRECTORY + SLASH + datasetName;
        File file = new File(fullPath);

        try {
            List<String> contents = FileUtils.readLines(file, "UTF-8");

            for (String line : contents) {
                String[] linex = line.split(SEPARATOR);
                label2xpaths.putIfAbsent(linex[0], new ArrayList<>());
                label2xpaths.get(linex[0]).add(linex[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return label2xpaths;
    }

    private List<String> readUrls(String datasetName) {
        List<String> urls = new ArrayList<>();

        String fullPath = URLS_DIRECTORY + SLASH + datasetName + TXT_EXTENSION;
        File file = new File(fullPath);

        try {
            List<String> contents = FileUtils.readLines(file, "UTF-8");

            for (String line : contents) {
                urls.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urls;
    }

}