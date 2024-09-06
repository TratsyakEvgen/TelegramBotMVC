package com.tratsiak.telegrambotmvc.core.parser;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Request {
    private final long id;
    private final String body;
    private final String path;
}
