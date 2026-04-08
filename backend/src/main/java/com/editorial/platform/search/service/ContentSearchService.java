package com.editorial.platform.search.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import com.editorial.platform.common.exception.BadRequestException;
import com.editorial.platform.search.api.dto.SearchResultResponse;
import com.editorial.platform.search.model.SearchDocument;

@Service
public class ContentSearchService {

    private final SolrClient solrClient;

    public ContentSearchService(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    public void index(SearchDocument document) {
        SolrInputDocument solrDocument = new SolrInputDocument();
        solrDocument.addField("id", document.getId());
        solrDocument.addField("contentType_s", document.getContentType());
        solrDocument.addField("contentId_l", document.getContentId());
        solrDocument.addField("title_t", document.getTitle());
        solrDocument.addField("description_t", document.getDescription());
        solrDocument.addField("categoryName_s", document.getCategoryName());
        solrDocument.addField("trainerName_s", document.getTrainerName());
        solrDocument.addField("status_s", document.getStatus());
        solrDocument.addField("tags_ss", document.getTags());

        try {
            solrClient.add(solrDocument);
            solrClient.commit();
        } catch (SolrServerException | IOException exception) {
            throw new BadRequestException("Failed to index content in Solr");
        }
    }

    public void delete(String id) {
        try {
            solrClient.deleteById(id);
            solrClient.commit();
        } catch (SolrServerException | IOException exception) {
            throw new BadRequestException("Failed to delete content from Solr");
        }
    }

    public List<SearchResultResponse> search(String query, String category, String trainer, String tag) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery((query == null || query.isBlank()) ? "*:*" : "title_t:*" + query + "*");

        if (category != null && !category.isBlank()) {
            solrQuery.addFilterQuery("categoryName_s:" + escape(category));
        }

        if (trainer != null && !trainer.isBlank()) {
            solrQuery.addFilterQuery("trainerName_s:" + escape(trainer));
        }

        if (tag != null && !tag.isBlank()) {
            solrQuery.addFilterQuery("tags_ss:" + escape(tag));
        }

        try {
            QueryResponse response = solrClient.query(solrQuery);
            return response.getResults()
                .stream()
                .map(document -> {
                    SearchResultResponse result = new SearchResultResponse();
                    result.setId((String) document.getFieldValue("id"));
                    result.setContentType((String) document.getFieldValue("contentType_s"));
                    result.setContentId((Long) document.getFieldValue("contentId_l"));
                    result.setTitle((String) document.getFieldValue("title_t"));
                    result.setDescription((String) document.getFieldValue("description_t"));
                    result.setCategoryName((String) document.getFieldValue("categoryName_s"));
                    result.setTrainerName((String) document.getFieldValue("trainerName_s"));
                    result.setStatus((String) document.getFieldValue("status_s"));

                    Object tagField = document.getFieldValue("tags_ss");
                    if (tagField instanceof Collection<?> tagCollection) {
                        result.setTags(tagCollection.stream().map(String::valueOf).toList());
                    }
                    return result;
                })
                .toList();
        } catch (SolrServerException | IOException exception) {
            throw new BadRequestException("Failed to query Solr");
        }
    }

    private String escape(String value) {
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }
}
