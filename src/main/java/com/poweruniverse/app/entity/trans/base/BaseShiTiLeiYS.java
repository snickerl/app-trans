package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：实体类映射
*/
@Version("2015-03-24 20:35:18")
public abstract class BaseShiTiLeiYS  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseShiTiLeiYS () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseShiTiLeiYS (java.lang.Integer id) {
		this.setShiTiLeiYSDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
	// 主键：shiTiLeiYSDM
	private java.lang.Integer shiTiLeiYSDM = null;
	public java.lang.Integer getShiTiLeiYSDM(){return this.shiTiLeiYSDM ;}
	public void setShiTiLeiYSDM(java.lang.Integer shiTiLeiYSDM){this.shiTiLeiYSDM = shiTiLeiYSDM;}

			
	// 属性：源实体类代号 （sourceSTLDH）
	private java.lang.String sourceSTLDH = null;
	public java.lang.String getSourceSTLDH(){return this.sourceSTLDH ;}
	public void setSourceSTLDH(java.lang.String sourceSTLDH){this.sourceSTLDH = sourceSTLDH;}
	
			
	// 属性：目标实体类代号 （targetSTLDH）
	private java.lang.String targetSTLDH = null;
	public java.lang.String getTargetSTLDH(){return this.targetSTLDH ;}
	public void setTargetSTLDH(java.lang.String targetSTLDH){this.targetSTLDH = targetSTLDH;}
	
			
	// 属性：实体类映射名称 （shiTiLeiYSMC）
	private java.lang.String shiTiLeiYSMC = null;
	public java.lang.String getShiTiLeiYSMC(){return this.shiTiLeiYSMC ;}
	public void setShiTiLeiYSMC(java.lang.String shiTiLeiYSMC){this.shiTiLeiYSMC = shiTiLeiYSMC;}
	
			
	// 属性：目标主键字段代号 （targetZJZDDH）
	private java.lang.String targetZJZDDH = null;
	public java.lang.String getTargetZJZDDH(){return this.targetZJZDDH ;}
	public void setTargetZJZDDH(java.lang.String targetZJZDDH){this.targetZJZDDH = targetZJZDDH;}
	
	// 对象：源应用系统 （sourceYYXT）
	private com.poweruniverse.app.entity.trans.YingYongXT sourceYYXT;
	public com.poweruniverse.app.entity.trans.YingYongXT getSourceYYXT(){return this.sourceYYXT ;}
	public void setSourceYYXT(com.poweruniverse.app.entity.trans.YingYongXT sourceYYXT){this.sourceYYXT = sourceYYXT;}

	// 对象：目标应用系统 （targetYYXT）
	private com.poweruniverse.app.entity.trans.YingYongXT targetYYXT;
	public com.poweruniverse.app.entity.trans.YingYongXT getTargetYYXT(){return this.targetYYXT ;}
	public void setTargetYYXT(com.poweruniverse.app.entity.trans.YingYongXT targetYYXT){this.targetYYXT = targetYYXT;}

