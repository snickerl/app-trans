package com.poweruniverse.app.entity.trans.base;
import java.io.Serializable;
import java.util.List;
import com.poweruniverse.nim.data.entity.Version;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
/*
* 实体类：传输记录
*/
@Version("2015-03-26 05:45:16")
public abstract class BaseChuanShuJL  implements Serializable,Comparable<Object> ,EntityI {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	// constructors
	public BaseChuanShuJL () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseChuanShuJL (java.lang.Integer id) {
		this.setChuanShuJLDM(id);
		initialize();
	}

	protected abstract void initialize ();
	
			
	// 属性：源实体类代号 （sourceSTLDH）
	private java.lang.String sourceSTLDH = null;
	public java.lang.String getSourceSTLDH(){return this.sourceSTLDH ;}
	public void setSourceSTLDH(java.lang.String sourceSTLDH){this.sourceSTLDH = sourceSTLDH;}
	
	// 主键：chuanShuJLDM
	private java.lang.Integer chuanShuJLDM = null;
	public java.lang.Integer getChuanShuJLDM(){return this.chuanShuJLDM ;}
	public void setChuanShuJLDM(java.lang.Integer chuanShuJLDM){this.chuanShuJLDM = chuanShuJLDM;}

			
	// 属性：源主键值 （sourceZJZ）
	private java.lang.Integer sourceZJZ = new java.lang.Integer(0);
	public java.lang.Integer getSourceZJZ(){return this.sourceZJZ ;}
	public void setSourceZJZ(java.lang.Integer sourceZJZ){this.sourceZJZ = sourceZJZ;}
	
			
	// 属性：目标系统代号 （targetXTDH）
	private java.lang.String targetXTDH = null;
	public java.lang.String getTargetXTDH(){return this.targetXTDH ;}
	public void setTargetXTDH(java.lang.String targetXTDH){this.targetXTDH = targetXTDH;}
	
			
	// 属性：目标功能代号 （targetGNDH）
	private java.lang.String targetGNDH = null;
	public java.lang.String getTargetGNDH(){return this.targetGNDH ;}
	public void setTargetGNDH(java.lang.String targetGNDH){this.targetGNDH = targetGNDH;}
	
			
	// 属性：目标操作代号 （targetCZDH）
	private java.lang.String targetCZDH = null;
	public java.lang.String getTargetCZDH(){return this.targetCZDH ;}
	public void setTargetCZDH(java.lang.String targetCZDH){this.targetCZDH = targetCZDH;}
	
			
	// 属性：目标主键值 （targetZJZ）
	private java.lang.Integer targetZJZ = new java.lang.Integer(0);
	public java.lang.Integer getTargetZJZ(){return this.targetZJZ ;}
	public void setTargetZJZ(java.lang.Integer targetZJZ){this.targetZJZ = targetZJZ;}
	
			
	// 属性：信息类别名称 （xinXiLBMC）
	private java.lang.String xinXiLBMC = null;
	public java.lang.String getXinXiLBMC(){return this.xinXiLBMC ;}
	public void setXinXiLBMC(java.lang.String xinXiLBMC){this.xinXiLBMC = xinXiLBMC;}
	
			
	// 属性：JSON数据 （jsonData）
	private java.lang.String jsonData = null;
	public java.lang.String getJsonData(){return this.jsonData ;}
	public void setJsonData(java.lang.String jsonData){this.jsonData = jsonData;}
	
			
	// 属性：数据类别名称 （shuJuLBMC）
	private java.lang.String shuJuLBMC = null;
	public java.lang.String getShuJuLBMC(){return this.shuJuLBMC ;}
	public void setShuJuLBMC(java.lang.String shuJuLBMC){this.shuJuLBMC = shuJuLBMC;}
	
			
	// 属性：是否传输完成 （shiFouCSWC）
	private java.lang.Boolean shiFouCSWC = new java.lang.Boolean(false);
	public java.lang.Boolean getShiFouCSWC(){return this.shiFouCSWC ;}
	public void setShiFouCSWC(java.lang.Boolean shiFouCSWC){this.shiFouCSWC = shiFouCSWC;}
	
			
	// 属性：传输次数 （chuanShuCS）
	private java.lang.Integer chuanShuCS = new java.lang.Integer(0);
	public java.lang.Integer getChuanShuCS(){return this.chuanShuCS ;}
	public void setChuanShuCS(java.lang.Integer chuanShuCS){this.chuanShuCS = chuanShuCS;}
	
			
	// 属性：错误信息 （cuoWuXXX）
	private java.lang.String cuoWuXXX = null;
	public java.lang.String getCuoWuXXX(){return this.cuoWuXXX ;}
	public void setCuoWuXXX(java.lang.String cuoWuXXX){this.cuoWuXXX = cuoWuXXX;}
	
