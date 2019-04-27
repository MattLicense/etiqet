package com.neueda.etiqet.rest.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.client.http.HttpRequestFactory;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.rest.RestConfig;
import com.neueda.etiqet.rest.message.impl.HttpRequestMsgTest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RestClientTest {

    private RestClient client;

    private String primaryConfig;

    private String secondaryConfig;

    private Cdr testCdr;

    private GlobalConfig globalConfig;

    @Before
    public void setUp() throws EtiqetException {
        globalConfig = GlobalConfig.getInstance(RestConfig.class);
        // override the HttpRequestFactory and Config objects for testing purposes
        primaryConfig = getClass().getClassLoader().getResource("config/ok/client.cfg").getPath();
        secondaryConfig = getClass().getClassLoader().getResource("config/ok/secondary_client.cfg").getPath();
        testCdr = new Cdr("test_01");
        testCdr.set("$httpEndpoint", "/test/api");
        testCdr.set("$httpVerb", "GET");
        testCdr.set("$header.responsecode", "200");
        testCdr.set("$header.responsebody", "{ \"text\" : \"Everything OK\" }");

        client = new RestClient(primaryConfig, secondaryConfig) {
            @Override
            HttpRequestFactory getHttpRequestFactory() {
                return HttpRequestMsgTest.getHttpRequestFactory();
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        Field field = GlobalConfig.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(globalConfig, null);
    }

    @Test
    public void testConstructor() throws EtiqetException {
        RestClient restClient = new RestClient(primaryConfig);
        assertNotNull(restClient.getPrimaryConfig());
        assertNull(restClient.getSecondaryConfig());
        restClient = new RestClient(primaryConfig, secondaryConfig);
        assertNotNull(restClient.getPrimaryConfig());
        assertNotNull(restClient.getSecondaryConfig());
    }

    @Test
    public void testIsAdmin() {
        for (String msgType : Arrays.asList("", "200", "404", "GET", "PUT", "POST", "DELETE", "301")) {
            assertFalse(client.isAdmin(msgType));
        }
    }

    @Test
    public void testDefaultSession() {
        assertEquals("", client.getDefaultSessionId());
    }

    @Test
    public void testIsLoggedOn() {
        assertFalse(client.isLoggedOn());
    }

    @Test
    public void testLaunchClient() throws EtiqetException {
        assertNull(client.getRequestFactory());
        client.launchClient();
        assertNotNull(client.getRequestFactory());
    }

    @Test
    public void testFailover() throws EtiqetException {
        assertTrue("Client was instantiated with 2 config files, so should be able to failover",
            client.canFailover());
        client.launchClient();
        assertNotNull(client.getClientConfig().getString("baseUrl"));
        client.failover();
        assertNotNull(client.getClientConfig().getString("baseUrl"));
        client.failover();
        assertNotNull(client.getClientConfig().getString("baseUrl"));
    }

    @Test
    public void testSendMessage() throws EtiqetException {
        client.send(testCdr);
        Cdr cdr = client.waitForMsgType("200", 5000);
        assertTrue(cdr.containsKey("text"));
        assertEquals("Everything OK", cdr.getAsString("text"));

        testCdr.clear();
        testCdr.set("$httpEndpoint", "/test/api");
        testCdr.set("$httpVerb", "GET");
        testCdr.set("$header.responsecode", "200");
        testCdr.set("$header.authorization", "key");
        client.send(testCdr);
        cdr = client.waitForAppMsg();
        assertNotNull(cdr.getItems());

        testCdr.set("$httpEndpoint", "/test/api");
        testCdr.set("$httpVerb", "GET");
        testCdr.set("$header.responsecode", "-1");
        try {
            client.send(testCdr, null);
            fail("Should have thrown an exception because the response code was set to -1");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Error sending HTTP Request", e.getMessage());
            assertTrue(e.getCause() instanceof IOException);
            assertEquals("ResponseCode -1 sent", e.getCause().getMessage());
        }
    }

    @Test
    public void testDecode() throws EtiqetException {
        client.send(testCdr);
        assertNotNull(client.waitForAppMsg());
    }

    @Test
    public void testwaitForNoMsgType() throws EtiqetException {
        assertNull(client.waitForNoMsgType("test", 100));
        client.send(testCdr);

        try {
            assertNull(client.waitForNoMsgType("test", 100));
            fail("Expected EtiqetException not thrown");
        } catch (EtiqetException e) {
        }
    }

    @Test
    public void testGetMsgName() {
        assertEquals("test_01", client.getMsgName("test_01"));
        assertEquals("400", client.getMsgName("400"));
    }

    @Test
    public void testGetMsgType() {
        assertNotNull(client.getMsgType("test_01"));
    }

    @Test
    public void testGetRequestFactory() throws EtiqetException {
        assertNull(client.getRequestFactory());
        client.launchClient();
        assertNotNull(client.getRequestFactory());
    }

    @Test
    public void testHttpRequestFactory() throws EtiqetException {
        RestClient restClient = new RestClient(primaryConfig, secondaryConfig);
        assertNotNull(restClient.getHttpRequestFactory());
    }
}
