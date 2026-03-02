package fms.controller.user;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fms.domain.LogDomain;
import fms.dto.UserDto;
import fms.entity.MUser;
import fms.form.UserForm;
import fms.service.UserService;
import fms.util.LogUtil;

/**
 * ユーザー管理コントローラー
 *
 * @author 大塚 月愛
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /** ユーザーサービス */
    @Autowired
    private UserService userService;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /**
     * ユーザー管理画面 初期表示
     *
     * @author 大塚 月愛
     *
     * @param retirementFlg
     * @param model
     * @return ユーザー管理画面
     */
    @RequestMapping(path = "/index")
    public String userList(@ModelAttribute UserForm userForm,
            @RequestParam(value = "retirementFlg", defaultValue = "0") Integer retirementFlg, Model model,
            HttpSession httpSession) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ユーザー管理", "USER_INDEX",
                mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        // ☆更新の戻るボタンからの遷移
        String userId = (String) httpSession.getAttribute("userId");
        userForm.setUserId(userId);

        // ユーザー情報の取得
        List<UserDto> userDtoList = userService.getUserDtoList(retirementFlg, null);

        model.addAttribute("userDtoList", userDtoList);

        return "user/index";
    }
}
