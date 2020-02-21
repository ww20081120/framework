package com.hbasesoft.framework.common.utils.xml;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class XmlFilterReader extends FilterReader {

    /**
     * @param in
     */
    public XmlFilterReader(Reader in) {
        super(in);
    }

    public int read(char cbuf[], int off, int len) throws IOException {
        int num = in.read(cbuf, off, len);
        for (int i = 0; i < cbuf.length; i++) {
            char ch = cbuf[i];
            if (ch >= 0x00 && ch <= 0x08) {
                cbuf[i] = ' ';
            }
        }
        return num;
    }
}
