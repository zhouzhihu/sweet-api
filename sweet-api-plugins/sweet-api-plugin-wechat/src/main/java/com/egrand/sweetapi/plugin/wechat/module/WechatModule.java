package com.egrand.sweetapi.plugin.wechat.module;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.egrand.sweetapi.core.*;
import com.egrand.sweetapi.starter.wechat.core.WxCpTemplate;
import com.egrand.sweetapi.starter.wechat.toolkit.DynamicWxCpContextHolder;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WechatModule implements ModuleService {

    private LocalFileServiceFactory localFileServiceFactory;

    protected WxCpTemplate wxCpTemplate;

    private TenantService tenantService;

    /**
     * 非必填，UserID列表（消息接收者，多个接收者用‘|’分隔）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     */
    private final List<String> userIdList = new ArrayList<>();

    /**
     * 非必填，PartyID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     */
    private final List<String> partyIdList = new ArrayList<>();

    /**
     * 非必填，TagID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
     */
    private final List<String> tagIdList = new ArrayList<>();

    private String messageType = "text";

    /**
     * 简单消息内容-文本消息
     */
    private String content = "";

    /**
     * 媒体ID-图片消息、语音消息、视频消息
     */
    private String mediaId = "";

    /**
     * 附件媒体ID-视频消息
     */
    private String thumbMediaId = "";

    /**
     * 主题-视频消息
     */
    private String title = "";

    /**
     * 描述-视频消息
     */
    private String description = "";

    /**
     * 图文消息内容
     */
    private List<Map<String, String>> articleList = new ArrayList<>();

    public WechatModule(){}

    public WechatModule(WxCpTemplate wxCpTemplate, TenantService tenantService, LocalFileServiceFactory localFileServiceFactory) {
        this.localFileServiceFactory = localFileServiceFactory;
        this.tenantService = tenantService;
        this.wxCpTemplate = wxCpTemplate;
    }

    public BoundWechatModule cp(String cpKey) {
        return new BoundWechatModule(wxCpTemplate, cpKey);
    }

    /**
     * 文本消息
     * @return
     */
    public WechatModule text() {
        this.messageType = "text";
        return this;
    }

    /**
     * 图片消息
     * @return
     */
    public WechatModule image() {
        this.messageType = "image";
        return this;
    }

    /**
     * 语音消息
     * @return
     */
    public WechatModule voice() {
        this.messageType = "voice";
        return this;
    }

    /**
     * 视频消息
     * @return
     */
    public WechatModule video() {
        this.messageType = "video";
        return this;
    }

    /**
     * 图文消息
     * @return
     */
    public WechatModule news() {
        this.messageType = "news";
        return this;
    }

    /**
     * 设置接收者
     * @param userId
     * @return
     */
    public WechatModule userId(String ...userId) {
        userIdList.addAll(Arrays.asList(userId));
        return this;
    }

    /**
     * 设置PartyId
     * @param partyId
     * @return
     */
    public WechatModule partyId(String ...partyId) {
        partyIdList.addAll(Arrays.asList(partyId));
        return this;
    }

    /**
     * 设置TagId
     * @param tagId
     * @return
     */
    public WechatModule tagId(String ...tagId) {
        tagIdList.addAll(Arrays.asList(tagId));
        return this;
    }

    /**
     * 文本消息内容
     * @param content
     * @return
     */
    public WechatModule content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 视频消息主题
     * @param title
     * @return
     */
    public WechatModule title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 视频消息描述
     * @param description
     * @return
     */
    public WechatModule description(String description) {
        this.description = description;
        return this;
    }

    public WechatModule article(List<Map<String, String>> articleList) {
        this.articleList = articleList;
        return this;
    }

    /**
     * 上传媒体文件
     * @param multipartFile 文件
     * @param parentId 存放文件夹ID
     * @param key 文件关键字
     * @return
     * @throws IOException
     * @throws WxErrorException
     */
    public WechatModule uploadMedia(MultipartFile multipartFile, Long parentId, String key) throws IOException, WxErrorException {
        InputStream is = null;
        try {
            LocalFileInfo file = this.localFileServiceFactory.getService(LocalFileService.FILE_SERVICE_TYPE_LOCAL).upload(multipartFile, parentId, key);
            if (null != file && FileUtil.exist(file.getLocalFilePath())) {
                is = FileUtil.getInputStream(file.getLocalFilePath());
                WxMediaUploadResult wxMediaUploadResult = this.wxCpTemplate.upload("", file.getFileType(), is);
                this.mediaId = wxMediaUploadResult.getMediaId();
                this.thumbMediaId = wxMediaUploadResult.getThumbMediaId();
            }
        } finally {
            if(null != is)
                is.close();
        }
        return this;
    }

    /**
     * 上传媒体文件
     * @param key 关键字
     * @return
     * @throws IOException
     * @throws WxErrorException
     */
    public WechatModule uploadMedia(String key) throws IOException, WxErrorException {
        InputStream is = null;
        try {
            String filePath = this.localFileServiceFactory.getService(LocalFileService.FILE_SERVICE_TYPE_LOCAL).getFilePath(key);
            if (StrUtil.isNotEmpty(filePath) && FileUtil.exist(filePath)) {
                is = FileUtil.getInputStream(filePath);
                WxMediaUploadResult wxMediaUploadResult = this.wxCpTemplate.upload("", filePath.substring(filePath.lastIndexOf(".") + 1), is);
                this.mediaId = wxMediaUploadResult.getMediaId();
                this.thumbMediaId = wxMediaUploadResult.getThumbMediaId();
            }
        } finally {
            if(null != is)
                is.close();
        }
        return this;
    }

    /**
     * 执行消息发送
     * @return
     */
    public WxCpMessageSendResult sendMessage() {
        return this.execute(() -> {
            try {
                String userIds = userIdList.size() != 0 ? StringUtils.join(userIdList, "|") : "";
                String partyIds = partyIdList.size() != 0 ? StringUtils.join(partyIdList, "|") : "";
                String tagIds = tagIdList.size() != 0 ? StringUtils.join(tagIdList, "|") : "";
                if ("text".equalsIgnoreCase(this.messageType)) {
                    return this.wxCpTemplate.sendTextMessage(userIds, partyIds, tagIds, content);
                } else if ("image".equalsIgnoreCase(this.messageType)) {
                    return this.wxCpTemplate.sendImageMessage(userIds, partyIds, tagIds, mediaId);
                } else if ("voice".equalsIgnoreCase(this.messageType)) {
                    return this.wxCpTemplate.sendVoiceMessage(userIds, partyIds, tagIds, mediaId);
                } else if ("video".equalsIgnoreCase(this.messageType)) {
                    return this.wxCpTemplate.sendVideoMessage(userIds, partyIds, tagIds, mediaId, thumbMediaId, title, description);
                } else if ("news".equalsIgnoreCase(this.messageType)) {
                    return this.wxCpTemplate.sendNewsMessage(userIds, partyIds, tagIds, articleList);
                }
            } catch (WxErrorException e) {
                throw new RuntimeException(e.getMessage());
            }
            return null;
        });
    }

    /**
     * 构造oauth2授权的url连接.
     * @param redirectUri 用户授权完成后的重定向链接，无需urlencode, 方法内会进行encode
     * @return
     */
    public String buildAuthorizationUrl(String redirectUri) {
        return this.wxCpTemplate.buildAuthorizationUrl(redirectUri);
    }

    /**
     * 构造oauth2授权的url连接.
     * @param redirectUri 用户授权完成后的重定向链接，无需urlencode, 方法内会进行encode
     * @param state 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * @return
     */
    public String buildAuthorizationUrl(String redirectUri, String state) {
        return this.wxCpTemplate.buildAuthorizationUrl(redirectUri, state);
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
        return this.wxCpTemplate.buildAuthorizationUrl(redirectUri, scope, state);
    }

    /**
     * 根据code获取成员信息
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     * @return
     * @throws WxErrorException
     */
    public WxCpOauth2UserInfo getUserInfo(String code) throws WxErrorException {
        return this.wxCpTemplate.getUserInfo(code);
    }

    public <T> T execute(Supplier<T> supplier) {
        try {
            String tenant = tenantService.getTenant();
            if (StrUtil.isNotEmpty(tenant)) {
                DynamicWxCpContextHolder.push(tenant);
            }
            return supplier.get();
        } finally {
            DynamicWxCpContextHolder.poll();
        }
    }

    @Override
    public String getType() {
        return "wechat";
    }
}
