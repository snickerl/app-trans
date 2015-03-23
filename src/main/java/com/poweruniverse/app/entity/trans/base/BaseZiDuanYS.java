package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：字段映射
*/
@Version("2015-03-17 05:41:17")
public abstract class BaseZiDuanYS  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseZiDuanYS () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseZiDuanYS (java.lang.Integer id) {
		this.setZiDuanYSDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
	// 主键：ziDuanYSDM
	private java.lang.Integer ziDuanYSDM = null;
	public java.lang.Integer getZiDuanYSDM(){return this.ziDuanYSDM ;}
	public void setZiDuanYSDM(java.lang.Integer ziDuanYSDM){this.ziDuanYSDM = ziDuanYSDM;}

	// 对象：实体类映射 （shiTiLeiYS）
	private com.poweruniverse.app.entity.trans.ShiTiLeiYS shiTiLeiYS;
	public com.poweruniverse.app.entity.trans.ShiTiLeiYS getShiTiLeiYS(){return this.shiTiLeiYS ;}
	public void setShiTiLeiYS(com.poweruniverse.app.entity.trans.ShiTiLeiYS shiTiLeiYS){this.shiTiLeiYS = shiTiLeiYS;}

			
	// 属性：源字段代号 （sourceZDDH）
	private java.lang.String sourceZDDH = null;
	public java.lang.String getSourceZDDH(){return this.sourceZDDH ;}
	public void setSourceZDDH(java.lang.String sourceZDDH){this.sourceZDDH = sourceZDDH;}
	
	// 对象：源字段类型 （sourceZDLX）
	private com.poweruniverse.nim.data.entity.sys.ZiDuanLX sourceZDLX;
	public com.poweruniverse.nim.data.entity.sys.ZiDuanLX getSourceZDLX(){return this.sourceZDLX ;}
	public void setSourceZDLX(com.poweruniverse.nim.data.entity.sys.ZiDuanLX sourceZDLX){this.sourceZDLX = sourceZDLX;}

			
	// 属性：目标字段代号 （targetZDDH）
	private java.lang.String targetZDDH = null;
	public java.lang.String getTargetZDDH(){return this.targetZDDH ;}
	public void setTargetZDDH(java.lang.String targetZDDH){this.targetZDDH = targetZDDH;}
	
	// 对象：目标字段类型 （targetZDLX）
	private com.poweruniverse.nim.data.entity.sys.ZiDuanLX targetZDLX;
	public com.poweruniverse.nim.data.entity.sys.ZiDuanLX getTargetZDLX(){return this.targetZDLX ;}
	public void setTargetZDLX(com.poweruniverse.nim.data.entity.sys.ZiDuanLX targetZDLX){this.targetZDLX = targetZDLX;}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.ZiDuanYS)) return false;
		else {
			com.poweruniverse.app.entity.trans.ZiDuanYS entity = (com.poweruniverse.app.entity.trans.ZiDuanYS) obj;
			if (null == this.getZiDuanYSDM() || null == entity.getZiDuanYSDM()) return false;
			else return (this.getZiDuanYSDM().equals(entity.getZiDuanYSDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getZiDuanYSDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getZiDuanYSDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.ziDuanYSDM+"";
	}

	public Integer pkValue() {
		return this.ziDuanYSDM;
	}

	public String pkName() {
		return "ziDuanYSDM";
	}

	public void pkNull() {
		this.ziDuanYSDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.ZiDuanYS obj = (com.poweruniverse.app.entity.trans.ZiDuanYS)o;
		if(this.getZiDuanYSDM()==null){
			return 1;
		}
		return this.getZiDuanYSDM().compareTo(obj.getZiDuanYSDM());
	}
	
	public com.poweruniverse.app.entity.trans.ZiDuanYS clone(){
		com.poweruniverse.app.entity.trans.ZiDuanYS ziDuanYS = new com.poweruniverse.app.entity.trans.ZiDuanYS();
		
		ziDuanYS.setShiTiLeiYS(shiTiLeiYS);
		ziDuanYS.setSourceZDDH(sourceZDDH);
		ziDuanYS.setSourceZDLX(sourceZDLX);
		ziDuanYS.setTargetZDDH(targetZDDH);
		ziDuanYS.setTargetZDLX(targetZDLX);
		
		return ziDuanYS;
	}
	
	
	
	
	
	
}