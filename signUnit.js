/*����*/
let unlock = function(type){
    console.log("ִ����unlock����");
    let request = {};
    let unlock = {};
    request.device="lock"
    request.unlock = unlock;
    unlock.type= type;	
        let successFun = function(data){
             console.log("�������ǩ�����:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){
                $('#myModal').modal('hide');
                sign_close_forSubmit();
            }else{
                if("error"==obj.type && "e1204" == obj.code){
                    showInfo_warning("����ɵ���ǩ��");
                    return;
                }else {
                    errorHandler("�������ǩ��ʧ��,"+data,"�������ǩ��ʧ��");     
                }
            }
        }       
        let failFun = function(err){
            console.log("�������ǩ���쳣:"+err);
            errorHandler("�������ǩ���쳣,"+err);
        }
        sign.save("png").then(successFun,failFun); 

}

$(function () {  
    let slider_Branch = new SliderUnlock(".slideunlock-slider.branch", {
        successLabelTip:"ȷ����",
        labelTip: "&gt;&nbsp;����ȷ��",
        duration: 200   // 
    }, function () {
        unlock("branch");
        setTimeout(function(){
            slider_Branch.reset();            
        },2000)
    }, function () {

    });
//slider_Branch.style.disply = "flex";
//slider_Branch.style.justify-content = "center";
//slider_Branch.style.align-items = "center";
    slider_Branch.init();
})

let domToCanvas = function(){
    html2canvas($("#pdfViewerDiv")[0]).then(canvas=>{
        let baseImgSuccess = function(result){
            //��ȥ��ģ̬��
            let options = {
                backdrop:false,
                keyboard:false,
                position:'center'
            }
            $('#myModal').modal(options);
        }

        let baseImgFail = function(result){
            showIsUserConfirm("�������ÿ���������ͼƬʧ��,�Ƿ�����","domToCanvas�����ʾ�ͼƬʧ��,ԭ����:"+result,domToCanvas);
        }

        let basePath = canvas.toDataURL('image/jpeg', '1.0');
        base64ToImg(basePath).then(baseImgSuccess,baseImgFail).catch(err =>{
            console.log(err);
        });
    });
}

let base64ToImg = function(basePath){
    const filePath = "signature\\answer.jpg";
    return domain.require("fs").then(cs =>{
        cs.writePhoto(filePath,basePath).then(result =>{
            return result;
        });
    });
}

//���϶�������ͼƬ	
//ģ̬����ȫ��Ⱦ���
$('#myModal').on('shown.zui.modal',function(){
    sign_open();
});

let sign_open = function (){
    let successFun = function(data){
        console.log("�򿪵���ǩ�����:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){}else{
            console.log("�򿪵���ǩ��ʧ��:"+data);
            errorHandler("�򿪵���ǩ��ʧ��,"+data,"�򿪵���ǩ��ʧ��");
        }
    }				
    let failFun = function(err){
        console.log("�򿪵���ǩ���쳣:"+err);
        errorHandler("�򿪵���ǩ���쳣,"+err,"�򿪵���ǩ���쳣");
    }		
    let transparent = "0";
    let xSite = "468";
    let ySite = "430";
    let height = "300";
    let	width = "600";
    let title = "";
    let desc = "";
let background = "rgba(255,255,255,1)";
    sign.openWindow(transparent,xSite,ySite,height,width,title,desc,background).then(successFun,failFun);
}

$("#save").click(function(){
    let successFun = function(data){
        console.log("�������ǩ�����:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){
            $('#myModal').modal('hide');
            sign_close_forSubmit();
        }else{
            if("error"==obj.type && "e1204" == obj.code){
                showInfo_warning("����ɵ���ǩ��");
                return;
            }else {
                errorHandler("�������ǩ��ʧ��,"+data,"�������ǩ��ʧ��");     
            }
        }
    }

    let failFun = function(err){
        console.log("�������ǩ���쳣:"+err);
        errorHandler("�������ǩ���쳣,"+err);
    }
    sign.save("png").then(successFun,failFun);
})

//�����رղ���������
let sign_close_forSubmit = function(){
    let successFun = function(data){
        console.log("�رյ���ǩ�����:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){
            domain.callMethodPromise('pageSubmit');
        }else{
            console.log("�ر�ʧ�ܣ�����1��,"+data);
            sign_close_forErr();
        }
    }

    let failFun = function(err){
        console.log("�رյ���ǩ���쳣:"+err);
        console.log("�ر�ʧ�ܣ�����1��");
        sign_close_forErr();
    }

    sign.close().then(successFun,failFun);
}

//�쳣�رգ�������
let sign_close_forErr = function(){
    let successFun = function(data){
        console.log("�رյ���ǩ�����Խ��:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){
            domain.callMethodPromise('pageSubmit');
        }else{
            console.log("�رյ���ǩ������ʧ��:,"+data);
            errorHandler("�رյ���ǩ������ʧ��:,"+data,"�رյ���ǩ��ʧ��");      
        }
    }

    let failFun = function(err){
         console.log("���Թرյ���ǩ���쳣:"+err);
         errorHandler("���Թرյ���ǩ���쳣:"+err);
    }
    sign.close().then(successFun,failFun);
}

$("#clean").click(function(){
    let successFun = function(data){
        console.log("���ǩ�����:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){}else{
            console.log("�رյ���ǩ������ʧ��:,"+data);
            errorHandler("���ǩ��ʧ��:"+data,"���ǩ��ʧ��");
        }
    }

    let failFun = function(err){
        console.log("���ǩ���쳣:"+err);
        errorHandler("���ǩ���쳣:"+err);
    }
    sign.clean().then(successFun,failFun);
})

//info չʾ���ͻ�����Ϣ  desc ��־��¼��������Ϣ
let showIsUserConfirm = function(info,desc,retry){
    //������ʾ��
    domain.userConfirm("",info,"����","ȡ��",true,retry,function(){
        recordLog(desc);
    });
}

//����˼�¼��Ϣ
let recordLog = function(desc){
    domain.callMethodPromise("recordLog",desc);
}

//������쳣���� 
let errorHandler = function(log,info){
    if(arguments.length == 1){
        domain.callMethodPromise("errorHandler",log,"");
    }else{
        domain.callMethodPromise("errorHandler",log,info);
    }
    
}

let showInfo = function(info,type){
    new $.zui.Messager(info, {
        type:type
    }).show();
}

let showInfo_danger = function(info){
    showInfo(info,'danger');
}

let showInfo_warning = function(info){
    showInfo(info,'warning');
}  