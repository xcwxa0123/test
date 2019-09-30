package smart.modules.�ͻ�.���߷�������;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import smart.lib.comm.CefCommon;
import smart.lib.comm.CommResult;
import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefTrade;
import smart.lib.module.SignSubmitCefTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import ceb00.trx.lib.tools.BaseUtil;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.dataStruct.view.View;
import cn.com.agree.commons.csv.CsvUtil;


public class ���߷�������  extends SignSubmitCefTrade{
	@In(Dictionary.�ͻ�.�ͻ���)
	public String custNo;
	
	@Out(Dictionary.�����ڵ����.������ʾ��־)
	public String ������ʾ��־;
	  
	@Out(Dictionary.�����ڵ����.������ʾ��Ϣ)
	public String ������ʾ��Ϣ;

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		return true;
	}

	@Override
	public void doInit() throws Exception {
		// TODO �Զ����ɵķ������
		try{
			JSONArray array = new JSONArray();
			JSONArray ������ѯ0353=������ѯ0353(this);
			if( ������ѯ0353 == null || ������ѯ0353.size()==0){
				pushError("��ȡ���������ʾ�ʧ�ܣ�");
				return;
			}
			for(int i=0;i<������ѯ0353.size();i++){
				JSONArray resultArray = ������ѯ0353.getJSONArray(i);
				
				String anwserTotal=resultArray.getString(13);//������
				String quesID=resultArray.getString(0);//������
				String quesType=resultArray.getString(1);//�������Ͷ�Ϊ0 
				String quesDesc=resultArray.getString(2);//��������
				JSONArray topic = new JSONArray();
				topic.add(quesID);
				topic.add(quesID+":"+quesDesc);	
				for(int j=0;j<Integer.parseInt(anwserTotal);j++){
					//4  0 1 2 3
					String anwser=resultArray.getString(j+3);
					topic.add(anwser);
				}
				array.add(topic);
			}
			String script = "createAndMonitor("+array.toString()+","+numTotal+")";
			logger.info("aaaaaaaa:"+array.toJSONString());
			eval(script);
		}catch(Exception e){
			pushError("��ȡ���������ʾ��쳣��"+e.getMessage());
			return;
		}
	

		
		//eval(script);
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO �Զ����ɵķ������

		try {
			List<String> list=Arrays.asList(answer.split(","));
			List<String[]> datalist=new ArrayList<String[]>();
			for(int i=0;i<list.size();i++){
				String[] split = list.get(i).split(":",-1);
				datalist.add(new String[]{split[0].replace("topic", ""),split[1]});
			}
			Map teller = getTellerInfo();
			String RECYLEDATE=(String) teller.get("G_SYSDATE");
			JSONObject map0351 = new JSONObject();
			map0351.put("ICUSTNO", custNo);//֤������
			map0351.put("ISTARTDATE",RECYLEDATE);//֤������
			map0351.put("F03511", datalist);//
			CommResult result = CefCommon.exchange(this, "0351",
					"O03510", map0351);
			JSONObject res= new JSONObject();
			res=JSONObject.parseObject(answer);
			for(Map.Entry<String, Object> entry:res.entrySet()){
				String a= entry.getKey();
				String b= entry.getValue().toString();
				logger.info("1111111111"+a+b);
			}
			if (result.isSuccess()) {
				response.putDataBasket("success", "true");
				return;
			}else{
				������ʾ��־="��ʾ";
				������ʾ��Ϣ="��Ʒ���������Ϣ����ʧ�ܣ�"+result.getErrorMsg();
				response.putDataBasket("success", "false");
				return;
			}
			
			
		} catch (Exception e) {
			������ʾ��־="��ʾ";
			������ʾ��Ϣ="��Ʒ���������Ϣ�����쳣��"+e.getMessage();
			response.putDataBasket("success", "false");
			return;
		}
	
//		if (result.isSuccess()) {
//			response.putDataBasket("success", "true");
//			return;
//			}else{
//				������ʾ��־="��ʾ";
//				������ʾ��Ϣ="����˰�վ�����Ϣ����ʧ�ܣ�"+result.getErrorMsg();
//				response.putDataBasket("success", "false");
//				return;
//			}
//		} catch (Exception e) {
//			������ʾ��־="��ʾ";
//			������ʾ��Ϣ="����˰�վ�����Ϣ�����쳣��"+e.getMessage();
//			response.putDataBasket("success", "false");
//			return;
//		}
//		   ArrayList<String[]> list = new ArrayList<String[]>(Arrays.asList(mapString));

		
	}
	public  String numTotal="";
	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		Model model = new Model();
		return FreeMarkerUtil.getTemplate(this,"index.ftl", model);
	}
	private  JSONArray ������ѯ0353(ModuleCefTrade trade)
	throws Exception {
		String num = "";
		String beginNum = "1";
		String selectNum = "20";
		JSONArray jsonArray0353 = new JSONArray();
		while (true) {
			try {		
				if ("".equals(num)) {
					JSONObject map0353 = new JSONObject();
					map0353.put("IQSHBS", beginNum);
					map0353.put("ICHXBS", selectNum);
					CommResult result = CefCommon.exchange(trade, "0353",
							"O03530", map0353);
					if (!result.isSuccess()) {
						break;
					}
					JSONObject jsonObjec0353 = result.getObjDataSop("O03531");
					String number = jsonObjec0353.getString("OBISHU");
					numTotal=number;
					trade.logger.info("��ȡ��̨�����ܱ���" + number);
					jsonArray0353.addAll(jsonObjec0353.getJSONArray("F03531"));
					num = number;
				} else {
		
					beginNum = BaseUtil.addBigDecimal(selectNum, beginNum);
					trade.logger.info("���β�ѯ��ʼ����" + beginNum);
					if (BaseUtil.compare(beginNum, num) > 0) {
						trade.logger.info("���β�ѯ��ʼ���������ܱ���");
						break;
					} else {
						JSONObject map0353 = new JSONObject();
						map0353.put("IQSHBS", beginNum);
						map0353.put("ICHXB", selectNum);
						CommResult result = CefCommon.exchange(trade, "0353",
								"O03530", map0353);
						if (!result.isSuccess()) {
							break;
						}
						JSONObject jsonObjec0353 = result
								.getObjDataSop("O03531");
						jsonArray0353.addAll(jsonObjec0353
								.getJSONArray("F03531"));
					}
				}
			} catch (Exception e) {
				break;
			}
		}
		if (jsonArray0353 != null && jsonArray0353.size() > 0)
			return jsonArray0353;
		else
			return null;
}
	
	public String answer="";
	public void pageSubmit(String mapString) throws Exception{
		if(mapString==null || "".equals(mapString)){
			pushError("�ύ�쳣�����˳����ԣ�");
			return;
		}
		answer =mapString;
		
		moduleSubmit(null);
//		this.pushError("��ȡ���������ʾ��쳣��"+);
//		logger.info(map.get("0001")+"::"+map.get("0002")+"::"+map.get("0003")+"::"+map.get("0004")+"::"+map.get("0005")+"::"+map.get("0006"));
	}

	@Override
	public void pageSubmit() throws Exception {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void recordLog(String info) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void errorHandler(String log, String info) throws Exception {
		// TODO �Զ����ɵķ������
		
	}
}
