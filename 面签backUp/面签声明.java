package smart.modules.Э���Ķ�.���ÿ���ǩ����;

import java.io.File;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;

import smart.lib.device.DeviceUtil;
import smart.lib.dic.Dictionary;
import smart.lib.module.SignSubmitCefTrade;
import smart.lib.template.FreeMarkerUtil2;
import smart.lib.template.Model;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.dataStruct.view.View;

public class ��ǩ����  extends SignSubmitCefTrade {
	public static final String Э������ = "protocal";
	public static final String �Ķ�ʱ�� = "time";
	public static final String ��ť���� = "btnName";
	public static final String PDF = "pdf";
	
	@In(Dictionary.�ͻ�.�ͻ�����)
	public String customName;

	@In(Dictionary.�ͻ�.���֤��)
	public String idCardNo;
	
	@Out(Dictionary.�����ڵ����.������ʾ��־)
	public String ������ʾ��־;

	@Out(Dictionary.�����ڵ����.������ʾ��Ϣ)
	public String ������ʾ��Ϣ;	
	
	@Out(Dictionary.COBPS.ҵ��Ӱ������)
	public String CCMQID;
	
	public String failResult = "";
	
	public boolean signFlag = true;
	
	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		return true;
	}

	@Override
	public void doInit() throws Exception {
		
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// ͬ��Э��,������,ʲô��������
		try {
			if (signFlag) {	
				String fileNumber =  request.getDataBasketString("�浵��");			
				logger.info("fileNumber"+fileNumber);
				JSONObject customInfo = new JSONObject();
				customInfo.put("customName", customName);
				customInfo.put("idCardNo", idCardNo);
				customInfo.put("fileNumber", fileNumber);
				JSONObject upImgResult = syncOpenFrame(�ϴ���ǩ������ǩ��Ӱ��.class.getName(), customInfo);
				String signResult = upImgResult.getString("signResult");
				String docId = upImgResult.getString("ccmqDocId");
				response.putDataBasket("signResult", signResult);
				if (null != signResult && "true".equals(signResult)) {
					response.putDataBasket("ccmqDocId",docId);
					CCMQID = docId;
				}else {
					response.putDataBasket("ccmqDocId","");
					������ʾ��־ = "����";
					������ʾ��Ϣ = "�ϴ�Ӱ��ʧ��,����ϵ���þ���!";
				}
			}else{
				response.putDataBasket("signResult", "false");
				������ʾ��־ = "����";
				������ʾ��Ϣ = failResult+",����ϵ���þ���!";
			}	
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

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		String ftlName = "viewer.html";	
//			String protocal = request.getDataBasketString(Э������);
			String btnName = request.getDataBasketString(��ť����);
			String time = request.getDataBasketString(�Ķ�ʱ��);
			Model model = new Model();
			model.put("time", time);
			model.put("btnName", btnName);
			String pdf="/resource/pdf/��������������ǩ������Ȩ��2.pdf";
			if (pdf != null) {
				// ���жϱ���www����û������ļ�
				URL url2 = this.getClass().getClassLoader().getResource(".");
				String pdfReally=pdf.substring(pdf.lastIndexOf("/")+1);
				logger.info("��ȡ�ļ�����"+pdfReally);
					if (url2 != null) {
						File f = new File(url2.getFile());
						File gts = f.getParentFile();
						File target = new File(gts, "resources/www/pdf/" + pdfReally);
						logger.info("���ļ�"+pdfReally+"  "+target.getPath());	
					}
				model.put(PDF, pdf);
			}
			return FreeMarkerUtil2.getTemplate(this, ftlName, model);
	}


	public void pageSubmit() throws Exception {
		moduleSubmit(null);
	}

	@Override
	public void pageSubmit(String str) throws Exception {
		// TODO �Զ����ɵķ������
		
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
	public void errorHandler(String log, String info) throws Exception {
		if (null == info || "".equals(info)) {
			failResult = log;
		} else {
			failResult = info;
		}
		//ʧ�ܴ��� ����Ӱ��
		signFlag = false;
		logger.info("js����ʧ��:" + log);
		moduleSubmit(null);
	}
	
	// ��Ҫ����־��һ��
	public void recordLog(String info) {
		logger.info(info);
	}
	
}
