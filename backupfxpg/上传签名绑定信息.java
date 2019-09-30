package smart.modules.�ͻ�.���߷�������;

import smart.lib.dic.Dictionary;
import smart.lib.module.CefTrade;
import smart.lib.module.ModuleCefCalcTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import smart.lib.tools.licai.UploadLCPGInfo;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.dataStruct.view.View;

public class �ϴ�ǩ������Ϣ extends ModuleCefCalcTrade {
	private final String SMART_TRADEQTLS = "qtls";
	private final String ECIF_RISKLEVEL = "ecif���յȼ�";
	private final String LCPGDOCID = "lcpgDocId";
	private final String UPLCPGDOCID = "uplcpgDocId";

	@In(Dictionary.�ͻ�.���֤��)
	public String cardNum;

	@In(Dictionary.�ͻ�.�ͻ���)
	public String customNum;

	@In(Dictionary.�ͻ�.�ͻ�����)
	public String customName;

	@Override
	public void doCalcSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO �Զ����ɵķ������
		String ecif_RiskLevel = request.getDataBasketString(ECIF_RISKLEVEL);
		if (null == ecif_RiskLevel || "".equals(ecif_RiskLevel)) {
			logger.info("ecif�޵ȼ�,���ϴ��ͻ�ǩ����Ϣ");
			String docId = request.getDataBasketString(LCPGDOCID);
			String updocId = request.getDataBasketString(UPLCPGDOCID);
			if ((null == docId || "".equals(docId))  && (null == updocId || "".equals(updocId))) {
				logger.info("lcpg��docId������,���ϴ���ذ���Ϣ!");
			}else {
				logger.info("lcpg��docId����,�ϴ���ذ���Ϣ!docId:"+docId);
				String fserialNo = request.getDataBasketString(SMART_TRADEQTLS);
				try {
					if(updocId != null && !"".equals(updocId)){
						pushInfo("����ǩ��",true);
						uploadLCPGInfo(this, updocId, fserialNo);
					}else{
						pushInfo("��ǩ��");
						uploadLCPGInfo(this, docId, fserialNo);
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.info("�Ǽ�Ӱ����Ϣ�쳣:", e);
				}
			}
			
		} else {
			logger.info("ecif�еȼ�,�����ϴ��ͻ�ǩ����Ϣ");
		}
	}

	private boolean uploadLCPGInfo(CefTrade trade, String docId,
			String fserialNo) {
		final String cardType = "1";
		boolean uploadFlag = UploadLCPGInfo.uploadInfoHandler(trade, docId,
				cardNum, fserialNo, customNum, customName, cardType);
		return uploadFlag;
	}

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		return true;
	}

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		Model model = new Model();
		return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
	}
}
