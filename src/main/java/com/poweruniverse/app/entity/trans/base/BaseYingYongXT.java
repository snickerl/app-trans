package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：应用系统
*/
@Version("2015-03-23 16:01:35")
public abstract class BaseYingYongXT  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseYingYongXT () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseYingYongXT (java.lang.Integer id) {
		this.setYingYongXTDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
	// 主键：yingYongXTDM
	private java.lang.Integer yingYongXTDM = null;
	public java.lang.Integer getYingYongXTDM(){return this.yingYongXTDM ;}
	public void setYingYongXTDM(java.lang.Integer yingYongXTDM){this.yingYongXTDM = yingYongXTDM;}

			
	// 属性：应用系统名称 （yingYongXTMC）
	private java.lang.String yingYongXTMC = null;
	public java.lang.String getYingYongXTMC(){return this.yingYongXTMC ;}
	public void setYingYongXTMC(java.lang.String yingYongXTMC){this.yingYongXTMC = yingYongXTMC;}
	
			
	// 属性：应用系统编号 （yingYongXTBH）
	private java.lang.String yingYongXTBH = null;
	public java.lang.String getYingYongXTBH(){return this.yingYongXTBH ;}
	public void setYingYongXTBH(java.lang.String yingYongXTBH){this.yingYongXTBH = yingYongXTBH;}
	
			
	// 属性：是否接收数据 （shiFouJSSJ）
	private java.lang.Boolean shiFouJSSJ = new java.lang.Boolean(false);
	public java.lang.Boolean getShiFouJSSJ(){return this.shiFouJSSJ ;}
	public void setShiFouJSSJ(java.lang.Boolean shiFouJSSJ){this.shiFouJSSJ = shiFouJSSJ;}
	
			
	// 属性：是否传出数据 （shiFouCCSJ）
	private java.lang.Boolean shiFouCCSJ = new java.lang.Boolean(false);
	public java.lang.Boolean getShiFouCCSJ(){return this.shiFouCCSJ ;}
	public void setShiFouCCSJ(java.lang.Boolean shiFouCCSJ){this.shiFouCCSJ = shiFouCCSJ;}
	
			
	// 属性：是否接收附件 （shiFouJSFJ）
	private java.lang.Boolean shiFouJSFJ = new java.lang.Boolean(false);
	public java.lang.Boolean getShiFouJSFJ(){return this.shiFouJSFJ ;}
	public void setShiFouJSFJ(java.lang.Boolean shiFouJSFJ){this.shiFouJSFJ = shiFouJSFJ;}
	
			
	// 属性：是否传出附件 （shiFouCCFJ）
	private java.lang.Boolean shiFouCCFJ = new java.lang.Boolean(false);
	public java.lang.Boolean getShiFouCCFJ(){return this.shiFouCCFJ ;}
	public void setShiFouCCFJ(java.lang.Boolean shiFouCCFJ){this.shiFouCCFJ = shiFouCCFJ;}
	
			
	// 属性：是否接收任务 （shiFouJSRW）
	private java.lang.Boolean shiFouJSRW = new java.lang.Boolean(false);
	public java.lang.Boolean getShiFouJSRW(){return this.shiFouJSRW ;}
	public void setShiFouJSRW(java.lang.Boolean shiFouJSRW){this.shiFouJSRW = shiFouJSRW;}
	
			
	// 属性：是否传出任务 （shiFouCCRW）
	private java.lang.Boolean shiFouCCRW = new java.lang.Boolean(false);
	public java.lang.Boolean getShiFouCCRW(){return this.shiFouCCRW ;}
	public void setShiFouCCRW(java.lang.Boolean shiFouCCRW){this.shiFouCCRW = shiFouCCRW;}
	
			
	// 属性：接口实现类 （jieKouSXL）
	private java.lang.String jieKouSXL = null;
	public java.lang.String getJieKouSXL(){return this.jieKouSXL ;}
	public void setJieKouSXL(java.lang.String jieKouSXL){this.jieKouSXL = jieKouSXL;}
	
			
	// 属性：应用系统ip地址 （yingYongXTIP）
	private java.lang.String yingYongXTIP = null;
	public java.lang.String getYingYongXTIP(){return this.yingYongXTIP ;}
	public void setYingYongXTIP(java.lang.String yingYongXTIP){this.yingYongXTIP = yingYongXTIP;}
	
			
	// 属性：应用系统webservice端口 （yingYongXTPort）
	private java.lang.String yingYongXTPort = null;
	public java.lang.String getYingYongXTPort(){return this.yingYongXTPort ;}
	public void setYingYongXTPort(java.lang.String yingYongXTPort){this.yingYongXTPort = yingYongXTPort;}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.YingYongXT)) return false;
		else {
			com.poweruniverse.app.entity.trans.YingYongXT entity = (com.poweruniverse.app.entity.trans.YingYongXT) obj;
			if (null == this.getYingYongXTDM() || null == entity.getYingYongXTDM()) return false;
			else return (this.getYingYongXTDM().equals(entity.getYingYongXTDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getYingYongXTDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getYingYongXTDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.yingYongXTMC+"";
	}

	public Integer pkValue() {
		return this.yingYongXTDM;
	}

	public String pkName() {
		return "yingYongXTDM";
	}

	public void pkNull() {
		this.yingYongXTDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.YingYongXT obj = (com.poweruniverse.app.entity.trans.YingYongXT)o;
		if(this.getYingYongXTDM()==null){
			return 1;
		}
		return this.getYingYongXTDM().compareTo(obj.getYingYongXTDM());
	}
	
	public com.poweruniverse.app.entity.trans.YingYongXT clone(){
		com.poweruniverse.app.entity.trans.YingYongXT yingYongXT = new com.poweruniverse.app.entity.trans.YingYongXT();
		
		yingYongXT.setYingYongXTMC(yingYongXTMC);
		yingYongXT.setYingYongXTBH(yingYongXTBH);
		yingYongXT.setShiFouJSSJ(shiFouJSSJ);
		yingYongXT.setShiFouCCSJ(shiFouCCSJ);
		yingYongXT.setShiFouJSFJ(shiFouJSFJ);
		yingYongXT.setShiFouCCFJ(shiFouCCFJ);
		yingYongXT.setShiFouJSRW(shiFouJSRW);
		yingYongXT.setShiFouCCRW(shiFouCCRW);
		yingYongXT.setJieKouSXL(jieKouSXL);
		yingYongXT.setYingYongXTIP(yingYongXTIP);
		yingYongXT.setYingYongXTPort(yingYongXTPort);
		
		return yingYongXT;
	}
	
	
	
	
	
	
}