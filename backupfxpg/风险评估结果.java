package smart.modules.�ͻ�.���߷�������;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import smart.lib.comm.CefCommon;
import smart.lib.comm.CommResult;
import smart.lib.constants.SmartConstants;
import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefCalcTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.core.scope.Scope;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONObject;

public class ����������� extends ModuleCefCalcTrade {
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

	public String numTotal = "";

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		Model model = new Model();
		model.put("info", "���ڽ��з������������Ժ�......");
		return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
	}
	
	@Override
	public void doCalcSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		String answer = request.getDataBasketString("�ʾ��");

		try {
			JSONObject res = new JSONObject();
			res = JSONObject.parseObject(answer);
			List<String[]> answerList = answerRank(res);
			Map teller = getTellerInfo();
			String RECYLEDATE = (String) teller.get("G_SYSDATE");
			JSONObject map0351 = new JSONObject();
			map0351.put("ICUSTNO", custNo);// ֤������
			map0351.put("ISTARTDATE", RECYLEDATE);// ֤������
			map0351.put("F03511", answerList);//
			CommResult result = CefCommon.exchange(this, "0351", "O03510",
					map0351);
			if (result.isSuccess()) {
				response.putDataBasket("success", "true");
				Scope scope = this.getScope(Scope.dataBasket);
				String ǰ̨��ˮ�� = (String) scope.get(SmartConstants.SMART_LSH);
				response.putDataBasket("qtls", ǰ̨��ˮ��);
				JSONObject jsonObjec0351 = result.getObjDataSop("O03511");
				String flag = jsonObjec0351.getString("OFLAG");
				// 07�������ͣ�06����ȡ�ͣ�04��ƽ���ͣ�02���Ƚ��ͣ�01��������
				String datas = "";
				String flagData = "";
				if ("01".equals(flag)) {
					flagData = "������";
					datas = "�����ڿ��Գе��ͷ��ն�����������͵�Ͷ���ߡ����ʺ�Ͷ�����Ա���Ϊ����Ͷ�ʹ��ߣ�������˻������ʱ���ֵ�Ļ��ᡣ";
				} else if ("02".equals(flag)) {
					flagData = "�Ƚ���";
					datas = "�����ڿ��Գе������еȷ������͵�Ͷ���ߡ����ʺ�Ͷ�����ܹ�Ȩ�Ᵽ��������������ֵ������Ͷ�ʹ��ߡ�";
				} else if ("04".equals(flag)) {
					flagData = "ƽ����";
					datas = "�����ڿ��Գе��еȷ������͵�Ͷ���ߡ����ʺ�Ͷ�����ܹ�Ϊ���ṩ�º���ֵ��������Ͷ�ʼ�ֵ���ºͲ�����Ͷ�ʹ��ߡ�";
				} else if ("06".equals(flag)) {
					flagData = "��ȡ��";
					datas = "�����ڿ��Գе��е����߷������͵�Ͷ���ߡ����ʺ�Ͷ�����ܹ�Ϊ���ṩ��ֵ��������Ͷ�ʼ�ֵ�в�����Ͷ�ʹ��ߡ�";
				} else if ("07".equals(flag)) {
					flagData = "������";
					datas = "�����ڿ��Գ��ܸ߷������͵�Ͷ���ߡ����ʺ�Ͷ�����ܹ�Ϊ���ṩ����ֵ������Ͷ�ʼ�ֵ�������Ͷ�ʹ��ߡ��������£�������ʧȥȫ��Ͷ�ʱ��������Ͷ�������µ��κο�ʴ�е����Ρ�";
				}
				������ʾ��־ = "�ɹ�";
				������ʾ��Ϣ = "���ķ����������Ϊ��" + flagData + "\n" + datas;
				return;
			} else {
				������ʾ��־ = "��ʾ";
				������ʾ��Ϣ = "��Ʒ���������Ϣ����ʧ�ܣ�" + result.getErrorMsg();
				response.putDataBasket("success", "false");
				return;
			}

		} catch (Exception e) {
			������ʾ��־ = "��ʾ";
			������ʾ��Ϣ = "��Ʒ���������Ϣ�����쳣��" + e.getMessage();
			response.putDataBasket("success", "false");
			return;
		}

	}

	/**
	 * ԭ��������������ʱΪ���� ����������� �Ե����ʾ��ȡ�Ĵ� �������򣬳ɹ���������� list ,��������쳣��ԭ������ list
	 * ��Ϊ��̨�Ѿ��Ż����� ��Ŷ�ȡ
	 * 
	 * @param answerList
	 * @return
	 */
	public List<String[]> answerRank(JSONObject res) {
		List<Entry<String, String>> list = new ArrayList(res.entrySet());
		Collections.sort(list, new Comparator<Entry<String, String>>() {

			@Override
			public int compare(Entry<String, String> o1,
					Entry<String, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		ListIterator<Entry<String, String>> iterator = list.listIterator();
		List<String[]> result = new ArrayList<String[]>();
		while (iterator.hasNext()) {
			Entry<String, String> e = iterator.next();
			String key = e.getKey().replace("topic", "");
			String value = e.getValue();
			result.add(new String[]{key,value});
		}
		return result;
	}

}
