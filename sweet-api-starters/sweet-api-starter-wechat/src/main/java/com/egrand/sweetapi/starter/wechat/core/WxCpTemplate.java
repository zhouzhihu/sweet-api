package com.egrand.sweetapi.starter.wechat.core;

import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpMessageServiceImpl;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * WxCpTemplate
 */
public class WxCpTemplate {

    @Nullable
    private WxCpServiceFactory wxCpServiceFactory;

    /**
     * 发送文本消息
     * @param userId 非必填，UserID列表（消息接收者，多个接收者用‘|’分隔）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     * @param partyId 非必填，PartyID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param tagId 非必填，TagID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param content 文本消息内容
     * @return
     */
    public WxCpMessageSendResult sendTextMessage(String userId, String partyId, String tagId, String content) throws WxErrorException {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        WxCpMessageServiceImpl wxCpMessageService = new WxCpMessageServiceImpl(wxCpService);
        // 文本消息
        WxCpMessage wxCpMessage = WxCpMessage
                .TEXT()
                .agentId(wxCpService.getWxCpConfigStorage().getAgentId())
                .toUser(userId)
                .toParty(partyId)
                .toTag(tagId)
                .content(content)
                .build();
        return wxCpMessageService.send(wxCpMessage);
    }

    /**
     * 发送图片消息
     * @param userId 非必填，UserID列表（消息接收者，多个接收者用‘|’分隔）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     * @param partyId 非必填，PartyID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param tagId 非必填，TagID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param mediaId 媒体ID
     * @return
     * @throws WxErrorException
     */
    public WxCpMessageSendResult sendImageMessage(String userId, String partyId, String tagId, String mediaId) throws WxErrorException {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        WxCpMessageServiceImpl wxCpMessageService = new WxCpMessageServiceImpl(wxCpService);
        // 图片消息
        WxCpMessage wxCpMessage = WxCpMessage
                .IMAGE()
                .agentId(wxCpService.getWxCpConfigStorage().getAgentId())
                .toUser(userId)
                .toParty(partyId)
                .toTag(tagId)
                .mediaId(mediaId)
                .build();
        return wxCpMessageService.send(wxCpMessage);
    }

    /**
     * 发送语音消息
     * @param userId 非必填，UserID列表（消息接收者，多个接收者用‘|’分隔）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     * @param partyId 非必填，PartyID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param tagId 非必填，TagID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param mediaId 媒体ID
     * @return
     * @throws WxErrorException
     */
    public WxCpMessageSendResult sendVoiceMessage(String userId, String partyId, String tagId, String mediaId) throws WxErrorException {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        WxCpMessageServiceImpl wxCpMessageService = new WxCpMessageServiceImpl(wxCpService);
        // 语音消息
        WxCpMessage wxCpMessage = WxCpMessage
                .VOICE()
                .agentId(wxCpService.getWxCpConfigStorage().getAgentId())
                .toUser(userId)
                .toParty(partyId)
                .toTag(tagId)
                .mediaId(mediaId)
                .build();
        return wxCpMessageService.send(wxCpMessage);
    }

    /**
     * 发送视频消息
     * @param userId 非必填，UserID列表（消息接收者，多个接收者用‘|’分隔）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     * @param partyId 非必填，PartyID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param tagId 非必填，TagID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param mediaId 媒体ID
     * @param thumbMediaId 附件ID
     * @param title 标题
     * @param description 描述
     * @return
     * @throws WxErrorException
     */
    public WxCpMessageSendResult sendVideoMessage(String userId, String partyId, String tagId,
                                                  String mediaId, String thumbMediaId, String title, String description) throws WxErrorException {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        WxCpMessageServiceImpl wxCpMessageService = new WxCpMessageServiceImpl(wxCpService);
        // 视频消息
        WxCpMessage wxCpMessage = WxCpMessage.VIDEO()
                .agentId(wxCpService.getWxCpConfigStorage().getAgentId())
                .toUser(userId)
                .toParty(partyId)
                .toTag(tagId)
                .title(title)
                .mediaId(mediaId)
                .thumbMediaId(thumbMediaId)
                .description(description)
                .build();
        return wxCpMessageService.send(wxCpMessage);
    }

