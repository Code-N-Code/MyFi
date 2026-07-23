package com.codencode.myfi.core.http;

import fi.iki.elonen.NanoHTTPD;

public interface HttpEndpoint {
    NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session);
}
