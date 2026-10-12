package pe.gob.onpe.consultadoc.web.servlet;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.*;
import pe.gob.onpe.consultadoc.service.CaptchaSessionService;
public class CaptchaSrvlt extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String token = CaptchaSessionService.issue(session);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("X-Captcha-Token", token);
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setContentType("image/png");
        ImageIO.write(CaptchaSessionService.render(session, token), "png", response.getOutputStream());
    }
}