    /**
     * 发送图文消息
     * @param userId 非必填，UserID列表（消息接收者，多个接收者用‘|’分隔）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     * @param partyId 非必填，PartyID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param tagId 非必填，TagID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     * @param articleList 图文列表
     * @return
     * @throws WxErrorException
     */
    public WxCpMessageSendResult sendNewsMessage(String userId, String partyId, String tagId, List<Map<String, String>> articleList) throws WxErrorException {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        WxCpMessageServiceImpl wxCpMessageService = new WxCpMessageServiceImpl(wxCpService);
        // 图文消息
        NewArticle article1 = new NewArticle();
        article1.setUrl("URL");
        article1.setPicUrl("PIC_URL");
        article1.setDescription("Is Really A Happy Day");
        article1.setTitle("Happy Day");
        NewArticle article2 = new NewArticle();
        article2.setUrl("URL");
        article2.setPicUrl("PIC_URL");
        article2.setDescription("Is Really A Happy Day");
        article2.setTitle("Happy Day");
        List<NewArticle> newArticleList = new ArrayList<>();
        articleList.forEach(map -> {
            NewArticle newArticle = new NewArticle();
            newArticle.setUrl(map.getOrDefault("url", ""));
            newArticle.setPicUrl(map.getOrDefault("picUrl", ""));
            newArticle.setTitle(map.getOrDefault("title", ""));
            newArticle.setDescription(map.getOrDefault("description", ""));
            newArticleList.add(newArticle);
        });

        WxCpMessage wxCpMessage = WxCpMessage.NEWS()
                .agentId(wxCpService.getWxCpConfigStorage().getAgentId())
                .toUser(userId)
                .toParty(partyId)
                .toTag(tagId)
                .addArticle(newArticleList.toArray(new NewArticle[newArticleList.size()]))
                .build();
        return wxCpMessageService.send(wxCpMessage);
    }

    /**
     * 上传文件
     * @param mediaType 媒体类型
     * @param fileType 文件类型
     * @param inputStream 文件流
     * @return
     * @throws WxErrorException
     * @throws IOException
     */
    public WxMediaUploadResult upload(String mediaType, String fileType, InputStream inputStream)
            throws WxErrorException, IOException {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        return wxCpService.getMediaService().upload(mediaType, fileType, inputStream);
    }

    /**
     * 构造oauth2授权的url连接.
     * @param redirectUri 用户授权完成后的重定向链接，无需urlencode, 方法内会进行encode
     * @return
     */
    public String buildAuthorizationUrl(String redirectUri) {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        return wxCpService.getOauth2Service().buildAuthorizationUrl(redirectUri, "");
    }

    /**
     * 构造oauth2授权的url连接.
     * @param redirectUri 用户授权完成后的重定向链接，无需urlencode, 方法内会进行encode
     * @param state 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * @return
     */
    public String buildAuthorizationUrl(String redirectUri, String state) {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        return wxCpService.getOauth2Service().buildAuthorizationUrl(redirectUri, state);
    }

    /**
     * 构造oauth2授权的url连接.
     * @param redirectUri 用户授权完成后的重定向链接，无需urlencode, 方法内会进行encode
     * @param scope 应用授权作用域，
     *              snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
     *              snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
     * @param state 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * @return
     */
    public String buildAuthorizationUrl(String redirectUri, String scope, String state) {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        return wxCpService.getOauth2Service().buildAuthorizationUrl(redirectUri, scope, state);
    }

    /**
     * 根据code获取成员信息
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     * @return
     * @throws WxErrorException
     */
    public WxCpOauth2UserInfo getUserInfo(String code) throws WxErrorException {
        WxCpService wxCpService = wxCpServiceFactory.getWxCpService();
        return wxCpService.getOauth2Service().getUserInfo(wxCpService.getWxCpConfigStorage().getAgentId(), code);
    }

    @Nullable
    public WxCpServiceFactory getWxCpServiceFactory() {
        return wxCpServiceFactory;
    }

    public void setWxCpServiceFactory(@Nullable WxCpServiceFactory wxCpServiceFactory) {
        this.wxCpServiceFactory = wxCpServiceFactory;
    }
}
