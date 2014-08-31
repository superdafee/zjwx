package com.zjs.cms.weixin.controller;

import com.zjs.cms.weixin.entity.EventMessage;
import com.zjs.cms.weixin.entity.PicMessage;
import com.zjs.cms.weixin.entity.TextMessage;
import com.zjs.cms.weixin.entity.VoiceMessage;
import com.zjs.cms.weixin.service.WeiXinReceiveService;
import com.zjs.cms.weixin.service.WeiXinSendService;
import com.zjs.cms.weixin.service.WeixinUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mayufeng
 * Date: 13-12-30
 * Time: 上午9:09
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/weixin")
public class WeiXinController {
    @Resource
    private WeiXinReceiveService weiXinReceiveService;
    @Resource
    private WeiXinSendService weiXinSendService;
    /**
     * get 验证
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "zjs",method = RequestMethod.GET)
    public String weixinGet(HttpServletRequest request,HttpServletResponse response) throws  Exception {
            PrintWriter pw=response.getWriter();
            String echostr=request.getParameter("echostr");
            if(WeixinUtil.validate(request)){
                pw.write(echostr);
            }
            System.out.println("验证是否合法");
            pw.close();
            return  null;
    }

    /**
     * post发送消息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "zjs",method = RequestMethod.POST)
    public String weixinPost(HttpServletRequest request,HttpServletResponse response) throws  Exception {
       // WeixinUtil.validate(request);
        String token="PhV_XP-5rIon3DJHwp_0A68JXfUOAy5zdMuVZ6lGOmJv6LeKNkuLVZs_BXNKDgymwTvz728O9zwxqtorjVAfrg";
        String sendXml=null;
        PrintWriter pw=response.getWriter();
        Map<String, String> map= WeixinUtil.parseXml(request);
        //文本消息
        if(map.get("MsgType").equals("text")){
            TextMessage message= weiXinReceiveService.receiveTextMessage(map);
             sendXml=weiXinSendService.sendTextMessage(message);

        }else if(map.get("MsgType").equals("image")){   //事件消息
            PicMessage message= weiXinReceiveService.receivePicMessage(map);
            message.setContent("已经接受到图片");
            message.setMediaId("gKXr0L2-YGjGW5aUDn2OempYqRQQb7Fku23BO2DII2ZhjaAb_VC9nubAw5Eh43IO");
            sendXml=weiXinSendService.sendPicMessage(message);
            WeixinUtil.weiXinDownByUrl(token, message.getPicUrl());
        }else if(map.get("MsgType").equals("voice")){   //事件消息
            VoiceMessage message= weiXinReceiveService.receiveVoiceMessage(map);
            message.setContent("已经接受到声音");
            message.setMediaId("w-AF6qmY19dKQVSWJ8VBzBVgQGTqMCGnV2BdBtXNeIFeu1nHBdiynrK5OikfgvVy");
            sendXml=weiXinSendService.sendVoiceMessage(message);
            WeixinUtil.weiXinDownFile(token,message.getMediaId(),message.getFomat());
        }else if(map.get("MsgType").equals("event")){   //事件消息
            EventMessage message= weiXinReceiveService.receiveEventMessage(map);
            sendXml=weiXinSendService.sendEventMessage(message);

        }
        System.out.println(sendXml);
        pw.write(sendXml);
        pw.close();
        return  null;
    }



}
