package com.jeson.imdemo;

public interface MessageTypeCst
{
	// 消息来源类型
	/**
	 * 接收
	 */
	int MESSAGE_FROM = 10000;
	/**
	 * 发送
	 */
	int MESSAGE_TO = 10001;

	// 消息类型
	/**
	 * 文本类型
	 */
	int MESSAGE_TYPE_TEXT = 1;
	/**
	 * 图片类型
	 */
	int MESSAGE_TYPE_IMGE = 3;
	/**
	 * 语音
	 */
	int MESSAGE_TYPE_AUDIO = 2;
	/**
	 * 视频
	 */
	int MESSAGE_TYPE_VIDEO = 4;
	/**
	 * 名片
	 */
	int MESSAGE_TYPE_VCARD = 5;
	/**
	 * 位置
	 */
	int MESSAGE_TYPE_LOCATION = 6;
	/**
	 * 草稿
	 */
	int MESSAGE_TYPE_DRAFT = 7;
	/**
	 * 文件
	 */
	int MESSAGE_TYPE_FILE = 8;
	/**
	 * 公共消息 链接
	 */
	int MESSAGE_TYPE_PUBLIC_TXT = 101;
	/**
	 * 公共消息 图片
	 */
	int MESSAGE_TYPE_PUBLIC_IMG = 102;
	/**
	 * 公共消息 链接
	 */
	int MESSAGE_TYPE_PUBLIC_LINK = 103;
	/**
	 * 公共消息 多条链接消息
	 */
	int MESSAGE_TYPE_PUBLIC_MULTI_LINK = 104;

	// 群消息类型
	/**
	 * 聊天消息
	 */
	String MULTI_MSG_CHAT = "MUTILS";
	/**
	 * 通知消息
	 */
	String MULTI_MSG_NOTIFY = "NOTIFY";

	// 会话列表消息类型

	/**
	 * 个人聊天
	 */
	int RECODER_SIGLE = 1000;
	/**
	 * 群聊天
	 */
	int RECODER_MUTIL = 1001;
	/**
	 * 系统通知消息
	 */
	int RECODER_SYS = 1002;

	/**
	 * 公共账号
	 */
	int RECODER_PUBLIC = 1003;

	// 通知消息类型
	/**
	 * 添加好友
	 */
	int OPT_ADDROTER = 11;
	/**
	 * 更新好友
	 */
	int OPT_UPDATEROTER = 12;
	/**
	 * 删除好友
	 */
	int OPT_DELETEROTER = 13;
	/**
	 * 邀请成员加入
	 */
	int OPT_MUITL_ADDUSER = 14;
	/**
	 * 移出成员
	 */
	int OPT_MUITL_REMOVEUSER = 15;
	/**
	 * 系统公告
	 */
	int OPT_SERVER_NOTICE = 16;

	// 会话列表状态
	/**
	 * 在线状态
	 */
	int RECODER_STATUS_ONLINE = 100001;

	/**
	 * 失败
	 */
	int MESSAGE_FAIL = -1;
	/**
	 * 正在处理
	 */
	int MESSAGE_SENDING = 0;
	/**
	 * 成功
	 */
	int MESSAGE_SUCCESS = 1;
	/**
	 * 已取消
	 */
	int MESSAGE_CANCLE = 2;
	/**
	 * 目标端已查看
	 */
	int MESSAGE_TARGET_READ = 3;

	/**
	 * BeShare相关
	 */

	String BESHARE_PREFIX = "nsbetalk://BeShare/recvfileurl";
	String BESHARE_PACKAGE_NAME = "nutstore.android";
}
