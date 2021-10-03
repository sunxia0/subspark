package org.subspark;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final int DEFAULT_CLEAN_INTERVAL = 5 * 60;

    public static final String SESSION_ID_COOKIE_NAME = "SESSION_ID";

    private final Map<String, SessionImpl> sessionHolder = new ConcurrentHashMap<>();
    private final Timer cleanWorker = new Timer();

    SessionManager() {
        startCleanTask(DEFAULT_CLEAN_INTERVAL);
    }

    SessionManager(int cleanInterval) {
        startCleanTask(cleanInterval);
    }

    private static String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static SessionImpl createSessionInstance() {
        String id = generateSessionId();
        return new SessionImpl(id);
    }

    static void setCookieSessionId(Request request, Response response) {
        Session session = request.session(false);
        if (session != null) {
            int maxAge = (int)((session.creationTime() + session.maxInactiveInterval() * 1000 - System.currentTimeMillis()) / 1000);
            response.cookie(SESSION_ID_COOKIE_NAME, session.id(), maxAge, true);
        }
    }

    void startCleanTask(int cleanInterval) {
        cleanWorker.schedule(new CleanTimerTask(), 0, cleanInterval);
    }

    void shutdown() {
        cleanWorker.cancel();
    }

    Session newSession() {
        SessionImpl session = createSessionInstance();
        sessionHolder.put(session.id(), session);
        return session;
    }

    Session fromSessionId(String id) {
        // If `id == null` or `id != null && !session.isValid()`, `null` will be returned.
        // Otherwise, corresponding session will be returned.
        if (id != null) {
            SessionImpl session = sessionHolder.get(id);
            if (session != null && session.isValid()) {
                return session;
            }
        }
        return null;
    }

    private class CleanTimerTask extends TimerTask {
        @Override
        public void run() {
            sessionHolder.entrySet().removeIf(entry -> !entry.getValue().isValid());
        }
    }
}
