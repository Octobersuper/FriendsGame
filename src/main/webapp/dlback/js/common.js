/*var reg = /\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/;
var ip = reg.exec(window.location.href)[0];
*/
//var baseurl = "http://"+ip+":8094/Guangan_Mahjong";
var baseurl = "http://103.70.225.189:8080/Gz_Majhong";
//var baseurl = "http://localhost:8099/Gz_Majhong";
function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var r = window.location.search.substr(1).match(reg);
	if(r != null) {
		return unescape(r[2]);
	}
	return null;
}

$(document).on('keydown', function(e){
	if (e.keyCode === 13) {
		$(".layui-keydown").click();
	}
})