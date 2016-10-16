package com.example.tripwegoapi.batch;

import com.example.tripwegoapi.entity.RegionCounterRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CountTripByRegionServlet extends HttpServlet {

    private static final Logger _logger = Logger.getLogger(CountTripByRegionServlet.class.getName());

    private RegionCounterRepository regionCounterRepository = new RegionCounterRepository();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            _logger.info("--> CountTripByRegionServlet - START");
            regionCounterRepository.refresh();
            _logger.info("--> CountTripByRegionServlet - END");
        } catch (Exception ex) {
            _logger.warning(ex.getMessage());
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

}
