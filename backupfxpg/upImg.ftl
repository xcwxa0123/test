<script>
	<#include "mergePic.js">
</script>
<div class="ab-wrapper">
    <div class="ab-content">
       <div class='trade-indicator'>
       		<#if info??>
       		<p class='ab-info'><i class='icon icon-spin icon-spinner icon-2x'></i>${info!"后台处理中，请稍候..."}</p>
       		<#else>
       		<p class='ab-info'><i class='icon icon-spin icon-spinner icon-2x'></i>上传影像中，请稍候...</p>
       		</#if>
       </div>
    </div>
    
    <div class='ab-controller-bar'>
    </div>
</div>

<script>
    const customName = `${customName!""}`;
	const idCardNo = `${idCardNo!""}`;
	const date = `${date!""}`;
	const time = `${time!""}`;
	let imgPath2 = "/file\\C:/Program Files\\CEB\\stm\\signature\\signature.png";
    let imgPath1 = "/file\\C:\\Program Files\\CEB\\stm\\signature\\answer.jpg";
    let merge = new mergePic();

    merge.getImgInfo(imgPath1).then(p1 =>{
    	let height = p1.height;	
    });
    let p2 = merge.getImgInfo(imgPath2);
    Promise.all([p1,p2]).then((img) => {
        mergeBase(img[0],img[1]);
    }).catch((err) => {
        console.log(err);
        failHandler("获取图片信息-->"+err);
    })

    let mergeBase = function (img1, img2) {
        let param1 = {};
        let param2 = {};
        let img1_width = img1.naturalWidth;
        let img1_height = img1.naturalHeight;
        let img2_width = img2.naturalWidth;
        let img2_height = img2.naturalHeight;
        let baseCode1 = img1.baseCode;
        let baseCode2 = img2.baseCode;
        console.log("nature size:\r\nimg1_width:"+img1_width+"\r\nimg1_height:"+img1_height+"\r\nimg2_width:"+img2_width+"\r\nimg2_height:"+img2_height);

        const sign_x = img2_width>img1_width?img2_width-img1_width:img1_width-img2_width;
        param2.x = sign_x;
        let param = new Array();
            {
                let p = {}
                p.x = sign_x - 100;
                p.y = img1_height + 20;
                p.font = "20px 微软雅黑";
                p.content = "客户签名：";
                param.push(p);
            }
            {
                let p = {};
                p.x = 0;
                p.y = img1_height + 20;
                p.font = "20px 微软雅黑";
                p.content = "客户姓名：";
                param.push(p);
            }
            {
                let p = {};
                p.x = 100;
                p.y = img1_height + 20;
                p.font = "20px 微软雅黑";
                p.content = customName;
                param.push(p);
            }
            {
                let p = {};
                p.x = 0;
                p.y = img1_height + 20 * 2 + 5;
                p.font = "20px 微软雅黑";
                p.content = "证件号码：";
                param.push(p);
            }
            {
                let p = {};
                p.x = 100;
                p.y = img1_height + 20 * 2 + 5;
                p.font = "20px 微软雅黑";
                p.content = idCardNo;
                param.push(p);
            }
            {
                let p = {};
                p.x = 0;
                p.y = img1_height + 20 * 3 + 10;
                p.font = "20px 微软雅黑";
                p.content = "交易日期：";
                param.push(p);
            }
            {
                let p = {};
                p.x = 100;
                p.y = img1_height + 20 * 3 + 10;
                p.font = "20px 微软雅黑";
                p.content = date;
                param.push(p);
            }
            {
                let p = {};
                p.x = 0;
                p.y = img1_height + 20 * 4 + 15;
                p.font = "20px 微软雅黑";
                p.content = "交易时间：";
                param.push(p);
            }
            {
                let p = {};
                p.x = 100;
                p.y = img1_height + 20 * 4 + 15;
                p.font = "20px 微软雅黑";
                p.content = time;
                param.push(p);
            }
        merge.merge(baseCode1, baseCode2, param1, param2).then(function (baseCode) {
            let img1 = new Image();
            img1.src = baseCode1;
            let img2 = new Image();
            img2.src = baseCode2;
            addContent(baseCode, img1, img2, param);
        }).catch((err) => {
            console.log(err);
            failHandler("图片合并-->"+err);
        });
    }

    let addContent = function (baseCode, img1, img2, param) {
        merge.additional(baseCode, param).then((baseImg) => {
           base64ToImg(baseImg);
        }).catch((err) => {
            console.log(err);
            failHandler("添加文字信息-->"+err);
        })
    }

    let base64ToImg = function(basePath){
        const filePath = "C:\\Program Files\\CEB\\stm\\signature\\lcpg.jpg";
        domain.require("fs").then(cs =>{
            cs.baseImg(filePath,basePath).then(result => {
            	if(result && "true" == result){
					domain.callMethodPromise("upImg");					
            	}else{
					failHandler("base64转图片失败");
            	}
            });
        })
    }
    
    let failHandler = function(info){
    	domain.callMethodPromise("failHandler",info)
    }
    
	
</script>



