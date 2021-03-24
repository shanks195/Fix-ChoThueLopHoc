package vn.thuephonghoc.utils.ip;

import com.alibaba.fastjson.JSONObject;

import vn.thuephonghoc.utils.StringUtils;
import vn.thuephonghoc.utils.http.HttpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get address class
 *
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";

    public static String getRealAddressByIP(String ip) {
        String address = "Unknown";
        // Intranet does not query
        if (IpUtils.internalIp(ip)) {
            return "Intranet IP";
        }
        String rspStr = HttpUtils.sendPost(IP_URL, "ip=" + ip);
        if (StringUtils.isEmpty(rspStr)) {
            log.error("Get location exception {}", ip);
            return address;
        }
        JSONObject obj = JSONObject.parseObject(rspStr);
        JSONObject data = obj.getObject("data" , JSONObject.class);
        String region = data.getString("region");
        String city = data.getString("city");
        String isp = data.getString("isp");
        address = region + " " + city + " " + isp;
        return address;
    }

}
