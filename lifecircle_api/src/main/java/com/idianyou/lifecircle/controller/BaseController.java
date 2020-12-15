package com.idianyou.lifecircle.controller;

import com.chigua.session.spi.constants.SessionKeyEnum;
import com.chigua.session.spi.service.SessionService;
import com.idianyou.business.cpa.bean.SessionVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 12:32
 */
public class BaseController {

    public static final String SESSION_ID_PARA_NAME = "userCertificate";

    public static final String SESSION_REDIS_KEY = "cpa_app_interface_session:%s";

    //@Autowired
    //private SysUcenterUserMainService sysUcenterUserMainService;

    @Autowired
    private SessionService sessionService;

    /**
     * 获取登录用户userId
     * @return
     */
    public Long getLoginUserId() {
        return getLoginUserId(getSessionId());
    }

    /**
     * 获取登录用户userId
     * @return
     */
    public Long getLoginUserId(String userCertificate) {
        if (StringUtils.isBlank(userCertificate)) {
            return null;
        }

        //SessionVO sessionVO = sysUcenterUserMainService.getUserInfoBySession(userCertificate, SESSION_REDIS_KEY);
        SessionVO sessionVO = sessionService.getSessionUser(userCertificate, SessionKeyEnum.CPA);
        if (sessionVO != null) {
            return sessionVO.getUserMain().getId();
        }

        return null;
    }

    /**
     * 获取登录用户SessionId
     * @return
     */
    public String getSessionId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String sessionId = request.getParameter(SESSION_ID_PARA_NAME);
        if (StringUtils.isBlank(sessionId)) {
            sessionId = (String) request.getAttribute(SESSION_ID_PARA_NAME);
        }

        if (StringUtils.isBlank(sessionId)) {
            sessionId = request.getHeader(SESSION_ID_PARA_NAME);
        }

        return sessionId;
    }
}
