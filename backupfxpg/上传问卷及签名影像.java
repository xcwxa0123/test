package smart.modules.�ͻ�.���߷�������;

import smart.lib.comm.ECMUtil;
import smart.lib.comm.SmartBaseUtil;
import smart.lib.module.ModuleCefTrade;
import smart.lib.module.SignUploadCefTrade;
import smart.lib.service.OpenService;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.FreeMarkerUtil2;
import smart.lib.template.Model;
import ceb00.trx.lib.tools.BaseUtil;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.core.scope.Scope;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONObject;

public class �ϴ��ʾ�ǩ��Ӱ�� extends SignUploadCefTrade {
	private final String srcfolder = "signature";
	private final String appcode = "LCPG";
	private final String imageType = "LCPG";
	private final String SUCCESS = "success";
	private final String upFileType = "lcpg";

	// Ĭ����ʹ��jsȥ�ϳ�ͼƬ��
	public boolean isClientPluginMerge = false;

	public String customName = "";

	public String idCardNo = "";

	public String date = BaseUtil.getCurrentDate();

	public String time = BaseUtil.getCurrentTime();
	
	public String isUpImg = "";

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		return true;
	}

	@Override
	public void doInit() throws Exception {
		// TODO �Զ����ɵķ������
		logger.info("����init");
		if (isClientPluginMerge) {
			moduleSubmit(null);
		}
	}

	public void upImg() throws Exception {
		logger.info("��ʼ�ϴ��ʾ�ǩ��Ӱ��");
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", true);
		moduleSubmit(request);
	}

	public void failHandler(String info) throws Exception {
		logger.info("����ͼƬʧ��:" + info);
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", false);
		moduleSubmit(request);
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO �Զ����ɵķ������
		Scope scope = this.getScope(Scope.dataBasket);
		scope.put("response", response);
		boolean flag = false;
		if (isClientPluginMerge) {
			if (isUpImg != null && !"".equals(isUpImg) && "�ϴ��ʾ�Ӱ��".equals(isUpImg)) {
				flag = imageAddContent();
			}else {				
				flag = imageMerge();
			}
		} else {
			flag = request.getDataBasketBooleanValue("creatImg");
		}
		if (flag) {
			uploadImgHandler();
		} else {
			showMaskError("ʧ����Ϣ", "����Ӱ��ʧ��", "����", "quitUpImg");
			response.stay();
		}
	}

	private boolean imageMerge() {
		JSONObject param = new JSONObject();
		param.put("method", "LCPG");
		JSONObject data = new JSONObject();
		data.put("flag", true);
		data.put("image1", srcfolder + "/answer.jpg");
		data.put("image2", srcfolder + "/signature.png");
		data.put("merge", srcfolder + "/lcpg.jpg");
		data.put("customName", customName);
		data.put("idCardNo", idCardNo);
		data.put("tradeDate", date);
		data.put("tradeTime", time);
		param.put("data", data);
		try {
			String filePath = issueCall("imageMerge", param.toString());
			if (null != filePath && filePath.length() > 0) {
				return true;
			}
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			logger.info("ͼƬ�ϲ������쳣:", e);
		}
		return false;
	}
	
	private boolean imageAddContent() {
		JSONObject param = new JSONObject();
		param.put("method", "LCPG");
		JSONObject data = new JSONObject();
		data.put("flag", false);
		data.put("image", srcfolder + "/answer.jpg");
		data.put("merge", srcfolder + "/lcpg.jpg");
		data.put("customName", customName);
		data.put("idCardNo", idCardNo);
		data.put("tradeDate", date);
		data.put("tradeTime", time);
		param.put("data", data);
		try {
			String filePath = issueCall("imageMerge", param.toString());
			if (null != filePath && filePath.length() > 0) {
				return true;
			}
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			logger.info("ͼƬ�ϲ������쳣:", e);
		}
		return false;
	}

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		isClientPluginMerge = isNewMerge();
		Model model = new Model();
		customName = request.getDataBasketString("customName");
		idCardNo = request.getDataBasketString("idCardNo");
		model.put("customName", customName);
		model.put("idCardNo", idCardNo);
		model.put("date", date);
		model.put("time", time);
		isUpImg = request.getDataBasketString("�ϴ�Ӱ���־");
		if (isClientPluginMerge) {
			return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
		} else {
			if (isUpImg != null && !"".equals(isUpImg) && "�ϴ��ʾ�Ӱ��".equals(isUpImg)) {
				logger.info("���ϴ�����ǩ����������");
				return FreeMarkerUtil2.getTemplate(this, "fupImg.ftl", model);
			}else {
				logger.info("�ϴ�����ǩ����������");
				return FreeMarkerUtil2.getTemplate(this, "upImg.ftl", model);
			}
		}
	}

	/**
	 * �ϴ�Ӱ�� �����ʾ� LCPG
	 * 
	 * @return
	 */
	private String uploadImg(String docid) throws Exception {
		logger.info("�ʾ�Ӱ�񼰵���ǩ���ϴ���ʼ");
		// ����-->����
		final String brno = "1001";
		String result = null;
		result = ECMUtil.upImage(this, srcfolder, docid, appcode, imageType,
				upFileType, brno);
		return result;
	}

	/**
	 * �ϴ�Ӱ����
	 * 
	 * @throws Exception
	 */
	public void uploadImgHandler() throws Exception {
		Scope scope = this.getScope(Scope.dataBasket);
		ModuleResponse response = (ModuleResponse) scope.get("response");
		final String docid = SmartBaseUtil.createDocIndex(this);
		try {
			String result = uploadImg(docid);
			if (null != result) {
				if (SUCCESS.equals(result)) {
					logger.info("�ʾ�Ӱ�񼰵���ǩ���ϴ��ɹ���");
					response.putDataBasket("lcpgDocId", docid);
					response.putDataBasket("signResult", "true");
					exit(0);
				} else {
					showMaskError("ʧ����Ϣ", "�ϴ�Ӱ��ʧ��,ԭ��Ϊ:" + result + "!", "����",
							"uploadImgHandler", "����", "quitUpImg");
					response.stay();
				}
			} else {
				showMaskError("ʧ����Ϣ", "�ϴ�Ӱ��ʧ��", "����", "" + "", "����",
						"quitUpImg");
				response.stay();
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("�ϴ�Ӱ���쳣", e);
			showMaskError("ʧ����Ϣ", "�ϴ�Ӱ���쳣", "����", "uploadImgHandler", "����",
					"quitUpImg");
			response.stay();
		}
	}

	/**
	 * �˳��ӽ���
	 * 
	 */
	public void quitUpImg() {
		Scope scope = this.getScope(Scope.dataBasket);
		ModuleResponse response = (ModuleResponse) scope.get("response");
		response.putDataBasket("signResult", "false");
		exit(0);
	}

	private boolean isNewMerge() {
		final String qualifier = "�ǹ�ͼƬ";
		final String key = "imageMerge";
		final String status = "0";
		try {
			String value = OpenService.db.selectOtherParamInfo(qualifier, key,
					status);
			if (null != value && "1".equals(value)) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("��ѯ�ϲ�ģʽ�����쳣:", e);
		}
		return false;
	}

	@Override
	public void failHandler(String error, String info) throws Exception {
		// TODO �Զ����ɵķ������
		
	}
}
