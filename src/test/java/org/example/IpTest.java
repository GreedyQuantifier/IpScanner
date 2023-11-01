package org.example;

import junit.framework.TestCase;
import org.example.service.IpFabric;
import org.junit.Test;

public class IpTest extends TestCase {

    @Test
    public void testRangeIp() {
        assertEquals(IpFabric.build("192.168.1.0 \\24").list().size(),256);

        assertEquals(IpFabric.build("192.168.1.0 \\23").list().size(),256*2);

        assertEquals(IpFabric.build("192.168.1.128 \\25").list().size(),128);


    }


}
