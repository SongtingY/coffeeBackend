// 首先是点击后发送邮件
$(function(){
	$("#verifyCodeBtn").click(getVerifyCode);
	$("form").submit(check_data);
    $("input").focus(clear_error);
});

function getVerifyCode() {
    var email = $("#your-email").val();

    if(!email) {
        alert("请先填写您的邮箱！");
        return false;
    }

	$.get(
	    CONTEXT_PATH + "/forget/code",
	    {"email":email},
	    function(data) {
	        data = $.parseJSON(data);
	        if(data.code == 0) {
                alert("验证码已发送至您的邮箱,请登录邮箱查看!");
	        } else {
                alert(data.msg);
	        }
	    }
	);
}

function check_data() {
	var pwd1 = $("#password").val();
	var pwd2 = $("#confirm-password").val();
	if(pwd1 != pwd2) {
		$("#confirm-password").addClass("is-invalid");
		return false;
	}
	return true;
}

function clear_error() {
	$(this).removeClass("is-invalid");
}