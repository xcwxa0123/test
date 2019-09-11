function Signature(){

}

Signature.prototype.clean = function(){
    let request = this.clean_config();
    return domain.promiseOpenService("/device","post",request);
}

Signature.prototype.close = function(){
    let request = this.close_config();
    return domain.promiseOpenService("/device","post",request);
}

Signature.prototype.openWindow = function(transparent,xSite,ySite,height,width,title,desc){
    if(arguments.length == 5){
    	title = "";
        desc = "";
    }
    if(arguments.length == 6){
        openWindow.desc = "";
    }
    let request = this.openWindow_config(transparent,xSite,ySite,height,width,title,desc);
    return domain.promiseOpenService("/device","post",request);
}

Signature.prototype.save = function(format){
    let request = this.save_config(format);
    return domain.promiseOpenService("/device","post",request);
}

/** 
 *  ģ̬�� height: 300px;
 *  Ĭ�Ͻ������   
 *  openWindow.transparent = "100";
    openWindow.xSite = "468";
    openWindow.ySite = "430";
    openWindow.height = "300";
    openWindow.width = "600";
 */
Signature.prototype.openWindow_config = function(transparent,xSite,ySite,height,width,title,desc){
    console.log(arguments.length);
    let request = {};
    request.device = "signature"
    let openWindow = {};
    openWindow.transparent = transparent;
    openWindow.xSite = xSite;
    openWindow.ySite = ySite;
    openWindow.height = height;
    openWindow.width = width;
    openWindow.title = title;
    openWindow.desc = desc;
    request.openWindow = openWindow;
    console.log(request);
    return request;
} 

Signature.prototype.close_config = function(){
    let request = {};
    request.device = "signature";
    request.interrupt = {};
    return request;
}

Signature.prototype.clean_config = function(){
    let request = {};
    request.device = "signature";
    request.clean = {};
    return request;
}

Signature.prototype.save_config = function(format){
    let request = {};
    let save = {};
    save.format = format?format:"png";
    request.device = "signature";
    request.save = save;
    return request;
}

