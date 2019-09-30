package smart.modules.�ͻ�.���߷�������;

import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import smart.modules.��Ա.��Ա���.TellerCheck;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.core.scope.Scope;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONObject;

public class CardBadCheck extends ModuleCefTrade {
	public String �˲��Ա = "";
	public boolean �˲��� = false;
	@In(Dictionary.�ͻ�.�ͻ�����)
	public String customName;

	@In(Dictionary.�ͻ�.���֤��)
	public String idCardNo;
	
	@Out(Dictionary.�����ڵ����.������ʾ��־)
	public String ������ʾ��־;

	@Out(Dictionary.�����ڵ����.������ʾ��Ϣ)
	public String ������ʾ��Ϣ;
	
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		return true;
	}

	@Override
	public void doInit() throws Exception {
		this.playSound(Ĭ��_����_�ȴ����);
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
			 ������ʾ��־ = "����";
		     ������ʾ��Ϣ = "�˲�δͨ��";
			response.putDataBasket("cardBadCheckValue", �˲���);
			return;
		}
		JSONObject send = new JSONObject();
		send.put(TellerCheck.��Ա, "��˹�Ա");
		send.put("cardBadCheck", true);
		JSONObject result = syncOpenFrame(TellerCheck.class.getName(), send);
		if (result != null && !result.isEmpty()) {
			�˲��� = (Boolean) result.getBooleanValue(TellerCheck.�˲���);
			�˲��Ա = result.getString(TellerCheck.��Ա);
			Scope scope = this.getScope(Scope.dataBasket);
			scope.put("response", response);
			
//			repeat();
			
			if(�˲���){
			  JSONObject customInfo = new JSONObject();
		      customInfo.put("customName",customName);
			  customInfo.put("idCardNo", idCardNo);
			  customInfo.put("�ϴ�Ӱ���־", "�ϴ��ʾ�Ӱ��");
			  JSONObject upImgResult = syncOpenFrame(�ϴ��ʾ�ǩ��Ӱ��.class.getName(), customInfo);
			  String signResult = upImgResult.getString("signResult");
		      String docId = upImgResult.getString("lcpgDocId");
			  response.putDataBasket("upsignResult", signResult);
			  if (null != signResult && "true".equals(signResult)) {
				 response.putDataBasket("uplcpgDocId",docId);
				 response.putDataBasket("cardBadCheckValue", �˲���);   //true
			  }else {
			    response.putDataBasket("uplcpgDocId","");
			     ������ʾ��־ = "����";
			     ������ʾ��Ϣ = "�ϴ�Ӱ��ʧ�ܣ�����ϵ��ƾ�����д���й�������пͻ�Ͷ�ʷ������������ʾ������������лл��";
			     response.putDataBasket("cardBadCheckValue", false);
			  }
			 }else{
				 ������ʾ��־ = "����";
			     ������ʾ��Ϣ = "�˲�δͨ����";
			     response.putDataBasket("cardBadCheckValue", �˲���);  //false
			 }
			
		}else{
			 ������ʾ��־ = "����";
		     ������ʾ��Ϣ = "�˲�δͨ��";
		     response.putDataBasket("cardBadCheckValue",�˲���);  //false
		}
	}
	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		Model model = new Model();
		model.put("cardNo", "����ǩ��ģ���쳣������ƾ�����˿ͻ�ǩ��������ֽ���ʾ�");
		return FreeMarkerUtil.getTemplate(this, "cardBadCheck.ftl", model);
	}

}
