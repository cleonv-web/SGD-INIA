package pe.gob.onpe.consultadoc.service;

import java.lang.reflect.*;
import java.util.*;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.junit.Assert.*;

public class CaptchaSessionServiceTest {
    private HttpSession session() {
        final Map<String,Object> attributes = new HashMap<String,Object>();
        return (HttpSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpSession.class}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) {
                if (method.getName().equals("getAttribute")) return attributes.get(args[0]);
                if (method.getName().equals("setAttribute")) { attributes.put((String) args[0], args[1]); return null; }
                return null;
            }
        });
    }
    @SuppressWarnings("unchecked")
    private Map<String,CaptchaSessionService.Challenge> challenges(HttpSession s) {
        return (Map<String,CaptchaSessionService.Challenge>) s.getAttribute(CaptchaSessionService.class.getName());
    }
    @Test public void acceptsOnceAndIgnoresCase() {
        HttpSession s=session(); String token=CaptchaSessionService.issue(s);
        String answer=challenges(s).get(token).answer;
        assertTrue(CaptchaSessionService.validate(s,token," " + answer.toLowerCase(Locale.ROOT) + " "));
        assertFalse(CaptchaSessionService.validate(s,token,answer));
    }
    @Test public void rejectsAnotherSession() {
        HttpSession a=session(), b=session(); String token=CaptchaSessionService.issue(a);
        String answer=challenges(a).get(token).answer;
        assertFalse(CaptchaSessionService.validate(b,token,answer));
        assertTrue(CaptchaSessionService.validate(a,token,answer));
    }
    @Test public void incorrectAttemptConsumesChallenge() {
        HttpSession s=session(); String token=CaptchaSessionService.issue(s);
        String answer=challenges(s).get(token).answer;
        assertFalse(CaptchaSessionService.validate(s,token,"WRONG-ANSWER"));
        assertFalse(CaptchaSessionService.validate(s,token,answer));
    }
    @Test public void rejectsExpiredAndMissingChallenges() {
        HttpSession s=session(); String token=CaptchaSessionService.issue(s);
        challenges(s).put(token,new CaptchaSessionService.Challenge("ABCDE",System.currentTimeMillis()-CaptchaSessionService.TTL-1));
        assertFalse(CaptchaSessionService.validate(s,token,"ABCDE"));
        assertFalse(CaptchaSessionService.validate(s,"unknown","ABCDE"));
        assertFalse(CaptchaSessionService.validate(null,token,"ABCDE"));
        assertFalse(CaptchaSessionService.validate(s,null,null));
    }
    @Test public void simultaneousTabsHaveIndependentChallenges() {
        HttpSession s=session(); String a=CaptchaSessionService.issue(s), b=CaptchaSessionService.issue(s);
        String answerA=challenges(s).get(a).answer, answerB=challenges(s).get(b).answer;
        assertNotEquals(a,b);
        assertTrue(CaptchaSessionService.validate(s,a,answerA));
        assertTrue(CaptchaSessionService.validate(s,b,answerB));
    }
    @Test public void limitsSessionMemoryAndRendersWithoutJcaptcha() {
        HttpSession s=session(); String first=CaptchaSessionService.issue(s), last=first;
        for(int i=0;i<CaptchaSessionService.MAX_CHALLENGES;i++) last=CaptchaSessionService.issue(s);
        assertEquals(CaptchaSessionService.MAX_CHALLENGES,challenges(s).size());
        assertFalse(challenges(s).containsKey(first));
        assertEquals(220,CaptchaSessionService.render(s,last).getWidth());
        assertEquals(76,CaptchaSessionService.render(s,last).getHeight());
    }
}
