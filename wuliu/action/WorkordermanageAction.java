package cn.itheima.bos.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Rows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.ctc.wstx.util.StringUtil;
import com.sun.mail.iap.Response;

import cn.itheima.bos.domain.Workordermanage;
import cn.itheima.bos.service.IWorkordermanageService;
import cn.itheima.bos.utils.BosUtils;
import cn.itheima.bos.utils.FileUtils;
import cn.itheima.bos.web.base.BaseAction;

@Controller
@Scope("prototype")
public class WorkordermanageAction extends BaseAction<Workordermanage> {
	
	@Resource
	private IWorkordermanageService workordermanageService;
	private File workorderFile;//客户端上传的文件
	public void setWorkorderFile(File workorderFile) {
		this.workorderFile = workorderFile;
	}
	/**
	 * 
	* @Description: TODO(模板下载)
	* @author 李保健
	* @date 2017年4月8日 下午5:16:32
	* @return String
	 * @throws IOException 
	 */
	public String download() throws IOException{
		//String fileName=  (String) ServletActionContext.getRequest().getAttribute("filename");
		String fileName=  "工作单导入模板.xls";
		//创建工作空间
		HSSFWorkbook wb=new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("工作单");
		//给第一行写入每一列的名字
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("编号");
		row.createCell(1).setCellValue("产品");
		row.createCell(2).setCellValue("产品时限");
		row.createCell(3).setCellValue("产品类型");
		row.createCell(4).setCellValue("发件人姓名");
		row.createCell(5).setCellValue("发件人电话");
		row.createCell(6).setCellValue("发件人地址");
		row.createCell(7).setCellValue("收件人姓名");
		row.createCell(8).setCellValue("收件人电话");
		row.createCell(9).setCellValue("收件人地址");
		row.createCell(10).setCellValue("实际重量");
		List<Workordermanage> list=workordermanageService.findAll();
		int i=1;
		for(Workordermanage workordermanage:list){
			//把每一个对象写到表格的每一列
			HSSFRow row2 = sheet.createRow(i++);
			row2.createCell(0).setCellValue(workordermanage.getId());
			row2.createCell(1).setCellValue(workordermanage.getProduct());
			row2.createCell(2).setCellValue(workordermanage.getProdtimelimit());
			row2.createCell(3).setCellValue(workordermanage.getProdtype());
			row2.createCell(4).setCellValue(workordermanage.getSendername());
			row2.createCell(5).setCellValue(workordermanage.getSenderphone());
			row2.createCell(6).setCellValue(workordermanage.getSenderaddr());
			row2.createCell(7).setCellValue(workordermanage.getReceivername());
			row2.createCell(8).setCellValue(workordermanage.getReceiverphone());
			row2.createCell(9).setCellValue(workordermanage.getReceiveraddr());
			if(StringUtils.isNotBlank(workordermanage.getActlweit().toString())){
				row2.createCell(10).setCellValue(workordermanage.getActlweit());
			}else {
				row2.createCell(10).setCellValue("");
			}
		}
		//获得浏览器的类型
		String agent = ServletActionContext.getRequest().getHeader("User-Agent");
		String filename=FileUtils.encodeDownloadFilename(fileName, agent);//对表头根据浏览器类型进行编码
		//下载的 一个流两个头
		ServletOutputStream os = ServletActionContext.getResponse().getOutputStream();
		String mimeType = ServletActionContext.getServletContext().getMimeType(fileName);
		//一个头
		ServletActionContext.getResponse().setContentType(mimeType);
		//一个头
		ServletActionContext.getResponse().setHeader("content-disposition", "attachment;filename="+filename);
		wb.write(os);
		return NONE;
	}
	/**
	 * 批量导入工作单数据
	 * @return
	 * @throws IOException 
	 */
	public String batchImport() throws IOException{
		String flag="1";
		try {
			//1.创建workspace
			HSSFWorkbook wb=new HSSFWorkbook(new FileInputStream(workorderFile));
			HSSFSheet sheet = wb.getSheetAt(0);//获取excel文件中的一张表
			List<Workordermanage> list=new ArrayList<>();
			for(Row row:sheet){//读取每一行
				int rowNum = row.getRowNum();
				if(rowNum==0){//第一行为标题
					continue;
				}
				//读取每一行中的单元格
				String id = row.getCell(0).getStringCellValue();
				String product = row.getCell(1).getStringCellValue();
				String prodtimelimit = row.getCell(2).getStringCellValue();
				String prodtype = row.getCell(3).getStringCellValue();
				String sendername = row.getCell(4).getStringCellValue();
				String senderphone = row.getCell(5).getStringCellValue();
				String senderaddr = row.getCell(6).getStringCellValue();
				String receivername = row.getCell(7).getStringCellValue();
				String receiverphone = row.getCell(8).getStringCellValue();
				String receiveraddr = row.getCell(9).getStringCellValue();
				Double actlweit = row.getCell(10).getNumericCellValue();
				Workordermanage workordermanage=new Workordermanage(id, null, product, null, null, null, prodtimelimit, prodtype, sendername, senderphone, senderaddr, receivername, receiverphone, receiveraddr, null, actlweit, null, null, new Date());
				list.add(workordermanage);
			}
			workordermanageService.upload(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			flag="0";
			e.printStackTrace();
		}
		BosUtils.getResponse().getWriter().println(flag);
		return NONE;
	}
	/**
	 * 添加工作单 快速录入工作单
	 * @return
	 * @throws IOException 
	 */
	public String editWorkorder() throws IOException{
		String flag="1";
		try {
			workordermanageService.add(model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flag="0";
			e.printStackTrace();
		}
		BosUtils.getResponse().setContentType("text/html;charset=utf-8");
		BosUtils.getResponse().getWriter().print(flag);
		return NONE;
	}
	/**
	 * 快速录入工作单分页查询
	 * @return
	 * @throws IOException 
	 */
	public String pagequery() throws IOException{
		workordermanageService.pagequery(pageBean);
		writeObject2JsonLib(pageBean, new String[]{});
		return NONE;
	}
}
