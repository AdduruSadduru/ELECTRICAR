function mainCommentSave() {
    let content = $(".mainCommentContent").val();
    if (!content) {
        alert("내용을 입력하세요");
    } else {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        let reviewId = $("#reviewId").val();
        let data = {
            "reviewId" : reviewId,
            "content" : content,
        };
        var param = JSON.stringify(data);
        $.ajax({
            url : "/comments/save",
            type : "post",
            contentType : "application/json",
            data : param,
            beforeSend : function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success : function (id) {
                alert("저장 하였습니다.");
                location.href='/review/view/' + id;
            },
            error : function(jqXHR, status, error) {
                if (jqXHR.status == '401') {
                    alert("로그인 후 이용해 주세요.");
                    location.href="/members/login";
                } else {
                    alert(jqXHR.responseText);
                }
            }
        });
    }
}

function commentDelete(val) {
    let select = confirm("삭제 하시겠습니까?");
    if (select) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        let commentId = val.parentNode.dataset.id;
        var url = "/comments/delete/" + commentId;

        $.ajax({
            url : url,
            type : "delete",

            beforeSend : function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success : function (id) {
                alert("삭제 하였습니다.");
                location.href='/review/view/' + id;
            },
            error : function(jqXHR, status, error) {
                if (jqXHR.status == '400') {
                    alert("알 수 없는 오류가 발생했습니다.");
                } else if (jqXHR.status == '401') {
                    alert("로그인 후 이용해 주세요.");
                    location.href="/members/login";
                } else if (jqXHR.status == '403') {
                    alert("삭제 할 권한이 없습니다.");
                } else {
                    alert(jqXHR.responseText);
                }
            }
        });
    }
}

function commentUpdate(val) {
    let content = val.parentNode.getElementsByTagName("textarea")[0].value;
    let originalContent = val.parentNode.parentNode.getElementsByClassName("mainContent")[0]
                        .getElementsByTagName("span")[0].textContent;
    if (!content) {
        alert("내용을 입력하세요.");
    } else if (content == originalContent) {
        alert("변경된 내용이 없습니다.");
    } else {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        let commentId = val.parentNode.parentNode.dataset.id;
        var url = "/comments/update/" + commentId + "?content=" + content;

        $.ajax({
            url : url,
            type : "patch",
            beforeSend : function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            dataType : "json",
            cache : false,
            success : function (id) {
                alert("수정 하였습니다.");
                location.href='/review/view/' + id;
            },
            error : function(jqXHR, status, error) {
                if (jqXHR.status == '400') {
                    alert("알 수 없는 오류가 발생했습니다.");
                } else if (jqXHR.status == '401') {
                    alert("로그인 후 이용해 주세요.");
                    location.href="/members/login";
                } else if (jqXHR.status == '403') {
                    alert("수정 할 권한이 없습니다.");
                } else {
                    alert(jqXHR.responseText);
                }
            },
        });
    }
}

function subCommentSave(val) {
    let content = val.parentNode.getElementsByTagName("textarea")[0].value;
    if (!content) {
        alert("내용을 입력하세요");
    } else {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        let parentId = val.parentNode.parentNode.dataset.id;
        let reviewId = $("#reviewId").val();
        let data = {
            "reviewId" : reviewId,
            "parentId" : parentId,
            "content" : content,
        };
        var param = JSON.stringify(data);
        $.ajax({
            url : "/comments/save",
            type : "post",
            contentType : "application/json",
            data : param,
            beforeSend : function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success : function (id) {
                alert("저장 하였습니다.");
                location.href='/review/view/' + id;
            },
            error : function(jqXHR, status, error) {
                if (jqXHR.status == '401') {
                    alert("로그인 후 이용해 주세요.");
                    location.href="/members/login";
                } else {
                    alert(jqXHR.responseText);
                }
            }
        });
    }
}

function mainUpdateColl(val) {
    let updateCollBtn = val;
    let parent = updateCollBtn.parentNode;
    let mainCommentUpdateDiv = parent.getElementsByClassName("mainCommentUpdateDiv")[0];
    let mainSaveBtn = document.getElementById("commentNew").getElementsByClassName("mainSaveBtn")[0];

    let textarea = mainCommentUpdateDiv.getElementsByTagName("textarea")[0];

    if (mainCommentUpdateDiv.style.display == "none") {
        let subCommentSaveDiv = parent.getElementsByClassName("subCommentSaveDiv")[0];
        let subCollBtn = parent.getElementsByClassName("subCollBtn")[0];
        let content = parent.getElementsByClassName("mainContent")[0]
                .getElementsByTagName("span")[0].textContent;
        textarea.value = content;
        subCommentSaveDiv.style.display = "none";
        subCollBtn.textContent = "답글 등록";

        mainCommentUpdateDiv.style.display = "block";
        mainSaveBtn.setAttribute("onClick", "#");
        updateCollBtn.textContent = "수정 취소";
    } else {
        textarea.value = "";
        mainCommentUpdateDiv.style.display = "none";
        mainSaveBtn.setAttribute("onClick", "mainCommentSave()");
        updateCollBtn.textContent = "수정 등록";
    }
}
function subCommentColl(val) {
    let subCollBtn = val;
    let parent = subCollBtn.parentNode;
    let subCommentSaveDiv = parent.getElementsByClassName("subCommentSaveDiv")[0];
    let mainSaveBtn = document.getElementById("commentNew").getElementsByClassName("mainSaveBtn")[0];

    let textarea = subCommentSaveDiv.getElementsByTagName("textarea")[0];

    if  (subCommentSaveDiv.style.display == "none") {
        let mainCommentUpdateDiv = parent.getElementsByClassName("mainCommentUpdateDiv")[0];
        let updateCollBtn = parent.getElementsByClassName("updateCollBtn")[0];
        textarea.value = "";
        mainCommentUpdateDiv.style.display = "none";
        updateCollBtn.textContent = "수정 등록";

        subCommentSaveDiv.style.display = "block";
        mainSaveBtn.setAttribute("onClick", "#");
        subCollBtn.textContent = "답글 취소";
    } else {
        textarea.value = "";
        subCommentSaveDiv.style.display = "none";
        mainSaveBtn.setAttribute("onClick", "#");
        subCollBtn.textContent = "답글 등록";
    }
}
function subUpdateColl(val) {
    let updateCollBtn = val;
    let parent = updateCollBtn.parentNode;
    let subCommentUpdateDiv = parent.getElementsByClassName("subCommentUpdateDiv")[0];
    let mainSaveBtn = document.getElementById("commentNew").getElementsByClassName("mainSaveBtn")[0];

    let textarea = subCommentUpdateDiv.getElementsByTagName("textarea")[0];

    if (subCommentUpdateDiv.style.display == "none") {
        let content = parent.getElementsByClassName("mainContent")[0]
                .getElementsByTagName("span")[0].textContent;
        textarea.value = content;

        subCommentUpdateDiv.style.display = "block";
        mainSaveBtn.setAttribute("onClick", "#");
        updateCollBtn.textContent = "수정 취소";
    } else {
        textarea.value = "";
        subCommentUpdateDiv.style.display = "none";
        mainSaveBtn.setAttribute("onClick", "mainCommentSave()");
        updateCollBtn.textContent = "수정 등록";
    }
}