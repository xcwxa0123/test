<script>
	<#include "signature.js">
    <#include "jquery.slideunlock.js">	
</script>
<style>
    <#include "slideunlock.css">
    <#include "signLayer.css">
	::-webkit-scrollbar{
	    display:none;
	}
	.msgBtn{
		    transform: translateY(-5px);
	}
	.msgContainer
	{
		margin-top:30px;
	}
	#submit{
		margin-top: 80px;
	}
	.ab-info.margin
	{
		font-size:28px;
	}
	
	.ceb .ab-wrapper .ab-content{
		text-align:center; 
		align-items:center;
	}
	.ab-body{
		text-align:left; 
		align-items:left;
	}	
	.ab-content .ab-text-title{
		text-align:left; 
		align-items:left;
	}	
	.my-button{
		outline: none;
		text-align: center;
		box-shadow: none;
		border: none;
		color: #ffffff;
		background: #AE41CF;
		width: 200px;
		height: 60px;
		line-height: 60px;
		border-radius: 4px;
		font-size: 16px;
		margin: 30px;
	}

</style>
<div class='ab-wrapper'>
	<div class="ab-content">

			<div style="margin-top: 1400px;" >
			<span class="title" style="font-size:25px" ><strong style="text-align:center">中国光大银行客户投资风险承受能力评估问卷</strong></span>	
			<div class="ab-text-title" style="margin-top:20px;">
			<p>向本行第一次申请任何理财投资产品前，请填写本问卷，并于每年进行重新评估。本问卷旨在了解</p>
			<p>您可承受的风险程度、以及您的投资经验，借此协助您选择合适的理财产品类别，以达到您的投资目标。</p>
			<p>本银行建议您，当您发生可能影响自身风险承受能力的情形，再次购买理财产品时应当主动要求商业银行</p>
			<p>对您进行风险承受能力评估。</p>
			</div>
			</div>

		<div class='ab-body'>
		
		</div>
		<div class='ab-controller-bar'>
			<button type="button" class="my-button" id='subBtn'><span>确认</span></button>
		</div>
	</div>

</div>

<#include "openLayer.ftl" >

<script>

	let map = {};
    //true首次 false非首次
    let firstFlag;

    let createAndMonitor = function (topicData,length,flag) {
    	console.log("flag:"+flag);
    	if("true" == flag){
            firstFlag = true;
        }else{
            firstFlag = false;
        }
        console.log(topicData);
        createTopicHandle(topicData);
        changeFun(length);
    }

    let createTopicHandle = function (topicData) {
        let topicArray = eval(topicData);
        console.log(topicArray);
        let body_div = $(".ab-body");
        for (let i = 0; i < topicArray.length; i++) {
            let topicGroup = topicArray[i];
            createTopicFrame(topicGroup,body_div);
        }
    }

    //数组索引 0为题号 1为题目 后面答案
    let createTopicFrame = function (array,body_div) {
    	console.log(array);
        let topicNum = array[0];
        let topic = array[1];
        let topic_answer_div = $("<div class='topic_answer_" + topicNum + "' data-num='topic" + topicNum + "'></div>");
        let topic_title_div = createTopicTitle(topic,topicNum);
        for (let i = 2; i < array.length; i++) {
            let topicInfo = array[i];
            let answerNum = i-1;
            let answer = createTopicAnswer(topicInfo, topicNum,answerNum);
            topic_answer_div.append(answer);
        }
        body_div.append(topic_title_div);
        body_div.append(topic_answer_div);
		return body_div;
    }

    let createTopicTitle = function(topic,topicNum){
        let topic_title_div = $("<div class='topic_title_ "+ topicNum + "'></div>");
        let topic_p = $("<p></p>");
        let topic_h = $("<h3 class='header-dividing'></h3>");
        topic_h.text(topic);
        topic_p.append(topic_h);
        topic_title_div.append(topic_p);
        return topic_title_div;
    }

    let createTopicAnswer = function (topicInfo, topicNum,answerNum) {
        let div = $("<div class='radio'></div>");
        let label = $("<label></label>");
        let input = $("<input class='radio_options' type='radio' name='radio_options_" + topicNum +"'>");
        let span = $("<span></span>");
        input.attr("value", answerNum);
        span.text(topicInfo);
        label.append(input);
        label.append(span);
        div.append(label);
        return div;
    }
    
    //上面都是为了构建界面的
    
    //收集数据
    $(".ab-content").on("change", ".radio_options", function () {
        let result = $(this).val();
        console.log(result);
        let label = $(this).parent();
        let div_radio = $(label).parent();
        let key = $($(div_radio).parent()).attr("data-num");
        map[key] = result;
    })

    //校验是否完成答题
    let check = function(obj,length){
        let arr = Object.keys(obj);
        let arrayLength = arr.length;
        if (arrayLength == length){
            return true;
        }
        return false;
    }

    //提交按钮触发的方法
    let changeFun = function(length){
    	let canvasArea = $(".ab-body");
        $('#subBtn').click(function () {
            let result = check(map,length);
            //给pageSubmit的值
            str = JSON.stringify(map);
            if (result){
                domain.userConfirm("","确定后评估问卷不能再修改，确定提交？", "确定", "取消", true ,function(){
                    if(firstFlag){
                        //首次处理
                        console.log("首次风险评估");
                        lockRadio();
                        let frameCoordinate = {
							"transparent":"0",
							"xSite":"477",
							"ySite":"435",
							"height":"303",
							"width":"570",
							"title":"",
							"desc":"",
						};
		                configJSign("", canvasArea, frameCoordinate, str);//设置签名框参数，若不通过submit点击触发，则传NULL
                    }else{
                        //非首次处理
                        console.log("非首次风险评估");
                        domain.callMethodPromise('pageSubmit',str);
                    }
                },function(){
                    return;
                });
            }else {
               	domain.pushType("", "请完成所有问题作答！", "info");
				return;
            }
        })    
    }

    //锁定界面
    let lockRadio = function(){
        $("input.radio_options").attr("disabled","disabled");
    }

</script>