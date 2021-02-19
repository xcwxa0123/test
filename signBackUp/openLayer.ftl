<script>
	<#include "signature.js">
</script>
<div class="modal fade" id="myModal">
    <div class="modal-dialog">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal"><span class="sr-only">�ر�</span></button>
            <h3 class="modal-title">������֪Ϥ�������ݣ�����<span class="modal-title-type">����</span>��¼��<span class="modal-title-type">���Ķ�������</span>����ǩ��<span class="modal-title-type">��������</span>��</h3>
        </div>
        <div class="modal-body" style="background: rgba(255,255,255,1) url('/resource/img/���ָ�.png') repeat;">
			
        </div>
        <div class="modal-footer">
           <!-- <button type="button" id="clean" class="my-button-small">��д</button> -->
            <div class="tab-content">
				<div class="slideunlock-wrapper">
					<input type="hidden" value="" class="slideunlock-lockable" />
					<div class="slideunlock-slider branch ">
						<span class="slideunlock-label">&gt;&gt;</span>
						<span class="slideunlock-lable-tip"></span>
					</div>
			    </div>
	        </div>
		<button type="button" id="clean" class="my-button-small">��д</button>
        </div>
    </div>
</div>

<script>


//����ǩ��������ʹ�С���ȳ�ʼ��Ϊ�գ����ڵ��submit��ť��ʱ��ֵ
let FCoordinate = {};

//����ύʱ�Ǵ��͵����ݡ��ȳ�ʼ��Ϊ�գ������ʱ����������ֵ������ΪĬ�Ͽ�
let signData = "";

let delsignature = function () {
    const srcPath = "signature";
    domain.require("fs").then(cs => {
        cs.delDirectory(srcPath).then(result => {
            console.log("ɾ��signature�ļ��������ݽ��:" + result);
        });
    });
}


let domToCanvas = function (canvasArea) {
    console.log("����domToCanvas");
    html2canvas(canvasArea[0]).then(canvas => {
        let baseImgSuccess = function (result) {
            //��ȥ��ģ̬��
            let options = {
                backdrop: false,
                keyboard: false,
                position: 'center'
            }
            console.log("��ģ̬��");
            $('#myModal').modal(options);
        }

        let baseImgFail = function (result) {
            showIsUserConfirm("�������ÿ���������ͼƬʧ��,�Ƿ�����", "domToCanvas�����ʾ�ͼƬʧ��,ԭ����:" + result, domToCanvas);
        }

        let basePath = canvas.toDataURL('image/jpeg', '1.0');
        base64ToImg(basePath).then(baseImgSuccess, baseImgFail).catch(err => {
            console.log(err);
        });
    });
}


let base64ToImg = function (basePath) {
    const filePath = "signature\\answer.jpg";
    return domain.require("fs").then(cs => {
        cs.writePhoto(filePath, basePath).then(result => {
            return result;
        });
    });
}

//ģ̬����ȫ��Ⱦ���

$('#myModal').on('shown.zui.modal', function () {
    console.log("����zui��ģ̬����Ⱦ��Ϸ���");
    sign_open(FCoordinate);
});

let sign_open = function (FCoordinate) {
    console.log("����ǩ������ʵ�������������鿴�Ƿ��н��ܵ�����ֵ");
    console.log(FCoordinate);
    console.log("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
    let successFun = function (data) {
        console.log("�򿪵���ǩ�����:" + data);
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) { } else {
            console.log("�򿪵���ǩ��ʧ��:" + data);
            errorHandler("�򿪵���ǩ��ʧ��," + data, "�򿪵���ǩ��ʧ��");
        }
    }
    let failFun = function (err) {
        console.log("�򿪵���ǩ���쳣:" + err);
        errorHandler("�򿪵���ǩ���쳣," + err, "�򿪵���ǩ���쳣");
    }
    let transparent = FCoordinate.transparent || "0";
    let xSite = FCoordinate.xSite || "477";
    let ySite = FCoordinate.ySite || "435";
    let height = FCoordinate.height || "303";
    let width = FCoordinate.width || "570";
    let title = FCoordinate.title || "";
    let desc = FCoordinate.desc || "";
    console.log("-------------���Խ�������--------------"+transparent+","+xSite+","+ySite+","+height+","+width+","+title+","+desc+"-------------------------------------");
    sign.openWindow(transparent, xSite, ySite, height, width, title, desc).then(successFun, failFun);
}

$("#save").click(function () {
	console.log('button has been clicked');
    let successFun = function (data) {
        console.log("�������ǩ�����:" + data);
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) {
            $('#myModal').modal('hide');
            sign_close_forSubmit();
        } else {
            if ("error" == obj.type && "e1204" == obj.code) {
                showInfo_warning("����ɵ���ǩ��");
                return;
            } else {
                errorHandler("�������ǩ��ʧ��," + data, "�������ǩ��ʧ��");
            }
        }
    }
    
    let failFun = function (err) {
        console.log("�������ǩ���쳣:" + err);
        errorHandler("�������ǩ���쳣," + err);
    }
    sign.save("png").then(successFun, failFun);
})

