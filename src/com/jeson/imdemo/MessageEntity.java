package com.jeson.imdemo;

import java.io.Serializable;

public class MessageEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 消息id
	 */
	private String id;
	// 内容
	private String content;
	/**
	 * 本地时间
	 */
	private long localTime;
	/**
	 * 服务器时间
	 */
	private long serverTime;
	/**
	 * 发送、接收标示
	 */
	private int direction;
	/**
	 * 文件token
	 */
	private String fileToken;
	/**
	 * 是否加密消息
	 */
	private boolean isEnc;
	/**
	 * 消息已读未读
	 */
	private int status;
	/**
	 * 消息类型
	 */
	private int type;
	/**
	 * 文件的保存路径
	 */
	private String path;
	/**
	 * 语音时长
	 */
	private int voiceLength;
	/**
	 * 文件名
	 */
	private String fileName;
	// 0是发送不成功，1正在发送中 2发送到服务器，3发送到目标客户端， 4目标客户端已读
	private int state;
	/**
	 * 日期标示
	 */
	private String dateSign;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 经度
	 */
	private double latitude;
	/**
	 * 纬度
	 */
	private double longitude;
	private String reserve; // 预留

	private String temp;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getLocalTime() {
		return localTime;
	}

	public void setLocalTime(long localTime) {
		this.localTime = localTime;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getFileToken() {
		return fileToken;
	}

	public void setFileToken(String fileToken) {
		this.fileToken = fileToken;
	}

	public boolean isEnc() {
		return isEnc;
	}

	public void setEnc(boolean isEnc) {
		this.isEnc = isEnc;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getVoiceLength() {
		return voiceLength;
	}

	public void setVoiceLength(int voiceLength) {
		this.voiceLength = voiceLength;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDateSign() {
		return dateSign;
	}

	public void setDateSign(String dateSign) {
		this.dateSign = dateSign;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

}
