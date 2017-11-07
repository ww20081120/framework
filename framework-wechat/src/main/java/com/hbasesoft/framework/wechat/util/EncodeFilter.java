/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.util;

import java.net.URLEncoder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.wx.util <br>
 */
public class EncodeFilter {

    // 过滤大部分html字符
    public static String encode(String input) {
        if (input == null) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0, c = input.length(); i < c; i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#x27;");
                    break;
                case '/':
                    sb.append("&#x2F;");
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }

    // js端过滤
    public static String encodeForJS(String input) {
        if (input == null) {
            return input;
        }

        StringBuilder sb = new StringBuilder(input.length());

        for (int i = 0, c = input.length(); i < c; i++) {
            char ch = input.charAt(i);

            // do not encode alphanumeric characters and ',' '.' '_'
            if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == ',' || ch == '.'
                || ch == '_') {
                sb.append(ch);
            }
            else {
                String temp = Integer.toHexString(ch);

                // encode up to 256 with \\xHH
                if (ch < 256) {
                    sb.append('\\').append('x');
                    if (temp.length() == 1) {
                        sb.append('0');
                    }
                    sb.append(temp.toLowerCase());

                    // otherwise encode with \\uHHHH
                }
                else {
                    sb.append('\\').append('u');
                    for (int j = 0, d = 4 - temp.length(); j < d; j++) {
                        sb.append('0');
                    }
                    sb.append(temp.toUpperCase());
                }
            }
        }

        return sb.toString();
    }

    /**
     * css非法字符过滤 http://www.w3.org/TR/CSS21/syndata.html#escaped-characters
     */
    public static String encodeForCSS(String input) {
        if (input == null) {
            return input;
        }

        StringBuilder sb = new StringBuilder(input.length());

        for (int i = 0, c = input.length(); i < c; i++) {
            char ch = input.charAt(i);

            // check for alphanumeric characters
            if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9') {
                sb.append(ch);
            }
            else {
                // return the hex and end in whitespace to terminate
                sb.append('\\').append(Integer.toHexString(ch)).append(' ');
            }
        }
        return sb.toString();
    }

    /**
     * URL参数编码 http://en.wikipedia.org/wiki/Percent-encoding
     */
    public static String encodeURIComponent(String input) {
        return encodeURIComponent(input, "utf-8");
    }

    public static String encodeURIComponent(String input, String encoding) {
        if (input == null) {
            return input;
        }
        String result;
        try {
            result = URLEncoder.encode(input, encoding);
        }
        catch (Exception e) {
            result = "";
        }
        return result;
    }

    public static boolean isValidURL(String input) {
        if (input == null || input.length() < 8) {
            return false;
        }
        char ch0 = input.charAt(0);
        if (ch0 == 'h') {
            if (input.charAt(1) == 't' && input.charAt(2) == 't' && input.charAt(3) == 'p') {
                char ch4 = input.charAt(4);
                if (ch4 == ':') {
                    if (input.charAt(5) == '/' && input.charAt(6) == '/') {

                        return isValidURLChar(input, 7);
                    }
                    else {
                        return false;
                    }
                }
                else if (ch4 == 's') {
                    if (input.charAt(5) == ':' && input.charAt(6) == '/' && input.charAt(7) == '/') {

                        return isValidURLChar(input, 8);
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }

        }
        else if (ch0 == 'f') {
            if (input.charAt(1) == 't' && input.charAt(2) == 'p' && input.charAt(3) == ':' && input.charAt(4) == '/'
                && input.charAt(5) == '/') {

                return isValidURLChar(input, 6);
            }
            else {
                return false;
            }
        }
        return false;
    }

    public static boolean isValidURLChar(String url, int start) {
        for (int i = start, c = url.length(); i < c; i++) {
            char ch = url.charAt(i);
            if (ch == '"' || ch == '\'') {
                return false;
            }
        }
        return true;
    }

}