//�����رղ���������
let sign_close_forSubmit = function () {
    let successFun = function (data) {
        console.log("�رյ���ǩ�����:" + data);
        console.log("�����Ƿ���Խ��ܵ�signData�ַ���"+signData+"--------------------------------------");
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) {
            if(signData == "" || signData == null || signData == undefined){
        		domain.callMethodPromise('pageSubmit');	
        	}else {
        		domain.callMethodPromise('pageSubmit',signData);
        	}
        } else {
            console.log("�ر�ʧ�ܣ�����1��," + data);
            sign_close_forErr();
        }
    }

    let failFun = function (err) {
        console.log("�رյ���ǩ���쳣:" + err);
        console.log("�ر�ʧ�ܣ�����1��");
        sign_close_forErr();
    }

    sign.close().then(successFun, failFun);
}

//�쳣�رգ�������
let sign_close_forErr = function () {
    let successFun = function (data) {
        console.log("�رյ���ǩ�����Խ��:" + data);
        console.log("�����Ƿ���Խ��ܵ�signData�ַ���"+signData+"--------------------------------------");
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) {
        	if(signData == "" || signData == null || signData == undefined){
        		domain.callMethodPromise('pageSubmit');	
        	}else {
        		domain.callMethodPromise('pageSubmit',signData);
        	}
        } else {
            console.log("�رյ���ǩ������ʧ��:," + data);
            errorHandler("�رյ���ǩ������ʧ��:," + data, "�رյ���ǩ��ʧ��");
        }
    }

    let failFun = function (err) {
        console.log("���Թرյ���ǩ���쳣:" + err);
        errorHandler("���Թرյ���ǩ���쳣:" + err);
    }
    sign.close().then(successFun, failFun);
}

$("#clean").click(function () {
	console.log('button has been clicked');
    let successFun = function (data) {
        console.log("���ǩ�����:" + data);
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) { } else {
            console.log("�رյ���ǩ������ʧ��:," + data);
            errorHandler("���ǩ��ʧ��:" + data, "���ǩ��ʧ��");
        }
    }

    let failFun = function (err) {
        console.log("���ǩ���쳣:" + err);
        errorHandler("���ǩ���쳣:" + err);
    }
    sign.clean().then(successFun, failFun);
})

//info չʾ���ͻ�����Ϣ  desc ��־��¼��������Ϣ
let showIsUserConfirm = function (info, desc, retry) {
    //������ʾ��
    domain.userConfirm("", info, "����", "ȡ��", true, retry, function () {
        recordLog(desc);
    });
}

//����˼�¼��Ϣ
let recordLog = function (desc) {
    domain.callMethodPromise("recordLog", desc);
}

//������쳣����
let errorHandler = function (log, info) {
    if (arguments.length == 1) {
        domain.callMethodPromise("errorHandler", log, "");
    } else {
        domain.callMethodPromise("errorHandler", log, info);
    }

}

let showInfo = function (info, type) {
    new $.zui.Messager(info, {
        type: type
    }).show();
}

let showInfo_danger = function (info) {
    showInfo(info, 'danger');
}

let showInfo_warning = function (info) {
    showInfo(info, 'warning');
}

let configJSign = function (submitButton, canvasArea, frameCoordinate, teleSignData) {
    //�������  ����ǩ������Ϣ��������Ϊһ�����������
    console.log("����configJSign,�����Ƿ��ܽ��ܵ���������");
    console.log(frameCoordinate);
    FCoordinate = frameCoordinate;
    signData = teleSignData;
    var swiper = new Swiper('#swiper', {
        scrollbar: '.swiper-scrollbar',
        direction: 'vertical',
        slidesPerView: 'auto',
        mousewheelControl: true,
        freeMode: true,
        roundLengths: true,//��ֹ����ģ��
    });
    sign = new Signature();
    delsignature();//����signInit.js
    if(submitButton == ""){
    	domToCanvas(canvasArea);
	    sliderInit();
    }else{
    	submitButton.click(function () {
	        console.log("submit��ť�������");
	        //����signInit.js�еĻ��򷽷�
	        domToCanvas(canvasArea);
		    sliderInit(); 
    	});
    }
    
}

/*����*/
//unlock�����������ɹ���ִ��
let unlock = function (type) {
    console.log("ִ����unlock����");
    let request = {};
    let unlock = {};
    request.device = "lock"
    request.unlock = unlock;
    unlock.type = type;
    let successFun = function (data) {
        console.log("�������ǩ�����:" + data);
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) {
            $('#myModal').modal('hide');
            sign_close_forSubmit();
        } else {
            if ("error" == obj.type && "e1204" == obj.code) {
                showInfo_warning("����ɵ���ǩ��");
                return;
            } else {
                errorHandler("�������ǩ��ʧ��," + data, "�������ǩ��ʧ��");
            }
        }
    }
    let failFun = function (err) {
        console.log("�������ǩ���쳣:" + err);
        errorHandler("�������ǩ���쳣," + err);
    }
    sign.save("png").then(successFun, failFun);

}
//sliderInit����
let sliderInit = function () {
    let slider_Branch = new SliderUnlock(".slideunlock-slider.branch", {
        successLabelTip: "ȷ����",
        labelTip: "&gt;&nbsp;����ȷ��",
        duration: 200   // 
    }, function () {
        unlock("branch");
        setTimeout(function () {
            slider_Branch.reset();
        }, 2000)
    }, function () {

    });
    //slider_Branch.style.disply = "flex";
    //slider_Branch.style.justify-content = "center";
    //slider_Branch.style.align-items = "center";
    slider_Branch.init();
};

</script>