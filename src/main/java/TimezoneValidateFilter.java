import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String utc = req.getParameter("timezone");
        if(utc == null){
            chain.doFilter(req,resp);
        }
        if(utc != null && utc.length()>=5){
            String rightUtc = utc.substring(0,3);
            if(!rightUtc.equals("UTC")){
                sendInvalidTimezone(resp, "Prefix 'UTC' is not right");
            }
            try {
                String clearUtc = utc.replaceAll("\\s", "+");
                int timeZone = Integer.parseInt(clearUtc.substring(3).trim());
                if (timeZone > 14 || timeZone < -12) {
                    sendInvalidTimezone(resp, "Invalid timezone");
                } else {
                    Cookie cookie = new Cookie("lastTimezone", clearUtc);
                    resp.addCookie(cookie);
                    chain.doFilter(req, resp);
                }
            }
            catch (Exception e){
                sendInvalidTimezone(resp, e.getMessage());
            }
        }
    }
    private static void sendInvalidTimezone(HttpServletResponse resp, String message) throws  IOException{
        System.out.println(message);
        resp.setContentType("text/html");
        resp.setStatus(400);
        resp.getWriter().write("Invalid timezone");
        resp.getWriter().close();
    }
}
