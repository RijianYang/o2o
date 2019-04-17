package com.ahead.pojo;

import lombok.Data;

import java.util.Date;
/**
 * 学校的区域
 * @author Yang
 *
 */
@Data
public class Area {
	
	private Integer areaId; //ID
	private String areaName; //区域名称
	private Integer priority; //权重
	private Date CreateTime; //创建时间
	private Date lastEditTime; //最后修改的时间
	
	public Integer getAreaId() {
		return areaId;
	}
	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Date getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}
	public Date getLastEditTime() {
		return lastEditTime;
	}
	public void setLastEditTime(Date lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
	@Override
	public String toString() {
		return "Area [areaId=" + areaId + ", areaName=" + areaName + ", priority=" + priority + ", CreateTime="
				+ CreateTime + ", lastEditTime=" + lastEditTime + "]";
	}
	
}
