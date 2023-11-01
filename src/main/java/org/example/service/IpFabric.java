package org.example.service;


import org.example.util.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class IpFabric {

    private static final int MIN_VALUE_MASK = 16;

    private final byte[] octets;
    private final int mask;
    private byte[] limit;


    public IpFabric(byte[] octets, int mask) {
        this.mask = mask;
        this.octets = octets;
        initOctet();
    }

    public static IpFabric build(String ip) throws IllegalArgumentException {

        byte[] bytes = new byte[4];

        if (ip == null || !ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s*/\\d{1,2}"))
            throw new IllegalArgumentException("Ip isn't correct");

        String[] split = ip.trim().split("/");

        String[] octets = split[0].trim().split("\\.");

        int mask = Utils.toInt(split[1]);


        if (mask < MIN_VALUE_MASK || mask > 31) throw new IllegalArgumentException("Illegal mask value");

        for (int i = 0; i < octets.length; i++) {
            int value = Utils.toInt(octets[i]);
            if (value < 0 || value > 255) throw new IllegalArgumentException("Illegal octet value");
            bytes[i] = (byte) value;
        }
        return new IpFabric(bytes, mask);
    }


    private void initOctet() {
        int indexChangeOctet = mask / 8;
        int notChangeBits = mask % 8;


//        Compute minimum
        octets[indexChangeOctet] &= (byte) ((byte) (Math.pow(2, notChangeBits) - 1) << (8 - notChangeBits));

//        Compute maximum
        limit = Arrays.copyOf(octets, 4);
        limit[indexChangeOctet] |= (byte) (Math.pow(2, 8 - notChangeBits) - 1);

        for (int i = indexChangeOctet + (notChangeBits > 0 ? 1 : 0); i < octets.length; i++) {
            octets[i] = 0;
            limit[i] = -1;
        }

    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        IpIterator ipIterator = new IpIterator(octets, limit);
        do {
            list.add(ipIterator.next());
        } while (ipIterator.hasNext());

        list.add(ipIterator.next());


        return list;
    }


}

class IpIterator implements Iterator<String> {

    byte[] bytes;

    byte[] limit;


    public IpIterator(byte[] minBytes, byte[] maxBytes) {
        bytes = minBytes;
        limit = maxBytes;
    }

    @Override
    public boolean hasNext() {
        nextIPAddress(bytes);
        return !Arrays.equals(bytes, limit);
    }

    @Override
    public String next() {
        try {
            return "https:/" + InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private void nextIPAddress(byte[] bytes) {
        for (int i = bytes.length - 1; i >= 0; i--)
            if (++bytes[i] != 0) break;
    }
}

