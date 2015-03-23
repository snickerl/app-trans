package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：传输记录依赖
*/
@Version("2015-03-17 04:41:16")
public abstract class BaseChuanShuJLYL  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseChuanShuJLYL () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseChuanShuJLYL (java.lang.Integer id) {
		this.setChuanShuJLYLDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
	// 主键：chuanShuJLYLDM
	private java.lang.Integer chuanShuJLYLDM = null;
	public java.lang.Integer getChuanShuJLYLDM(){return this.chuanShuJLYLDM ;}
	public void setChuanShuJLYLDM(java.lang.Integer chuanShuJLYLDM){this.chuanShuJLYLDM = chuanShuJLYLDM;}

	// 对象：传输记录 （chuanShuJL）
	private com.poweruniverse.app.entity.trans.ChuanShuJL chuanShuJL;
	public com.poweruniverse.app.entity.trans.ChuanShuJL getChuanShuJL(){return this.chuanShuJL ;}
	public void setChuanShuJL(com.poweruniverse.app.entity.trans.ChuanShuJL chuanShuJL){this.chuanShuJL = chuanShuJL;}

	// 对象：依赖的传输记录 （yiLaiCSJL）
	private com.poweruniverse.app.entity.trans.ChuanShuJL yiLaiCSJL;
	public com.poweruniverse.app.entity.trans.ChuanShuJL getYiLaiCSJL(){return this.yiLaiCSJL ;}
	public void setYiLaiCSJL(com.poweruniverse.app.entity.trans.ChuanShuJL yiLaiCSJL){this.yiLaiCSJL = yiLaiCSJL;}

			
	// 属性：字段代号 （ziDuanDH）
	private java.lang.String ziDuanDH = null;
	public java.lang.String getZiDuanDH(){return this.ziDuanDH ;}
	public void setZiDuanDH(java.lang.String ziDuanDH){this.ziDuanDH = ziDuanDH;}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.ChuanShuJLYL)) return false;
		else {
			com.poweruniverse.app.entity.trans.ChuanShuJLYL entity = (com.poweruniverse.app.entity.trans.ChuanShuJLYL) obj;
			if (null == this.getChuanShuJLYLDM() || null == entity.getChuanShuJLYLDM()) return false;
			else return (this.getChuanShuJLYLDM().equals(entity.getChuanShuJLYLDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getChuanShuJLYLDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getChuanShuJLYLDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.chuanShuJLYLDM+"";
	}

	public Integer pkValue() {
		return this.chuanShuJLYLDM;
	}

	public String pkName() {
		return "chuanShuJLYLDM";
	}

	public void pkNull() {
		this.chuanShuJLYLDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.ChuanShuJLYL obj = (com.poweruniverse.app.entity.trans.ChuanShuJLYL)o;
		if(this.getChuanShuJLYLDM()==null){
			return 1;
		}
		return this.getChuanShuJLYLDM().compareTo(obj.getChuanShuJLYLDM());
	}
	
	public com.poweruniverse.app.entity.trans.ChuanShuJLYL clone(){
		com.poweruniverse.app.entity.trans.ChuanShuJLYL chuanShuJLYL = new com.poweruniverse.app.entity.trans.ChuanShuJLYL();
		
		chuanShuJLYL.setChuanShuJL(chuanShuJL);
		chuanShuJLYL.setYiLaiCSJL(yiLaiCSJL);
		chuanShuJLYL.setZiDuanDH(ziDuanDH);
		
		return chuanShuJLYL;
	}
	
	
	
	
	
	
}