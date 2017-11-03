package com.tripwego.api.batch;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.tripwego.api.document.DocumentService;
import com.tripwego.api.trip.TripQueries;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.tripwego.api.ConfigurationConstants.SEARCH_TRIP_INDEX;
import static com.tripwego.api.Constants.*;

@SuppressWarnings("serial")
public class StoreTripsInDocumentServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(StoreTripsInDocumentServlet.class.getName());

    private TripQueries tripQueries = new TripQueries();
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private DocumentService documentService = new DocumentService();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            //documentService.deleteAll();
            final List<Entity> tripEntitiesToStoreInDocument = tripQueries.findTripEntitiesToStoreInDocument();
            LOGGER.info("--> nb trip to store (max 200) : " + tripEntitiesToStoreInDocument.size());
            final List<Document> documents = new ArrayList<>();
            for (Entity entity : tripEntitiesToStoreInDocument) {
                final Document document = createDocumentFromEntity(entity);
                documents.add(document);
            }
            documentService.indexDocuments(SEARCH_TRIP_INDEX, documents);
            for (Entity entity : tripEntitiesToStoreInDocument) {
                entity.setProperty(IS_STORE_IN_DOCUMENT, true);
            }
            datastore.put(tripEntitiesToStoreInDocument);
            LOGGER.info("Cron Job has been executed");
        } catch (Exception ex) {
            LOGGER.warning(ex.getMessage());
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    private Document createDocumentFromEntity(Entity entity) {
        final String documentId = KeyFactory.keyToString(entity.getKey());
        final Document document = Document.newBuilder()
                .setId(documentId)
                .addField(Field.newBuilder().setName(NAME).setText(String.valueOf(entity.getProperty(NAME))))
                .addField(Field.newBuilder().setName("domain").setAtom(KIND_TRIP))
                .addField(Field.newBuilder().setName("published").setDate(new Date()))
                .build();
        return document;
    }
}
