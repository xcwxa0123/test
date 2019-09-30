/*滑块*/
let unlock = function(type){
    console.log("执行了unlock方法");
    let request = {};
    let unlock = {};
    request.device="lock"
    request.unlock = unlock;
    unlock.type= type;	
        let successFun = function(data){
             console.log("保存电子签名结果:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){
                $('#myModal').modal('hide');
                sign_close_forSubmit();
            }else{
                if("error"==obj.type && "e1204" == obj.code){
                    showInfo_warning("请完成电子签名");
                    return;
                }else {
                    errorHandler("保存电子签名失败,"+data,"保存电子签名失败");     
                }
            }
        }       
        let failFun = function(err){
            console.log("保存电子签名异常:"+err);
            errorHandler("保存电子签名异常,"+err);
        }
        sign.save("png").then(successFun,failFun); 

}

$(function () {  
    let slider_Branch = new SliderUnlock(".slideunlock-slider.branch", {
        successLabelTip:"确认中",
        labelTip: "&gt;&nbsp;滑动确认",
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
            //先去打开模态窗
            let options = {
                backdrop:false,
                keyboard:false,
                position:'center'
            }
            $('#myModal').modal(options);
        }

        let baseImgFail = function(result){
            showIsUserConfirm("生成信用卡申请声明图片失败,是否重试","domToCanvas生成问卷图片失败,原因是:"+result,domToCanvas);
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

//以上都是生成图片	
//模态窗完全渲染完成
$('#myModal').on('shown.zui.modal',function(){
    sign_open();
});

let sign_open = function (){
    let successFun = function(data){
        console.log("打开电子签名结果:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){}else{
            console.log("打开电子签名失败:"+data);
            errorHandler("打开电子签名失败,"+data,"打开电子签名失败");
        }
    }				
    let failFun = function(err){
        console.log("打开电子签名异常:"+err);
        errorHandler("打开电子签名异常,"+err,"打开电子签名异常");
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
        console.log("保存电子签名结果:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){
            $('#myModal').modal('hide');
            sign_close_forSubmit();
        }else{
            if("error"==obj.type && "e1204" == obj.code){
                showInfo_warning("请完成电子签名");
                return;
            }else {
                errorHandler("保存电子签名失败,"+data,"保存电子签名失败");     
            }
        }
    }

    let failFun = function(err){
        console.log("保存电子签名异常:"+err);
        errorHandler("保存电子签名异常,"+err);
    }
    sign.save("png").then(successFun,failFun);
})

//正常关闭并继续流程
let sign_close_forSubmit = function(){
    let successFun = function(data){
        console.log("关闭电子签名结果:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){
            domain.callMethodPromise('pageSubmit');
        }else{
            console.log("关闭失败，重试1次,"+data);
            sign_close_forErr();
        }
    }

    let failFun = function(err){
        console.log("关闭电子签名异常:"+err);
        console.log("关闭失败，重试1次");
        sign_close_forErr();
    }

    sign.close().then(successFun,failFun);
}

//异常关闭，并重试
let sign_close_forErr = function(){
    let successFun = function(data){
        console.log("关闭电子签名重试结果:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){
            domain.callMethodPromise('pageSubmit');
        }else{
            console.log("关闭电子签名重试失败:,"+data);
            errorHandler("关闭电子签名重试失败:,"+data,"关闭电子签名失败");      
        }
    }

    let failFun = function(err){
         console.log("重试关闭电子签名异常:"+err);
         errorHandler("重试关闭电子签名异常:"+err);
    }
    sign.close().then(successFun,failFun);
}

$("#clean").click(function(){
    let successFun = function(data){
        console.log("清除签名结果:"+data);
        let obj = eval('('+data+')');
        if("result" == obj.type && "true" == obj.result){}else{
            console.log("关闭电子签名重试失败:,"+data);
            errorHandler("清除签名失败:"+data,"清除签名失败");
        }
    }

    let failFun = function(err){
        console.log("清除签名异常:"+err);
        errorHandler("清除签名异常:"+err);
    }
    sign.clean().then(successFun,failFun);
})

//info 展示给客户的信息  desc 日志记录的描述信息
let showIsUserConfirm = function(info,desc,retry){
    //弹出提示窗
    domain.userConfirm("",info,"重试","取消",true,retry,function(){
        recordLog(desc);
    });
}

//服务端记录信息
let recordLog = function(desc){
    domain.callMethodPromise("recordLog",desc);
}

//服务端异常处理 
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