package cn.itheima.bos.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;

import cn.itheima.bos.domain.PageBean;
import cn.itheima.bos.domain.Region;
import cn.itheima.bos.domain.Staff;
import cn.itheima.bos.service.IStaffService;
import cn.itheima.bos.utils.BosUtils;
import cn.itheima.bos.utils.CommonUtils;
import cn.itheima.bos.web.base.BaseAction;
@Controller
@Scope(value="prototype")
public class StaffAction extends BaseAction<Staff> {
	@Resource
	private IStaffService staffService;
	private String value;//员工删除与否的状态
	private String ids;//选中的员工的id
	

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setIds(String ids) {
		this.ids = ids;
	}
	
	/**
	 * 添加Staff
	 * @return
	 */
	@RequiresPermissions(value="staff")
	public String add(){
		 staffService.save(model);
		return "tolist";
	}
	
	/**
	 * 查看所有Staff   没有用
	 * @return
	 */
	public String list(){
		List<Staff> list=staffService.findAll();
		String json = (String) JSON.toJSON(list);
		try {
			BosUtils.getResponse().getWriter().write(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return NONE;
	}
	
	/**
	 * 编辑Staff
	 */
	@RequiresPermissions(value="add")
	public String edit(){
		staffService.update(model);
		return "tolist";
	}
	
	/**
	 * 删除staff
	 * @return
	 */
	@RequiresPermissions("ad")
	public String delete(){
		 String[] arr = ids.split(",");
		 try {
			 staffService.deleteAndupdate(arr,value);
			 BosUtils.getResponse().getWriter().print("yes");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return NONE;
	}
	
	/**
	 * 查看所有Staff 实现分页查询 
	 * @return
	 * @throws IOException 
	 */
	public String pagelist() throws IOException{
//		PageBean pageBean=new PageBean();
//		pageBean.setCurrentPage(page);
//		pageBean.setPageCount(rows);
//		DetachedCriteria dc=DetachedCriteria.forClass(Staff.class);//创建离线查询对象
//		pageBean.setDetachedCriteria(dc);
		staffService.pageQuery(pageBean);
		//writeObject2Json(pageBean);//fastjson用法需要在实体中加注释
		writeObject2JsonLib(pageBean, new String[]{"staffDecidedzones","staffWorkbills","staffNoticebills"});
		
		/*//*********指定不需要转换成json的属性
		JsonConfig config=new JsonConfig();
		config.setExcludes(new String[]{"currentPage","pageCount","detachedCriteria","decidedzones"});
		//*********使用json-lib转化数据时
		//如果转换的是一个数组或者集合  用JSONArray  JSONArray.toJSON(javaObject)
		//如果转换的只是一个对象  用JSONObject  JSONObject.fromObject
		String json = JSONObject.fromObject(pageBean, config).toString();
		System.out.println(json);*/
//		String json = CommonUtils.toJSONStr(pageBean);
//		try {
//			BosUtils.getResponse().setContentType("text/html;charset=utf-8");
//			BosUtils.getResponse().getWriter().write(json);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return NONE;
	}
	
	/**
	 * 加载没有失效的staff delfalg =0
	 * 页面只需要id 和name 转换成json
	 * @return
	 * @throws IOException 
	 */
	public String listAjaxStaff() throws IOException{
		List<Staff> list=staffService.findNoDel();
		//writeObject2Json(list);
		writeList2JsonLib(list, new String[]{"staffDecidedzones","staffWorkbills","staffNoticebills"});
		return NONE;
	}
	
}
