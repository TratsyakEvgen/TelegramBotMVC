package com.tratsiak.telegrambotmvc.core.session;

import java.util.Optional;

public interface SessionInitializer {
    Optional<Session> init(long id);
}
