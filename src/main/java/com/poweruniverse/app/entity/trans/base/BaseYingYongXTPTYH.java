package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：应用系统数据平台用户
*/
@Version("2015-04-01 08:03:27")
public abstract class BaseYingYongXTPTYH  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseYingYongXTPTYH () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseYingYongXTPTYH (java.lang.Integer id) {
		this.setShuJuPTYHDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
	// 主键：shuJuPTYHDM
	private java.lang.Integer shuJuPTYHDM = null;
	public java.lang.Integer getShuJuPTYHDM(){return this.shuJuPTYHDM ;}
	public void setShuJuPTYHDM(java.lang.Integer shuJuPTYHDM){this.shuJuPTYHDM = shuJuPTYHDM;}

	// 对象：用户 （yongHu）
	private com.poweruniverse.nim.data.entity.sys.YongHu yongHu;
	public com.poweruniverse.nim.data.entity.sys.YongHu getYongHu(){return this.yongHu ;}
	public void setYongHu(com.poweruniverse.nim.data.entity.sys.YongHu yongHu){this.yongHu = yongHu;}

	// 对象：应用系统 （yingYongXT）
	private com.poweruniverse.app.entity.trans.YingYongXT yingYongXT;
	public com.poweruniverse.app.entity.trans.YingYongXT getYingYongXT(){return this.yingYongXT ;}
	public void setYingYongXT(com.poweruniverse.app.entity.trans.YingYongXT yingYongXT){this.yingYongXT = yingYongXT;}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.YingYongXTPTYH)) return false;
		else {
			com.poweruniverse.app.entity.trans.YingYongXTPTYH entity = (com.poweruniverse.app.entity.trans.YingYongXTPTYH) obj;
			if (null == this.getShuJuPTYHDM() || null == entity.getShuJuPTYHDM()) return false;
			else return (this.getShuJuPTYHDM().equals(entity.getShuJuPTYHDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getShuJuPTYHDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getShuJuPTYHDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.shuJuPTYHDM+"";
	}

	public Integer pkValue() {
		return this.shuJuPTYHDM;
	}

	public String pkName() {
		return "shuJuPTYHDM";
	}

	public void pkNull() {
		this.shuJuPTYHDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.YingYongXTPTYH obj = (com.poweruniverse.app.entity.trans.YingYongXTPTYH)o;
		if(this.getShuJuPTYHDM()==null){
			return 1;
		}
		return this.getShuJuPTYHDM().compareTo(obj.getShuJuPTYHDM());
	}
	
	public com.poweruniverse.app.entity.trans.YingYongXTPTYH clone(){
		com.poweruniverse.app.entity.trans.YingYongXTPTYH yingYongXTPTYH = new com.poweruniverse.app.entity.trans.YingYongXTPTYH();
		
		yingYongXTPTYH.setYongHu(yongHu);
		yingYongXTPTYH.setYingYongXT(yingYongXT);
		
		return yingYongXTPTYH;
	}
	
	
	
	
	
	
}