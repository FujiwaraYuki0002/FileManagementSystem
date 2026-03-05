/**
 * 共通処理
 */

// 登録確認ダイアログ
function insertcheck() {
    return window.confirm('登録します。よろしいですか？');
}

// 更新確認ダイアログ
function updatecheck() {
    return window.confirm('更新します。よろしいですか？');
}

// 削除確認ダイアログ
function deletecheck() {
    return window.confirm('削除します。よろしいですか？');
}

// 行をクリックした際に、対応するラジオボタンを選択し、他の行のラジオボタンを解除する関数
function selectRadioButton(optionName) {
    // ラジオボタンを選択
    const radio = $(`input[id="${optionName}"]`);

    if (radio.length) {
        radio.prop('checked', true);

        // ボタン活性・非活性
        // 削除
        if ($('input[name="delete"]').length) {
            var delInput = $('#del');
            delInput.val($(`#${optionName}`).val());
            $('input[name="delete"]').prop('disabled', false);
        }
        // 更新
        if ($('input[name="update"]').length) {
            var updInput = $('#upd');
            updInput.val($(`#${optionName}`).val());
            $('input[name="update"]').prop('disabled', false);
        }
        // 登録
        if ($('input[name="insert"]').length) {
            var insInput = $('#ins');
            insInput.val($(`#${optionName}`).val());
            $('input[name="insert"]').prop('disabled', false);
        }

        // ファイル管理画面でのversion受け渡し用
        if ($('input[name="hiddenVersion"]').length) {
            var delFileVersionInput = $('#delFileVersion');
            delFileVersionInput.val($(`#${$(`#${optionName}`).val()}Version`).val());
        }
    }

    // 他のラジオボタンを解除
    clearOtherRadios(optionName);
}

/**
 * エラーメッセージダイアログ
 */

// エラーメッセージアラート
function errorAlert() {
    // アラート表示
    $('#errorAlert').show();
    document.getElementById("errorAlert").style.display = "block";
        trapTab();
}

// アラートを閉じる
function closeAlert(targetId) {
    $('#errorAlert').hide();

    document.getElementById("errorAlert").style.display = "none";
       restoreTab();

    // targetIdに値が入っている場合はその値に紐づくidにフォーカス
    if (targetId != null && targetId != "") {
        // targetIdに入っているidのテキストボックスにフォーカスを設定する
        $('#' + targetId).select();
    }
}
// Tabキーに関する規制
function trapTab() {
  let focusableElements = document.querySelectorAll("input, button, a, textarea, select");
  focusableElements.forEach(el => {
    if (el.id !== "okButton") {
      el.setAttribute("tabindex", "-1");
    }
  });

  document.addEventListener("keydown", restrictTab);
  document.getElementById("okButton").focus();
}

function restoreTab() {
  let focusableElements = document.querySelectorAll("input, button, a, textarea, select");
  focusableElements.forEach(el => {
    el.removeAttribute("tabindex");
  });

  document.removeEventListener("keydown", restrictTab);
}

function restrictTab(event) {
  if (event.key === "Tab") {
    event.preventDefault();
    document.getElementById("okButton").focus();
  }
}

/**
 * 参加者選択モーダルウィンドウ
 */

// モーダルを開く
function modalSetUp() {

    teamContentsList.forEach(function(teamContents) {
        if (!details["team" + teamContents.teamId]) {
            details["team" + teamContents.teamId] = []; // 配列がまだなければ初期化
            externalDetails["team" + teamContents.teamId] = []; // 配列がまだなければ初期化
            detailIds["team" + teamContents.teamId] = []; // 配列がまだなければ初期化
        }
        userList.forEach(function(user) {
            user.teamDtoList.forEach(function(team) {
                if (teamContents.teamId == team.teamId) {
                    details["team" + teamContents.teamId].push("氏名：" + user.userName + ", 所属：" + user.teamDtoList.map(function(team) { return team.teamName; }).join(", ") +  ", 役職：" + user.postName);
                    externalDetails["team" + teamContents.teamId].push(user.userName);
                    detailIds["team" + teamContents.teamId].push(user.userId);
                }
            });
        });
    });
    postContentsList.forEach(function(postContents) {
        if (!details["post" + postContents.postId]) {
            details["post" + postContents.postId] = []; // 配列がまだなければ初期化
            externalDetails["post" + postContents.postId] = []; // 配列がまだなければ初期化
            detailIds["post" + postContents.postId] = []; // 配列がまだなければ初期化
        }
        userList.forEach(function(user) {
            if (postContents.postName == user.postName) {
                details["post" + postContents.postId].push("氏名：" + user.userName + ", 所属：" + user.teamDtoList.map(function(team) { return team.teamName; }).join(", ") +  ", 役職：" + user.postName);
                externalDetails["post" + postContents.postId].push(user.userName);
                detailIds["post" + postContents.postId].push(user.userId);
            }
        });
    });
}

// モーダルを開く
function modalOpen() {
    // jQuery を使ってスタイルを設定
    $('#myModal').css('display', 'block');
    $('body').css('overflow', 'hidden');
    // 初期表示のリスト1にイベントを設定
    setUpClickEvents("teamList");
}

// モーダルを閉じる
function modalClose() {
    $('body').css('overflow', 'auto');

    $('#myModal').css('display', 'none');
    $('#detailList').css('display', 'none');

    // 配列としてselectedDataInputに渡す
    $('#selectedId').val($('#externalSelectedList li')
        .map(function() {
            // 各<li>のid属性を取得
            return this.id;
        })
        .get());

    // 配列としてselectedDataInputに渡す
    $('#selectedName').val($('#externalSelectedList li')
        .map(function() {
            // 各<li>のid属性を取得
            return $(this).text();
        })
        .get());

    // リストの表示状態を更新
    updateExternalSelectedListVisibility();
}

function toggleList(activeList, inactiveList, toggleActive, toggleInactive) {
    activeList.classList.add("active");
    inactiveList.classList.remove("active");
    toggleActive.disabled = true;
    toggleInactive.disabled = false;
}

// 所属リストを開く
function teamOpen() {
    toggleList(teamList, postList, toggleTeamList, togglePostList);

    // リスト1が表示された時にクリックイベントを設定
    setUpClickEvents("teamList");
}

// 役職リストを開く
function postOpen() {
    toggleList(postList, teamList, togglePostList, toggleTeamList);

    // リスト2が表示された時にクリックイベントを設定
    setUpClickEvents("postList");
}

// リストの名前をクリックした時に詳細リストを表示
function showDetails(region) {

    // 詳細リストをクリア
    $('#detailList').empty();

    // detailsとdetailIdsが存在するか確認
    if (details[region] && detailIds[region]) {
        details[region].forEach(function(person, index) {
            // jQueryで<li>を作成
            var li = $('<li title="追加"></li>').text(person);

            // ボタンを作成して、リストアイテムの前に追加
            const addButton = $('<input type="button" class="userPlus" value="↓">');
            li.prepend(addButton); // ボタンをリストアイテムの前に追加

            // 対応するdetailIdを取得
            const detailId = detailIds[region][index];
            // モーダルウィンドウ外リストに追加する名前
            const externalName = externalDetails[region][index];

            // クリックイベントを設定
            li.on('click', function() {
                // detailIdを渡す
                addToSelectedList(person, detailId, externalName);
            });

            // ここで、addButtonに対してmouseenterとmouseleaveイベントを追加
            li.on('mouseenter', function() {
                // hover時にaddButtonの色を青に変更
                addButton.css('background-color', '#2589d0');
            });

            li.on('mouseleave', function() {
                // hoverを外した時にaddButtonの色を元に戻す
                addButton.css('background-color', '');
            });

            // リストに追加
            $('#detailList').append(li);
        });

        // リストが空の場合は「何もありません」を表示
        if ($('#detailList').text().trim() === '') {
            $('#detailList').text('ユーザーが見つかりませんでした');
        }

        // リストを表示
        $('#detailList').css('display', 'block');
    }
}

// モーダル内リストに名前を追加する関数
function addToSelectedList(name, detailId, externalName) {

    // 既に追加されていないかチェック
    // jQueryでリストアイテムを取得
    const selectedItems = $("#selectedList li");
    selectedItems.each(function() {
        if ($(this).attr("id") === detailId) {
            throw new Error("既に追加されています。");
        }
    });

    // モーダル内の選択者リストに追加
    // jQueryで<li>を作成
    const selectedListItem = $('<li title="選択を解除"></li>').text(name);

    // ボタンを作成して、リストアイテムの前に追加
    const deleteButton = $('<input type="button" class="userMinus" value="×">');
    selectedListItem.prepend(deleteButton); // ボタンをリストアイテムの前に追加

    // detailIdをid属性として設定
    selectedListItem.attr("id", detailId);

    // クリックイベントを設定
    selectedListItem.on('click', function() {
        // クリックされた名前を削除
        deleteSelected(detailId);
    });

    // ここでリストアイテムに対してmouseenterとmouseleaveイベントを追加
    selectedListItem.on('mouseenter', function() {
        // hover時にdeleteButtonの色を赤に変更
        deleteButton.css('background-color', 'red');
    });

    selectedListItem.on('mouseleave', function() {
        // hoverを外した時にdeleteButtonの色を元に戻す
        deleteButton.css('background-color', '');
    });

    // モーダル内リストに追加
    $('#selectedList').append(selectedListItem);

    // モーダル外の選択者リストに追加
    // jQueryで<li>を作成
    const externalListItem = $('<li></li>').text(externalName);

    // detailIdをid属性として設定
    externalListItem.attr("id", detailId);
    $('#externalSelectedList').append(externalListItem);

    // モーダル外の選択者リストを表示
    updateExternalSelectedListVisibility();
}

// リストから名前を削除する関数
function deleteSelected(detailId) {
    // モーダル内の選択者リストから削除
    $("#selectedList li").each(function() {
        if ($(this).attr("id") === detailId) {
            // jQueryでリストアイテムを削除
            $(this).remove();
        }
    });

    // モーダル外の選択者リストから削除
    $("#externalSelectedList li").each(function() {
        if ($(this).attr("id") === detailId) {
            // jQueryでリストアイテムを削除
            $(this).remove();

            // モーダル外の選択者リストを非表示にするかどうか判定
            updateExternalSelectedListVisibility();
        }
    });
}

function setUpClickEvents(listType) {
    var listItems;
    if (listType === "teamList") {
        // jQueryでli要素を取得
        listItems = $('#teamList li');
    } else {
        // jQueryでli要素を取得
        listItems = $('#postList li');
    }

    listItems.each(function() {
        $(this).on('click', function() {
            // item.idがdetailsに存在するか確認
            if ($(this).attr('id') in details) {
                // showDetailsを呼び出し
                showDetails($(this).attr('id'));
            }
        });
    });
}

// 外部選択者リストが空かどうかを確認する関数
function updateExternalSelectedListVisibility() {
    if ($('#externalSelectedList').children().length === 0) {
        // リストが空の場合、非表示にする
        $('.external_selected_container').css('display', 'none');
    } else {
        // リストが空でない場合、表示する
        $('.external_selected_container').css('display', 'block');
    }
}

// クリックしたラジオボタン以外を解除する関数
function clearOtherRadios(selectedOption) {
    // 全てのラジオボタンを取得
    const radios = $('input[type="radio"]');

    radios.each((index, radio) => {
        // クリックしたラジオボタン以外の選択を解除
        if (radio.id !== selectedOption) {
            radio.checked = false;
        }
    });
}

// ユーザーIDを渡して対応するデータを取得する関数
function getDetailsByUserId(userId, userName) {
    // 結果を格納する変数
    let result = null;

    // teamの情報を探す
    for (let key in detailIds) {
        if (detailIds[key].includes(userId)) {
            // 該当するチームや役職の情報を取得
            const detailsArray = details[key];

            // detailsArray内で、ユーザーIDが含まれる情報だけを抽出
            result = detailsArray.filter(item => item.includes("氏名：" + userName));

            // 絞り込んだ結果を出力
            if (result.length > 0) {

                addToSelectedList(result[0], userId, userName);
                modalClose();
                return;
            }
            break;
        }
    }
}

/**
 * 参加者一覧のドロップダウン
 */

// 参加者一覧のドロップダウン表示
function dropdownOpen(dropdownObj, event) {
    // 現在のドロップダウンを表示または非表示にする
    // jQueryで次の兄弟要素を取得
    var dropdownContent = $(dropdownObj).next('.dropdown-content');

    // すべてのドロップダウンを閉じる
    $('.dropdown-content').each(function() {
        if ($(this)[0] !== dropdownContent[0]) {
            // 他のドロップダウンを閉じる
            $(this).removeClass('show');
        }
    });

    // 現在のドロップダウンをトグル（開く/閉じる）
    dropdownContent.toggleClass('show');

    // イベントの伝播を停止（ボタン内の他のイベントに影響を与えないようにする）
    event.stopPropagation();
}

// 参加者一覧のドロップダウン非表示
function dropdownCloseCheck(event) {
    if (!$(event.target).closest('.dropdown').length) {
        // dropdown-contentの情報を全て取得
        $('.dropdown-content').each(function() {
            // dropdown-contentを全て非表示
            $(this).removeClass('show');
        });
    }
}