package cn.itheima.bos.web.base;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.DetachedCriteria;

import com.alibaba.fastjson.parser.deserializer.TimestampDeserializer;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itheima.bos.domain.PageBean;
import cn.itheima.bos.domain.Region;
import cn.itheima.bos.domain.Subarea;
import cn.itheima.bos.domain.User;
import cn.itheima.bos.utils.BosUtils;
import cn.itheima.bos.utils.CommonUtils;
import cn.itheima.crm.service.CustomerServiceInterface;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.test.JSONAssert;

public class BaseAction<T> extends ActionSupport implements ModelDriven<T> {
	protected PageBean pageBean=new PageBean();
	public void setRows(Integer rows) {
		pageBean.setPageCount(rows);
	}
	public void setPage(Integer page) {
		pageBean.setCurrentPage(page);
	}
	//protected 类型只有子类可以使用
	@Resource 
	protected CustomerServiceInterface customerService;
	//在一个包里面可以使用
	protected T model;
	@Override
	public T getModel() {
		return  model;
	}
	public BaseAction() {
		// 现获得当前运行对象UserAction   通过当前对象获得该对象的继承父类的 参数化类型BaseAction<User>
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
		//通过参数化类型 再继续获得实际类型参数<User>
		Type[] types = parameterizedType.getActualTypeArguments();
		Class<T> type = (Class<T>) types[0];//用Type接口的一个实现类区接收对象
		DetachedCriteria detachedCriteria=DetachedCriteria.forClass(type); 
		pageBean.setDetachedCriteria(detachedCriteria);
		try {
			model= (T) type.newInstance();
		} catch (InstantiationException | IllegalAccessException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * json对象发送到页面客户端 Object类型的Fastjson对象转换
	 * 只需要将被转换的对象或集合传递进去
	 * @param pageBean
	 * @throws IOException
	 */
	public void writeObject2Json(Object obj) throws IOException{
		String jsonStr = CommonUtils.toJSONStr(obj);
		BosUtils.getResponse().setContentType("text/html;charset=utf-8");
		BosUtils.getResponse().getWriter().print(jsonStr);
	}
	/**
	 * json对象发送到页面客户端 Object类型的json-lib对象转换 
	 * 需要传入两个个参数  需要转换的对象   obj  需要排除的参数
	 * @throws IOException 
	 */
	public void writeObject2JsonLib(Object obj,String [] arrs) throws IOException{
		JsonConfig config=new JsonConfig();//获取config对象 
		config.setExcludes(arrs);
		//config.registerJsonValueProcessor(java.sql.Date.class, (JsonValueProcessor) new TimestampDeserializer());
		String string = JSONObject.fromObject(obj, config).toString();
		BosUtils.getResponse().setContentType("text/html;charset=utf-8");
		BosUtils.getResponse().getWriter().print(string);
	}
	/**
	 * json对象发送到页面客户端 list类型的json-lib对象转换 
	 * 需要传入两个个参数  需要转换的集合   obj  需要排除的参数  
	 * @throws IOException 
	 */
	public void writeList2JsonLib(List<?> list,String [] arrs) throws IOException{
		JsonConfig config=new JsonConfig();//获取config对象 
		config.setExcludes(arrs);
		//如果转换的是一个数组或者集合  用JSONArray 
		String string = JSONArray.fromObject(list,config).toString();
		BosUtils.getResponse().setContentType("text/html;charset=utf-8");
		BosUtils.getResponse().getWriter().print(string);
	}
	/**
	 * 页面导出选中的数据信息 导出excel格式
	 * @param list
	 * @throws IOException
	 */
	public void export(List<Subarea> list) throws IOException{
		if(list!=null){
			HSSFWorkbook wb=new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();//创建一个sheet表默认表名
			HSSFRow row = sheet.createRow(0);//第一行存放标题
			row.createCell(0).setCellValue("分拣编号");
			row.createCell(1).setCellValue("省份");
			row.createCell(2).setCellValue("城市");
			row.createCell(3).setCellValue("区域");
			row.createCell(4).setCellValue("关键字");
			row.createCell(5).setCellValue("起始号");
			row.createCell(6).setCellValue("终止号");
			row.createCell(7).setCellValue("单双号");
			row.createCell(8).setCellValue("位置");
			//
			int i=1;
			for (Subarea subarea:list) {
				HSSFRow row2 = sheet.createRow(i++);
				row2.createCell(0).setCellValue(subarea.getId());
				if(subarea.getSubareaRegion()!=null){
					row2.createCell(1).setCellValue(subarea.getSubareaRegion().getProvince());
					row2.createCell(2).setCellValue(subarea.getSubareaRegion().getCity());
					row2.createCell(3).setCellValue(subarea.getSubareaRegion().getDistrict());
				}else {
					row2.createCell(1).setCellValue("未定义");
					row2.createCell(2).setCellValue("未定义");
					row2.createCell(3).setCellValue("未定义");
				}
				row2.createCell(4).setCellValue(subarea.getAddresskey());
				row2.createCell(5).setCellValue(subarea.getStartnum());
				row2.createCell(6).setCellValue(subarea.getEndnum());
				row2.createCell(7).setCellValue(subarea.getSingle());
				row2.createCell(8).setCellValue(subarea.getPosition());
			}
			//将excel数据通过response回显到页面  一个流(输出流) 两个头
			String filename="abc.xls";
			ServletOutputStream os = BosUtils.getResponse().getOutputStream();
			//获得浏览器的类型User-Agent:"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0"
			String header = ServletActionContext.getRequest().getHeader("User-Agent");
			//第一个头 告诉浏览器返回的文件类型 
			String mimeType = ServletActionContext.getServletContext().getMimeType(filename);
			BosUtils.getResponse().setContentType(mimeType);
			//第二个头 告诉浏览器返回数据的打开方式content-disposition  
			BosUtils.getResponse().setHeader("content-disposition", "attachment;filename="+filename);
			//调用workbook的write方法
			wb.write(os);
			}
	}
	
}
