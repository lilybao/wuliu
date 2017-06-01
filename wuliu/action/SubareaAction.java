package cn.itheima.bos.web.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itheima.bos.domain.Subarea;
import cn.itheima.bos.service.ISubareaService;
import cn.itheima.bos.utils.BosUtils;
import cn.itheima.bos.web.base.BaseAction;
@Controller
@Scope("prototype")
public class SubareaAction extends BaseAction<Subarea> {
	@Resource
	private ISubareaService subareaService;
	private String ids;
	public void setIds(String ids) {
		this.ids = ids;
	}
	private String decidedzoneId;
	public void setDecidedzoneId(String decidedzoneId) {
		this.decidedzoneId = decidedzoneId;
	}
	/**
	 * 查询分区中的每个省市下的分区的数量  返回连个参数
	 * @return
	 * @throws IOException 
	 */
	public String findGroupedSubarea() throws IOException{
		List<Object>list=subareaService.findGroupedSubarea();
		writeList2JsonLib(list, new String[]{});
		return NONE;
	}
	/**
	 * subarea列表导出到xls中
	 * @return
	 * @throws IOException 
	 */
	public String exportXls() throws IOException{
		if(StringUtils.isNotBlank(ids)){//对页面传递数据进行非空校验
			List<Subarea> list=subareaService.findById(ids);//根据页面传递的id进入数据库查找数据 并写入xls中
			export(list);
		}
		return NONE;
	}
	/**
	 * 添加subarea分区数据
	 * @return
	 */
	public String add(){
		if(model!=null){
			subareaService.save(model);
		}
		return "list";
	}
	/**
	 * subarea列表分页查询
	 * @return
	 * @throws IOException
	 */
	public String pageQuery() throws IOException{
		subareaService.pageQuery(pageBean,model);
		//writeObject2Json(pageBean);
		writeObject2JsonLib(pageBean, new String[]{"subareaDecidedzone","regionSubareas"});
		return NONE;
	}
	/**
	 * ajax加载没有关联定区的分区 subarea中没有decidezone_id外键
	 * @return
	 * @throws IOException 
	 */
	public String listAjaxNoDecidezone() throws IOException{
		List<Subarea> list=subareaService.findNoDecidezone();
		//writeObject2Json(list);
		writeList2JsonLib(list, new String[]{"subareaDecidedzone","subareaRegion"});
		return NONE;
	}
	/**
	 * 查找与定区关联的分区
	 * @return
	 * @throws IOException 
	 */
	public String  decidedZoneAssociationSubarea() throws IOException{
		List<Subarea> list=subareaService.finddecidedZoneAssociationSubarea(decidedzoneId);
		writeList2JsonLib(list, new String[]{"subareaDecidedzone","regionSubareas"});
		return NONE;
	}
}
