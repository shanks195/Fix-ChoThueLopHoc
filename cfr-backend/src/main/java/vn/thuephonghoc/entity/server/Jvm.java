package vn.thuephonghoc.entity.server;

import java.lang.management.ManagementFactory;

import vn.thuephonghoc.utils.Arith;
import vn.thuephonghoc.utils.DateUtils;

/**
 * JVM related information
 *
 */
public class Jvm {

    /**
     * The total memory occupied by the current JVM (M)
     */
    private double total;

    /**
     * JVM maximum available memory total (M)
     */
    private double max;

    /**
     * JVM free memory (M)
     */
    private double free;

    /**
     * JDK version
     */
    private String version;

    /**
     * JDK path
     */
    private String home;


    public double getTotal() {
        return Arith.div(total, (1024 * 1024), 2);
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMax() {
        return Arith.div(max, (1024 * 1024), 2);
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getFree() {
        return Arith.div(free, (1024 * 1024), 2);
    }

    public void setFree(double free) {
        this.free = free;
    }

    public double getUsed() {
        return Arith.div(total - free, (1024 * 1024), 2);
    }

    public double getUsage() {
        return Arith.mul(Arith.div(total - free, total, 4), 100);
    }

    /**
     * 获取JDK名称
     */
    public String getName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    /**
     * JDK启动时间
     */
    public String getStartTime() {
        return DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getServerStartDate());
    }

    /**
     * JDK运行时间
     */
    public String getRunTime() {
        return DateUtils.getDatePoor(DateUtils.getNowDate(), DateUtils.getServerStartDate());
    }

}
