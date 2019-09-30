package smart.modules.客户.在线风险评估;

import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import smart.modules.柜员.柜员审核.TellerCheck;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.core.scope.Scope;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONObject;

public class CardBadCheck extends ModuleCefTrade {
	public String 核查柜员 = "";
	public boolean 核查结果 = false;
	@In(Dictionary.客户.客户姓名)
	public String customName;

	@In(Dictionary.客户.身份证号)
	public String idCardNo;
	
	@Out(Dictionary.公共节点变量.公共提示标志)
	public String 公共提示标志;

	@Out(Dictionary.公共节点变量.公共提示信息)
	public String 公共提示信息;
	
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		return true;
	}

	@Override
	public void doInit() throws Exception {
		this.playSound(默认_语音_等待审核);
	}
	
	public void reject() throws Exception {
		ModuleRequest request = getRequest();
		request.putDataBasket("status", "reject");
		moduleSubmit(request);
	}
	
	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		String status = request.getDataBasketString("status");
		if("reject".equals(status)){
			 公共提示标志 = "错误";
		     公共提示信息 = "核查未通过";
			response.putDataBasket("cardBadCheckValue", 核查结果);
			return;
		}
		JSONObject send = new JSONObject();
		send.put(TellerCheck.柜员, "审核柜员");
		send.put("cardBadCheck", true);
		JSONObject result = syncOpenFrame(TellerCheck.class.getName(), send);
		if (result != null && !result.isEmpty()) {
			核查结果 = (Boolean) result.getBooleanValue(TellerCheck.核查结果);
			核查柜员 = result.getString(TellerCheck.柜员);
			Scope scope = this.getScope(Scope.dataBasket);
			scope.put("response", response);
			
//			repeat();
			
			if(核查结果){
			  JSONObject customInfo = new JSONObject();
		      customInfo.put("customName",customName);
			  customInfo.put("idCardNo", idCardNo);
			  customInfo.put("上传影像标志", "上传问卷及影像");
			  JSONObject upImgResult = syncOpenFrame(上传问卷及签名影像.class.getName(), customInfo);
			  String signResult = upImgResult.getString("signResult");
		      String docId = upImgResult.getString("lcpgDocId");
			  response.putDataBasket("upsignResult", signResult);
			  if (null != signResult && "true".equals(signResult)) {
				 response.putDataBasket("uplcpgDocId",docId);
				 response.putDataBasket("cardBadCheckValue", 核查结果);   //true
			  }else {
			    response.putDataBasket("uplcpgDocId","");
			     公共提示标志 = "错误";
			     公共提示信息 = "上传影像失败，请联系理财经理，填写《中国光大银行客户投资风险能力评估问卷》后继续操作，谢谢！";
			     response.putDataBasket("cardBadCheckValue", false);
			  }
			 }else{
				 公共提示标志 = "错误";
			     公共提示信息 = "核查未通过！";
			     response.putDataBasket("cardBadCheckValue", 核查结果);  //false
			 }
			
		}else{
			 公共提示标志 = "错误";
		     公共提示信息 = "核查未通过";
		     response.putDataBasket("cardBadCheckValue",核查结果);  //false
		}
	}
	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		Model model = new Model();
		model.put("cardNo", "电子签名模块异常，请理财经理审核客户签名并留存纸质问卷");
		return FreeMarkerUtil.getTemplate(this, "cardBadCheck.ftl", model);
	}

}