	// 集合：字段映射集合 （zds）
	private java.util.Set<com.poweruniverse.app.entity.trans.ZiDuanYS> zds = new java.util.TreeSet<com.poweruniverse.app.entity.trans.ZiDuanYS>();
	public java.util.Set<com.poweruniverse.app.entity.trans.ZiDuanYS> getZds(){return this.zds ;}
	public void setZds(java.util.Set<com.poweruniverse.app.entity.trans.ZiDuanYS> zds){this.zds = zds;}
	public void addTozds(Object parent,Object detail){
		com.poweruniverse.app.entity.trans.ShiTiLeiYS mainObj = (com.poweruniverse.app.entity.trans.ShiTiLeiYS)parent;
		com.poweruniverse.app.entity.trans.ZiDuanYS subObj = (com.poweruniverse.app.entity.trans.ZiDuanYS)detail;
		subObj.setShiTiLeiYS(mainObj);
		mainObj.getZds().add(subObj);
	}
	public void removeFromzds(Object parent,Object detail){
		com.poweruniverse.app.entity.trans.ShiTiLeiYS mainObj = (com.poweruniverse.app.entity.trans.ShiTiLeiYS)parent;
		com.poweruniverse.app.entity.trans.ZiDuanYS subObj = (com.poweruniverse.app.entity.trans.ZiDuanYS)detail;
		subObj.setShiTiLeiYS(null);
		mainObj.getZds().remove(subObj);
	}
	public Object getzdsById(Object id){
		java.util.Iterator<com.poweruniverse.app.entity.trans.ZiDuanYS> ds = this.getZds().iterator();
		com.poweruniverse.app.entity.trans.ZiDuanYS d = null;
		while(ds.hasNext()){
			d = ds.next();
			if(d.getZiDuanYSDM()!=null && d.getZiDuanYSDM().equals(id)){
				return d;
			}
		}
		return null;
	}
	public com.poweruniverse.app.entity.trans.ZiDuanYS newzdsByParent(com.poweruniverse.app.entity.trans.ShiTiLeiYS parent) throws Exception{
		com.poweruniverse.app.entity.trans.ZiDuanYS subObj = new com.poweruniverse.app.entity.trans.ZiDuanYS();
		//
		subObj.setShiTiLeiYS(parent);
		//
		return subObj;
	}
	
			
	// 属性：源主键字段代号 （sourceZJZDDH）
	private java.lang.String sourceZJZDDH = null;
	public java.lang.String getSourceZJZDDH(){return this.sourceZJZDDH ;}
	public void setSourceZJZDDH(java.lang.String sourceZJZDDH){this.sourceZJZDDH = sourceZJZDDH;}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.ShiTiLeiYS)) return false;
		else {
			com.poweruniverse.app.entity.trans.ShiTiLeiYS entity = (com.poweruniverse.app.entity.trans.ShiTiLeiYS) obj;
			if (null == this.getShiTiLeiYSDM() || null == entity.getShiTiLeiYSDM()) return false;
			else return (this.getShiTiLeiYSDM().equals(entity.getShiTiLeiYSDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getShiTiLeiYSDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getShiTiLeiYSDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.shiTiLeiYSDM+"";
	}

	public Integer pkValue() {
		return this.shiTiLeiYSDM;
	}

	public String pkName() {
		return "shiTiLeiYSDM";
	}

	public void pkNull() {
		this.shiTiLeiYSDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.ShiTiLeiYS obj = (com.poweruniverse.app.entity.trans.ShiTiLeiYS)o;
		if(this.getShiTiLeiYSDM()==null){
			return 1;
		}
		return this.getShiTiLeiYSDM().compareTo(obj.getShiTiLeiYSDM());
	}
	
	public com.poweruniverse.app.entity.trans.ShiTiLeiYS clone(){
		com.poweruniverse.app.entity.trans.ShiTiLeiYS shiTiLeiYS = new com.poweruniverse.app.entity.trans.ShiTiLeiYS();
		
		shiTiLeiYS.setSourceSTLDH(sourceSTLDH);
		shiTiLeiYS.setTargetSTLDH(targetSTLDH);
		shiTiLeiYS.setShiTiLeiYSMC(shiTiLeiYSMC);
		shiTiLeiYS.setTargetZJZDDH(targetZJZDDH);
		shiTiLeiYS.setSourceYYXT(sourceYYXT);
		shiTiLeiYS.setTargetYYXT(targetYYXT);
		for(com.poweruniverse.app.entity.trans.ZiDuanYS subObj:this.getZds()){
			shiTiLeiYS.addTozds(shiTiLeiYS, subObj.clone());
		}
		shiTiLeiYS.setSourceZJZDDH(sourceZJZDDH);
		
		return shiTiLeiYS;
	}
	
	
	
	
	
	
}