package servlets;

import beans.Row;
import beans.Table;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class AreaCheckServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long startTime = System.nanoTime();

        String xString = request.getParameter("xval");
        String yString = request.getParameter("yval").replace(',', '.');
        String rString = request.getParameter("rval");
        boolean isValuesValid = validateValues(xString, yString, rString);

        if (isValuesValid) {
            int x = Integer.parseInt(xString);
            double y = Double.parseDouble(yString);
            double r = Double.parseDouble(rString);
            boolean isHit = checkHit(x, y, r);

            OffsetDateTime currentTimeObject = OffsetDateTime.now(ZoneOffset.UTC);
            String currentTime;
            try {
                currentTimeObject = currentTimeObject.minusMinutes(Long.parseLong(request.getParameter("timezone")));
                currentTime = currentTimeObject.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            } catch (Exception exception) {
                currentTime = "HH:mm:ss";
            }

            String executionTime = String.valueOf(System.nanoTime() - startTime);

            Table entries = (Table) request.getSession().getAttribute("entries");
            if (entries == null) entries = new Table();
            entries.getRows().add(new Row(x, y, r, currentTime, executionTime, isHit));
            request.getSession().setAttribute("entries", entries);
        }

        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private boolean validateX(String xString) {
        try {
            Integer[] xRange = {-3, -2, -1, 0, 1, 2, 3, 4, 5};
            int x = Integer.parseInt(xString);
            return Arrays.asList(xRange).contains(x);
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private boolean validateY(String yString) {
        try {
            double y = Double.parseDouble(yString);
            return y > -5 && y < 5;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private boolean validateR(String rString) {
        try {
            double r = Double.parseDouble(rString);
            return r > 1 && r < 4;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private boolean validateValues(String xString, String yString, String rString) {
        return validateX(xString) && validateY(yString) && validateR(rString);
    }

    private boolean checkTriangle(int x, double y, double r) {
        return x <= 0 && y <= 0 && y >= (-x - r / 2);
    }

    private boolean checkRectangle(int x, double y, double r) {
        return x >= 0 && y >= 0 && x <= r && y <= r / 2;
    }

    private boolean checkCircle(int x, double y, double r) {
        return x >= 0 && y <= 0 && (Math.pow(x, 2) + Math.pow(y, 2)) <= Math.pow(r, 2);
    }

    private boolean checkHit(int x, double y, double r) {
        return checkTriangle(x, y, r) || checkRectangle(x, y, r) ||
                checkCircle(x, y, r);
    }
}