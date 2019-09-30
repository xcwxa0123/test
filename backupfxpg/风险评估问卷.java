package smart.modules.�ͻ�.���߷�������;

import smart.lib.comm.CefCommon;
import smart.lib.comm.CommResult;
import smart.lib.device.DeviceUtil;
import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefTrade;
import smart.lib.module.SignSubmitCefTrade;
import smart.lib.template.FreeMarkerUtil2;
import smart.lib.template.Model;
import ceb00.trx.lib.tools.BaseUtil;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ���������ʾ� extends SignSubmitCefTrade {
	@In(Dictionary.�ͻ�.�ͻ���)
	public String custNo;

	@Out(Dictionary.�����ڵ����.������ʾ��־)
	public String ������ʾ��־;

	@Out(Dictionary.�����ڵ����.������ʾ��Ϣ)
	public String ������ʾ��Ϣ;
	
	@In(Dictionary.�ͻ�.�ͻ�����)
	public String customName;

	@In(Dictionary.�ͻ�.���֤��)
	public String idCardNo;

	public boolean signFlag = true;
	
	public String failResult = "";
	
	public String ecif_RiskLevel;

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		return true;
	}
	public String ���������ʾ����� = "";
	@Override
	public void doInit() throws Exception {
		// TODO �Զ����ɵķ������
		try {
			JSONArray array = new JSONArray();
			JSONArray ������ѯ0353 = ������ѯ0353(this);
			if (������ѯ0353 == null || ������ѯ0353.size() == 0) {
				pushError("��ȡ���������ʾ�ʧ�ܣ�");
				return;
			}
			for (int i = 0; i < ������ѯ0353.size(); i++) {
				JSONArray resultArray = ������ѯ0353.getJSONArray(i);
				String anwserTotal = resultArray.getString(13);// ������
				String quesID = resultArray.getString(0);// ������
				String quesType = resultArray.getString(1);// �������Ͷ�Ϊ0
				String quesDesc = resultArray.getString(2);// ��������
				JSONArray topic = new JSONArray();
				topic.add(quesID);
				topic.add(quesID + ":" + quesDesc);
				for (int j = 0; j < Integer.parseInt(anwserTotal); j++) {
					// 4 0 1 2 3
					String anwser = resultArray.getString(j + 3);
					topic.add(anwser);
				}
				array.add(topic);
			}
			���������ʾ����� = array.toString();
			ecif_RiskLevel = getRequest().getDataBasketString("ecif���յȼ�");
			String script = "";
			if (null == ecif_RiskLevel || "".equals(ecif_RiskLevel)) {
				script = "createAndMonitor(" + array.toString() + ","
						+ numTotal +",'true'"+")";
			}else {
				script = "createAndMonitor(" + array.toString() + ","
						+ numTotal +",'false'"+")";
			}
			logger.info("���������ʾ��script:"+script);
			eval(script);
		} catch (Exception e) {
			pushError("��ȡ���������ʾ��쳣��" + e.getMessage());
			return;
		}

		// eval(script);
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO �Զ����ɵķ������
		try {
			response.putDataBasket("���������ʾ�����", ���������ʾ�����);
			response.putDataBasket("signFlag",signFlag);
			response.putDataBasket("�ʾ��", answer);
			if (signFlag) {
				// TODO �Զ����ɵķ������
				if(null == ecif_RiskLevel || "".equals(ecif_RiskLevel)){
					JSONObject customInfo = new JSONObject();
					customInfo.put("customName", customName);
					customInfo.put("idCardNo", idCardNo);
					JSONObject upImgResult = syncOpenFrame(�ϴ��ʾ�ǩ��Ӱ��.class.getName(), customInfo);
					String signResult = upImgResult.getString("signResult");
					String docId = upImgResult.getString("lcpgDocId");
					response.putDataBasket("signResult", signResult);
					if (null != signResult && "true".equals(signResult)) {
						response.putDataBasket("lcpgDocId",docId);
					}else {
						response.putDataBasket("lcpgDocId","");
						failResult = "�ϴ�Ӱ��ʧ��";
					}
				}else {
					response.putDataBasket("signResult", "true");
				}
			} else {
				response.putDataBasket("signResult", "true");
			}
			������ʾ��־ = "����";
			������ʾ��Ϣ = failResult + "������ϵ��ƾ�����д���й�������пͻ�Ͷ�ʷ������������ʾ������������лл��";
		} finally {
			for (int i = 0; i < 3; i++) {
				logger.info("��" + (i + 1) + "�ιرյ���ǩ������");
				try {
					String result = DeviceUtil.interrupt(this,
							DeviceUtil.SIGNATURE);
					if (null != result && "true".equals(result)) {
						logger.info("��" + (i + 1) + "�ιرյ���ǩ�����ڳɹ�");
						break;
					} else {
						logger.info("��" + (i + 1) + "�ιرյ���ǩ������ʧ��");
						continue;
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.info("��" + (i + 1) + "�ιرյ���ǩ������ʧ��", e);
					continue;
				}
			}
		}

	}

	public String numTotal = "";

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		Model model = new Model();
		return FreeMarkerUtil2.getTemplate(this, "index.ftl", model);
	}

	private JSONArray ������ѯ0353(ModuleCefTrade trade) throws Exception {
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
					numTotal = number;
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

	public String answer = "";

	public void pageSubmit(String mapString) throws Exception {
		if (mapString == null || "".equals(mapString)) {
			pushError("�ύ�쳣�����˳����ԣ�");
			return;
		}
		answer = mapString;

		moduleSubmit(null);
		// this.pushError("��ȡ���������ʾ��쳣��"+);
		// logger.info(map.get("0001")+"::"+map.get("0002")+"::"+map.get("0003")+"::"+map.get("0004")+"::"+map.get("0005")+"::"+map.get("0006"));
	}

	// ��Ҫ����־��һ��
	public void recordLog(String info) {
		logger.info(info);
	}

	/**
	 * JavaScript�����쳣����
	 * 
	 * @param log
	 *            ��¼��־��Ϣ
	 * @param info
	 *            չʾ��Ϣ
	 * @throws Exception
	 */
	public void errorHandler(String log, String info,String mapString) throws Exception {
		if (null == info || "".equals(info)) {
			failResult = log;
		} else {
			failResult = info;
		}
		//ʧ�ܴ��� ����Ӱ��
		signFlag = false;
		answer = mapString;
		logger.info("js����ʧ��:" + log);
		moduleSubmit(null);
	}

	@Override
	public void pageSubmit() throws Exception {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void errorHandler(String log, String info) throws Exception {
		// TODO �Զ����ɵķ������
		
	}
}
