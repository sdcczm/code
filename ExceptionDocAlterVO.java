package com.wcs.ncp.service.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ExceptionDocAlterVO {
	
	private String exceptionDocNo; //异常单号
	
	@ToCompare(name="调整类型")
    private Short adjustmentType; //调整类型
	
	@ToCompare(name="调整项目")
    private Short adjustmentItem; //调整项目	
	
	@ToCompare(name="异常情况")
    private String reason; //异常情况
	
	
    private Double adjustedValue ;
    
    private String unitName; //单位
	
	
    
    
	@SuppressWarnings("rawtypes")
	public List<String> compare(ExceptionDocAlterVO other) {
		List<String> list = new ArrayList<String>();
		try {
			Class clazz = this.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				ToCompare toCompare = field.getAnnotation(ToCompare.class);
				if (toCompare == null) {
					continue;
				}
				String name=toCompare.name();
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(),clazz);
				Method getMethod = pd.getReadMethod();
				Object o1 = getMethod.invoke(this);
				Object o2 = getMethod.invoke(other);
				String s1 = o1 == null ? "" : o1.toString();// 避免空指针异常
				String s2 = o2 == null ? "" : o2.toString();// 避免空指针异常
				if (!s1.equals(s2)) { //异常单作了修改
					String fieldName=field.getName();
					if("adjustmentType".equals(fieldName)){
						s1=AdjustmentType.getName((Short)o1);
						s2=AdjustmentType.getName((Short)o2);
					}else if("adjustmentItem".equals(fieldName)){
						s1=AdjustmentItem.getName((Short)o1)+this.adjustedValue+this.unitName;
						s2=AdjustmentItem.getName((Short)o2)+other.getAdjustedValue()+other.getUnitName();
					}
					String msg = "异常单【{0}】的【{1}】由【{2}】修改成【{3}】";
					String logContent=MessageFormat.format(msg,exceptionDocNo,name,s2,s1);
					list.add(logContent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}	
    
    
    
    
    
	public String getExceptionDocNo() {
		return exceptionDocNo;
	}
	public void setExceptionDocNo(String exceptionDocNo) {
		this.exceptionDocNo = exceptionDocNo;
	}
	public Short getAdjustmentType() {
		return adjustmentType;
	}
	public void setAdjustmentType(Short adjustmentType) {
		this.adjustmentType = adjustmentType;
	}
	public Short getAdjustmentItem() {
		return adjustmentItem;
	}
	public void setAdjustmentItem(Short adjustmentItem) {
		this.adjustmentItem = adjustmentItem;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Double getAdjustedValue() {
		return adjustedValue;
	}

	public void setAdjustedValue(Double adjustedValue) {
		this.adjustedValue = adjustedValue;
	}		
}
