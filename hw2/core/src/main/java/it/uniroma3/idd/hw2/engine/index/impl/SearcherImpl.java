package it.uniroma3.idd.hw2.engine.index.impl;

import it.uniroma3.idd.hw2.engine.entity.ResultEntry;
import it.uniroma3.idd.hw2.engine.index.Searcher;
import it.uniroma3.idd.hw2.engine.index.enums.QueryType;
import it.uniroma3.idd.hw2.utils.PropertiesReader;
import it.uniroma3.idd.hw2.utils.StatsWriter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static it.uniroma3.idd.hw2.utils.constants.Constants.*;

public class SearcherImpl implements Searcher {

    private static final int SEARCH_RESULTS = 10;

    @Override
    public Set<ResultEntry> search(String queryString) {
        return genericSearch(queryString, QueryType.TERM_QUERY);
    }
    @Override
    public Set<ResultEntry> searchPhraseQuery(String queryString) {
        return genericSearch(queryString, QueryType.PHRASE_QUERY);
    }
    @Override
    public Set<ResultEntry> searchWithParser(String queryString) {
        return genericSearch(queryString, QueryType.QUERY_PARSER);
    }

    /** =================== Private methods ======================= */

    private Set<ResultEntry> genericSearch(String queryString, QueryType queryType) {
        Set<ResultEntry> results = null;

        Path path = Paths.get(INDEX_DIR);
        Query query = generateQuery(queryString, queryType);

        try (Directory directory = FSDirectory.open(path)) {
            try (IndexReader reader = DirectoryReader.open(directory)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                results = runQuery(searcher, queryString, query, PropertiesReader.readExplainProperty());
            } finally {
                directory.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return results;
    }


    private Query generateQuery(String queryString, QueryType queryType) {
        Query query = null;
        if(queryType.equals(QueryType.TERM_QUERY)) {
            query = new TermQuery(new Term(CONTENT, queryString));
        } else if(queryType.equals(QueryType.PHRASE_QUERY)) {
            PhraseQuery.Builder pqnb = new PhraseQuery.Builder();
            String[] phrase = queryString.split(" ");
            for(String term: phrase) {
                pqnb.add(new Term(CONTENT, term));
            }
            query = pqnb.build();
        } else if(queryType.equals(QueryType.QUERY_PARSER)) {
            QueryParser queryParser = new QueryParser(CONTENT, new WhitespaceAnalyzer());
            try {
                query = queryParser.parse(queryString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return query;
    }

    private Set<ResultEntry> runQuery(IndexSearcher searcher, String queryString, Query query, boolean explain) throws IOException {
        Set<ResultEntry> results = new TreeSet<>();

        Long timestamp = new Date().getTime();

        if (explain)
            StatsWriter.initStatsFile(queryString,timestamp);

        TopDocs hits = searcher.search(query, SEARCH_RESULTS);
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);

            ResultEntry resultEntry = new ResultEntry();
            resultEntry.setDocId(scoreDoc.doc);
            resultEntry.setTitle(doc.get(TITLE));
            resultEntry.setRankWeight(scoreDoc.score);
            results.add(resultEntry);

            if (explain) {
                Explanation explanation = searcher.explain(query, scoreDoc.doc);
                StatsWriter.writeStats(timestamp, explanation);
            }
        }

        if (explain) {
            StatsWriter.appendElapsedTimeAndHits(timestamp, new Date().getTime(),hits.totalHits.value);
        }
        return results;
    }


}
