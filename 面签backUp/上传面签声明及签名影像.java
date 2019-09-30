package smart.modules.Э���Ķ�.���ÿ���ǩ����;

import com.alibaba.fastjson.JSONObject;

import smart.lib.comm.ECMUtil;
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


public class �ϴ���ǩ������ǩ��Ӱ��  extends SignUploadCefTrade {
	//-------------------Ĭ�ϲ���-------------------------
	private final String srcfolder = "signature";
	private final String appcode = "CCMQ";
	private final String imageType = "CCMQ";
	private final String SUCCESS = "success";
	private final String upFileType = "ccmq";

	// Ĭ����ʹ��jsȥ�ϳ�ͼƬ��
	public static boolean isClientPluginMerge = false;

	public String customName = "";

	public String idCardNo = "";

	public String date = BaseUtil.getCurrentDate();

	public String time = BaseUtil.getCurrentTime();

	public String fileNumber = "";
	
	//------------------------------------------------
	
	
	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		return true;
	}

	@Override
	public void doInit() throws Exception {
		logger.info("����init");
		if (isClientPluginMerge) {
			moduleSubmit(null);
		}
	}



	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO �Զ����ɵķ������
		try {
			Scope scope = this.getScope(Scope.dataBasket);
			scope.put("response", response);
			boolean flag = false;
			if (isClientPluginMerge) {
				//�ȵ���ͼ������
				flag = imageCCMQ();
			} else {
				//upImg�������óɹ���creatImg����Ϊtrue
				flag = request.getDataBasketBooleanValue("creatImg");
			}
			//����ͼ�����ɷ����ķ����������Ƿ�����ϴ�
			if (flag) {
				uploadImgHandler();
			} else {
				showMaskError("ʧ����Ϣ", "����Ӱ��ʧ��", "����", "quitUpImg");
				response.stay();
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("����Ӱ���쳣", e);
			showMaskError("ʧ����Ϣ", "����Ӱ���쳣", "����", "quitUpImg");
			response.stay();
		
		}
	}


	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
				isClientPluginMerge = isNewMerge();
				Model model = new Model();
				customName = request.getDataBasketString("customName");
				idCardNo = request.getDataBasketString("idCardNo");
				model.put("customName", customName);
				model.put("idCardNo", idCardNo);
				model.put("date", date);
				model.put("time", time);
				fileNumber = request.getDataBasketString("fileNumber");
				if (isClientPluginMerge) {
					return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
				} else {
					logger.info("�ϴ�����ǩ����������");
					return FreeMarkerUtil2.getTemplate(this, "upImg.ftl", model);
				}
		
	}
	/**
	 * �ϴ�Ӱ����
	 * ���
	 * @throws Exception
	 */
	public void uploadImgHandler() throws Exception {
		Scope scope = this.getScope(Scope.dataBasket);
		ModuleResponse response = (ModuleResponse) scope.get("response");
		try {
			final String docid = fileNumber + fileNumber;
			logger.info("Ӱ�������浵��docid" + fileNumber);
			String result = delete(docid);
			if (null != result) {
				if (SUCCESS.equals(result)) {
					logger.info("ԭӰ��ɾ���ɹ���");
					result = uploadImg(docid);
					if (null != result) {
						if (SUCCESS.equals(result)) {
							logger.info("��ǩ����������ǩ���ϴ��ɹ���");
							response.putDataBasket("ccmqDocId", docid);
							response.putDataBasket("signResult", "true");
							exit(0);
						} else {
							showMaskError("ʧ����Ϣ", "�ϴ�Ӱ��ʧ��,ԭ��Ϊ:" + result + "!",
									"����", "uploadImgHandler", "����", "quitUpImg");
							response.stay();
						}
					} else {
						showMaskError("ʧ����Ϣ", "�ϴ�Ӱ��ʧ��", "����",
								"uploadImgHandler", "����", "quitUpImg");
						response.stay();
					}
				} else {
					showMaskError("ʧ����Ϣ", "Ӱ����ʧ��,ԭ��Ϊ:" + result + "!", "����",
							"uploadImgHandler", "����", "quitUpImg");
					response.stay();
				}
			} else {
				showMaskError("ʧ����Ϣ", "Ӱ����ʧ��", "����", "uploadImgHandler", "����",
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
	@Override
	public void upImg() throws Exception {
		logger.info("��ʼ�ϴ���ǩ����ǩ�𺯼�ǩ��Ӱ��");
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", true);
		moduleSubmit(request);
	}
	
	@Override
	public void failHandler(String info) throws Exception {
		logger.info("����ͼƬʧ��:" + info);
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", false);
		moduleSubmit(request);
	}
	
	@Override
	public void failHandler(String error, String info) throws Exception {
		logger.info(error + "����ͼƬʧ��:" + info);
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", false);
		moduleSubmit(request);
	}
	
	protected boolean imageCCMQ() {
		JSONObject param = new JSONObject();
		param.put("method", "CCMQ");
		JSONObject data = new JSONObject();
		data.put("flag", true);
		data.put("image1", srcfolder + "/answer.jpg");
		data.put("image2", srcfolder + "/signature.png");
		data.put("merge", srcfolder + "/ccmq.jpg");
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
	
	protected boolean isNewMerge() {
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
	
	protected String delete(String docid) throws Exception {
		logger.info("ɾ����ǩ����������ǩ��");
		String result = null;
		result = ECMUtil.deleteImage(this, docid, appcode, imageType);
		return result;
	}
	
	protected String uploadImg(String docid) throws Exception {
		logger.info("��ǩ����������ǩ���ϴ���ʼ");
		// ����-->����
		// final String brno = "1001";
		String result = null;
		// Map tellerInfo = this.getTellerInfo();
		// String brno = (String)tellerInfo.get("G_BUSIBRNO");
		result = ECMUtil.upImage(this, srcfolder, docid, appcode, imageType,
				upFileType);
		return result;
	}

}
