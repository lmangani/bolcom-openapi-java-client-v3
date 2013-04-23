package com.bol.openapi.client.util;

import java.io.StringWriter;
import javax.xml.bind.JAXB;
import org.junit.Ignore;

@Ignore
public final class TestUtils {
    
    private TestUtils() {        
    }

    public static <T> String serializeUsingJAXB(T entity) {
        final StringWriter writer = new StringWriter();
        JAXB.marshal(entity, writer);
        return writer.toString();
    }
    
}
