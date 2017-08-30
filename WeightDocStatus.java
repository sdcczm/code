package com.wcs.ncp.service.common;


/**
 * 日志操作范围
 * @author QiHuifang
 * 混批的单子
 * batch_status  --创建批次状态   0-未推送 ；1-成功 ；2-失败；3-正在推送
 * 单子状态
 * transStatus   --推送状态  0-失败；1-成功；2-待传输; 3-正在传输  
 */
public enum WeightDocStatus {
	
	I(1,"收货未导入","2","1"),
	II(2,"正在导入收货","3","1"),
	III(3,"收货成功","1","2"),
	IIII(4,"收货失败","0","1");
	
	
    // 成员变量  
	private Integer value;  //当前状态
    private String name;  //当前状态
	private String transStatus; //GR推送状态
	private String nextStatus; //重置的状态
	
    
    // 构造方法  
    private WeightDocStatus(Integer value,String name,String transStatus,String nextStatus) { 
    	this.value=value;
    	this.name=name;
    	this.transStatus=transStatus;
    	this.nextStatus=nextStatus;
        
    }
    
    
    //类的静态方法 
    //根据状态获取重置的状态
    public static String getNextStatusByTransStatus(String transStatus) {  
        for (WeightDocStatus item : WeightDocStatus.values()) {  
            if (item.getTransStatus().equals(transStatus)) {  
                return item.nextStatus;  
            }  
        }  
        return null;  
    }
    
    //根据状态获取状态名称
    public static String getNameByTransStatus(String transStatus) {  
        for (WeightDocStatus item : WeightDocStatus.values()) {  
            if (item.getTransStatus().equals(transStatus)) {  
                return item.name;  
            }  
        }  
        return null;  
    }


	public Integer getValue() {
		return value;
	}


	public void setValue(Integer value) {
		this.value = value;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getTransStatus() {
		return transStatus;
	}


	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}


	public String getNextStatus() {
		return nextStatus;
	}


	public void setNextStatus(String nextStatus) {
		this.nextStatus = nextStatus;
	}  


    
    
}
