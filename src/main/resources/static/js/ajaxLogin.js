function formLogin(e) {
        let username = $("input[name='username']").val().trim();
        let password = $("input[name='password']").val().trim();
        let data = {"username" : username, "password" : password};

   //     var csrfHeader = $('meta[name="_csrf_header"]').attr('content')
   //     var csrfToken = $('meta[name="_csrf"]').attr('content')

        $.ajax({
            type: "post",
            url: "api/login",
            data: JSON.stringify(data),
            dataType: "json",
            beforeSend : function(xhr){
                // xhr.setRequestHeader(csrfHeader, csrfToken);
                xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhr.setRequestHeader("Content-type","application/json");
            },
            success: function (data) {
                console.log(data);
                window.location = '/';
            },
            error : function(xhr, status, error) {
                console.log(error);
                window.location = '/login?error=true&exception=' + xhr.responseText;
            }
        });
    }