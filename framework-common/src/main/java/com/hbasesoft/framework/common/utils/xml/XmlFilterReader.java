package com.hbasesoft.framework.common.utils.xml;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class XmlFilterReader extends FilterReader {

    /** max_blank_code */
    private static final int MAX_BLANK_CODE = 0x08;

    /**
     * @param in
     */
    public XmlFilterReader(final Reader in) {
        super(in);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param cbuf
     * @param off
     * @param len
     * @return 值
     * @throws IOException <br>
     */
    public int read(final char cbuf[], final int off, final int len) throws IOException {
        int num = in.read(cbuf, off, len);
        for (int i = 0; i < cbuf.length; i++) {
            char ch = cbuf[i];
            if (ch >= 0x00 && ch <= MAX_BLANK_CODE) {
                cbuf[i] = ' ';
            }
        }
        return num;
    }
}
