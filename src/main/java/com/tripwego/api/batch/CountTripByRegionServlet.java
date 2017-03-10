package com.tripwego.api.batch;

import com.tripwego.api.region.RegionCounterRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CountTripByRegionServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CountTripByRegionServlet.class.getName());

    private RegionCounterRepository regionCounterRepository = new RegionCounterRepository();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            LOGGER.info("--> CountTripByRegionServlet - START");
            regionCounterRepository.refresh();
            LOGGER.info("--> CountTripByRegionServlet - END");
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
