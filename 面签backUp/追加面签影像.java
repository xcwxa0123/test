package smart.modules.Э���Ķ�.���ÿ���ǩ����;

import smart.lib.comm.ECMUtil;
import smart.lib.dic.Dictionary;
import smart.lib.face.FaceFileUtil;
import smart.lib.module.ModuleCefTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ׷����ǩӰ�� extends ModuleCefTrade {
	
	@In(Dictionary.�ͻ�.�ֳ���������Ϣ)
	public String �ֳ���������Ϣ;
	
	@In(Dictionary.�ͻ�.����ʶ���ֳ�������)
	public String ImglocalId;
	
	/**
	 * ������ʷӰ������---�ֳ���
	 */
	public String docidRlls = "";
	/**
	 * ����ʶ�����
	 */
	public String score = "";
	
	private final String SUCCESS = "success";
	
	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		return true;
	}

	@Override
	public void doInit() throws Exception {
		// TODO �Զ����ɵķ������
		moduleSubmit(null);
	}
	
	public void submitAndcloseMaskPage() throws Exception{
		closeMaskPage();
		submit();
	}
	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO �Զ����ɵķ������
		try {
			 String docid = request.getDataBasketString("ccmqdocid");
				String result =appendUpImg(docid); 
				if (null != result) {
					if (SUCCESS.equals(result)) {
						logger.info("׷�ӿͻ�֤��Ӱ��ɹ���");
						response.putDataBasket("׷��Ӱ����", "true");
					} else {
						showMaskError("ʧ����Ϣ", "Ӱ����ʧ��,ԭ��Ϊ:" + result + "!", "����",
								"submitAndcloseMaskPage","����","moduleDone");
						response.stay();
						return;
					}
				}else{
					showMaskError("ʧ����Ϣ", "Ӱ����ʧ��", "����", "submitAndcloseMaskPage","����","moduleDone");
					response.stay();
					return;
				}
			
		}  catch (Exception e) {
			// TODO: handle exception
			logger.info("׷�ӿͻ�Ӱ���쳣", e);
			showMaskError("ʧ����Ϣ", "׷��Ӱ���쳣", "����", "submitAndcloseMaskPage","����","moduleDone");
			response.stay();
			return;
		}	
	}

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO �Զ����ɵķ������
		Model model = new Model();
		model.put("info", "���ڽ���Ӱ���ϴ������Ժ�......");
		return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
	}

	
	/**
	 * �ϴ�Ӱ��  CCMQ
	 * 
	 * @return
	 */
	private String appendUpImg(String docid) throws Exception {
		logger.info("׷��Ӱ����Ϣ");
		String result = null;
		JSONArray fileName = new JSONArray();
		fileName.add("IDback");
		fileName.add("IDfront");
		fileName.add("IDhead");
		fileName.add("finger");
		result = ECMUtil.appendUpImage(this, "idcard", docid, "CCMQ", "CCMQ", fileName);
		if (null != result && SUCCESS.equals(result)) {
			JSONArray fileNameface = new JSONArray();
				JSONObject faceInfo = null;
				if (�ֳ���������Ϣ != null && !"".equals(�ֳ���������Ϣ)) {
					faceInfo = JSONObject.parseObject(�ֳ���������Ϣ);
					docidRlls = faceInfo.getString("DOCID");
					score = faceInfo.getString("LOCALESCORE");
				}
				/**
				 * 2�����пͻ����ղ�������������������takephoto
				 * 2.1����ǰ��������ͷ������
				 * 2.2���ֳ���Ӱ���ǲ�����
				 * 2.3������������δ���棨������ʧ�ܣ�������ֵΪ-200��
				 */
				boolean status = checkCameraStatus();
				if (!status || docidRlls == null || "".equals(docidRlls)
						|| "-200".equals(score)) {
					fileNameface.add("takePhoto");
				}else{				
					//�Զ����յ���Ƭ����
					String localId= faceInfo.getString("LOCALEID");
					if(localId == null || localId.length() == 0){
						localId = ImglocalId;
					}
					fileNameface.add(localId);
				}
				result = ECMUtil.appendUpImage(this, "faceImg", docid, "CCMQ", "CCMQ", fileNameface);

		}else{
			return result;
		}
		return result;
	}
		/**
		 * �����������ͷ״̬�Ƿ�����
		 */
		public boolean checkCameraStatus() {
			String cameraStatus = "-15";
			try {
				cameraStatus = issueCall("faceCamera",
						FaceFileUtil.issueParameter("status"));
			} catch (Exception e) {
				logger.error("��ȡ����ͷ״̬�쳣");
			}
			if ("1".equals(cameraStatus)) {
				return true;
			}
			return false;
		}

}
