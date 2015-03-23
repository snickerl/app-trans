package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：传输接口
*/
@Version("2015-03-18 10:27:01")
public abstract class BaseChuanShuJK  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseChuanShuJK () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseChuanShuJK (java.lang.Integer id) {
		this.setChuanShuJKDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
	// 主键：chuanShuJKDM
	private java.lang.Integer chuanShuJKDM = null;
	public java.lang.Integer getChuanShuJKDM(){return this.chuanShuJKDM ;}
	public void setChuanShuJKDM(java.lang.Integer chuanShuJKDM){this.chuanShuJKDM = chuanShuJKDM;}

			
	// 属性：传输接口名称 （chuanShuJKMC）
	private java.lang.String chuanShuJKMC = null;
	public java.lang.String getChuanShuJKMC(){return this.chuanShuJKMC ;}
	public void setChuanShuJKMC(java.lang.String chuanShuJKMC){this.chuanShuJKMC = chuanShuJKMC;}
	
			
	// 属性：传输接口实现类 （chuanShuJKClassName）
	private java.lang.String chuanShuJKClassName = null;
	public java.lang.String getChuanShuJKClassName(){return this.chuanShuJKClassName ;}
	public void setChuanShuJKClassName(java.lang.String chuanShuJKClassName){this.chuanShuJKClassName = chuanShuJKClassName;}
	
	// 对象：系统 （xiTong）
	private com.poweruniverse.nim.data.entity.sys.XiTong xiTong;
	public com.poweruniverse.nim.data.entity.sys.XiTong getXiTong(){return this.xiTong ;}
	public void setXiTong(com.poweruniverse.nim.data.entity.sys.XiTong xiTong){this.xiTong = xiTong;}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.ChuanShuJK)) return false;
		else {
			com.poweruniverse.app.entity.trans.ChuanShuJK entity = (com.poweruniverse.app.entity.trans.ChuanShuJK) obj;
			if (null == this.getChuanShuJKDM() || null == entity.getChuanShuJKDM()) return false;
			else return (this.getChuanShuJKDM().equals(entity.getChuanShuJKDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getChuanShuJKDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getChuanShuJKDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.chuanShuJKMC+"";
	}

	public Integer pkValue() {
		return this.chuanShuJKDM;
	}

	public String pkName() {
		return "chuanShuJKDM";
	}

	public void pkNull() {
		this.chuanShuJKDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.ChuanShuJK obj = (com.poweruniverse.app.entity.trans.ChuanShuJK)o;
		if(this.getChuanShuJKDM()==null){
			return 1;
		}
		return this.getChuanShuJKDM().compareTo(obj.getChuanShuJKDM());
	}
	
	public com.poweruniverse.app.entity.trans.ChuanShuJK clone(){
		com.poweruniverse.app.entity.trans.ChuanShuJK chuanShuJK = new com.poweruniverse.app.entity.trans.ChuanShuJK();
		
		chuanShuJK.setChuanShuJKMC(chuanShuJKMC);
		chuanShuJK.setChuanShuJKClassName(chuanShuJKClassName);
		chuanShuJK.setXiTong(xiTong);
		
		return chuanShuJK;
	}
	
	
	
	
	
	
}