	// 集合：依赖传输记录 （yls）
	private java.util.Set<com.poweruniverse.app.entity.trans.ChuanShuJLYL> yls = new java.util.TreeSet<com.poweruniverse.app.entity.trans.ChuanShuJLYL>();
	public java.util.Set<com.poweruniverse.app.entity.trans.ChuanShuJLYL> getYls(){return this.yls ;}
	public void setYls(java.util.Set<com.poweruniverse.app.entity.trans.ChuanShuJLYL> yls){this.yls = yls;}
	public void addToyls(Object parent,Object detail){
		com.poweruniverse.app.entity.trans.ChuanShuJL mainObj = (com.poweruniverse.app.entity.trans.ChuanShuJL)parent;
		com.poweruniverse.app.entity.trans.ChuanShuJLYL subObj = (com.poweruniverse.app.entity.trans.ChuanShuJLYL)detail;
		subObj.setChuanShuJL(mainObj);
		mainObj.getYls().add(subObj);
	}
	public void removeFromyls(Object parent,Object detail){
		com.poweruniverse.app.entity.trans.ChuanShuJL mainObj = (com.poweruniverse.app.entity.trans.ChuanShuJL)parent;
		com.poweruniverse.app.entity.trans.ChuanShuJLYL subObj = (com.poweruniverse.app.entity.trans.ChuanShuJLYL)detail;
		subObj.setChuanShuJL(null);
		mainObj.getYls().remove(subObj);
	}
	public Object getylsById(Object id){
		java.util.Iterator<com.poweruniverse.app.entity.trans.ChuanShuJLYL> ds = this.getYls().iterator();
		com.poweruniverse.app.entity.trans.ChuanShuJLYL d = null;
		while(ds.hasNext()){
			d = ds.next();
			if(d.getChuanShuJLYLDM()!=null && d.getChuanShuJLYLDM().equals(id)){
				return d;
			}
		}
		return null;
	}
	public com.poweruniverse.app.entity.trans.ChuanShuJLYL newylsByParent(com.poweruniverse.app.entity.trans.ChuanShuJL parent) throws Exception{
		com.poweruniverse.app.entity.trans.ChuanShuJLYL subObj = new com.poweruniverse.app.entity.trans.ChuanShuJLYL();
		//
		subObj.setChuanShuJL(parent);
		//
		return subObj;
	}
	
			
	// 属性：源系统代号 （sourceXTDH）
	private java.lang.String sourceXTDH = null;
	public java.lang.String getSourceXTDH(){return this.sourceXTDH ;}
	public void setSourceXTDH(java.lang.String sourceXTDH){this.sourceXTDH = sourceXTDH;}
	
			
	// 属性：创建时间 （chuangJianSJ）
	private java.util.Date chuangJianSJ = null;
	public java.util.Date getChuangJianSJ(){return this.chuangJianSJ ;}
	public void setChuangJianSJ(java.util.Date chuangJianSJ){this.chuangJianSJ = chuangJianSJ;}
	
			
	// 属性：最后发送时间 （zuiHouFSSJ）
	private java.util.Date zuiHouFSSJ = null;
	public java.util.Date getZuiHouFSSJ(){return this.zuiHouFSSJ ;}
	public void setZuiHouFSSJ(java.util.Date zuiHouFSSJ){this.zuiHouFSSJ = zuiHouFSSJ;}
	
			
	// 属性：源功能代号 （sourceGNDH）
	private java.lang.String sourceGNDH = null;
	public java.lang.String getSourceGNDH(){return this.sourceGNDH ;}
	public void setSourceGNDH(java.lang.String sourceGNDH){this.sourceGNDH = sourceGNDH;}
	
			
	// 属性：源操作代号 （sourceCZDH）
	private java.lang.String sourceCZDH = null;
	public java.lang.String getSourceCZDH(){return this.sourceCZDH ;}
	public void setSourceCZDH(java.lang.String sourceCZDH){this.sourceCZDH = sourceCZDH;}
	
			
	// 属性：目标实体类代号 （targetSTLDH）
	private java.lang.String targetSTLDH = null;
	public java.lang.String getTargetSTLDH(){return this.targetSTLDH ;}
	public void setTargetSTLDH(java.lang.String targetSTLDH){this.targetSTLDH = targetSTLDH;}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.poweruniverse.app.entity.trans.ChuanShuJL)) return false;
		else {
			com.poweruniverse.app.entity.trans.ChuanShuJL entity = (com.poweruniverse.app.entity.trans.ChuanShuJL) obj;
			if (null == this.getChuanShuJLDM() || null == entity.getChuanShuJLDM()) return false;
			else return (this.getChuanShuJLDM().equals(entity.getChuanShuJLDM()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getChuanShuJLDM()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getChuanShuJLDM().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	public String toString() {
		return this.chuanShuJLDM+"";
	}

	public Integer pkValue() {
		return this.chuanShuJLDM;
	}

	public String pkName() {
		return "chuanShuJLDM";
	}

	public void pkNull() {
		this.chuanShuJLDM = null;;
	}
	
	public int compareTo(Object o) {
		com.poweruniverse.app.entity.trans.ChuanShuJL obj = (com.poweruniverse.app.entity.trans.ChuanShuJL)o;
		if(this.getChuanShuJLDM()==null){
			return 1;
		}
		return this.getChuanShuJLDM().compareTo(obj.getChuanShuJLDM());
	}
	
	public com.poweruniverse.app.entity.trans.ChuanShuJL clone(){
		com.poweruniverse.app.entity.trans.ChuanShuJL chuanShuJL = new com.poweruniverse.app.entity.trans.ChuanShuJL();
		
		chuanShuJL.setSourceSTLDH(sourceSTLDH);
		chuanShuJL.setSourceZJZ(sourceZJZ);
		chuanShuJL.setTargetXTDH(targetXTDH);
		chuanShuJL.setTargetGNDH(targetGNDH);
		chuanShuJL.setTargetCZDH(targetCZDH);
		chuanShuJL.setTargetZJZ(targetZJZ);
		chuanShuJL.setXinXiLBMC(xinXiLBMC);
		chuanShuJL.setJsonData(jsonData);
		chuanShuJL.setShuJuLBMC(shuJuLBMC);
		chuanShuJL.setShiFouCSWC(shiFouCSWC);
		chuanShuJL.setChuanShuCS(chuanShuCS);
		chuanShuJL.setCuoWuXXX(cuoWuXXX);
		for(com.poweruniverse.app.entity.trans.ChuanShuJLYL subObj:this.getYls()){
			chuanShuJL.addToyls(chuanShuJL, subObj.clone());
		}
		chuanShuJL.setSourceXTDH(sourceXTDH);
		chuanShuJL.setChuangJianSJ(chuangJianSJ);
		chuanShuJL.setZuiHouFSSJ(zuiHouFSSJ);
		chuanShuJL.setSourceGNDH(sourceGNDH);
		chuanShuJL.setSourceCZDH(sourceCZDH);
		chuanShuJL.setTargetSTLDH(targetSTLDH);
		
		return chuanShuJL;
	}
	
	
	
	
	
	
}