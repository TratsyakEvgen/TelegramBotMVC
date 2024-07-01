package com.tratsiak.telegrambotmvc.core.path;

import java.util.Map;

public interface PathParser {
    String getPath(String path);

    Map<String, String> getParam(String path);
}
