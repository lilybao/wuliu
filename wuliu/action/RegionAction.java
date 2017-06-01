package cn.itheima.bos.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itheima.bos.domain.PageBean;
import cn.itheima.bos.domain.Region;
import cn.itheima.bos.service.IRegionService;
import cn.itheima.bos.utils.BosUtils;
import cn.itheima.bos.utils.CommonUtils;
import cn.itheima.bos.utils.PinYin4jUtils;
import cn.itheima.bos.web.base.BaseAction;
@Controller
@Scope(value="prototype")
public class RegionAction extends BaseAction<Region> {
	@Resource
	private IRegionService regionService;
	private File regionFile;//客服端上传的文件
	private String q;
	public void setQ(String q) {
		this.q = q;
	}
	public void setRegionFile(File regionFile) {
		this.regionFile = regionFile;
	}
	/**
	 * 分页查询区域列表
	 * @return
	 * @throws IOException
	 */
	public String pagelist() throws IOException{
		regionService.pageQuery(pageBean);
		//writeObject2Json(pageBean);
		writeObject2JsonLib(pageBean, new String[]{"regionSubareas"});
		return NONE;
	}
	/**
	 * AJAX异步加载下拉框region数据
	 * 需要的查询数据 province+city+district 
	 * @return
	 * @throws IOException 
	 */
	public String nameAjax() throws IOException{
		List<Region> list=null;
		if(StringUtils.isNotBlank(q)){
			list=regionService.findByCondition(q);
		}else {
			
			list=regionService.findAll();
		}
		writeList2JsonLib(list, new String[]{"regionSubareas"});
		//String jsonStr = CommonUtils.toJSONStr(list);
		//BosUtils.getResponse().setContentType("text/html;charset=utf-8");
		//BosUtils.getResponse().getWriter().print(jsonStr);
		return NONE;
	}
	/**
	 * 上传文件
	 * @return
	 * @throws IOException 
	 */
	public String upload() throws IOException{
		String flag="1";//若成功上传并插入到数据库 返回1
		try {
			//1 创建workbook空间 读取整个文档
			HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(regionFile));
			//2 获取一个sheet 表
			HSSFSheet sheet=workbook.getSheetAt(0);
			//3 把读取到的表中的数据放到集合里
			List<Region> list=new ArrayList<>();
			// 4循环读取表中每一行的数据
			for(Row row:sheet){
				int rowNum = row.getRowNum();
				if(rowNum==0){
					continue;
				}
				// 5读取某一行中的单元格
				String id=row.getCell(0).getStringCellValue();
				String province=row.getCell(1).getStringCellValue();
				String city=row.getCell(2).getStringCellValue();
				String district=row.getCell(3).getStringCellValue();
				String postcode=row.getCell(4).getStringCellValue();
				//创建一个Region对象 将数据放入对象中
				Region region=new Region(id, province, city, district, postcode, null, null, null);
				//插入另外的数据
				province=province.substring(0, province.length()-1);
				city=city.substring(0, city.length()-1);
				district=district.substring(0, district.length()-1);
				String[] headByString = PinYin4jUtils.getHeadByString(province+city+district);
				String shortcode = StringUtils.join(headByString, "");
				System.out.println(shortcode);
				region.setShortcode(shortcode);
				String citycode=PinYin4jUtils.hanziToPinyin(city, "");
				System.out.println(citycode);
				region.setCitycode(citycode);
				list.add(region);
			}
			//
			regionService.upload(list);
		} catch (IOException e) {
			flag="0";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BosUtils.getResponse().getWriter().print(flag);
		return NONE;
	}
}
