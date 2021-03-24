package vn.thuephonghoc.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

/**
 * String tool class, inherited from org.apache.commons.lang3.StringUtils class
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    private static final char SEPARATOR ='_';

    private static final String UNKNOWN = "unknown";


    /**
     * Camel case nomenclature tool
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i <s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Camel case nomenclature tool
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Camel case nomenclature tool
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i <s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i <(s.length()-1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i> 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * The day of the acquisition is the day of the week
     */
    public static String getWeekDay() {
// String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int w = cal.get(Calendar.DAY_OF_WEEK)-1;
        if (w <0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static String getBrowser(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser browser = userAgent.getBrowser();
        return browser.getName();
    }

    /**
     * * Determine whether an object is empty
     *
     * @param object Object
     * @return true: empty false: not empty
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

}
