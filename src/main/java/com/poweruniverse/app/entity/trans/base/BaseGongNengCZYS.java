package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：功能操作映射
*/
@Version("2015-03-17 04:41:16")
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

			
	// 属性：目标功能代号 （targetGNDH）
	private java.lang.String targetGNDH = null;
	public java.lang.String getTargetGNDH(){return this.targetGNDH ;}
	public void setTargetGNDH(java.lang.String targetGNDH){this.targetGNDH = targetGNDH;}
	
			
	// 属性：源操作代号 （sourceCZDH）
	private java.lang.String sourceCZDH = null;
	public java.lang.String getSourceCZDH(){return this.sourceCZDH ;}
	public void setSourceCZDH(java.lang.String sourceCZDH){this.sourceCZDH = sourceCZDH;}
	
			
	// 属性：源功能代号 （sourceGNDH）
	private java.lang.String sourceGNDH = null;
	public java.lang.String getSourceGNDH(){return this.sourceGNDH ;}
	public void setSourceGNDH(java.lang.String sourceGNDH){this.sourceGNDH = sourceGNDH;}
	
			
	// 属性：目标系统代号 （targetXTDH）
	private java.lang.String targetXTDH = null;
	public java.lang.String getTargetXTDH(){return this.targetXTDH ;}
	public void setTargetXTDH(java.lang.String targetXTDH){this.targetXTDH = targetXTDH;}
	
			
	// 属性：源系统代号 （sourceXTDH）
	private java.lang.String sourceXTDH = null;
	public java.lang.String getSourceXTDH(){return this.sourceXTDH ;}
	public void setSourceXTDH(java.lang.String sourceXTDH){this.sourceXTDH = sourceXTDH;}
	
			
	// 属性：目标操作代号 （targetCZDH）
	private java.lang.String targetCZDH = null;
	public java.lang.String getTargetCZDH(){return this.targetCZDH ;}
	public void setTargetCZDH(java.lang.String targetCZDH){this.targetCZDH = targetCZDH;}
	
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
		return this.gongNengCZYSDM+"";
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
		
		gongNengCZYS.setTargetGNDH(targetGNDH);
		gongNengCZYS.setSourceCZDH(sourceCZDH);
		gongNengCZYS.setSourceGNDH(sourceGNDH);
		gongNengCZYS.setTargetXTDH(targetXTDH);
		gongNengCZYS.setSourceXTDH(sourceXTDH);
		gongNengCZYS.setTargetCZDH(targetCZDH);
		
		return gongNengCZYS;
	}
	
	
	
	
	
	
}