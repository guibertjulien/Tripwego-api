package com.tripwego.api.batch;

import com.tripwego.api.trip.TripRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static com.tripwego.api.ConfigurationConstants.NB_DAYS_BEFORE_REMOVE;

@SuppressWarnings("serial")
public class DeleteTripsCancelledServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CountTripByRegionServlet.class.getName());

    private TripRepository tripRepository = new TripRepository();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            tripRepository.deleteTripsCancelled(NB_DAYS_BEFORE_REMOVE);
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

}
