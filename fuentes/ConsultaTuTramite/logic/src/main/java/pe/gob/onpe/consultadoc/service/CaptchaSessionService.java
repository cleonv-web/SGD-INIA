package pe.gob.onpe.consultadoc.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpSession;

/** Desafíos independientes por imagen, ligados a la sesión y de un solo uso. */
public final class CaptchaSessionService {
    private static final String ATTRIBUTE = CaptchaSessionService.class.getName();
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    static final long TTL = 10 * 60 * 1000L;
    static final int MAX_CHALLENGES = 8;
    static final class Challenge implements Serializable {
        final String answer;
        final long created;
        Challenge(String answer, long created) { this.answer = answer; this.created = created; }
    }
    @SuppressWarnings("unchecked")
    private static Map<String, Challenge> challenges(HttpSession session) {
        Map<String, Challenge> values = (Map<String, Challenge>) session.getAttribute(ATTRIBUTE);
        if (values == null) { values = new LinkedHashMap<String, Challenge>(); session.setAttribute(ATTRIBUTE, values); }
        return values;
    }
    public static String issue(HttpSession session) {
        synchronized (session) {
            Map<String, Challenge> values = challenges(session);
            Iterator<Challenge> it = values.values().iterator();
            long now = System.currentTimeMillis();
            while (it.hasNext()) { if (now - it.next().created > TTL) it.remove(); }
            while (values.size() >= MAX_CHALLENGES) values.remove(values.keySet().iterator().next());
            StringBuilder answer = new StringBuilder();
            for (int i = 0; i < 5; i++) answer.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
            String token = UUID.randomUUID().toString(); values.put(token, new Challenge(answer.toString(), now));
            return token;
        }
    }
    public static boolean validate(HttpSession session, String token, String answer) {
        if (session == null || token == null || answer == null) return false;
        synchronized (session) {
            Challenge value = challenges(session).remove(token);
            return value != null && System.currentTimeMillis() - value.created <= TTL
                && value.answer.equals(answer.trim().toUpperCase(Locale.ROOT));
        }
    }
    public static BufferedImage render(HttpSession session, String token) {
        String answer;
        synchronized (session) { answer = challenges(session).get(token).answer; }
        BufferedImage image = new BufferedImage(220, 76, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(243, 248, 240)); g.fillRect(0, 0, 220, 76);
            for (int i = 0; i < 9; i++) {
                g.setColor(new Color(130 + RANDOM.nextInt(90), 165 + RANDOM.nextInt(60), 130 + RANDOM.nextInt(90)));
                g.drawLine(RANDOM.nextInt(220), RANDOM.nextInt(76), RANDOM.nextInt(220), RANDOM.nextInt(76));
            }
            for (int i = 0; i < 5; i++) {
                Graphics2D letter = (Graphics2D) g.create();
                letter.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 34 + RANDOM.nextInt(5)));
                letter.setColor(new Color(20 + RANDOM.nextInt(50), 65 + RANDOM.nextInt(40), 35 + RANDOM.nextInt(45)));
                letter.translate(17 + 39 * i, 51 + RANDOM.nextInt(7) - 3);
                letter.rotate((RANDOM.nextDouble() - .5) * .35);
                letter.drawString(answer.substring(i, i + 1), 0, 0); letter.dispose();
            }
        } finally { g.dispose(); }
        return image;
    }
    private CaptchaSessionService() { }
}
