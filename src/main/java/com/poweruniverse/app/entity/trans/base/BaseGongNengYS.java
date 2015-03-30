package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：功能映射
*/
@Version("2015-03-24 20:34:20")
public abstract class BaseGongNengYS  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseGongNengYS () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseGongNengYS (java.lang.Integer id) {
		this.setGongNengYSDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
	// 主键：gongNengYSDM
	private java.lang.Integer gongNengYSDM = null;
	public java.lang.Integer getGongNengYSDM(){return this.gongNengYSDM ;}
	public void setGongNengYSDM(java.lang.Integer gongNengYSDM){this.gongNengYSDM = gongNengYSDM;}

	// 对象：源应用系统 （sourceYYXT）
	private com.poweruniverse.app.entity.trans.YingYongXT sourceYYXT;
	public com.poweruniverse.app.entity.trans.YingYongXT getSourceYYXT(){return this.sourceYYXT ;}
	public void setSourceYYXT(com.poweruniverse.app.entity.trans.YingYongXT sourceYYXT){this.sourceYYXT = sourceYYXT;}

	// 对象：目标应用系统 （targetYYXT）
	private com.poweruniverse.app.entity.trans.YingYongXT targetYYXT;
	public com.poweruniverse.app.entity.trans.YingYongXT getTargetYYXT(){return this.targetYYXT ;}
	public void setTargetYYXT(com.poweruniverse.app.entity.trans.YingYongXT targetYYXT){this.targetYYXT = targetYYXT;}

			
	// 属性：源功能代号 （sourceGNDH）
	private java.lang.String sourceGNDH = null;
	public java.lang.String getSourceGNDH(){return this.sourceGNDH ;}
	public void setSourceGNDH(java.lang.String sourceGNDH){this.sourceGNDH = sourceGNDH;}
	
			
	// 属性：目标功能代号 （targetGNDH）
	private java.lang.String targetGNDH = null;
	public java.lang.String getTargetGNDH(){return this.targetGNDH ;}
	public void setTargetGNDH(java.lang.String targetGNDH){this.targetGNDH = targetGNDH;}
	
	// 对象：关联实体类映射 （shiTiLeiYS）
	private com.poweruniverse.app.entity.trans.ShiTiLeiYS shiTiLeiYS;
	public com.poweruniverse.app.entity.trans.ShiTiLeiYS getShiTiLeiYS(){return this.shiTiLeiYS ;}
	public void setShiTiLeiYS(com.poweruniverse.app.entity.trans.ShiTiLeiYS shiTiLeiYS){this.shiTiLeiYS = shiTiLeiYS;}

			
	// 属性：功能映射名称 （gongNengYSMC）
	private java.lang.String gongNengYSMC = null;
	public java.lang.String getGongNengYSMC(){return this.gongNengYSMC ;}
	public void setGongNengYSMC(java.lang.String gongNengYSMC){this.gongNengYSMC = gongNengYSMC;}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.GongNengYS)) return false;
		else {
			com.poweruniverse.app.entity.trans.GongNengYS entity = (com.poweruniverse.app.entity.trans.GongNengYS) obj;
			if (null == this.getGongNengYSDM() || null == entity.getGongNengYSDM()) return false;
			else return (this.getGongNengYSDM().equals(entity.getGongNengYSDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getGongNengYSDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getGongNengYSDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.gongNengYSMC+"";
	}

	public Integer pkValue() {
		return this.gongNengYSDM;
	}

	public String pkName() {
		return "gongNengYSDM";
	}

	public void pkNull() {
		this.gongNengYSDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.GongNengYS obj = (com.poweruniverse.app.entity.trans.GongNengYS)o;
		if(this.getGongNengYSDM()==null){
			return 1;
		}
		return this.getGongNengYSDM().compareTo(obj.getGongNengYSDM());
	}
	
	public com.poweruniverse.app.entity.trans.GongNengYS clone(){
		com.poweruniverse.app.entity.trans.GongNengYS gongNengYS = new com.poweruniverse.app.entity.trans.GongNengYS();
		
		gongNengYS.setSourceYYXT(sourceYYXT);
		gongNengYS.setTargetYYXT(targetYYXT);
		gongNengYS.setSourceGNDH(sourceGNDH);
		gongNengYS.setTargetGNDH(targetGNDH);
		gongNengYS.setShiTiLeiYS(shiTiLeiYS);
		gongNengYS.setGongNengYSMC(gongNengYSMC);
		
		return gongNengYS;
	}
	
	
	
	
	
	
}