// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpClient
{
    public final Map<String, String> Headers = new LinkedHashMap<String, String>();

    public HttpClient(final String userAgent)
    {
        Headers.put("User-Agent", userAgent);
    }

    HttpURLConnection createConnection(final URI uri, final String method, final Action<HttpURLConnection> interceptor)
    {
        final URL url;
        try
        {
            url = uri.toURL();
        }
        catch (final MalformedURLException e)
        {
            throw new Error(e);
        }

        final HttpURLConnection connection;
        try
        {
            connection = (HttpURLConnection) url.openConnection();
        }
        catch (final IOException e)
        {
            throw new Error(e);
        }

        try
        {
            connection.setRequestMethod(method);
        }
        catch (final ProtocolException e)
        {
            throw new Error(e);
        }

        for (final Map.Entry<String, String> entry : Headers.entrySet())
        {
            final String key = entry.getKey();
            final String value = entry.getValue();
            connection.setRequestProperty(key, value);
        }

        if (interceptor != null)
        {
            interceptor.call(connection);
        }

        return connection;
    }

    public HttpURLConnection head(final URI uri) throws IOException
    {
        return head(uri, null);
    }

    public HttpURLConnection head(final URI uri, final Action<HttpURLConnection> interceptor) throws IOException
    {
        final HttpURLConnection connection = createConnection(uri, "HEAD", interceptor);
        connection.connect();

        return connection;
    }
}
