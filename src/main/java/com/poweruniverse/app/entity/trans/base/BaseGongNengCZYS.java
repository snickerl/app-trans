package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：功能操作映射
*/
@Version("2015-04-01 08:03:27")
public abstract class BaseGongNengCZYS  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseGongNengCZYS () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseGongNengCZYS (java.lang.Integer id) {
		this.setGongNengCZYSDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
	// 主键：gongNengCZYSDM
	private java.lang.Integer gongNengCZYSDM = null;
	public java.lang.Integer getGongNengCZYSDM(){return this.gongNengCZYSDM ;}
	public void setGongNengCZYSDM(java.lang.Integer gongNengCZYSDM){this.gongNengCZYSDM = gongNengCZYSDM;}

			
	// 属性：源操作代号 （sourceCZDH）
	private java.lang.String sourceCZDH = null;
	public java.lang.String getSourceCZDH(){return this.sourceCZDH ;}
	public void setSourceCZDH(java.lang.String sourceCZDH){this.sourceCZDH = sourceCZDH;}
	
			
	// 属性：目标操作代号 （targetCZDH）
	private java.lang.String targetCZDH = null;
	public java.lang.String getTargetCZDH(){return this.targetCZDH ;}
	public void setTargetCZDH(java.lang.String targetCZDH){this.targetCZDH = targetCZDH;}
	
			
	// 属性：功能操作映射名称 （gongNengCZYSMC）
	private java.lang.String gongNengCZYSMC = null;
	public java.lang.String getGongNengCZYSMC(){return this.gongNengCZYSMC ;}
	public void setGongNengCZYSMC(java.lang.String gongNengCZYSMC){this.gongNengCZYSMC = gongNengCZYSMC;}
	
	// 对象：功能映射 （gongNengYS）
	private com.poweruniverse.app.entity.trans.GongNengYS gongNengYS;
	public com.poweruniverse.app.entity.trans.GongNengYS getGongNengYS(){return this.gongNengYS ;}
	public void setGongNengYS(com.poweruniverse.app.entity.trans.GongNengYS gongNengYS){this.gongNengYS = gongNengYS;}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.GongNengCZYS)) return false;
		else {
			com.poweruniverse.app.entity.trans.GongNengCZYS entity = (com.poweruniverse.app.entity.trans.GongNengCZYS) obj;
			if (null == this.getGongNengCZYSDM() || null == entity.getGongNengCZYSDM()) return false;
			else return (this.getGongNengCZYSDM().equals(entity.getGongNengCZYSDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getGongNengCZYSDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getGongNengCZYSDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.gongNengCZYSMC+"";
	}

	public Integer pkValue() {
		return this.gongNengCZYSDM;
	}

	public String pkName() {
		return "gongNengCZYSDM";
	}

	public void pkNull() {
		this.gongNengCZYSDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.GongNengCZYS obj = (com.poweruniverse.app.entity.trans.GongNengCZYS)o;
		if(this.getGongNengCZYSDM()==null){
			return 1;
		}
		return this.getGongNengCZYSDM().compareTo(obj.getGongNengCZYSDM());
	}
	
	public com.poweruniverse.app.entity.trans.GongNengCZYS clone(){
		com.poweruniverse.app.entity.trans.GongNengCZYS gongNengCZYS = new com.poweruniverse.app.entity.trans.GongNengCZYS();
		
		gongNengCZYS.setSourceCZDH(sourceCZDH);
		gongNengCZYS.setTargetCZDH(targetCZDH);
		gongNengCZYS.setGongNengCZYSMC(gongNengCZYSMC);
		gongNengCZYS.setGongNengYS(gongNengYS);
		
		return gongNengCZYS;
	}
	
	
	
	
	
	
}