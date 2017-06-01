package cn.itheima.bos.web.action;

import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itheima.bos.domain.Workbill;
import cn.itheima.bos.service.IWorkbillService;
import cn.itheima.bos.web.base.BaseAction;
@Controller
@Scope("prototype")
public class WorkbillAction extends BaseAction<Workbill> {
	@Resource
	private IWorkbillService  workbillService;
	public String pagequery() throws IOException{
		workbillService.pagequery(pageBean);
		writeObject2JsonLib(pageBean, new String[]{"noticebillUser","staffDecidedzones","staffWorkbills","staffNoticebills","noticebillStaff","noticeWorkbills"});
		return NONE;
	}
}
