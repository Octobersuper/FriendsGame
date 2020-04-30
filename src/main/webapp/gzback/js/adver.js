  var path1='';
  var path2='';
layui.use(['form','layer','layedit','laydate','upload'],function(){
    var form = layui.form
        layer = parent.layer === undefined ? layui.layer : top.layer,
        laypage = layui.laypage,
        upload = layui.upload,
        layedit = layui.layedit,
        laydate = layui.laydate,
        $ = layui.jquery;
    form.on("submit(addNews)",function(data){
			//如果有选择图片则上传图片
			console.log(data)
			var formData = new FormData();
			        //append是将信息文件放到formdata中
					path1=path1
					$.ajax({
						type: "post",
						url: baseurl + "/admin",
						data: {
							back:"addphone",
							picture:path1
						},
						success: function(data) {
							if(data.state == "success"){
								setTimeout(function() {
									location.reload();
									layer.closeAll();
									layer.msg('添加成功');
							});
							}else{
								setTimeout(function() {
									location.reload();
									layer.closeAll();
									layer.msg('添加失败');
									});
							}
						}
				})
        return false;
    })
})


function xmTanUploadImg1(obj) {
	var file = obj.files[0];
	var reader = new FileReader();
	var files = document.getElementById("file").files;
	var filedata;
	
	//读取文件过程方法
	reader.onload = function(e) {
		console.log("成功读取1111....");
		var img = document.getElementById("xmTanImg");
		img.src = e.target.result;
	}
	reader.readAsDataURL(file)
	var formData = new FormData();
		if(files.length > 0) {
			for(var i = 0; i < files.length; i++) {
				//append是将信息文件放到formdata中
				formData.append('filedata', files[i]);
			}
		}
	$.ajax({
		type: "post",
		url: baseurl+"/upload",
		data:formData,
		cache: false,//上传文件无需缓存
        processData: false,//用于对data参数进行序列化处理 这里必须false
        contentType: false, //必须
		success: function(res) {
			console.log(res)
			path1=res.picurl;
			console.log(path1)
		}
	})
}
function xmTanUploadImg2(obj) {
	var file = obj.files[0];
	var reader = new FileReader();
	var files = document.getElementById("file2").files;
	var filedata;
	
	//读取文件过程方法
	reader.onload = function(e) {
		console.log("成功读取2222....");
		var img = document.getElementById("xmTanImg2");
		img.src = e.target.result;
		//或者 img.src = this.result;  //e.target == this
	}
	reader.readAsDataURL(file)
	var formData = new FormData();
		if(files.length > 0) {
			for(var i = 0; i < files.length; i++) {
				//append是将信息文件放到formdata中
				formData.append('filedata', files[i]);
			}
		}
	$.ajax({
		type: "post",
		url: baseurl+"/upload",
		data:formData,
		cache: false,//上传文件无需缓存
        processData: false,//用于对data参数进行序列化处理 这里必须false
        contentType: false, //必须
		success: function(res) {
			path2=res.data;
			console.log(path2)
		}
	})
}
