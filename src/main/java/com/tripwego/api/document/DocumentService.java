package com.tripwego.api.document;

import com.google.appengine.api.search.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.tripwego.api.ConfigurationConstants.SEARCH_TRIP_INDEX;

/**
 * Created by JG on 23/10/17.
 */
public class DocumentService {

    private static final Logger LOGGER = Logger.getLogger(DocumentService.class.getName());

    public Results<ScoredDocument> searchTripByText(String text) {
        Results<ScoredDocument> results;
        // [START search_document]
        final int maxRetry = 3;
        int attempts = 0;
        int delay = 2;
        while (true) {
            try {
                String queryString = "name = " + text;
                results = getIndex().search(queryString);
            } catch (SearchException e) {
                if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
                        && ++attempts < maxRetry) {
                    // retry
                    try {
                        Thread.sleep(delay * 1000);
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                    delay *= 2; // easy exponential backoff
                    continue;
                } else {
                    throw e;
                }
            }
            break;
        }
        // [END search_document]
        return results;
    }

    public void deleteAll() {
        // [START delete_documents]
        try {
            // looping because getRange by default returns up to 100 documents at a time
            while (true) {
                List<String> docIds = new ArrayList<>();
                // Return a set of doc_ids.
                GetRequest request = GetRequest.newBuilder().setReturningIdsOnly(true).build();
                GetResponse<Document> response = getIndex().getRange(request);
                if (response.getResults().isEmpty()) {
                    break;
                }
                for (Document doc : response) {
                    docIds.add(doc.getId());
                }
                getIndex().delete(docIds);
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete documents", e);
        }
        // [END delete_documents]
    }

    /**
     * Put a given document into an index with the given indexName.
     *
     * @param indexName The name of the index.
     * @param document  A document to add.
     * @throws InterruptedException When Thread.sleep is interrupted.
     */
    // [START putting_document_with_retry]
    public void indexADocument(String indexName, Document document)
            throws InterruptedException {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

        final int maxRetry = 3;
        int attempts = 0;
        int delay = 2;
        while (true) {
            try {
                index.put(document);
            } catch (PutException e) {
                if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
                        && ++attempts < maxRetry) { // retrying
                    Thread.sleep(delay * 1000);
                    delay *= 2; // easy exponential backoff
                    continue;
                } else {
                    throw e; // otherwise throw
                }
            }
            break;
        }
    }
    // [END putting_document_with_retry]

    public void indexDocuments(String indexName, List<Document> documents) throws InterruptedException {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

        final int maxRetry = 3;
        int attempts = 0;
        int delay = 2;
        while (true) {
            try {
                index.put(documents);
            } catch (PutException e) {
                if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
                        && ++attempts < maxRetry) { // retrying
                    Thread.sleep(delay * 1000);
                    delay *= 2; // easy exponential backoff
                    continue;
                } else {
                    throw e; // otherwise throw
                }
            }
            break;
        }
    }

    private Index getIndex() {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName(SEARCH_TRIP_INDEX).build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
        return index;
    }
}
