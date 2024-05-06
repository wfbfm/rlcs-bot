package com.wfbfm.rlcsbot.websocket;


import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.KEYSTORE_PASSWORD;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.KEYSTORE_PATH;

public final class SslContextProvider
{
    public static SSLContext getSslContext()
    {
        try
        {
            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());

            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, KEYSTORE_PASSWORD.toCharArray());

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);
            return sslContext;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}