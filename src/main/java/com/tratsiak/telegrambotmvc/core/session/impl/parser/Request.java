package com.tratsiak.telegrambotmvc.core.session.impl.parser;

import lombok.Getter;


@Getter
public class Request {
    private final long id;
    private final String body;
    private final String path;

    Request(long id, String body, String path) {
        this.id = id;
        this.body = body;
        this.path = path;
    }

    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    public static class RequestBuilder {
        private long id;
        private String body;
        private String path;

        RequestBuilder() {
        }

        public RequestBuilder id(long id) {
            this.id = id;
            return this;
        }

        public RequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        public RequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        public Request build() {
            return new Request(this.id, this.body, this.path);
        }

        public String toString() {
            return "Request.RequestBuilder(id=" + this.id + ", body=" + this.body + ", path=" + this.path + ")";
        }
    }
}
