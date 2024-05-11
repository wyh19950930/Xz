package com.chuzhi.xzyx.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author : wyh
 * @Time : On 2023/6/7 17:13
 * @Description : PlurkInputStream
 */
public class PlurkInputStream extends FilterInputStream {

    public PlurkInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read(byte[] buffer, int offset, int count)
            throws IOException {
        int ret = super.read(buffer, offset, count);
        for ( int i = 6; i < buffer.length - 4; i++ ) {
            if ( buffer[i] == 0x2c ) {
                if ( buffer[i + 2] == 0 && buffer[i + 1] > 0
                        && buffer[i + 1] <= 48 ) {
                    buffer[i + 1] = 0;
                }
                if ( buffer[i + 4] == 0 && buffer[i + 3] > 0
                        && buffer[i + 3] <= 48 ) {
                    buffer[i + 3] = 0;
                }
            }
        }
        return ret;
    }

}