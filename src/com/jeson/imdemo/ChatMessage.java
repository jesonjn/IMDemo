package com.jeson.imdemo;

public class ChatMessage extends MessageEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 对方nid
	 */
	private String nid;

	/**
	 * 聊天ID
	 */
	private String imid;

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getImid() {
		return imid;
	}

	public void setImid(String imid) {
		this.imid = imid;
	}

}
