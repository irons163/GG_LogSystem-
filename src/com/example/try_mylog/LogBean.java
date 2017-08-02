package com.example.try_mylog;

public class LogBean {
	private String level;
	private String time;
	private String pid;
	private String tid;
	private String applicatioin;
	private String tag;
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LogBean(String level, String Time, String PID, String TID,
			String Application, String Tag, String Text) {
		this.level = level;
		this.time = Time;
		this.pid = PID;
		this.tid = TID;
		this.applicatioin = Application;
		this.tag = Tag;
		this.text = Text;
	}

	public LogBean(String level, String Time, String Tag, String Text) {
		this.level = level;
		this.time = Time;
		this.tag = Tag;
		this.text = Text;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getApplicatioin() {
		return applicatioin;
	}

	public void setApplicatioin(String applicatioin) {
		this.applicatioin = applicatioin;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
