package it.uniroma3.idd.hw2.api;

import it.uniroma3.idd.hw2.dto.PaginatedResultsDTO;
import it.uniroma3.idd.hw2.dto.ResultEntryDTO;
import it.uniroma3.idd.hw2.dto.ResultsDTO;
import it.uniroma3.idd.hw2.engine.entity.ResultEntry;
import it.uniroma3.idd.hw2.engine.index.Searcher;
import it.uniroma3.idd.hw2.engine.index.impl.SearcherImpl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchApiImpl implements SearchApi {

    @Override
    public ResultsDTO getAllResults(String query) {
        ResultsDTO resultsDTO = new ResultsDTO();

        Searcher searcher = new SearcherImpl();
        Set<ResultEntry> scoreDocs = searcher.search(query);

        resultsDTO.getResultListDTO().addAll(scoreDocs.stream()
                .map(resultEntry -> toResultEntryDTO(resultEntry))
                .collect(Collectors.toList()));

        return resultsDTO;
    }

    @Override
    public ResultsDTO getAllResultsPhrase(String query) {
        ResultsDTO resultsDTO = new ResultsDTO();

        Searcher searcher = new SearcherImpl();
        Set<ResultEntry> scoreDocs = searcher.search(query);

        resultsDTO.getResultListDTO().addAll(scoreDocs.stream()
                .map(resultEntry -> toResultEntryDTO(resultEntry))
                .collect(Collectors.toList()));


        return resultsDTO;
    }

    @Override
    public ResultsDTO getAllResultsWithParser(String query) {
        ResultsDTO resultsDTO = new ResultsDTO();

        Searcher searcher = new SearcherImpl();
        Set<ResultEntry> scoreDocs = searcher.search(query);

        resultsDTO.getResultListDTO().addAll(scoreDocs.stream()
                .map(resultEntry -> toResultEntryDTO(resultEntry))
                .collect(Collectors.toList()));

        return resultsDTO;
    }

    @Override
    public PaginatedResultsDTO getPaginatedResults(String query, int pageNumber, int pageSize) {
        // TODO
        return null;
    }

    /* =================== DTO CONVERTERS ====================== */

    private ResultEntryDTO toResultEntryDTO(ResultEntry resultEntry) {
        ResultEntryDTO resultEntryDTO = new ResultEntryDTO();

        resultEntryDTO.setFileName(resultEntry.getTitle());
        resultEntryDTO.setDocId(String.valueOf(resultEntry.getDocId()));

        return resultEntryDTO;
    }

}
