package com.tripwego.api.batch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by JG on 30/01/17.
 */
public class RefreshServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RefreshServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            LOGGER.info("--> RefreshServlet - START");
            LOGGER.info("--> RefreshServlet - END");
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
