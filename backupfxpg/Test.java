package smart.modules.客户.在线风险评估;

import java.io.IOException;

import smart.lib.device.ManagerUtil;
import smart.lib.module.ModuleCefTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.FreeMarkerUtil2;
import smart.lib.template.Model;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.core.tools.PictureUtil;
import cn.com.agree.ab.trade.dataStruct.view.View;

public class Test extends ModuleCefTrade{

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public void doInit() throws Exception {
		// TODO 自动生成的方法存根
		//String result = issueCall("blnakCardCount",ManagerUtil.issueParameter("getAndSetCount","50"));
		//pushInfo("诶嘿嘿嘿\n啦啦啦啦");
		/*Model model = new Model();
		//model.put("test", "这是一个测试");
		String html = FreeMarkerUtil2.getTemplateString(this,"test.ftl",model);
		logger.info("html:"+html);
		String script = "html = `"+html+"`";
		logger.info("script:"+script);
		eval(script);*/
		/*Model model = new Model();
		String code = "C://world.jpg";
		String world = "C://world.jpg";
		model.put("code",code);
		model.put("world",world);
		String html = FreeMarkerUtil2.getTemplateString(this,"page.ftl", model);
		logger.info("html:"+html);
		String script = "getImg(`"+html+"`)";
		logger.info("script:"+script);
		eval(script);*/
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO 自动生成的方法存根
		
	}

	public String test(){
		return null;
	}
	
	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		Model model = new Model();
		return FreeMarkerUtil2.getTemplate(this,"default.ftl", model);
	}
	
	public void exit(String id){
		exit(0);
	}
	
	public void remove(String id) throws IOException{
		String result = null;
		try {
			result = issueCall("blnakCardCount",ManagerUtil.issueParameter("removeOne"));
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		pushInfo(result);
	}
	
	public void get(String id) throws IOException{
		String result = null;
		try {
			result = issueCall("blnakCardCount",ManagerUtil.issueParameter("getCount"));
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		pushInfo(result);
	}
	
	public void set(String id) throws IOException{
		String result = null;
		try {
			result = issueCall("blnakCardCount",ManagerUtil.issueParameter("setCount","150"));
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		pushInfo(result);
	}
	
}
