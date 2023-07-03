import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine templateEngine;
    private String utc;
    @Override
    public void init(){
        templateEngine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("D:\\JavaGoIt\\JavaDev\\Ex9\\src\\main\\webapp\\WEB-INF\\templates\\");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(templateEngine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        templateEngine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=utf-8");
        utc = req.getParameter("timezone");
        checkCookie(req);
        String time;
        Map<String, String> stringMap = new LinkedHashMap<>();
        if(utc != null) {
            utc = utc.replaceAll("\\s", "+");
            Long hours = Long.parseLong(utc.substring(3));
            time = LocalDateTime.now(ZoneId.of("UTC")).plusHours(hours)
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss"));
            stringMap.put("UTC", utc);
            stringMap.put("time", time);
        } else {
            time = LocalDateTime.now(ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss"));
            stringMap.put("UTC", "UTC 0");
            stringMap.put("time", time);
        }
        Context context = new Context(req.getLocale(), Map.of("timeParams", stringMap));
        templateEngine.process("UTCtime", context, resp.getWriter());
        resp.getWriter().close();
    }

    private void checkCookie(HttpServletRequest req){
        if(utc==null){
            Cookie[] cookies = req.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies){
                    if(cookie.getName().equals("lastTimezone")){
                        utc = cookie.getValue();
                    }
                }
            }
        }
    }
}
