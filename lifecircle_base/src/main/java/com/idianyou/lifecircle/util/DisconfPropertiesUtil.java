package com.idianyou.lifecircle.util;


import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Description: 读取disconf.properties配置
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2019/11/4 11:24
 */
@Slf4j
public class DisconfPropertiesUtil {

    private static DisconfPropertiesUtil instance = null;

    private static final String FILE_PATH = "application.properties";

    private Properties disconfProp = null;

    public static DisconfPropertiesUtil getInstance() {
        if (instance == null) {
            synchronized (DisconfPropertiesUtil.class) {
                if (instance == null) {
                    instance = new DisconfPropertiesUtil();
                }
            }
        }
        return instance;
    }

    private DisconfPropertiesUtil() {
        try {
            InputStream is = DisconfPropertiesUtil.class.getClassLoader().getResourceAsStream(FILE_PATH);
            disconfProp = new Properties();
            disconfProp.load(is);
        } catch (Exception e) {
            log.error("加载application.properties出错：", e);
        }
    }

    public boolean isQaEnv() {
        return "qa".equals(disconfProp.getProperty("disconf.env"));
    }

    public boolean isOnlineEnv() {
        return "online".equals(disconfProp.getProperty("disconf.env"));
    }

    public String getProperty(String key) {
        return disconfProp.getProperty(key);
    }

}
